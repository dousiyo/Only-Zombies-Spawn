package com.dousiyo.onlyzombiesspawn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class OnlyZombiesSpawn implements ModInitializer {
    @Override
    public void onInitialize() {
        SpawnWhitelistConfig.load(FabricLoader.getInstance().getConfigDir());
        Constants.LOGGER.info("Only Zombies Spawn initialized");
    }
}
