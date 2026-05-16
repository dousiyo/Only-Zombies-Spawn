package com.dousiyo.onlyzombiesspawn;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ServerLevelAccessor;

public final class SpawnRestrictionRules {
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

    public static boolean shouldReplace(Mob mob, EntitySpawnReason spawnType, ServerLevelAccessor level) {
        return !SpawnWhitelistConfig.isDimensionDisabled(level.getLevel())
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
