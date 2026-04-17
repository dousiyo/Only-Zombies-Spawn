package com.dousiyo.onlyzombiesspawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

final class SpawnReplacementHandler {
    private SpawnReplacementHandler() {
    }

    static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        EntitySpawnReason spawnType = event.getSpawnType();
        if (!SpawnRestrictionRules.shouldReplace(mob, spawnType)) {
            return;
        }

        event.setCanceled(true);
        event.setSpawnCancelled(true);
        spawnZombieReplacement(event.getLevel(), mob, spawnType, event.getDifficulty());
    }

    static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Mob mob)) {
            return;
        }

        EntitySpawnReason spawnType = mob.getSpawnType();
        if (spawnType == null || !SpawnRestrictionRules.shouldReplace(mob, spawnType)) {
            return;
        }

        event.setCanceled(true);
        spawnZombieReplacement((ServerLevel) event.getLevel(), mob, spawnType, ((ServerLevel) event.getLevel()).getCurrentDifficultyAt(mob.blockPosition()));
    }

    private static void spawnZombieReplacement(ServerLevelAccessor level, Mob source, EntitySpawnReason spawnType, DifficultyInstance difficulty) {
        Zombie zombie = EntityType.ZOMBIE.create(level.getLevel(), spawnType);
        if (zombie == null) {
            OnlyZombiesSpawn.LOGGER.warn("Failed to create zombie replacement for {}", source.getType());
            return;
        }

        zombie.snapTo(source.getX(), source.getY(), source.getZ(), source.getYRot(), source.getXRot());
        if (source.isPersistenceRequired()) {
            zombie.setPersistenceRequired();
        }

        zombie.finalizeSpawn(level, difficulty, spawnType, null);
        if (zombie.isSpawnCancelled()) {
            zombie.discard();
            OnlyZombiesSpawn.LOGGER.warn("Zombie replacement spawn was cancelled for {}", source.getType());
            return;
        }

        level.addFreshEntity(zombie);
    }
}
