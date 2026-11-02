package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.worldgen.SaltWaterEvaporation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;

@EventBusSubscriber(modid=FloraExpansion.MODID)
public final class SaltWaterEvaporationTest {
    private static final Identifier ID=Identifier.fromNamespaceAndPath(FloraExpansion.MODID,"salt_water_evaporation");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION=ResourceKey.create(Registries.TEST_FUNCTION,ID);
    @SubscribeEvent public static void function(RegisterEvent event){event.register(Registries.TEST_FUNCTION,ID,()->SaltWaterEvaporationTest::run);}
    @SubscribeEvent public static void register(RegisterGameTestsEvent event) {
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;
        var rules=net.minecraft.world.level.gamerules.GameRuleMap.of();
        rules.set(net.minecraft.world.level.gamerules.GameRules.RANDOM_TICK_SPEED,0);
        var env=event.registerEnvironment(ID,new TestEnvironmentDefinition.SetGameRules(rules));
        event.registerTest(ID,new FunctionGameTestInstance(FUNCTION,new TestData<>(env,Identifier.withDefaultNamespace("empty"),200,0,true)));
    }
    private static void run(GameTestHelper helper) {
        var level=helper.getLevel(); var water=ModBlocks.SALT_WATER.get().defaultBlockState();
        var base=helper.absolutePos(new BlockPos(96,240,32));
        for(int depth=1;depth<=8;depth++) {
            var top=base.east(depth*4);
            for(int y=-depth;y<=1;y++) {
                var pos=top.above(y);
                level.setBlockAndUpdate(pos,y==-depth?Blocks.STONE.defaultBlockState():y<=0?water:Blocks.AIR.defaultBlockState());
                for(var face:Direction.Plane.HORIZONTAL)level.setBlockAndUpdate(pos.relative(face),Blocks.STONE.defaultBlockState());
            }
        }
        var checkPos=base.west(8);
        level.setBlockAndUpdate(checkPos.below(),Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(checkPos,water);
        setTime(level,6000);level.getServer().setWeatherParameters(100000,0,false,false);level.setRainLevel(0);level.setThunderLevel(0);
        helper.runAfterDelay(5,()->{
            check(level.canSeeSky(checkPos.above()) && level.isBrightOutside(),"Daylight fixture is not sunny");
            check(water.isRandomlyTicking(),"Source does not receive random ticks");
            var flowing=water.setValue(LiquidBlock.LEVEL,2);
            check(!flowing.isRandomlyTicking(),"Flowing water receives evaporation ticks");
            level.setBlockAndUpdate(checkPos,flowing);
            check(!SaltWaterEvaporation.tryEvaporate(level,checkPos),"Flowing water produced salt");
            level.setBlockAndUpdate(checkPos,Blocks.WATER.defaultBlockState());
            check(!SaltWaterEvaporation.tryEvaporate(level,checkPos),"Fresh water produced salt");
            level.setBlockAndUpdate(checkPos,water);
            setTime(level,18000);check(!SaltWaterEvaporation.tryEvaporate(level,checkPos),"Nighttime evaporation");
            setTime(level,6000);level.setRainLevel(1);
            check(!SaltWaterEvaporation.tryEvaporate(level,checkPos),"Rain evaporation");level.setRainLevel(0);
            level.setBlockAndUpdate(checkPos.above(),Blocks.STONE.defaultBlockState());
        });
        helper.runAfterDelay(8,()->{
            check(!SaltWaterEvaporation.tryEvaporate(level,checkPos),"Shaded water evaporated");
            level.setBlockAndUpdate(checkPos.above(),Blocks.AIR.defaultBlockState());
        });
        helper.runAfterDelay(12,()->evaporatePools(helper,base,new int[9],0));
        helper.runAfterDelay(55,()->{
            check(SaltWaterEvaporation.tryEvaporate(level,checkPos),"Exposed single source did not evaporate");
            var dry=level.getBlockState(checkPos);check(dry.is(ModBlocks.SALT_DEPOSIT.get())&&dry.getValue(SaltDepositBlock.LAYERS)==1&&!dry.getValue(SaltDepositBlock.WATERLOGGED),"Single source yielded wrong deposit");
            check(!dry.getValue(SaltDepositBlock.NATURAL),"Evaporation manufactured buried finds");
            check(!SaltWaterEvaporation.tryEvaporate(level,checkPos),"Dry salt generated more salt");
            var wet=dry.setValue(SaltDepositBlock.LAYERS,3).setValue(SaltDepositBlock.WATERLOGGED,true).setValue(SaltDepositBlock.BRINE,true);
            level.setBlockAndUpdate(checkPos,wet);
            check(wet.isRandomlyTicking()&&SaltWaterEvaporation.tryEvaporate(level,checkPos),"Brine in deposit did not evaporate");
            check(level.getBlockState(checkPos).getValue(SaltDepositBlock.LAYERS)==4&&!level.getBlockState(checkPos).getValue(SaltDepositBlock.WATERLOGGED),"Wet layer did not produce exactly one additional layer");
            FloraExpansion.LOGGER.info("Salt evaporation passed: sunlight/weather restrictions, source-only behavior, 1..8-deep pools and exact layer conservation");

        });
    }
    private static void evaporatePools(GameTestHelper helper,BlockPos base,int[] done,int attempts) {
        var level=helper.getLevel();boolean finished=true;
        for(int depth=1;depth<=8;depth++) {
            if(done[depth]==depth)continue;
            finished=false;var top=base.east(depth*4);BlockPos exposed=null;
            for(int y=0;y>-depth;y--) {
                var p=top.above(y);var fluid=level.getFluidState(p);
                if(fluid.isSource()&&fluid.is(ModFluids.SALT_WATER.get())){exposed=p;break;}
            }
            check(exposed!=null,"Missing source at depth "+depth+" after "+done[depth]+" evaporations");
            check(SaltWaterEvaporation.tryEvaporate(level,exposed),"Exposed source rejected at "+exposed+": "+level.getBlockState(exposed));
            done[depth]++;int sources=0,layers=0;
            for(int y=0;y>-depth;y--){var state=level.getBlockState(top.above(y));var f=state.getFluidState();if(f.isSource()&&f.is(ModFluids.SALT_WATER.get()))sources++;if(state.is(ModBlocks.SALT_DEPOSIT.get()))layers+=state.getValue(SaltDepositBlock.LAYERS);}
            check(sources==depth-done[depth]&&layers==done[depth],"Conservation failed: depth="+depth+", sources="+sources+", layers="+layers);
        }
        if(finished){helper.runAfterDelay(50,helper::succeed);return;}
        check(attempts<25,"Pool did not finish evaporating before test deadline");
        helper.runAfterDelay(5,()->evaporatePools(helper,base,done,attempts+1));
    }    private static void setTime(net.minecraft.server.level.ServerLevel level,long time) {
        var clock=level.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD);
        level.getServer().clockManager().setTotalTicks(clock,time);level.environmentAttributes().invalidateTickCache();level.updateSkyBrightness();
    }
    private static void check(boolean pass,String message){if(!pass)throw new IllegalStateException(message);}
}
