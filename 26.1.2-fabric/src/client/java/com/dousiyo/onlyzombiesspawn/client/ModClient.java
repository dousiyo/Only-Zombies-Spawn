package com.dousiyo.onlyzombiesspawn.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.server.permissions.Permissions;

public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("onlyzombiesspawnconfig").requires(source -> hasConfigPermission()).executes(context -> openConfigScreen()));
            dispatcher.register(ClientCommands.literal("ozsconfig").requires(source -> hasConfigPermission()).executes(context -> openConfigScreen()));
        });
    }

    private static boolean hasConfigPermission() {
        Minecraft client = Minecraft.getInstance();
        return client.player != null && client.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    private static int openConfigScreen() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new WhitelistConfigScreen(client.screen)));
        return 1;
    }
}
