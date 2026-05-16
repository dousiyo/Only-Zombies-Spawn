package com.dousiyo.onlyzombiesspawn.client;

import com.dousiyo.onlyzombiesspawn.SpawnWhitelistConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.Nullable;

public final class WhitelistConfigScreen extends net.minecraft.client.gui.screens.Screen {
    private static final Component TITLE = Component.literal("Only Zombies Spawn");
    private static final Component SEARCH_HINT = Component.literal("Search mobs or dimensions");
    private static final int TEXT_INPUT_MAX_WIDTH = 190;
    private static final int TEXT_INPUT_MIN_WIDTH = 120;
    private static final int TEXT_INPUT_GAP = 12;
    private static final List<String> DEFAULT_DIMENSIONS = List.of(
            "minecraft:overworld",
            "minecraft:the_nether",
            "minecraft:the_end"
    );

    private final net.minecraft.client.gui.screens.Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 42, 33);
    private final Set<String> allowedMobs = new HashSet<>();
    private final Set<String> disabledDimensions = new HashSet<>();
    private @Nullable EditBox searchBox;
    private @Nullable EntryList entryList;
    private String extraDimensions = "";
    private String zombieSpawnMultiplier = "1";

    public WhitelistConfigScreen(net.minecraft.client.gui.screens.Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.allowedMobs.addAll(SpawnWhitelistConfig.allowedMobIds());
        this.disabledDimensions.addAll(SpawnWhitelistConfig.disabledDimensionIds());
        this.extraDimensions = String.join(",", this.disabledDimensions.stream()
                .filter(id -> !DEFAULT_DIMENSIONS.contains(id))
                .sorted()
                .toList());
        this.zombieSpawnMultiplier = Integer.toString(SpawnWhitelistConfig.zombieSpawnMultiplier());
    }

    @Override
    protected void init() {
        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical().spacing(4));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(TITLE, this.font));
        this.searchBox = header.addChild(new EditBox(this.font, 220, 16, SEARCH_HINT));
        this.searchBox.setHint(SEARCH_HINT);
        this.searchBox.setResponder(value -> {
            if (this.entryList != null) {
                this.entryList.populate(value);
                this.entryList.setScrollAmount(0.0);
            }
        });

        this.entryList = this.layout.addToContents(new EntryList());

        LinearLayout footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> this.saveAndClose()).width(100).build());
        footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose()).width(100).build());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.entryList != null) {
            this.entryList.updateSize(this.width, this.layout);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void saveAndClose() {
        Set<String> dimensions = new HashSet<>(this.disabledDimensions);
        for (String id : this.extraDimensions.split(",")) {
            String trimmed = id.trim();
            if (!trimmed.isEmpty() && Identifier.tryParse(trimmed) != null) {
                dimensions.add(trimmed);
            }
        }
        int multiplier = parseMultiplier(this.zombieSpawnMultiplier);
        if (!this.sendServerConfig(dimensions, multiplier)) {
            SpawnWhitelistConfig.save(FMLPaths.CONFIGDIR.get(), this.allowedMobs, dimensions, multiplier);
        }
        this.onClose();
    }

    private boolean sendServerConfig(Set<String> dimensions, int multiplier) {
        if (this.minecraft.player == null || this.minecraft.player.connection == null) {
            return false;
        }
        this.minecraft.player.connection.sendCommand("ozsconfig_apply " + encodeList(this.allowedMobs) + " " + encodeList(dimensions) + " " + multiplier);
        return true;
    }

    private static String encodeList(Set<String> values) {
        String joined = values.stream().sorted().collect(Collectors.joining(","));
        return joined.isEmpty() ? "-" : joined;
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
            super(Minecraft.getInstance(), WhitelistConfigScreen.this.width, WhitelistConfigScreen.this.layout.getContentHeight(), WhitelistConfigScreen.this.layout.getHeaderHeight(), 24);
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
                    .map(Identifier::toString)
                    .sorted()
                    .filter(id -> id.contains(lowerFilter))
                    .forEach(id -> this.addEntry(new ToggleEntry(id, WhitelistConfigScreen.this.allowedMobs)));
            this.addEntry(new CategoryEntry("Disabled dimensions"));
            DEFAULT_DIMENSIONS.stream()
                    .filter(id -> id.contains(lowerFilter))
                    .forEach(id -> this.addEntry(new ToggleEntry(id, WhitelistConfigScreen.this.disabledDimensions)));
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
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            graphics.centeredText(WhitelistConfigScreen.this.font, this.label, this.getContentXMiddle(), this.getContentY() + 6, -1);
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
            this.toggle = CycleButton.onOffBuilder(values.contains(id))
                    .displayOnlyValue()
                    .create(0, 0, 44, 20, this.label, (button, enabled) -> {
                        if (enabled) {
                            values.add(id);
                        } else {
                            values.remove(id);
                        }
                    });
            this.children.add(this.toggle);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            graphics.text(WhitelistConfigScreen.this.font, this.label, this.getContentX(), this.getContentY() + 6, -1);
            this.toggle.setX(this.getContentRight() - 45);
            this.toggle.setY(this.getContentY() + 2);
            this.toggle.extractRenderState(graphics, mouseX, mouseY, partialTick);
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
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            int labelX = this.getContentX();
            int right = this.getContentRight();
            int labelWidth = WhitelistConfigScreen.this.font.width(this.label);
            int inputWidth = Math.min(TEXT_INPUT_MAX_WIDTH, Math.max(TEXT_INPUT_MIN_WIDTH, right - labelX - labelWidth - TEXT_INPUT_GAP));
            graphics.text(WhitelistConfigScreen.this.font, this.label, labelX, this.getContentY() + 6, -1);
            this.input.setWidth(inputWidth);
            this.input.setX(right - inputWidth);
            this.input.setY(this.getContentY() + 2);
            this.input.extractRenderState(graphics, mouseX, mouseY, partialTick);
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
