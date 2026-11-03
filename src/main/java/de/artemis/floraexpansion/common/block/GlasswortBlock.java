package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A salt-loving shore succulent; cave growth deliberately does not require light. */
public final class GlasswortBlock extends BushBlock implements BonemealableBlock {
    @NotNull @Override protected com.mojang.serialization.MapCodec<GlasswortBlock> codec() { return simpleCodec(GlasswortBlock::new); }

    private static final VoxelShape SHAPE = box(3,0,3,13,13,13);
    public GlasswortBlock(Properties properties) { super(properties); }
    @NotNull @Override protected VoxelShape getShape(@NotNull BlockState state,@NotNull BlockGetter level,@NotNull BlockPos pos,@NotNull CollisionContext context) { return SHAPE; }
    @Override protected boolean canSurvive(@NotNull BlockState state,LevelReader level,@NotNull BlockPos pos) {
        if (!level.getFluidState(pos).isEmpty()) return false;
        var ground=pos.below(); var support=level.getBlockState(ground);
        if (!support.is(ModTags.Blocks.GLASSWORT_PLANTABLE_ON) || !support.isFaceSturdy(level,ground,Direction.UP)) return false;
        for (int x=-2;x<=2;x++) for (int z=-2;z<=2;z++) {
            if (Math.abs(x)+Math.abs(z)>2) continue;
            for (int y=-1;y<=0;y++)
                if (level.getFluidState(ground.offset(x,y,z)).is(ModTags.Fluids.SALT_WATER)) return true;
        }
        return false;
    }
    @Override public boolean isValidBonemealTarget(@NotNull LevelReader level,@NotNull BlockPos pos,BlockState state) { return state.canSurvive(level,pos); }
    @Override public boolean isBonemealSuccess(@NotNull Level level,@NotNull RandomSource random,@NotNull BlockPos pos,@NotNull BlockState state) { return true; }
    @Override public void performBonemeal(@NotNull ServerLevel level,@NotNull RandomSource random,@NotNull BlockPos pos,@NotNull BlockState state) {
        popResource(level,pos,new ItemStack(this));
    }
}
