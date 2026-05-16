package com.dousiyo.onlyzombiesspawn.client;

import com.dousiyo.onlyzombiesspawn.SpawnWhitelistConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;

public final class WhitelistConfigScreen extends Screen {
    private static final Component TITLE = Component.literal("Only Zombies Spawn");
    private static final Component SEARCH_HINT = Component.literal("Search mobs or dimensions");
    private static final int TEXT_INPUT_MAX_WIDTH = 190;
    private static final int TEXT_INPUT_MIN_WIDTH = 120;
    private static final int TEXT_INPUT_GAP = 12;
    private static final List<String> DEFAULT_DIMENSIONS = List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");

    private final Screen parent;
    private final Set<String> allowedMobs = new HashSet<>();
    private final Set<String> disabledDimensions = new HashSet<>();
    private EditBox searchBox;
    private EntryList entryList;
    private String extraDimensions = "";
    private String zombieSpawnMultiplier = "1";

    public WhitelistConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.allowedMobs.addAll(SpawnWhitelistConfig.allowedMobIds());
        this.disabledDimensions.addAll(SpawnWhitelistConfig.disabledDimensionIds());
        this.extraDimensions = String.join(",", this.disabledDimensions.stream().filter(id -> !DEFAULT_DIMENSIONS.contains(id)).sorted().toList());
        this.zombieSpawnMultiplier = Integer.toString(SpawnWhitelistConfig.zombieSpawnMultiplier());
    }

    @Override
    protected void init() {
        this.searchBox = new EditBox(this.font, this.width / 2 - 120, 22, 240, 20, SEARCH_HINT);
        this.searchBox.setHint(SEARCH_HINT);
        this.searchBox.setResponder(value -> {
            if (this.entryList != null) {
                this.entryList.populate(value);
                this.entryList.setScrollAmount(0.0);
            }
        });
        this.entryList = new EntryList();
        this.addRenderableWidget(this.searchBox);
        this.addRenderableWidget(this.entryList);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.saveAndClose()).bounds(this.width / 2 - 102, this.height - 28, 100, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose()).bounds(this.width / 2 + 2, this.height - 28, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, TITLE, this.width / 2, 8, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void saveAndClose() {
        Set<String> dimensions = new HashSet<>(this.disabledDimensions);
        for (String id : this.extraDimensions.split(",")) {
            String trimmed = id.trim();
            if (!trimmed.isEmpty() && ResourceLocation.tryParse(trimmed) != null) {
                dimensions.add(trimmed);
            }
        }
        SpawnWhitelistConfig.save(FabricLoader.getInstance().getConfigDir(), this.allowedMobs, dimensions, parseMultiplier(this.zombieSpawnMultiplier));
        this.onClose();
    }

    private static int parseMultiplier(String value) {
        try {
            long parsed = Long.parseLong(value.trim());
            if (parsed < 1L) {
                return 1;
            }
            return parsed > 40L ? 40 : (int) parsed;
        } catch (NumberFormatException exception) {
            return 1;
        }
    }

    private final class EntryList extends ContainerObjectSelectionList<ConfigEntry> {
        EntryList() {
            super(Minecraft.getInstance(), WhitelistConfigScreen.this.width, WhitelistConfigScreen.this.height, 48, WhitelistConfigScreen.this.height - 34, 24);
            this.populate("");
        }

        void populate(String filter) {
            this.clearEntries();
            String lowerFilter = filter.toLowerCase(Locale.ROOT);
            this.addEntry(new CategoryEntry("General"));
            this.addEntry(new TextEntry("Zombie multiplier", WhitelistConfigScreen.this.zombieSpawnMultiplier, Component.literal("1"), value -> WhitelistConfigScreen.this.zombieSpawnMultiplier = value));
            this.addEntry(new CategoryEntry("Allowed mobs"));
            BuiltInRegistries.ENTITY_TYPE.stream()
                    .filter(type -> type.getCategory() == MobCategory.MONSTER)
                    .map(BuiltInRegistries.ENTITY_TYPE::getKey)
                    .map(ResourceLocation::toString)
                    .sorted()
                    .filter(id -> id.contains(lowerFilter))
                    .forEach(id -> this.addEntry(new ToggleEntry(id, WhitelistConfigScreen.this.allowedMobs)));
            this.addEntry(new CategoryEntry("Disabled dimensions"));
            DEFAULT_DIMENSIONS.stream().filter(id -> id.contains(lowerFilter)).forEach(id -> this.addEntry(new ToggleEntry(id, WhitelistConfigScreen.this.disabledDimensions)));
            this.addEntry(new TextEntry("Other", WhitelistConfigScreen.this.extraDimensions, Component.literal("modid:dimension,modid:other"), value -> WhitelistConfigScreen.this.extraDimensions = value));
        }
    }

    private abstract static class ConfigEntry extends ContainerObjectSelectionList.Entry<ConfigEntry> {
    }

    private final class CategoryEntry extends ConfigEntry {
        private final Component label;

        CategoryEntry(String label) {
            this.label = Component.literal(label).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            graphics.drawCenteredString(WhitelistConfigScreen.this.font, this.label, left + width / 2, top + 6, -1);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    private final class ToggleEntry extends ConfigEntry {
        private final Component label;
        private final List<AbstractWidget> children = new ArrayList<>();
        private final CycleButton<Boolean> toggle;

        ToggleEntry(String id, Set<String> values) {
            this.label = Component.literal(id);
            this.toggle = CycleButton.onOffBuilder(values.contains(id)).displayOnlyValue().create(0, 0, 44, 20, this.label, (button, enabled) -> {
                if (enabled) values.add(id); else values.remove(id);
            });
            this.children.add(this.toggle);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            graphics.drawString(WhitelistConfigScreen.this.font, this.label, left + 4, top + 6, -1);
            this.toggle.setX(left + width - 48);
            this.toggle.setY(top + 2);
            this.toggle.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }

    private final class TextEntry extends ConfigEntry {
        private final Component label;
        private final List<AbstractWidget> children = new ArrayList<>();
        private final EditBox input;

        TextEntry(String label, String value, Component hint, java.util.function.Consumer<String> responder) {
            this.label = Component.literal(label);
            this.input = new EditBox(WhitelistConfigScreen.this.font, 0, 0, TEXT_INPUT_MAX_WIDTH, 20, this.label);
            this.input.setValue(value);
            this.input.setResponder(responder);
            this.input.setHint(hint);
            this.children.add(this.input);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            int labelX = left + 4;
            int right = left + width - 4;
            int labelWidth = WhitelistConfigScreen.this.font.width(this.label);
            int inputWidth = Math.min(TEXT_INPUT_MAX_WIDTH, Math.max(TEXT_INPUT_MIN_WIDTH, right - labelX - labelWidth - TEXT_INPUT_GAP));
            graphics.drawString(WhitelistConfigScreen.this.font, this.label, labelX, top + 6, -1);
            this.input.setWidth(inputWidth);
            this.input.setX(right - inputWidth);
            this.input.setY(top + 2);
            this.input.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}
