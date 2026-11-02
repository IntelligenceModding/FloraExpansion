package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.DesertMossBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

final class FloweringMossTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var center = helper.absolutePos(new BlockPos(2, 70, 2));
        var moss = (DesertMossBlock) ModBlocks.DESERT_MOSS.get();
        var flowering = (DesertMossBlock) ModBlocks.FLOWERING_DESERT_MOSS.get();
        var random = RandomSource.create(42);
        var naturalRandom = RandomSource.create(12345);
        var originalRandom = RandomSource.create(12345);
        int floweringCount = 0;
        int[] appearances = new int[8];
        for (int i = 0; i < 1024; i++) {
            var natural = DesertMossBlock.naturalState(naturalRandom);
            int variant = natural.getValue(DesertMossBlock.VARIANT);
            check(variant == originalRandom.nextInt(4), "Natural moss changed existing seeded patterns");
            check(natural.is(moss) || natural.is(flowering), "Natural moss selected an unrelated block");
            if (natural.is(flowering)) floweringCount++;
            appearances[variant * 2 + (natural.is(flowering) ? 1 : 0)]++;
        }
        check(floweringCount >= 450 && floweringCount <= 574, "Natural moss distribution must be approximately half flowering");
        for (int count : appearances) check(count > 80 && count < 180, "Natural moss must retain all four patterns for both types");
        check(naturalRandom.nextLong() == originalRandom.nextLong(), "Flowering moss shifted subsequent seeded generation");
        for (var pos : BlockPos.betweenClosed(center.offset(-3, -2, -3), center.offset(3, 2, 3))) {
            level.setBlock(pos, pos.getY() == center.getY() - 1 ? Blocks.SAND.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
        }
        for (int variant = 0; variant < 4; variant++) {
            var state = moss.defaultBlockState().setValue(DesertMossBlock.VARIANT, variant);
            level.setBlockAndUpdate(center, state);
            check(moss.isValidBonemealTarget(level, center, state), "Desert moss cannot flower");
            moss.performBonemeal(level, random, center, state);
            check(level.getBlockState(center).is(flowering)
                    && level.getBlockState(center).getValue(DesertMossBlock.VARIANT) == variant, "Flowering changed moss pattern");
        }
        var protectedPos = center.east();
        var unsupportedPos = center.west();
        level.setBlockAndUpdate(protectedPos, Blocks.CHEST.defaultBlockState());
        level.setBlockAndUpdate(unsupportedPos.below(), Blocks.AIR.defaultBlockState());
        flowering.performBonemeal(level, random, center, level.getBlockState(center));
        check(count(level, center, flowering) == 5, "One bone meal must create four patches");
        check(level.getBlockState(protectedPos).is(Blocks.CHEST) && level.isEmptyBlock(unsupportedPos),
                "Moss spread overwrote a block or floated without support");
        for (int i = 0; i < 10; i++) flowering.performBonemeal(level, random, center, level.getBlockState(center));
        check(!flowering.isValidBonemealTarget(level, center, level.getBlockState(center)),
                "Blocked flowering moss must not consume bone meal");
        check(level.getBlockState(center.offset(3, 0, 0)).isAir(), "Moss spread exceeded two blocks");
        var refill = center.north();
        level.setBlockAndUpdate(refill, moss.stateForPosition(refill));
        flowering.performBonemeal(level, random, center, level.getBlockState(center));
        check(level.getBlockState(refill).is(flowering), "Spreading did not flower ordinary moss");
        var drops = Block.getDrops(level.getBlockState(center), level, center, null);
        check(drops.size() == 1 && drops.getFirst().is(flowering.asItem()), "Flowering moss must drop itself");
        check(level.getBlockState(center).is(BlockTags.FLOWERS)
                && new ItemStack(flowering).is(ItemTags.BEE_FOOD), "Flowering moss missing flower and bee tags");
        level.destroyBlock(center.below(), false);
        check(level.isEmptyBlock(center), "Unsupported flowering moss did not break");
    }

    private static int count(net.minecraft.server.level.ServerLevel level, BlockPos center, Block block) {
        int count = 0;
        for (var pos : BlockPos.betweenClosed(center.offset(-2, -1, -2), center.offset(2, 1, 2))) {
            if (level.getBlockState(pos).is(block)) count++;
        }
        return count;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
