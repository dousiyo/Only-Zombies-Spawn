package com.dousiyo.onlyzombiesspawn;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

public final class ServerConfigCommandRegistrar {
    private ServerConfigCommandRegistrar() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ozsconfig_apply")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("allowedMobs", StringArgumentType.word())
                        .then(Commands.argument("disabledDimensions", StringArgumentType.word())
                                .then(Commands.argument("zombieSpawnMultiplier", IntegerArgumentType.integer(1, 40))
                                        .executes(context -> {
                                            SpawnWhitelistConfig.save(
                                                    FMLPaths.CONFIGDIR.get(),
                                                    parseList(StringArgumentType.getString(context, "allowedMobs")),
                                                    parseList(StringArgumentType.getString(context, "disabledDimensions")),
                                                    IntegerArgumentType.getInteger(context, "zombieSpawnMultiplier")
                                            );
                                            return 1;
                                        })))));
    }

    private static Set<String> parseList(String value) {
        if ("-".equals(value)) {
            return Set.of();
        }
        return Stream.of(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .collect(Collectors.toSet());
    }
}
