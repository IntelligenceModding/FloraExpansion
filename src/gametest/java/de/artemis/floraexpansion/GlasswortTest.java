package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.worldgen.feature.BrineSpringFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;

@EventBusSubscriber(modid=FloraExpansion.MODID)
public final class GlasswortTest {
    private static final ResourceLocation ID=ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID,"glasswort");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION=ResourceKey.create(Registries.TEST_FUNCTION,ID);
    @SubscribeEvent public static void function(RegisterEvent event) { event.register(Registries.TEST_FUNCTION,ID,()->GlasswortTest::run); }
    @SubscribeEvent public static void register(RegisterGameTestsEvent event) {
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;
        var environment=event.registerEnvironment(ID);
        event.registerTest(ID,new FunctionGameTestInstance(FUNCTION,new TestData<>(environment,ResourceLocation.withDefaultNamespace("empty"),100,0,true)));
    }
    private static void run(GameTestHelper helper) {
        var level=helper.getLevel();var plant=ModBlocks.GLASSWORT.get();var state=plant.defaultBlockState();
        var pos=helper.absolutePos(new BlockPos(4,4,4));
        for(var p:BlockPos.betweenClosed(pos.offset(-3,-2,-3),pos.offset(3,1,3)))level.setBlockAndUpdate(p,Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(pos.below().east(),ModBlocks.SALT_WATER.get().defaultBlockState());
        for(var soil:new net.minecraft.world.level.block.Block[]{Blocks.DIRT,Blocks.SAND,ModBlocks.CRYSTALLINE_SALT.get(),ModBlocks.SMOOTH_SALT.get(),Blocks.CALCITE}) {
            level.setBlockAndUpdate(pos.below(),soil.defaultBlockState());
            check(state.canSurvive(level,pos),"Glasswort rejects suitable brine shore: "+soil);
        }
        level.setBlockAndUpdate(pos.below(),Blocks.OAK_PLANKS.defaultBlockState());check(!state.canSurvive(level,pos),"Glasswort accepts wooden floor");
        level.setBlockAndUpdate(pos.below(),ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
        level.setBlockAndUpdate(pos.below().east(),Blocks.WATER.defaultBlockState());check(!state.canSurvive(level,pos),"Glasswort accepts fresh water");
        level.setBlockAndUpdate(pos.below().east(),Blocks.AIR.defaultBlockState());check(!state.canSurvive(level,pos),"Glasswort accepts dry ground");
        level.setBlockAndUpdate(pos.below().east(),ModBlocks.SALT_WATER.get().defaultBlockState());
        level.setBlockAndUpdate(pos,ModBlocks.SALT_WATER.get().defaultBlockState());check(!state.canSurvive(level,pos),"Glasswort accepts submerged position");
        level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
        for(var p:BlockPos.betweenClosed(pos.offset(-2,2,-2),pos.offset(2,2,2)))level.setBlockAndUpdate(p,Blocks.STONE.defaultBlockState());
        check(state.canSurvive(level,pos),"Glasswort requires sunlight");
        level.setBlockAndUpdate(pos,state);
        check(plant.isValidBonemealTarget(level,pos,state),"Invalid bone meal target");
        plant.performBonemeal(level,RandomSource.create(2),pos,state);
        check(level.getBlockState(pos).is(plant),"Bone meal consumed original plant");
        var drops=level.getEntitiesOfClass(ItemEntity.class,new AABB(pos).inflate(2));
        check(drops.stream().anyMatch(item->item.getItem().is(plant.asItem())),"Bone meal gave no glasswort");
        drops.forEach(ItemEntity::discard);level.destroyBlock(pos,true);
        check(level.getEntitiesOfClass(ItemEntity.class,new AABB(pos).inflate(2)).stream().anyMatch(item->item.getItem().is(plant.asItem())),"Harvest gave no glasswort");

        var named=new net.minecraft.world.item.ItemStack(plant);
        var itemName=named.get(net.minecraft.core.component.DataComponents.ITEM_NAME);
        check(itemName!=null && itemName.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents text
                && text.getKey().equals("block.floraexpansion.glasswort"),"Wrong Glasswort item translation key: "+itemName);
        check(de.artemis.floraexpansion.common.item.BasketHarvestHelper.canGather(state),"Basket rejects glasswort");
        var cratePos=pos.east(8);level.setBlockAndUpdate(cratePos,ModBlocks.CRATE.get().defaultBlockState());
        var crate=(de.artemis.floraexpansion.common.block.entity.CrateBlockEntity)level.getBlockEntity(cratePos);
        check(crate!=null && crate.insertItem(new net.minecraft.world.item.ItemStack(plant,12))==12
                && crate.getItem(0).is(plant.asItem()),"Crate rejects glasswort");
        var player=helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        var interactionPos=pos.west(8);level.setBlockAndUpdate(interactionPos.below(),Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(interactionPos,Blocks.COMPOSTER.defaultBlockState());
        var compost=new net.minecraft.world.item.ItemStack(plant,2);
        check(compost.getItemHolder().getData(net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps.COMPOSTABLES) != null,"Glasswort lacks compostable component");
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,compost);
        var hit=new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(interactionPos),net.minecraft.core.Direction.UP,interactionPos,false);
        level.getBlockState(interactionPos).useItemOn(compost,level,player,net.minecraft.world.InteractionHand.MAIN_HAND,hit);
        check(compost.getCount()==1 && level.getBlockState(interactionPos).getValue(net.minecraft.world.level.block.ComposterBlock.LEVEL)==1,"Composter did not accept glasswort");
        level.setBlockAndUpdate(interactionPos,Blocks.FLOWER_POT.defaultBlockState());
        var potPlant=new net.minecraft.world.item.ItemStack(plant,2);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,potPlant);
        level.getBlockState(interactionPos).useItemOn(potPlant,level,player,net.minecraft.world.InteractionHand.MAIN_HAND,hit);
        check(level.getBlockState(interactionPos).is(ModBlocks.POTTED_GLASSWORT.get()) && potPlant.getCount()==1,"Flower pot rejected glasswort");
        var potDrops=net.minecraft.world.level.block.Block.getDrops(level.getBlockState(interactionPos),level,interactionPos,null);
        check(potDrops.stream().anyMatch(stack->stack.is(plant.asItem())) && potDrops.stream().anyMatch(stack->stack.is(net.minecraft.world.item.Items.FLOWER_POT)),"Potted glasswort has incorrect drops");
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,net.minecraft.world.item.ItemStack.EMPTY);
        level.getBlockState(interactionPos).useWithoutItem(level,player,hit);
        check(level.getBlockState(interactionPos).is(Blocks.FLOWER_POT) && player.getMainHandItem().is(plant.asItem()),"Removing glasswort did not return the plant and empty pot");
        var surface=helper.absolutePos(new BlockPos(64,80,64));
        int count=0;
        for(int sample=0;sample<16 && count==0;sample++) {
        for(int x=-8;x<=8;x++)for(int z=-8;z<=8;z++)for(int y=-8;y<=4;y++)
            level.setBlock(surface.offset(x,y,z),y<0?Blocks.STONE.defaultBlockState():Blocks.AIR.defaultBlockState(),2);
        check(new BrineSpringFeature().place(net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.INSTANCE,level,level.getChunkSource().getGenerator(),RandomSource.create(1 + sample),surface),"Spring fixture failed");
        for(var p:BlockPos.betweenClosed(surface.offset(-8,-2,-8),surface.offset(8,2,8)))if(level.getBlockState(p).is(plant)) {
            count++;check(state.canSurvive(level,p),"Worldgen put glasswort away from a valid brine shore");
        }
        }
        check(count>0,"Brine spring generated no glasswort across 16 seeded pools");
        FloraExpansion.LOGGER.info("Glasswort fixture: {} natural plants; placement, fresh-water rejection, bone meal and harvest passed",count);
        var kelp = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DRIED_KELP);
        for (var snack : new net.minecraft.world.item.ItemStack[]{new net.minecraft.world.item.ItemStack(plant,2),
                new net.minecraft.world.item.ItemStack(de.artemis.floraexpansion.common.registry.ModItems.SALTED_GLASSWORT.get(),2)}) {
            check(snack.getUseDuration(player)==kelp.getUseDuration(player),"Glasswort is not a kelp-speed snack");
            int nutrition=snack.is(plant.asItem())?1:2;
            check(snack.get(net.minecraft.core.component.DataComponents.FOOD).nutrition()==nutrition,"Wrong glasswort nutrition");
            player.getFoodData().setFoodLevel(10);
            var remainder=snack.finishUsingItem(level,player);
            check(remainder.getCount()==1&&player.getFoodData().getFoodLevel()==10+nutrition,"Eating glasswort did not consume/feed correctly");
        }
        var tubPos=pos.south(8);level.setBlockAndUpdate(tubPos,ModBlocks.SALTING_TUB.get().defaultBlockState());
        var tub=(de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity)level.getBlockEntity(tubPos);
        for(int layer=0;layer<4;layer++)check(tub.addFood(new net.minecraft.world.item.ItemStack(plant)),"Tub rejects glasswort");
        check(tub.salt(),"Glasswort batch cannot be salted");
        SaltingTubTests.advance(tub,101);
        var salted=tub.take(false);
        check(salted.size()==1&&salted.getFirst().is(de.artemis.floraexpansion.common.registry.ModItems.SALTED_GLASSWORT.get())
                &&salted.getFirst().getCount()==4,"Tub did not yield four salted glasswort");
        FloraExpansion.LOGGER.info("Glasswort foods passed: kelp eating speed, native consumption/hunger, four-item salting batch, existing planting/pot/crate/compost behavior");
        helper.succeed();
    }
    private static void check(boolean pass,String message){if(!pass)throw new IllegalStateException(message);}
}
