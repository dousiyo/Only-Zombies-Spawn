package com.dousiyo.onlyzombiesspawn;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

final class SpawnReplacementHandler {
    @SubscribeEvent
    public void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        Mob originalMob = event.getEntity();
        if (!SpawnRestrictionRules.shouldReplace(originalMob, event.getSpawnType(), event.getLevel())) {
            return;
        }

        event.setSpawnCancelled(true);
        for (int i = 0; i < SpawnWhitelistConfig.zombieSpawnMultiplier(); i++) {
            spawnZombie(event);
        }
    }

    private static void spawnZombie(MobSpawnEvent.FinalizeSpawn event) {
        Mob originalMob = event.getEntity();
        Zombie zombie = EntityType.ZOMBIE.create(event.getLevel().getLevel());
        if (zombie == null) {
            Constants.LOGGER.warn("Failed to create replacement zombie for {}", originalMob.getType());
            return;
        }

        zombie.moveTo(event.getX(), event.getY(), event.getZ(), originalMob.getYRot(), originalMob.getXRot());
        zombie.setBaby(false);
        zombie.finalizeSpawn(event.getLevel(), event.getDifficulty(), event.getSpawnType(), null, null);
        zombie.setBaby(false);
        event.getLevel().addFreshEntityWithPassengers(zombie);
    }
}
