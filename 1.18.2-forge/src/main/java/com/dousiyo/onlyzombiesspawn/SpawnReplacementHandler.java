package com.dousiyo.onlyzombiesspawn;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

final class SpawnReplacementHandler {
    @SubscribeEvent
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (!(event.getWorld() instanceof ServerLevelAccessor level)) {
            return;
        }

        Mob originalMob = (Mob) event.getEntityLiving();
        if (!SpawnRestrictionRules.shouldReplace(originalMob, event.getSpawnReason(), level)) {
            return;
        }

        event.setResult(Event.Result.DENY);
        for (int i = 0; i < SpawnWhitelistConfig.zombieSpawnMultiplier(); i++) {
            spawnZombie(level, originalMob, event);
        }
    }

    private static void spawnZombie(ServerLevelAccessor level, Mob originalMob, LivingSpawnEvent.CheckSpawn event) {
        Zombie zombie = EntityType.ZOMBIE.create(level.getLevel());
        if (zombie == null) {
            Constants.LOGGER.warn("Failed to create replacement zombie for {}", originalMob.getType());
            return;
        }

        zombie.moveTo(event.getX(), event.getY(), event.getZ(), originalMob.getYRot(), originalMob.getXRot());
        zombie.setBaby(false);
        zombie.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), event.getSpawnReason(), null, null);
        zombie.setBaby(false);
        level.addFreshEntityWithPassengers(zombie);
    }
}
