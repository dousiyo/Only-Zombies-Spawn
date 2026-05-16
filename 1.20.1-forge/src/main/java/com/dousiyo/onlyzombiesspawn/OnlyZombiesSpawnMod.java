package com.dousiyo.onlyzombiesspawn;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Constants.MOD_ID)
public final class OnlyZombiesSpawnMod {
    public OnlyZombiesSpawnMod() {
        SpawnWhitelistConfig.load(FMLPaths.CONFIGDIR.get());
        MinecraftForge.EVENT_BUS.register(new SpawnReplacementHandler());
        MinecraftForge.EVENT_BUS.register(ServerConfigCommandRegistrar.class);
        Constants.LOGGER.info("Only Zombies Spawn is active.");
    }
}
