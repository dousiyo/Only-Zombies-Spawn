package com.dousiyo.onlyzombiesspawn;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;

@net.neoforged.fml.common.Mod(Constants.MOD_ID)
public final class OnlyZombiesSpawn {
    public OnlyZombiesSpawn() {
        SpawnWhitelistConfig.load(FMLPaths.CONFIGDIR.get());
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            com.dousiyo.onlyzombiesspawn.client.ClientConfigScreenRegistrar.register();
            com.dousiyo.onlyzombiesspawn.client.ClientConfigCommandRegistrar.register();
        }
        NeoForge.EVENT_BUS.addListener(SpawnReplacementHandler::onFinalizeSpawn);
        NeoForge.EVENT_BUS.addListener(SpawnReplacementHandler::onEntityJoinLevel);
    }
}
