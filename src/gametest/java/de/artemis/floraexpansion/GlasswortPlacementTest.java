package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;

/** Exercises the same BlockItem.useOn path as right-clicking a shore in survival. */
@EventBusSubscriber(modid=FloraExpansion.MODID)
public final class GlasswortPlacementTest {
    private static final ResourceLocation ID=ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID,"glasswort_item_placement");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION=ResourceKey.create(Registries.TEST_FUNCTION,ID);
    @SubscribeEvent public static void function(RegisterEvent event){event.register(Registries.TEST_FUNCTION,ID,()->GlasswortPlacementTest::run);}
    @SubscribeEvent public static void tests(RegisterGameTestsEvent event){
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;event.registerTest(ID,new FunctionGameTestInstance(FUNCTION,new TestData<>(event.registerEnvironment(ID),ResourceLocation.withDefaultNamespace("empty"),100,0,true)));}
    private static void run(GameTestHelper helper) {
        var level=helper.getLevel();var pos=helper.absolutePos(new BlockPos(8,6,8));var ground=pos.below();
        var player=helper.makeMockPlayer(GameType.SURVIVAL);player.setPos(Vec3.atCenterOf(pos.north(4)));
        Block[] soils={Blocks.GRASS_BLOCK,Blocks.DIRT,Blocks.COARSE_DIRT,Blocks.ROOTED_DIRT,Blocks.PODZOL,Blocks.MUD,
                Blocks.MYCELIUM,Blocks.MOSS_BLOCK,Blocks.PALE_MOSS_BLOCK,Blocks.MUDDY_MANGROVE_ROOTS,Blocks.SAND,Blocks.RED_SAND,
                ModBlocks.CRYSTALLINE_SALT.get(),ModBlocks.SMOOTH_SALT.get(),ModBlocks.CUT_SALT.get(),ModBlocks.CHISELED_SALT.get(),Blocks.CALCITE,Blocks.DIORITE};
        for(var soil:soils) for(int offset:new int[]{0,-1}) {
            for(var p:BlockPos.betweenClosed(pos.offset(-3,-3,-3),pos.offset(3,1,3)))level.setBlockAndUpdate(p,Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(ground.below(),Blocks.STONE.defaultBlockState());
            level.setBlockAndUpdate(ground,soil.defaultBlockState());
            level.setBlockAndUpdate(ground.east().above(offset),ModBlocks.SALT_WATER.get().defaultBlockState());
            var stack=new ItemStack(ModBlocks.GLASSWORT.get(),2);player.setItemInHand(InteractionHand.MAIN_HAND,stack);
            var hit=new BlockHitResult(Vec3.atCenterOf(ground).add(0,0.5,0),Direction.UP,ground,false);
            var result=stack.getItem().useOn(new UseOnContext(player,InteractionHand.MAIN_HAND,hit));
            check(result.consumesAction()&&level.getBlockState(pos).is(ModBlocks.GLASSWORT.get()),
                    "Right-click placement failed on "+soil+" with water offset "+offset+": "+result);
            check(stack.getCount()==1,"Placement did not consume exactly one plant");
        }
        level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(ground,Blocks.GRASS_BLOCK.defaultBlockState());
        level.setBlockAndUpdate(ground.east().below(),Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(ground.east(),Blocks.WATER.defaultBlockState());
        var stack=new ItemStack(ModBlocks.GLASSWORT.get(),2);player.setItemInHand(InteractionHand.MAIN_HAND,stack);
        var hit=new BlockHitResult(Vec3.atCenterOf(ground).add(0,0.5,0),Direction.UP,ground,false);
        var failed=stack.getItem().useOn(new UseOnContext(player,InteractionHand.MAIN_HAND,hit));
        check(!failed.consumesAction()&&stack.getCount()==2&&!level.getBlockState(pos).is(ModBlocks.GLASSWORT.get()),"Fresh water incorrectly allowed placement or consumed a plant");
        FloraExpansion.LOGGER.info("Glasswort item placement passed: {} soils, brine at both supported heights, proper item consumption and freshwater rejection",soils.length);
        helper.succeed();
    }
    private static void check(boolean pass,String message){if(!pass)throw new IllegalStateException(message);}
}
