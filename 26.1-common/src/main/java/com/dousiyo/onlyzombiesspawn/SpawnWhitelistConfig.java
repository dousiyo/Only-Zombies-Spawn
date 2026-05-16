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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ServerLevelAccessor;

public final class SpawnWhitelistConfig {
    private static final String FILE_NAME = "onlyzombiesspawn.properties";
    private static final String ALLOWED_MOBS = "allowedMobs";
    private static final String DISABLED_DIMENSIONS = "disabledDimensions";
    private static final Set<Identifier> allowedMobs = new HashSet<>();
    private static final Set<Identifier> disabledDimensions = new HashSet<>();

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
    }

    public static boolean isMobAllowed(EntityType<?> entityType) {
        return allowedMobs.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    public static boolean isDimensionDisabled(ServerLevelAccessor level) {
        return disabledDimensions.contains(level.getLevel().dimension().identifier());
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
            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                defaults.store(writer, "Only Zombies Spawn whitelist config");
            }
        } catch (IOException exception) {
            Constants.LOGGER.warn("Failed to create {}", configPath, exception);
        }
    }

    private static Set<Identifier> parseResourceLocations(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .map(Identifier::tryParse)
                .filter(identifier -> identifier != null)
                .collect(Collectors.toSet());
    }
}
