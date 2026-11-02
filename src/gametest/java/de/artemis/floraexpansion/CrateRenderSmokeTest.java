package de.artemis.floraexpansion;

import de.artemis.floraexpansion.client.renderer.CrateBlockEntityRenderer;
import de.artemis.floraexpansion.client.renderer.CrateContentsPattern;
import de.artemis.floraexpansion.client.renderer.state.CrateRenderState;
import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

final class CrateRenderSmokeTest {
    private static final Item[] CONTENTS = {Items.WHEAT_SEEDS, ModItems.BLUEBERRIES.get(),
            Items.BONE_MEAL, ModBlocks.APPLE_LEAVES.get().asItem(), Items.IRON_HOE};
    private static BlockPos origin;

    private CrateRenderSmokeTest() {}

    static boolean check(Minecraft minecraft, int worldTicks) {
        if (origin == null) {
            checkPixelPattern();
            origin = new BlockPos(minecraft.player.getBlockX(), 250, minecraft.player.getBlockZ());
            var server = minecraft.getSingleplayerServer();
            if (server == null) throw new IllegalStateException("Missing integrated server");
            var playerId = minecraft.player.getUUID();
            server.execute(() -> {
                var level = server.overworld();
                server.setWeatherParameters(100000, 0, false, false);
                for (int x = -2; x <= 8; x++) {
                    for (int z = -2; z <= 11; z++) {
                        level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
                    }
                }
                for (int row = 0; row < CONTENTS.length; row++) {
                    for (int column = 0; column < 3; column++) {
                        var pos = origin.offset(column * 2, 0, row * 2);
                        level.setBlockAndUpdate(pos, ModBlocks.CRATE.get().defaultBlockState());
                        var crate = (CrateBlockEntity) level.getBlockEntity(pos);
                        int capacity = CrateBlockEntity.CONTAINER_SIZE * CONTENTS[row].getDefaultMaxStackSize();
                        int count = column == 0 ? 1 : column == 1 ? capacity / 2 : capacity;
                        if (crate == null || crate.insertItem(new ItemStack(CONTENTS[row], count)) != count) {
                            throw new IllegalStateException("Cannot prepare render fixture: " + CONTENTS[row]);
                        }
                    }
                }
                var player = server.getPlayerList().getPlayer(playerId);
                var colors = java.util.List.of((net.minecraft.world.level.block.CandleBlock) Blocks.DYED_CANDLE.green(),
                        (net.minecraft.world.level.block.CandleBlock) Blocks.DYED_CANDLE.red(),
                        (net.minecraft.world.level.block.CandleBlock) Blocks.DYED_CANDLE.blue());
                for (int count = 1; count <= 3; count++) {
                    var cakePos = origin.offset(6, 0, (count - 1) * 2);
                    var cakeState = de.artemis.floraexpansion.common.block.StrawberryCakeWithCandleBlock.byCandle(colors.getFirst())
                            .setValue(de.artemis.floraexpansion.common.block.StrawberryCakeWithCandleBlock.CANDLES, count)
                            .setValue(de.artemis.floraexpansion.common.block.StrawberryCakeWithCandleBlock.LIT, count % 2 == 1);
                    level.setBlockAndUpdate(cakePos, cakeState);
                    ((de.artemis.floraexpansion.common.block.entity.StrawberryCakeBlockEntity) level.getBlockEntity(cakePos))
                            .setCandles(colors.subList(0, count));
                }
                if (player == null) throw new IllegalStateException("Missing fixture player");
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.setYRot(180);
                player.setXRot(48);
                player.teleportTo(origin.getX() + 2.5, 259, origin.getZ() + 12);
            });
            return false;
        }
        minecraft.player.setYRot(180);
        minecraft.player.setXRot(48);
        if (worldTicks < 180) return false;
        for (int row = 0; row < CONTENTS.length; row++) {
            float previous = 0;
            for (int column = 0; column < 3; column++) {
                var crate = (CrateBlockEntity) minecraft.level.getBlockEntity(origin.offset(column * 2, 0, row * 2));
                if (crate == null || crate.getTotalItemCount() == 0) {
                    if (worldTicks < 400) return false;
                    throw new IllegalStateException("Crate render fixture did not synchronize");
                }
                var renderer = (CrateBlockEntityRenderer) minecraft.getBlockEntityRenderDispatcher()
                        .<CrateBlockEntity, CrateRenderState>getRenderer(crate);
                if (renderer == null) throw new IllegalStateException("Missing crate renderer");
                var state = renderer.createRenderState();
                renderer.extractRenderState(crate, state, 0, minecraft.player.position(), null);
                if (state.contentsPixels.length != 144
                        || state.fillRatio <= previous) {
                    throw new IllegalStateException("Missing item appearance or wrong crate fill: " + CONTENTS[row]);
                }
                previous = state.fillRatio;
            }
        }
        var source = (CrateBlockEntity) minecraft.level.getBlockEntity(origin);
        for (int count = 1; count <= 3; count++) {
            var cake = (de.artemis.floraexpansion.common.block.entity.StrawberryCakeBlockEntity)
                    minecraft.level.getBlockEntity(origin.offset(6, 0, (count - 1) * 2));
            if (cake == null || cake.getCandles().size() != count) return false;
            var renderer = (de.artemis.floraexpansion.client.renderer.StrawberryCakeBlockEntityRenderer)
                    minecraft.getBlockEntityRenderDispatcher()
                            .<de.artemis.floraexpansion.common.block.entity.StrawberryCakeBlockEntity,
                                    de.artemis.floraexpansion.client.renderer.state.StrawberryCakeRenderState>getRenderer(cake);
            if (renderer == null) throw new IllegalStateException("Missing mixed candle renderer");
            var cakeRenderState = renderer.createRenderState();
            renderer.extractRenderState(cake, cakeRenderState, 0, minecraft.player.position(), null);
            String[] colors = {"green", "red", "blue"};
            for (int slot = 0; slot < count; slot++) {
                String expected = "minecraft:textures/block/" + colors[slot] + "_candle" + (count % 2 == 1 ? "_lit" : "") + ".png";
                if (!cakeRenderState.textures.get(slot).toString().equals(expected)) {
                    if (worldTicks < 400) return false;
                    throw new IllegalStateException("Mixed cake candle colors did not synchronize: " + cakeRenderState.textures);
                }
            }
        }
        var rejectedStack = new ItemStack(Items.BIRCH_LOG);
        int storedCount = source.getTotalItemCount();
        var rejectedResult = source.getBlockState().useItemOn(rejectedStack, minecraft.level, minecraft.player,
                net.minecraft.world.InteractionHand.MAIN_HAND,
                new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(origin),
                        net.minecraft.core.Direction.UP, origin, false));
        if (rejectedResult != net.minecraft.world.InteractionResult.FAIL
                || rejectedStack.getCount() != 1 || source.getTotalItemCount() != storedCount) {
            throw new IllegalStateException("Client did not reject unsupported crate deposit without a swing: "
                    + rejectedResult + ", state=" + source.getBlockState() + ", shift=" + minecraft.player.isShiftKeyDown()
                    + ", held=" + minecraft.player.getMainHandItem() + ", count=" + source.getTotalItemCount());
        }
        var renderer = (CrateBlockEntityRenderer) minecraft.getBlockEntityRenderDispatcher()
                .<CrateBlockEntity, CrateRenderState>getRenderer(source);
        var state = renderer.createRenderState();
        var packed = new ItemStack(ModBlocks.CRATE.get());
        source.saveToItem(packed, minecraft.level.registryAccess());
        for (var hiddenState : new net.minecraft.world.level.block.state.BlockState[]{
                ModBlocks.CRATE.get().defaultBlockState().setValue(CrateBlock.PACKED, true),
                ModBlocks.CRATE.get().defaultBlockState().setValue(CrateBlock.POWERED, true)}) {
            var hidden = new CrateBlockEntity(origin, hiddenState);
            hidden.setLevel(minecraft.level);
            hidden.applyComponentsFromItemStack(packed);
            renderer.extractRenderState(source, state, 0, minecraft.player.position(), null);
            renderer.extractRenderState(hidden, state, 0, minecraft.player.position(), null);
            if (state.contentsPixels.length != 0) throw new IllegalStateException("Closed crate retained visible fill");
        }
        var empty = new CrateBlockEntity(origin, ModBlocks.CRATE.get().defaultBlockState());
        empty.setLevel(minecraft.level);
        renderer.extractRenderState(source, state, 0, minecraft.player.position(), null);
        renderer.extractRenderState(empty, state, 0, minecraft.player.position(), null);
        if (state.contentsPixels.length != 0) throw new IllegalStateException("Empty crate retained the previous item appearance");
        FloraExpansion.LOGGER.info("Crate render smoke test passed: seeds, berries, bone meal, tinted leaves and tools at three fill levels");
        return true;
    }

    private static void checkPixelPattern() {
        var icon = new int[256];
        for (int i = 0; i < icon.length; i++) icon[i] = 0xFF000000 | i;
        var pattern = CrateContentsPattern.compose(icon);
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 12; x++) {
                if (pattern[y * 12 + x] != icon[(y + 2) * 16 + x + 2]) {
                    throw new IllegalStateException("Crate pattern rescaled the source pixels");
                }
            }
        }
        java.util.Arrays.fill(icon, 0);
        if (CrateContentsPattern.compose(icon).length != 0) throw new IllegalStateException("Transparent icon acquired a fake fill");
        icon[0] = 0xFF12AB34;
        var sparse = CrateContentsPattern.compose(icon);
        for (int color : sparse) {
            if (color != icon[0]) throw new IllegalStateException("Sparse icon left holes or introduced unrelated colors");
        }
        if (!java.util.Arrays.equals(sparse, CrateContentsPattern.compose(icon))) {
            throw new IllegalStateException("Crate pattern changes between extractions");
        }
    }
}
