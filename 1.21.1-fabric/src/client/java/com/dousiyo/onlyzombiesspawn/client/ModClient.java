package com.dousiyo.onlyzombiesspawn.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;

public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("onlyzombiesspawnconfig").executes(context -> openConfigScreen()));
            dispatcher.register(ClientCommandManager.literal("ozsconfig").executes(context -> openConfigScreen()));
        });
    }

    private static int openConfigScreen() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new WhitelistConfigScreen(client.screen)));
        return 1;
    }
}
