package com.dousiyo.onlyzombiesspawn;

import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@net.neoforged.fml.common.Mod(OnlyZombiesSpawn.MOD_ID)
public final class OnlyZombiesSpawn {
    public static final String MOD_ID = "onlyzombiesspawn";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OnlyZombiesSpawn() {
        NeoForge.EVENT_BUS.addListener(SpawnReplacementHandler::onFinalizeSpawn);
        NeoForge.EVENT_BUS.addListener(SpawnReplacementHandler::onEntityJoinLevel);
    }
}
