package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import java.util.ArrayList;
import java.util.List;

/** Exercises the actual 1.21.1 model bakery and texture atlas, including generated sprites. */
@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class CompatibilityClientAudit {
    private static int ticks;
    private static boolean checked;
    private static int shutdownTicks;
    private static final List<ItemStack> ITEMS = new ArrayList<>();
    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
    private static void model(BakedModel model, String name, net.minecraft.world.level.block.state.BlockState state) {
        var mc = Minecraft.getInstance();
        check(model != mc.getModelManager().getMissingModel(), "Missing model: " + name);
        for (int face = 0; face < 7; face++) {
            var direction = face == 6 ? null : Direction.values()[face];
            for (var quad : model.getQuads(state, direction, RandomSource.create(0)))
                check(!quad.getSprite().contents().name().getPath().equals("missingno"), "Missing quad texture: " + name);
        }
    }
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        if (!Boolean.getBoolean("floraexpansion.compatibilityAudit")) return;
        var mc = Minecraft.getInstance();
        if (CompatibilityWorldAudit.finished) {
            check(CompatibilityWorldAudit.failure == null, "World audit failed: " + CompatibilityWorldAudit.failure);
            if (mc.level != null) {
                shutdownTicks++;
                if (shutdownTicks == 1) {
                    CompatibilityAnimationAudit.run(mc);
                    CompatibilityMachineVisualAudit.start(mc);
                }
                if (shutdownTicks <= 120) {
                    CompatibilityMachineVisualAudit.tick(mc, shutdownTicks);
                    return;
                }
                int previewTicks = shutdownTicks - 120;
                if (previewTicks == 1) {
                    var advancements = mc.player.connection.getAdvancements();
                    var root = advancements.get(ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "nature/root"));
                    var background = root.value().display().orElseThrow().getBackground().orElseThrow();
                    check(mc.getResourceManager().getResource(background).isPresent(), "Missing advancement background: " + background);
                    mc.setScreen(new net.minecraft.client.gui.screens.advancements.AdvancementsScreen(advancements));
                    advancements.setSelectedTab(root, true);
                }
                if (previewTicks == 4) net.minecraft.client.Screenshot.grab(mc.gameDirectory, "advancement-background.png", mc.getMainRenderTarget(),
                        message -> FloraExpansion.LOGGER.info("Advancement screenshot: {}", message.getString()));
                if (previewTicks == 5) mc.setScreen(new SaltingPreview(3.5F));
                if (previewTicks == 35) mc.setScreen(new SaltingPreview(6.7F));
                if (previewTicks == 65) mc.setScreen(new SaltingPreview(9.5F));
                if (previewTicks == 20 || previewTicks == 50 || previewTicks == 80) {
                    String name = "salting-phase-" + previewTicks + ".png";
                    net.minecraft.client.Screenshot.grab(mc.gameDirectory, name, mc.getMainRenderTarget(),
                            message -> FloraExpansion.LOGGER.info("Salting audit screenshot: {}", message.getString()));
                }
                if (previewTicks > 110) mc.stop();
            }
            return;
        }
        if (mc.getOverlay() != null) return;
        if (!checked && !(mc.screen instanceof TitleScreen)) { mc.setScreen(new TitleScreen()); return; }
        if (!checked) {
            int states = 0;
            for (var entry : ModBlocks.BLOCKS.getEntries()) {
                for (var state : entry.get().getStateDefinition().getPossibleStates()) {
                    if (state.getRenderShape() == net.minecraft.world.level.block.RenderShape.MODEL)
                        model(mc.getBlockRenderer().getBlockModel(state), state.toString(), state);
                    states++;
                }
            }
            for (var entry : ModItems.ITEMS.getEntries()) {
                var stack = new ItemStack(entry.get());
                model(mc.getItemRenderer().getModel(stack, null, null, 0), entry.getId().toString(), null);
                ITEMS.add(stack);
            }
            for (var entry : ModBlocks.BLOCKS.getEntries()) {
                if (entry.get().asItem() == net.minecraft.world.item.Items.AIR) continue;
                var stack = new ItemStack(entry.get());
                model(mc.getItemRenderer().getModel(stack, null, null, 0), entry.getId().toString(), null);
                if (ITEMS.stream().noneMatch(s -> s.is(stack.getItem()))) ITEMS.add(stack);
            }
            for (var jam : List.of(ModItems.BLUEBERRY_JAM, ModItems.STRAWBERRY_JAM, ModItems.CHERRY_JAM, ModItems.APPLE_JAM)) {
                var full = mc.getItemRenderer().getModel(new ItemStack(jam.get()), null, null, 0);
                for (int portions = 1; portions < 4; portions++) {
                    var stack = new ItemStack(jam.get());
                    stack.set(net.minecraft.core.component.DataComponents.BLOCK_STATE,
                            net.minecraft.world.item.component.BlockItemStateProperties.EMPTY.with(
                                    de.artemis.floraexpansion.common.block.JamJarBlock.PORTIONS, portions));
                    var partial = mc.getItemRenderer().getModel(stack, null, null, 0);
                    model(partial, jam.getId() + " portion " + portions, null);
                    check(partial != full, "Partial jam model did not change: " + jam.getId());
                    ITEMS.add(stack);
                }
            }
            var leaf = new ItemStack(ModBlocks.APPLE_LEAVES.get());
            check(mc.getItemColors().getColor(leaf, 0) == net.minecraft.world.level.FoliageColor.getDefaultColor(), "Leaf inventory tint missing");
            for (var layer : ModArmorMaterials.SPIKED_LEATHER_ARMOR_MATERIAL.value().layers()) {
                for (boolean inner : new boolean[]{false, true})
                    check(mc.getResourceManager().getResource(layer.texture(inner)).isPresent(), "Missing worn armor texture: " + layer.texture(inner));
            }
            var packed = new ItemStack(ModBlocks.CRATE.get());
            de.artemis.floraexpansion.common.block.CrateBlock.setPackedItem(packed, true);
            var packedModel = mc.getItemRenderer().getModel(packed, null, null, 0);
            model(packedModel, "packed crate", null);
            check(packedModel != mc.getItemRenderer().getModel(new ItemStack(ModBlocks.CRATE.get()), null, null, 0), "Packed crate model override failed");
            ITEMS.add(packed);
            FloraExpansion.LOGGER.info("COMPATIBILITY AUDIT PASSED: {} block states, {} inventory variants", states, ITEMS.size());
            checked = true;
            mc.setScreen(new Preview());
        }
        if (++ticks == 50) net.minecraft.client.Screenshot.grab(mc.gameDirectory, "all-items-1211.png", mc.getMainRenderTarget(),
                message -> FloraExpansion.LOGGER.info("Audit screenshot: {}", message.getString()));
        if (ticks == 70) {
            mc.createWorldOpenFlows().createFreshLevel("audit-" + System.currentTimeMillis(),
                    new net.minecraft.world.level.LevelSettings("1.21.1 compatibility audit", net.minecraft.world.level.GameType.CREATIVE, false,
                            net.minecraft.world.Difficulty.NORMAL, true, new net.minecraft.world.level.GameRules(),
                            net.minecraft.world.level.WorldDataConfiguration.DEFAULT),
                    new net.minecraft.world.level.levelgen.WorldOptions(12345L, true, false),
                    net.minecraft.world.level.levelgen.presets.WorldPresets::createNormalWorldDimensions, mc.screen);
        }
    }
    private static final class Preview extends Screen {
        Preview() { super(Component.literal("1.21.1 model audit")); }
        @Override public void render(GuiGraphics graphics, int x, int y, float partialTick) {
            super.render(graphics, x, y, partialTick);
            graphics.fill(0, 0, width, height, 0xFF30343C);
            graphics.drawString(font, title, 10, 8, -1);
            int columns = Math.max(1, (width - 20) / 22);
            for (int i = 0; i < ITEMS.size(); i++) graphics.renderItem(ITEMS.get(i), 10 + i % columns * 22, 28 + i / columns * 22);
        }
    }
    private static final class SaltingPreview extends Screen {
        private final float time;
        SaltingPreview(float time) { super(Component.literal("Salting recipe preview")); this.time = time; }
        @Override public void render(GuiGraphics graphics, int x, int y, float partialTick) {
            super.render(graphics, x, y, partialTick);
            graphics.fill(0, 0, width, height, 0xFF30343C);
            graphics.drawString(font, title, 10, 8, -1);
            graphics.pose().pushPose();
            graphics.pose().translate((width - 176) / 2F, (height - 160) / 2F, 0);
            de.artemis.floraexpansion.client.renderer.SaltingRecipeRenderer.draw(graphics,
                    new de.artemis.floraexpansion.client.renderer.SaltingRecipeRenderer.State(
                            new ItemStack(ModItems.SPRUCE_NUTS.get()), new ItemStack(ModItems.SALT.get()),
                            new ItemStack(ModItems.SALTED_SPRUCE_NUTS.get(), 4), time));
            graphics.pose().popPose();
        }
    }
}
