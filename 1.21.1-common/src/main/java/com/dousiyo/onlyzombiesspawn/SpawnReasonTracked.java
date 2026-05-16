package com.dousiyo.onlyzombiesspawn;

import net.minecraft.world.entity.MobSpawnType;

public interface SpawnReasonTracked {
    MobSpawnType onlyzombiesspawn$getSpawnType();

    void onlyzombiesspawn$setSpawnType(MobSpawnType spawnType);
}
