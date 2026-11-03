package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.item.WoodenBucketUtil;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;

@EventBusSubscriber(modid=FloraExpansion.MODID)
public final class NetherSaltEvaporationTest {
    private static final ResourceLocation ID=ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID,"nether_salt_evaporation");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION=ResourceKey.create(Registries.TEST_FUNCTION,ID);
    @SubscribeEvent public static void function(RegisterEvent event){event.register(Registries.TEST_FUNCTION,ID,()->NetherSaltEvaporationTest::run);}
    @SubscribeEvent public static void tests(RegisterGameTestsEvent event){
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;event.registerTest(ID,new FunctionGameTestInstance(FUNCTION,new TestData<>(event.registerEnvironment(ID),ResourceLocation.withDefaultNamespace("empty"),100,0,true)));}
    private static void run(GameTestHelper helper) {
        var level=helper.getLevel().getServer().getLevel(Level.NETHER);
        check(level!=null,"No Nether level");var pos=new BlockPos(0,100,0);
        for(var p:BlockPos.betweenClosed(pos.offset(-3,-2,-3),pos.offset(3,3,3)))level.setBlockAndUpdate(p,Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(pos.below(),Blocks.NETHERRACK.defaultBlockState());
        check(level.environmentAttributes().getValue(net.minecraft.world.attribute.EnvironmentAttributes.WATER_EVAPORATES,pos),"Nether does not vaporize water");
        var player=FakePlayerFactory.getMinecraft(level);player.setGameMode(GameType.SURVIVAL);
        var bucket=ModItems.SALT_WATER_BUCKET.get();
        for(int i=1;i<=4;i++) {
            check(bucket.emptyContents(player,level,pos,null,new ItemStack(bucket)),"Metal bucket failed at layer "+i);
            checkLayers(level,pos,i);
        }
        level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
        var wood=new ItemStack(ModItems.WOODEN_SALT_WATER_BUCKET.get());wood.setDamageValue(2);
        var result=WoodenBucketUtil.emptyContents((BucketItem)wood.getItem(),level,player,wood,pos,
                new BlockHitResult(Vec3.atCenterOf(pos.below()),Direction.UP,pos.below(),false));
        check(result instanceof InteractionResult.Success,"Wooden bucket failed");
        var empty=((InteractionResult.Success)result).heldItemTransformedTo();
        check(empty!=null&&empty.is(ModItems.WOODEN_BUCKET.get())&&empty.getDamageValue()==3,"Wooden durability/empty result changed");
        checkLayers(level,pos,1);
        level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
        var dispenser=pos.west();var state=Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING,Direction.EAST);
        level.setBlockAndUpdate(dispenser,state);
        var source=new BlockSource(level,dispenser,state,(DispenserBlockEntity)level.getBlockEntity(dispenser));
        var dispensed=DispenserBlock.DISPENSER_REGISTRY.get(bucket).dispense(source,new ItemStack(bucket));
        check(dispensed.is(Items.BUCKET),"Dispenser did not return an empty bucket");checkLayers(level,pos,1);
        level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(pos,ModBlocks.SALT_WATER.get().defaultBlockState());checkLayers(level,pos,1);
        level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
        check(bucket.emptyContents(null,level,pos,null),"Legacy bucket placement failed");checkLayers(level,pos,1);
        // Ordinary water retains vanilla's salt-free vaporization behavior.
        level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
        check(((BucketItem)Items.WATER_BUCKET).emptyContents(player,level,pos,null,new ItemStack(Items.WATER_BUCKET)),"Vanilla water bucket failed");
        check(level.getBlockState(pos).isAir(),"Fresh water left salt");
        var overworld=helper.getLevel();var normal=helper.absolutePos(new BlockPos(3,4,3));
        overworld.setBlockAndUpdate(normal.below(),Blocks.STONE.defaultBlockState());
        overworld.setBlockAndUpdate(normal,Blocks.AIR.defaultBlockState());
        check(bucket.emptyContents(null,overworld,normal,null,new ItemStack(bucket))&&overworld.getFluidState(normal).is(ModFluids.SALT_WATER.get()),"Overworld placement evaporated instantly");
        FloraExpansion.LOGGER.info("Nether salt residue passed: metal and wooden buckets, four-layer stacking, dispenser, direct/legacy placement, and fresh-water/Overworld preservation");
        helper.succeed();
    }
    private static void checkLayers(net.minecraft.server.level.ServerLevel level,BlockPos pos,int count) {
        var state=level.getBlockState(pos);
        check(state.is(ModBlocks.SALT_DEPOSIT.get())&&state.getValue(SaltDepositBlock.LAYERS)==count,"Expected "+count+" salt layers, got "+state);
        check(!state.getValue(SaltDepositBlock.NATURAL),"Vaporization manufactured buried finds");
        check(state.getFluidState().isEmpty(),"Salt residue still contains fluid");
    }
    private static void check(boolean pass,String message){if(!pass)throw new IllegalStateException(message);}
}
