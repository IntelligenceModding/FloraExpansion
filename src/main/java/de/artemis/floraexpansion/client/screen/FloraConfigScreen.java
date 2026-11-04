package de.artemis.floraexpansion.client.screen;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import de.artemis.floraexpansion.common.config.ClientConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Client settings are kept as a draft until the player presses Save. */
public final class FloraConfigScreen extends Screen {
    private final Screen parent;
    private final List<OptionInstance<?>> options = new ArrayList<>();
    private final List<Runnable> saves = new ArrayList<>();
    private final List<Runnable> resets = new ArrayList<>();

    public FloraConfigScreen(Screen parent) {
        super(Component.translatable("floraexpansion.configuration.section.floraexpansion.client.toml.title"));
        this.parent = parent;
        decimal("particles.needles", ClientConfig.needleParticles, 0, 100);
        decimal("particles.flax", ClientConfig.flaxParticles, 0, 100);
        decimal("particles.fruit", ClientConfig.fruitParticles, 0, 100);
        add(ClientConfig.impactParticles, new OptionInstance<>(key("particles.impactCount"),
                OptionInstance.cachedConstantTooltip(Component.literal("Number of fragments when a throwable hits something.")),
                (label, value) -> caption(label, value.toString()), new OptionInstance.IntRange(0, 8),
                ClientConfig.impactParticles.get(), ignored -> {}));
        toggle("rendering.crateFill", ClientConfig.crateFill);
        decimal("rendering.crateFillDistance", ClientConfig.crateFillDistance, 800, 12800);
        decimal("interface.scrollSensitivity", ClientConfig.scrollSensitivity, 10, 1000);
        toggle("interface.interactionHints", ClientConfig.interactionHints);
        toggle("interface.quickExtraction", ClientConfig.quickExtraction);
        var detailValues = new OptionInstance.Enum<>(List.of(ClientConfig.TooltipDetail.values()),
                Codec.STRING.xmap(ClientConfig.TooltipDetail::valueOf, Enum::name));
        add(ClientConfig.tooltipDetail, new OptionInstance<>(key("interface.tooltipDetail"),
                OptionInstance.noTooltip(), (label, value) -> caption(label,
                        Component.translatable("floraexpansion.configuration.tooltipDetail." + value.name())),
                detailValues, ClientConfig.tooltipDetail.get(), ignored -> {}));
    }

    private static String key(String path) {
        return "floraexpansion.configuration.client." + path;
    }

    private static Component caption(Component label, Object value) {
        return Component.translatable("options.generic_value", label, value);
    }

    private <T> void add(ForgeConfigSpec.ConfigValue<T> config, OptionInstance<T> option) {
        options.add(option);
        saves.add(() -> config.set(option.get()));
        resets.add(() -> option.set(config.getDefault()));
    }

    private void toggle(String path, ForgeConfigSpec.BooleanValue config) {
        add(config, OptionInstance.createBoolean(key(path), config.get(), ignored -> {}));
    }

    private void decimal(String path, ForgeConfigSpec.DoubleValue config, int min, int max) {
        add(config, new OptionInstance<>(key(path), OptionInstance.noTooltip(),
                (label, value) -> caption(label, String.format(Locale.ROOT, "%.2f", value)),
                new OptionInstance.IntRange(min, max).xmap(value -> value / 100.0,
                        value -> (int) Math.round(value * 100)), config.get(), ignored -> {}));
    }

    @Override
    protected void init() {
        var list = new OptionsList(net.minecraft.client.Minecraft.getInstance(), width, height, 32, height - 64, 25);
        options.forEach(list::addBig);
        addRenderableWidget(list);
        int buttonWidth = Math.min(100, (width - 32) / 3);
        int left = (width - buttonWidth * 3 - 8) / 2;
        addRenderableWidget(Button.builder(Component.translatable("floraexpansion.configuration.reset"), button -> {
            double scroll = list.getScrollAmount();
            resets.forEach(Runnable::run);
            rebuildWidgets();
            // Keep the currently visible settings when resetting a scrolled list.
            ((OptionsList) children().get(0)).setScrollAmount(scroll);
        }).bounds(left, height - 28, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(left + buttonWidth + 4, height - 28, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("floraexpansion.configuration.save"), button -> {
            saves.forEach(Runnable::run);
            ClientConfig.SPEC.save();
            onClose();
        }).bounds(left + (buttonWidth + 4) * 2, height - 28, buttonWidth, 20).build());
    }

    @Override
    public void onClose() {
        net.minecraft.client.Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.translatable("floraexpansion.configuration.serverFiles"),
                width / 2, height - 52, 0xA0A0A0);
    }
}
