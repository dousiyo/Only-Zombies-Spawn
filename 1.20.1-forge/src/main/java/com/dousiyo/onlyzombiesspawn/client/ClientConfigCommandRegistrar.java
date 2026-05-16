package com.dousiyo.onlyzombiesspawn.client;

import com.dousiyo.onlyzombiesspawn.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientConfigCommandRegistrar {
    private ClientConfigCommandRegistrar() {
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ozsconfig").requires(source -> source.hasPermission(2)).executes(context -> openConfigScreen()));
    }

    private static int openConfigScreen() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new WhitelistConfigScreen(client.screen)));
        return 1;
    }
}
