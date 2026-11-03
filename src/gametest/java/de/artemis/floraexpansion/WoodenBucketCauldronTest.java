package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.config.ServerConfig;
import de.artemis.floraexpansion.common.event.SaltWaterInteractions;
import de.artemis.floraexpansion.common.item.WoodenBucketUtil;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;

/** Opt-in integration coverage: -PportTests runGameTestServer. */
@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class WoodenBucketCauldronTest {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID,"wooden_bucket_cauldron");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION = ResourceKey.create(Registries.TEST_FUNCTION,ID);
    @SubscribeEvent public static void functions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION,ID,()->WoodenBucketCauldronTest::run);
    }
    @SubscribeEvent public static void tests(RegisterGameTestsEvent event) {
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;
        var environment=event.registerEnvironment(ID);
        event.registerTest(ID,new FunctionGameTestInstance(FUNCTION,
                new TestData<>(environment,ResourceLocation.withDefaultNamespace("empty"),100,0,true)));
    }
    private record Case(BlockState state,CauldronInteraction.InteractionMap dispatcher,Item filled,boolean full) {}
    private static void run(GameTestHelper helper) {
        var level=helper.getLevel(); var pos=helper.absolutePos(new BlockPos(1,2,1));
        var player=FakePlayerFactory.getMinecraft(level); player.setGameMode(GameType.SURVIVAL);
        var cases=new java.util.ArrayList<Case>();
        cases.add(new Case(Blocks.CAULDRON.defaultBlockState(),CauldronInteraction.EMPTY,null,false));
        for(int n=1;n<=3;n++) {
            cases.add(new Case(Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,n),CauldronInteraction.WATER,ModItems.WOODEN_WATER_BUCKET.get(),n==3));
            cases.add(new Case(Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,n),CauldronInteraction.POWDER_SNOW,ModItems.WOODEN_POWDER_SNOW_BUCKET.get(),n==3));
            cases.add(new Case(ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,n),SaltWaterInteractions.CAULDRON,ModItems.WOODEN_SALT_WATER_BUCKET.get(),n==3));
        }
        cases.add(new Case(Blocks.LAVA_CAULDRON.defaultBlockState(),CauldronInteraction.LAVA,ModItems.WOODEN_LAVA_BUCKET.get(),true));
        boolean allowed=ServerConfig.lavaEnabled.get(); ServerConfig.lavaEnabled.set(true);
        try {
            level.setBlockAndUpdate(pos.above(),Blocks.AIR.defaultBlockState());
            // Collection: only full layered cauldrons, returning the correct wooden variant.
            for(var c:cases) {
                level.setBlockAndUpdate(pos,c.state); var bucket=stack(ModItems.WOODEN_BUCKET.get(),3);
                player.setItemInHand(InteractionHand.MAIN_HAND,bucket);
                c.dispatcher.map().get(bucket.getItem()).interact(c.state,level,pos,player,InteractionHand.MAIN_HAND,bucket);
                var result=player.getMainHandItem();
                check(level.getBlockState(pos).equals(c.full?Blocks.CAULDRON.defaultBlockState():c.state),"Collection state "+c.state);
                check(result.is(c.full?c.filled:ModItems.WOODEN_BUCKET.get()),"Collection item "+c.state);
                check(result.getDamageValue()==(c.full?4:3),"Collection durability");
                check(Component.literal("Named bucket").equals(result.get(DataComponents.CUSTOM_NAME)),"Collection name");
                if(c.filled==ModItems.WOODEN_LAVA_BUCKET.get() && c.full)
                    check(WoodenBucketUtil.hasExpiredLavaTimeout(result,level.getGameTime()+72001),"Lava timeout initialized");
            }
            var fills=new Item[]{ModItems.WOODEN_WATER_BUCKET.get(),ModItems.WOODEN_LAVA_BUCKET.get(),ModItems.WOODEN_POWDER_SNOW_BUCKET.get(),ModItems.WOODEN_SALT_WATER_BUCKET.get()};
            var targets=new BlockState[]{Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,3),Blocks.LAVA_CAULDRON.defaultBlockState(),Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,3),ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,3)};
            for(var c:cases) for(int i=0;i<fills.length;i++) {
                level.setBlockAndUpdate(pos,c.state); var bucket=stack(fills[i],3); player.setItemInHand(InteractionHand.MAIN_HAND,bucket);
                c.dispatcher.map().get(bucket.getItem()).interact(c.state,level,pos,player,InteractionHand.MAIN_HAND,bucket);
                check(level.getBlockState(pos).equals(targets[i]),"Pour "+fills[i]+" into "+c.state);
                check(player.getMainHandItem().is(ModItems.WOODEN_BUCKET.get()) && player.getMainHandItem().getDamageValue()==4,"Pour durability/wooden return");
                check(Component.literal("Named bucket").equals(player.getMainHandItem().get(DataComponents.CUSTOM_NAME)),"Pour name");
            }
            // Vanilla underwater restrictions: lava and powder snow must not alter either object.
            level.setBlockAndUpdate(pos.above(),Blocks.WATER.defaultBlockState());
            for(int i:new int[]{1,2}) {
                level.setBlockAndUpdate(pos,Blocks.CAULDRON.defaultBlockState());var bucket=stack(fills[i],3);player.setItemInHand(InteractionHand.MAIN_HAND,bucket);
                CauldronInteraction.EMPTY.map().get(bucket.getItem()).interact(level.getBlockState(pos),level,pos,player,InteractionHand.MAIN_HAND,bucket);
                check(level.getBlockState(pos).is(Blocks.CAULDRON) && player.getMainHandItem()==bucket && bucket.getDamageValue()==3,"Underwater restriction");
            }
            level.setBlockAndUpdate(pos.above(),Blocks.AIR.defaultBlockState());
            ServerConfig.lavaEnabled.set(false);level.setBlockAndUpdate(pos,Blocks.LAVA_CAULDRON.defaultBlockState());
            var empty=stack(ModItems.WOODEN_BUCKET.get(),3);player.setItemInHand(InteractionHand.MAIN_HAND,empty);
            CauldronInteraction.LAVA.map().get(empty.getItem()).interact(level.getBlockState(pos),level,pos,player,InteractionHand.MAIN_HAND,empty);
            check(level.getBlockState(pos).is(Blocks.LAVA_CAULDRON) && player.getMainHandItem()==empty,"Disabled lava pickup");
            for(int i=0;i<fills.length;i++) {
                level.setBlockAndUpdate(pos,Blocks.CAULDRON.defaultBlockState());var bucket=stack(fills[i],9);player.setItemInHand(InteractionHand.MAIN_HAND,bucket);
                CauldronInteraction.EMPTY.map().get(bucket.getItem()).interact(level.getBlockState(pos),level,pos,player,InteractionHand.MAIN_HAND,bucket);
                check(player.getMainHandItem().isEmpty() && level.getBlockState(pos).equals(targets[i]),"Final durability pour");
            }
            player.setGameMode(GameType.CREATIVE);
            var creative=stack(fills[0],3);player.setItemInHand(InteractionHand.MAIN_HAND,creative);level.setBlockAndUpdate(pos,Blocks.CAULDRON.defaultBlockState());
            CauldronInteraction.EMPTY.map().get(creative.getItem()).interact(level.getBlockState(pos),level,pos,player,InteractionHand.MAIN_HAND,creative);
            check(player.getMainHandItem()==creative && creative.getDamageValue()==3,"Creative pour retains original bucket");
            var creativeEmpty=stack(ModItems.WOODEN_BUCKET.get(),3);player.setItemInHand(InteractionHand.MAIN_HAND,creativeEmpty);
            CauldronInteraction.WATER.map().get(creativeEmpty.getItem()).interact(level.getBlockState(pos),level,pos,player,InteractionHand.MAIN_HAND,creativeEmpty);
            check(player.getMainHandItem().is(fills[0]) && player.getMainHandItem().getDamageValue()==3,"Creative pickup preserves wooden behavior");
            helper.succeed();
        } finally { ServerConfig.lavaEnabled.set(allowed); player.setGameMode(GameType.SURVIVAL); }
    }
    private static ItemStack stack(Item item,int damage) { var s=new ItemStack(item);s.setDamageValue(damage);s.set(DataComponents.CUSTOM_NAME,Component.literal("Named bucket"));return s; }
    private static void check(boolean pass,String message) {if(!pass)throw new AssertionError(message);}
}
