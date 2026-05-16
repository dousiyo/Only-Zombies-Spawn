package com.dousiyo.onlyzombiesspawn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientConfigCommandRegistrar {
    private ClientConfigCommandRegistrar() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientConfigCommandRegistrar::registerCommands);
    }

    private static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("onlyzombiesspawnconfig").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)).executes(context -> openConfigScreen()));
        event.getDispatcher().register(Commands.literal("ozsconfig").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)).executes(context -> openConfigScreen()));
    }

    private static int openConfigScreen() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new WhitelistConfigScreen(client.screen)));
        return 1;
    }
}
