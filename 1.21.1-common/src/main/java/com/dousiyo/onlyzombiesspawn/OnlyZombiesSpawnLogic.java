package com.dousiyo.onlyzombiesspawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Zombie;

public final class OnlyZombiesSpawnLogic {
    private OnlyZombiesSpawnLogic() {
    }

    public static Entity createReplacementIfNeeded(ServerLevel level, Entity entity) {
        MobSpawnType spawnType = getSpawnType(entity);
        if (!(entity instanceof Mob mob) || !SpawnRestrictionRules.shouldReplace(mob, spawnType, level)) {
            return entity;
        }

        Zombie zombie = EntityType.ZOMBIE.create(level);
        if (zombie == null) {
            return null;
        }

        zombie.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
        zombie.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), spawnType, (SpawnGroupData) null);
        return zombie;
    }

    private static MobSpawnType getSpawnType(Entity entity) {
        if (entity instanceof SpawnReasonTracked tracked) {
            return tracked.onlyzombiesspawn$getSpawnType();
        }

        return null;
    }
}
