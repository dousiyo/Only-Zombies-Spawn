package com.dousiyo.onlyzombiesspawn;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;

public final class SpawnRestrictionRules {
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

    public static boolean shouldReplace(Mob mob, MobSpawnType spawnType, ServerLevelAccessor level) {
        return !SpawnWhitelistConfig.isDimensionDisabled(level)
                && spawnType != null
                && CONTROLLED_SPAWN_TYPES.contains(spawnType)
                && isRestrictedHostile(mob);
    }

    private static boolean isRestrictedHostile(Mob mob) {
        EntityType<?> entityType = mob.getType();
        return entityType != EntityType.ZOMBIE
                && entityType != EntityType.ENDER_DRAGON
                && entityType != EntityType.WITHER
                && !SpawnWhitelistConfig.isMobAllowed(entityType)
                && entityType.getCategory() == MobCategory.MONSTER;
    }
}
