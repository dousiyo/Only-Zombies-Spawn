package com.dousiyo.onlyzombiesspawn;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(OnlyZombiesSpawnMod.MODID)
public final class OnlyZombiesSpawnMod {
    public static final String MODID = "onlyzombiesspawn";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OnlyZombiesSpawnMod() {
        MinecraftForge.EVENT_BUS.register(new SpawnReplacementHandler());
        LOGGER.info("Only Zombies Spawn is active.");
    }
}
