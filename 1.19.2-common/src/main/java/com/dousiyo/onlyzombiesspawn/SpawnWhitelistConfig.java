package com.dousiyo.onlyzombiesspawn;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ServerLevelAccessor;

public final class SpawnWhitelistConfig {
    private static final String CONFIG_COMMENT = """
            Only Zombies Spawn config
            allowedMobs: Comma-separated mob IDs that are allowed to spawn even when they are not zombies. Example: minecraft:skeleton,minecraft:creeper
            disabledDimensions: Comma-separated dimension IDs where zombie-only spawn replacement is disabled. Example: minecraft:the_nether
            zombieSpawnMultiplier: Number of zombies to spawn for each replaced hostile mob. Minimum: 1. Maximum: 40.
            """;
    private static final String FILE_NAME = "onlyzombiesspawn.properties";
    private static final String ALLOWED_MOBS = "allowedMobs";
    private static final String DISABLED_DIMENSIONS = "disabledDimensions";
    private static final String ZOMBIE_SPAWN_MULTIPLIER = "zombieSpawnMultiplier";
    private static final int MAX_ZOMBIE_SPAWN_MULTIPLIER = 40;
    private static final Set<ResourceLocation> allowedMobs = new HashSet<>();
    private static final Set<ResourceLocation> disabledDimensions = new HashSet<>();
    private static int zombieSpawnMultiplier = 1;

    private SpawnWhitelistConfig() {
    }

    public static void load(Path configDirectory) {
        Path configPath = configDirectory.resolve(FILE_NAME);
        createDefaultConfig(configPath);

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException exception) {
            Constants.LOGGER.warn("Failed to read {}", configPath, exception);
            return;
        }

        allowedMobs.clear();
        disabledDimensions.clear();
        allowedMobs.addAll(parseResourceLocations(properties.getProperty(ALLOWED_MOBS, "")));
        disabledDimensions.addAll(parseResourceLocations(properties.getProperty(DISABLED_DIMENSIONS, "")));
        zombieSpawnMultiplier = parseMultiplier(properties.getProperty(ZOMBIE_SPAWN_MULTIPLIER, "1"));
    }

    public static boolean isMobAllowed(EntityType<?> entityType) {
        return allowedMobs.contains(Registry.ENTITY_TYPE.getKey(entityType));
    }

    public static boolean isDimensionDisabled(ServerLevelAccessor level) {
        return disabledDimensions.contains(level.getLevel().dimension().location());
    }

    public static int zombieSpawnMultiplier() {
        return zombieSpawnMultiplier;
    }

    private static void createDefaultConfig(Path configPath) {
        if (Files.exists(configPath)) {
            return;
        }

        try {
            Files.createDirectories(configPath.getParent());
            Properties defaults = new Properties();
            defaults.setProperty(ALLOWED_MOBS, "");
            defaults.setProperty(DISABLED_DIMENSIONS, "");
            defaults.setProperty(ZOMBIE_SPAWN_MULTIPLIER, "1");
            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                defaults.store(writer, CONFIG_COMMENT);
            }
        } catch (IOException exception) {
            Constants.LOGGER.warn("Failed to create {}", configPath, exception);
        }
    }

    private static Set<ResourceLocation> parseResourceLocations(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .map(ResourceLocation::tryParse)
                .filter(location -> location != null)
                .collect(Collectors.toSet());
    }

    private static int parseMultiplier(String value) {
        try {
            long parsed = Long.parseLong(value.trim());
            if (parsed < 1L) {
                return 1;
            }
            return parsed > MAX_ZOMBIE_SPAWN_MULTIPLIER ? MAX_ZOMBIE_SPAWN_MULTIPLIER : (int) parsed;
        } catch (NumberFormatException exception) {
            return 1;
        }
    }
}
