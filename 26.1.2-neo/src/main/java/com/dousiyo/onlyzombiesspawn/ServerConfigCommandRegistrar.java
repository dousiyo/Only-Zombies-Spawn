package com.dousiyo.onlyzombiesspawn;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ServerConfigCommandRegistrar {
    private ServerConfigCommandRegistrar() {
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ozsconfig_apply")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
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
