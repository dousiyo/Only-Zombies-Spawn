package com.dousiyo.onlyzombiesspawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.Zombie;

public final class OnlyZombiesSpawnLogic {
	private OnlyZombiesSpawnLogic() {
	}

	public static Entity createReplacementIfNeeded(ServerLevel level, Entity entity) {
		EntitySpawnReason spawnReason = getSpawnReason(entity);
		if (!shouldReplace(entity, spawnReason)) {
			return entity;
		}

		Zombie zombie = EntityType.ZOMBIE.create(level, spawnReason);
		if (zombie == null) {
			return null;
		}

		zombie.copyPosition(entity);
		zombie.setYRot(entity.getYRot());
		zombie.setXRot(entity.getXRot());

		if (!zombie.checkSpawnRules(level, spawnReason) || !zombie.checkSpawnObstruction(level)) {
			return null;
		}

		zombie.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), spawnReason, null);
		return zombie;
	}

	private static EntitySpawnReason getSpawnReason(Entity entity) {
		if (entity instanceof SpawnReasonTracked tracked) {
			return tracked.onlyzombiesspawn$getSpawnReason();
		}

		return null;
	}

	private static boolean shouldReplace(Entity entity, EntitySpawnReason spawnReason) {
		if (!(entity instanceof Mob)) {
			return false;
		}

		if (spawnReason == null || !isControlledSpawnReason(spawnReason)) {
			return false;
		}

		if (entity.getType() == EntityType.ZOMBIE || isExcludedBoss(entity)) {
			return false;
		}

		return entity.getType().getCategory() == MobCategory.MONSTER || entity instanceof Enemy;
	}

	private static boolean isControlledSpawnReason(EntitySpawnReason spawnReason) {
		return switch (spawnReason) {
			case NATURAL, CHUNK_GENERATION, STRUCTURE, PATROL -> true;
			default -> EntitySpawnReason.isSpawner(spawnReason);
		};
	}

	private static boolean isExcludedBoss(Entity entity) {
		return entity instanceof EnderDragon || entity instanceof WitherBoss;
	}
}
