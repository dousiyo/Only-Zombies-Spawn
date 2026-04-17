package com.dousiyo.onlyzombiesspawn;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;

final class SpawnRestrictionRules {
    private static final Set<EntitySpawnReason> CONTROLLED_SPAWN_TYPES = EnumSet.of(
            EntitySpawnReason.NATURAL,
            EntitySpawnReason.CHUNK_GENERATION,
            EntitySpawnReason.STRUCTURE,
            EntitySpawnReason.SPAWNER,
            EntitySpawnReason.TRIAL_SPAWNER,
            EntitySpawnReason.PATROL,
            EntitySpawnReason.JOCKEY,
            EntitySpawnReason.REINFORCEMENT
    );

    private SpawnRestrictionRules() {
    }

    static boolean shouldReplace(Mob mob, EntitySpawnReason spawnType) {
        return shouldControlSpawnType(spawnType) && isRestrictedHostile(mob);
    }

    static boolean shouldControlSpawnType(EntitySpawnReason spawnType) {
        return CONTROLLED_SPAWN_TYPES.contains(spawnType);
    }

    static boolean isRestrictedHostile(Mob mob) {
        EntityType<?> entityType = mob.getType();
        return entityType != EntityType.ZOMBIE
                && entityType.getCategory() == MobCategory.MONSTER
                && entityType != EntityType.ENDER_DRAGON
                && entityType != EntityType.WITHER;
    }
}
