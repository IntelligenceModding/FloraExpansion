package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.ScatteredPebblesBlock;
import de.artemis.floraexpansion.common.registry.*;
import de.artemis.floraexpansion.common.projectile.PebblesProjectile;
import de.artemis.floraexpansion.common.worldgen.feature.PebbleClusterFeature;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;

@EventBusSubscriber(modid=FloraExpansion.MODID)
public final class PebbleReworkTest {
    private static final Identifier ID=Identifier.fromNamespaceAndPath(FloraExpansion.MODID,"pebble_rework");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION=ResourceKey.create(Registries.TEST_FUNCTION,ID);
    private static BlockPos protectedPos;
    @SubscribeEvent public static void function(RegisterEvent e){e.register(Registries.TEST_FUNCTION,ID,()->PebbleReworkTest::run);}
    @SubscribeEvent public static void tests(RegisterGameTestsEvent e){e.registerTest(ID,new FunctionGameTestInstance(FUNCTION,new TestData<>(e.registerEnvironment(ID),Identifier.withDefaultNamespace("empty"),200,0,true)));}
    @SubscribeEvent public static void protect(BreakEvent e){if(e.getPos().equals(protectedPos))e.setCanceled(true);}

    private static void run(GameTestHelper h){
        var level=h.getLevel();var pos=h.absolutePos(new BlockPos(4,6,4));
        for(String removed:java.util.List.of("pebble_block","packed_pebbles","packed_pebbles_stairs","packed_pebbles_slab","packed_pebbles_wall")){
            var old=Identifier.fromNamespaceAndPath(FloraExpansion.MODID,removed);
            check(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(old)==Blocks.GRAVEL
                    &&net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(old)==Items.GRAVEL,"Removed packed content did not migrate to gravel: "+removed);
        }
        var player=h.makeMockPlayer(GameType.SURVIVAL);player.setPos(Vec3.atCenterOf(pos.north(3)));
        for(var p:BlockPos.betweenClosed(pos.offset(-2,-1,-2),pos.offset(2,2,2)))level.setBlock(p,p.getY()<pos.getY()?Blocks.STONE.defaultBlockState():Blocks.AIR.defaultBlockState(),2);
        var held=new ItemStack(ModItems.PEBBLES.get(),8);player.setItemInHand(InteractionHand.MAIN_HAND,held);
        for(int count=1;count<=5;count++){
            var clicked=count==1?pos.below():pos;
            ModItems.PEBBLES.get().useOn(new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(clicked),Direction.UP,clicked,false)));
            check(level.getBlockState(pos).is(ModBlocks.SCATTERED_PEBBLES.get())&&level.getBlockState(pos).getValue(ScatteredPebblesBlock.AMOUNT)==count,"Unified pebble placement failed at "+count);
        }
        check(held.getCount()==3,"Placement lost pebbles");
        var legacy=net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(FloraExpansion.MODID,"scattered_pebbles"));
        check(legacy!=ModItems.PEBBLES.get()&&ModBlocks.SCATTERED_PEBBLES.get().asItem()==legacy,"Legacy block item mapping changed");
        var conversion=(net.minecraft.world.item.crafting.CraftingRecipe)level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE,Identifier.fromNamespaceAndPath(FloraExpansion.MODID,"pebbles_from_scattered_pebbles"))).orElseThrow().value();
        var input=net.minecraft.world.item.crafting.CraftingInput.of(1,1,java.util.List.of(new ItemStack(legacy)));
        check(((net.minecraft.world.item.crafting.CraftingRecipe)conversion).matches(input,level)
                &&conversion.assemble(input).is(ModItems.PEBBLES.get()),"Legacy conversion recipe is broken");
        player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
        var hit=new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false);
        protectedPos=pos;level.getBlockState(pos).useWithoutItem(level,player,hit);
        check(level.getBlockState(pos).getValue(ScatteredPebblesBlock.AMOUNT)==5,"Collection bypassed protection");protectedPos=null;
        level.getBlockState(pos).useWithoutItem(level,player,hit);
        check(level.getBlockState(pos).getValue(ScatteredPebblesBlock.AMOUNT)==4,"Bare hand did not collect one");
        check(player.getMainHandItem().isEmpty(),"Collection filled the active hand and prevents repeated collecting");
        player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);player.setShiftKeyDown(true);
        level.getBlockState(pos).useWithoutItem(level,player,hit);check(level.getBlockState(pos).isAir(),"Sneak collection did not clear pile");player.setShiftKeyDown(false);
        for(boolean brine:new boolean[]{false,true}){
            var fluid=brine?ModFluids.SALT_WATER.get():net.minecraft.world.level.material.Fluids.WATER;
            level.setBlock(pos,fluid.defaultFluidState().createLegacyBlock(),3);
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(ModItems.PEBBLES.get(),2));
            ModItems.PEBBLES.get().useOn(new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos.below()),Direction.UP,pos.below(),false)));
            check(level.getFluidState(pos).is(fluid)&&level.getBlockState(pos).is(ModBlocks.SCATTERED_PEBBLES.get()),"Placement displaced fluid");
            player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);level.getBlockState(pos).useWithoutItem(level,player,hit);
            check(level.getFluidState(pos).is(fluid)&&level.getFluidState(pos).isSource(),"Collection lost fluid");
            level.setBlock(pos,ModBlocks.SCATTERED_PEBBLES.get().defaultBlockState().setValue(ScatteredPebblesBlock.WATERLOGGED,true).setValue(ScatteredPebblesBlock.BRINE,brine),3);
            level.setBlockAndUpdate(pos.below(),Blocks.AIR.defaultBlockState());
            check(!level.getBlockState(pos).is(ModBlocks.SCATTERED_PEBBLES.get())&&level.getFluidState(pos).is(fluid),"Support loss erased fluid");
            level.setBlockAndUpdate(pos.below(),Blocks.STONE.defaultBlockState());
        }
        for(int amount=1;amount<=5;amount++){
            var state=ModBlocks.SCATTERED_PEBBLES.get().defaultBlockState().setValue(ScatteredPebblesBlock.AMOUNT,amount);
            var drops=Block.getDrops(state,level,pos,null);
            check(drops.size()==1&&drops.getFirst().is(ModItems.PEBBLES.get())&&drops.getFirst().getCount()==amount,"Breaking lost pebbles");
        }
        level.setBlock(pos,ModFluids.FLOWING_SALT_WATER.get().defaultFluidState().createLegacyBlock(),3);
        var brinePlacement=new ItemStack(ModItems.PEBBLES.get(),2);player.setItemInHand(InteractionHand.MAIN_HAND,brinePlacement);
        ModItems.PEBBLES.get().useOn(new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos.below()),Direction.UP,pos.below(),false)));
        check(brinePlacement.getCount()==2&&!level.getFluidState(pos).isSource(),"Waterlogging duplicated a flowing brine source");
        var throwing=new ItemStack(ModItems.PEBBLES.get(),3);player.setItemInHand(InteractionHand.MAIN_HAND,throwing);
        ModItems.PEBBLES.get().use(level,player,InteractionHand.MAIN_HAND);ModItems.PEBBLES.get().use(level,player,InteractionHand.MAIN_HAND);
        check(throwing.getCount()==2&&player.getCooldowns().isOnCooldown(throwing),"Throw cooldown or survival consumption failed");
        var creative=h.makeMockPlayer(GameType.CREATIVE);creative.getAbilities().instabuild=true;var creativeStack=new ItemStack(ModItems.PEBBLES.get());creative.setItemInHand(InteractionHand.MAIN_HAND,creativeStack);
        ModItems.PEBBLES.get().use(level,creative,InteractionHand.MAIN_HAND);check(creativeStack.getCount()==1,"Creative throw consumed its item");
        var dispPos=pos.west(5);var dispState=Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING,Direction.WEST);
        level.setBlockAndUpdate(dispPos,dispState);
        var source=new net.minecraft.core.dispenser.BlockSource(level,dispPos,dispState,(net.minecraft.world.level.block.entity.DispenserBlockEntity)level.getBlockEntity(dispPos));
        var remaining=DispenserBlock.DISPENSER_REGISTRY.get(ModItems.PEBBLES.get()).dispense(source,new ItemStack(ModItems.PEBBLES.get(),2));
        check(remaining.getCount()==1&&!level.getEntitiesOfClass(PebblesProjectile.class,new AABB(dispPos).inflate(3)).isEmpty(),"Dispenser did not launch a pebble");
        // Actual projectile ticks over a long still-water channel: shallow throws skim, steep throws sink.
        var waterPos=h.absolutePos(new BlockPos(64,20,32));
        for(var p:BlockPos.betweenClosed(waterPos.offset(-2,-3,-2),waterPos.offset(82,3,2))){
            int y=p.getY()-waterPos.getY();level.setBlock(p,y==-3?Blocks.STONE.defaultBlockState():y<0?Blocks.WATER.defaultBlockState():Blocks.AIR.defaultBlockState(),2);
        }
        class Stone extends PebblesProjectile {
            int returned;
            Stone(){super(level,player,new ItemStack(ModItems.PEBBLES.get()));}
            @Override protected void spawnDrop(ItemStack stack,double x,double y,double z,double scatter,double upward){returned+=stack.getCount();}
            void impact(Entity target){onHit(new EntityHitResult(target));}
        }
        var skipCounts=new java.util.ArrayList<Integer>();
        double[][] throwsToCheck={{.3,-.08},{.5,-.08},{.8,-.08},{1.2,-.08},{1.5,-.08},{1.2,-.4},{1.2,-.8}};
        for(var trajectory:throwsToCheck){
            var stone=new Stone();stone.setPos(waterPos.getX(),waterPos.getY()+.15,waterPos.getZ()+.5);stone.setDeltaMovement(trajectory[0],trajectory[1],0);
            int skips=0;for(int t=0;t<220&&stone.isAlive();t++){double previous=stone.getDeltaMovement().y;stone.tick();if(previous<0&&stone.getDeltaMovement().y>0&&stone.isAlive())skips++;}
            skipCounts.add(skips);
            check(stone.returned==1&&!stone.isAlive(),"Projectile lost or duplicated a pebble");
            var cow=EntityType.COW.create(level,EntitySpawnReason.COMMAND);stone.impact(cow);check(stone.returned==1,"Repeated collision duplicated a pebble");
        }
        FloraExpansion.LOGGER.info("Pebble trajectory skip counts (speed/angle matrix): {}",skipCounts);
        check(skipCounts.getFirst()==0&&skipCounts.getLast()==0,"Slow/steep throws should sink: "+skipCounts);
        check(skipCounts.get(4)>3,"Fast shallow throws are still capped at three: "+skipCounts);
        check(skipCounts.get(1)<skipCounts.get(3)&&skipCounts.get(3)<=skipCounts.get(4),"Speed does not affect skip distance: "+skipCounts);
        check(skipCounts.get(5)<skipCounts.get(3),"Steeper throws do not lose more skipping energy: "+skipCounts);
        check(new java.util.HashSet<>(skipCounts).size()>=4,"Throws lack varied skip counts: "+skipCounts);
        worldgen(h);
        FloraExpansion.LOGGER.info("Pebble rework passed: placement/collection, protection, legacy IDs, fresh/brine waterlogging, exact drops, original textured pebbles, speed/angle-dependent skipping, steep throws, single recovery and shoreline generation");h.succeed();
    }

    private static void worldgen(GameTestHelper h){
        var level=h.getLevel();var raw=h.absolutePos(new BlockPos(256,100,256));var origin=new BlockPos(raw.getX()&~15,raw.getY(),raw.getZ()&~15);
        for(var p:BlockPos.betweenClosed(origin.offset(-4,-4,-4),origin.offset(20,6,20))){int y=p.getY()-origin.getY();
            level.setBlock(p,y< -1?Blocks.STONE.defaultBlockState():y==-1?Blocks.GRASS_BLOCK.defaultBlockState():Blocks.AIR.defaultBlockState(),2);
        }
        for(int x=9;x<16;x++)for(int z=0;z<16;z++)level.setBlock(origin.offset(x,-1,z),Blocks.WATER.defaultBlockState(),2);
        var chest=origin.offset(7,0,7);var plant=origin.offset(6,0,7);level.setBlock(chest,Blocks.CHEST.defaultBlockState(),2);level.setBlock(plant,Blocks.SHORT_GRASS.defaultBlockState(),2);
        net.minecraft.server.commands.FillBiomeCommand.fill(level,origin.offset(-4,-4,-4),origin.offset(20,6,20),level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(net.minecraft.world.level.biome.Biomes.RIVER));
        for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)Heightmap.primeHeightmaps(level.getChunk((origin.getX()>>4)+x,(origin.getZ()>>4)+z),java.util.EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG,Heightmap.Types.WORLD_SURFACE_WG));
        var feature=new PebbleClusterFeature();for(int seed=0;seed<24;seed++)feature.place(new PebbleClusterFeature.Config(3,3),level,level.getChunkSource().getGenerator(),RandomSource.create(seed),origin.offset(8,0,8));
        int dry=0,wet=0;for(var p:BlockPos.betweenClosed(origin.offset(0,-1,0),origin.offset(15,0,15))){var state=level.getBlockState(p);if(state.is(ModBlocks.SCATTERED_PEBBLES.get())){if(state.getValue(ScatteredPebblesBlock.WATERLOGGED))wet++;else dry++;check(state.canSurvive(level,p),"Unsupported worldgen pebble");}}
        check(dry>0&&wet>0,"Shore generator missed dry/underwater stones: "+dry+"/"+wet);
        check(level.getBlockState(chest).is(Blocks.CHEST)&&level.getBlockState(plant).is(Blocks.SHORT_GRASS),"Worldgen erased existing content");
        for(int x=9;x<16;x++)for(int z=0;z<16;z++)check(level.getFluidState(origin.offset(x,-1,z)).is(net.minecraft.tags.FluidTags.WATER),"Worldgen drained water");
    }
    private static void check(boolean pass,String message){if(!pass)throw new IllegalStateException(message);}
}
