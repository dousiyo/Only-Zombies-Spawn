package com.dousiyo.onlyzombiesspawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

final class SpawnReplacementHandler {
    private SpawnReplacementHandler() {
    }

    static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        MobSpawnType spawnType = event.getSpawnType();
        if (!SpawnRestrictionRules.shouldReplace(mob, spawnType, event.getLevel())) {
            return;
        }

        event.setCanceled(true);
        event.setSpawnCancelled(true);
        spawnZombieReplacements(event.getLevel(), mob, spawnType, event.getDifficulty());
    }

    static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Mob mob)) {
            return;
        }

        MobSpawnType spawnType = mob.getSpawnType();
        if (spawnType == null || !SpawnRestrictionRules.shouldReplace(mob, spawnType, (ServerLevel) event.getLevel())) {
            return;
        }

        ServerLevel level = (ServerLevel) event.getLevel();
        event.setCanceled(true);
        spawnZombieReplacements(level, mob, spawnType, level.getCurrentDifficultyAt(mob.blockPosition()));
    }

    private static void spawnZombieReplacements(ServerLevelAccessor level, Mob source, MobSpawnType spawnType, DifficultyInstance difficulty) {
        for (int i = 0; i < SpawnWhitelistConfig.zombieSpawnMultiplier(); i++) {
            spawnZombieReplacement(level, source, spawnType, difficulty);
        }
    }

    private static void spawnZombieReplacement(ServerLevelAccessor level, Mob source, MobSpawnType spawnType, DifficultyInstance difficulty) {
        Zombie zombie = EntityType.ZOMBIE.create(level.getLevel());
        if (zombie == null) {
            Constants.LOGGER.warn("Failed to create zombie replacement for {}", source.getType());
            return;
        }

        zombie.moveTo(source.getX(), source.getY(), source.getZ(), source.getYRot(), source.getXRot());
        if (source.isPersistenceRequired()) {
            zombie.setPersistenceRequired();
        }

        zombie.finalizeSpawn(level, difficulty, spawnType, null);
        if (zombie.isSpawnCancelled()) {
            zombie.discard();
            Constants.LOGGER.warn("Zombie replacement spawn was cancelled for {}", source.getType());
            return;
        }

        level.addFreshEntity(zombie);
    }
}
