package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.migration.LegacyContentAliases;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModLootModifiers;
import de.artemis.floraexpansion.common.registry.ModMenuTypes;
import de.artemis.floraexpansion.common.registry.ModParticles;
import de.artemis.floraexpansion.common.registry.ModFeatures;
import de.artemis.floraexpansion.common.registry.ModTreeDecorators;
import de.artemis.floraexpansion.common.network.ModPayloads;
import org.slf4j.Logger;
import de.artemis.floraexpansion.common.registry.ModCreativeModeTabs;

import com.mojang.logging.LogUtils;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import de.artemis.floraexpansion.common.config.ServerConfig;

@Mod(FloraExpansion.MODID)
public class FloraExpansion {
    public static final String MODID = "floraexpansion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FloraExpansion(net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        // Register explicitly so older 1.20.1 loaders also use the correct bus.
        modEventBus.register(de.artemis.floraexpansion.common.datagen.DataGenerators.class);
        modEventBus.register(de.artemis.floraexpansion.common.event.SaltWaterInteractions.class);
        modEventBus.register(de.artemis.floraexpansion.common.event.WoodenBucketCauldronInteractions.class);
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ServerConfig.SPEC);
        LegacyContentAliases.register(modEventBus);
        ModPayloads.register();
        if (!net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData()) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> FloraExpansionClient.initialize(context));
        }
        modEventBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) -> event.enqueueWork(de.artemis.floraexpansion.common.datagen.ModDataMapProvider::registerCompostables));
        modEventBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) -> event.enqueueWork(de.artemis.floraexpansion.common.projectile.PebbleDispenser::register));

        modEventBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            var pot = (net.minecraft.world.level.block.FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT;
            pot.addPlant(java.util.Objects.requireNonNull(ModBlocks.APPLE_CORE.getId()), ModBlocks.POTTED_APPLE_CORE);
            pot.addPlant(java.util.Objects.requireNonNull(ModBlocks.CHERRY_PIT.getId()), ModBlocks.POTTED_CHERRY_PIT);
            pot.addPlant(java.util.Objects.requireNonNull(ModBlocks.GIANT_CACTUS_STEM.getId()), ModBlocks.POTTED_GIANT_CACTUS_STEM);
            pot.addPlant(java.util.Objects.requireNonNull(ModBlocks.GLASSWORT.getId()), ModBlocks.POTTED_GLASSWORT);
        }));
        de.artemis.floraexpansion.common.registry.ModArmorMaterials.register(modEventBus);
        ModItems.register(modEventBus);
        de.artemis.floraexpansion.common.advancement.FloraActionTrigger.register(modEventBus);
        de.artemis.floraexpansion.common.registry.ModFluids.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModParticles.register(modEventBus);
        de.artemis.floraexpansion.common.registry.ModSounds.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModTreeDecorators.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        de.artemis.floraexpansion.common.registry.ModRecipes.register(modEventBus);
    }

}
