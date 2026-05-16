package com.dousiyo.onlyzombiesspawn;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;

@net.neoforged.fml.common.Mod(Constants.MOD_ID)
public final class OnlyZombiesSpawn {
    public OnlyZombiesSpawn() {
        SpawnWhitelistConfig.load(FMLPaths.CONFIGDIR.get());
        NeoForge.EVENT_BUS.addListener(SpawnReplacementHandler::onFinalizeSpawn);
        NeoForge.EVENT_BUS.addListener(SpawnReplacementHandler::onEntityJoinLevel);
    }
}
