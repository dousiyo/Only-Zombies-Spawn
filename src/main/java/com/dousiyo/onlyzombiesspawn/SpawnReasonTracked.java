package com.dousiyo.onlyzombiesspawn;

import net.minecraft.world.entity.EntitySpawnReason;

public interface SpawnReasonTracked {
	EntitySpawnReason onlyzombiesspawn$getSpawnReason();

	void onlyzombiesspawn$setSpawnReason(EntitySpawnReason spawnReason);
}
