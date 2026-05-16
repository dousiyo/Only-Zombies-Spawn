package com.dousiyo.onlyzombiesspawn.client;

import com.dousiyo.onlyzombiesspawn.Constants;
import java.util.function.Supplier;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class ClientConfigScreenRegistrar {
    private ClientConfigScreenRegistrar() {
    }

    public static void register() {
        ModList.get().getModContainerById(Constants.MOD_ID).ifPresent(container ->
                container.registerExtensionPoint(
                        IConfigScreenFactory.class,
                        (Supplier<IConfigScreenFactory>) () -> (modContainer, parent) -> new WhitelistConfigScreen(parent)
                ));
    }
}
