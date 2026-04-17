package com.dousiyo.onlyzombiesspawn;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.EnumSet;
import java.util.Set;

final class SpawnReplacementHandler {
    private static final Set<MobSpawnType> REPLACEABLE_SPAWN_TYPES = EnumSet.of(
            MobSpawnType.NATURAL,
            MobSpawnType.CHUNK_GENERATION,
            MobSpawnType.SPAWNER,
            MobSpawnType.STRUCTURE,
            MobSpawnType.PATROL,
            MobSpawnType.REINFORCEMENT
    );

    @SubscribeEvent
    public void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        Mob originalMob = event.getEntity();
        if (!shouldReplace(originalMob, event.getSpawnType())) {
            return;
        }

        Zombie zombie = EntityType.ZOMBIE.create(event.getLevel().getLevel());
        if (zombie == null) {
            event.setSpawnCancelled(true);
            OnlyZombiesSpawnMod.LOGGER.warn("Failed to create replacement zombie for {}", originalMob.getType());
            return;
        }

        zombie.moveTo(event.getX(), event.getY(), event.getZ(), originalMob.getYRot(), originalMob.getXRot());
        zombie.setBaby(false);
        zombie.finalizeSpawn(event.getLevel(), event.getDifficulty(), event.getSpawnType(), null, null);
        zombie.setBaby(false);

        event.setSpawnCancelled(true);
        event.getLevel().addFreshEntityWithPassengers(zombie);
    }

    private boolean shouldReplace(Mob mob, MobSpawnType spawnType) {
        if (!REPLACEABLE_SPAWN_TYPES.contains(spawnType)) {
            return false;
        }

        if (mob.getType() == EntityType.ZOMBIE) {
            return false;
        }

        return mob.getType().getCategory() == MobCategory.MONSTER;
    }
}
