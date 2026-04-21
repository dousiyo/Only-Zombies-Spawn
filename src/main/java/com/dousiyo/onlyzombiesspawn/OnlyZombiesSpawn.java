package com.dousiyo.onlyzombiesspawn;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OnlyZombiesSpawn implements ModInitializer {
	public static final String MOD_ID = "onlyzombiesspawn";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Only Zombies Spawn initialized");
	}
}
