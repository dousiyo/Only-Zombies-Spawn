package com.dousiyo.onlyzombiesspawn;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;

final class SpawnRestrictionRules {
    private static final Set<MobSpawnType> CONTROLLED_SPAWN_TYPES = EnumSet.of(
            MobSpawnType.NATURAL,
            MobSpawnType.CHUNK_GENERATION,
            MobSpawnType.STRUCTURE,
            MobSpawnType.SPAWNER,
            MobSpawnType.TRIAL_SPAWNER,
            MobSpawnType.PATROL,
            MobSpawnType.JOCKEY,
            MobSpawnType.REINFORCEMENT
    );

    private SpawnRestrictionRules() {
    }

    static boolean shouldReplace(Mob mob, MobSpawnType spawnType) {
        return shouldControlSpawnType(spawnType) && isRestrictedHostile(mob);
    }

    static boolean shouldControlSpawnType(MobSpawnType spawnType) {
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
