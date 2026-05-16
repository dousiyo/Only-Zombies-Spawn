package com.dousiyo.onlyzombiesspawn.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;

public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("onlyzombiesspawnconfig").requires(source -> hasConfigPermission()).executes(context -> openConfigScreen()));
            dispatcher.register(ClientCommandManager.literal("ozsconfig").requires(source -> hasConfigPermission()).executes(context -> openConfigScreen()));
        });
    }

    private static boolean hasConfigPermission() {
        Minecraft client = Minecraft.getInstance();
        return client.player != null && client.player.hasPermissions(2);
    }

    private static int openConfigScreen() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new WhitelistConfigScreen(client.screen)));
        return 1;
    }
}
