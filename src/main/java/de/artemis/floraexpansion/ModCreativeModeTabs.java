package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FloraExpansion.MODID);

    public static final Supplier<CreativeModeTab> FLORA_EXPANSION_CREATIVE_TAB = CREATIVE_MODE_TAB.register("flora_expansion_creative_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.PINE_CONE.get()))
                    .title(Component.translatable("floraexpansion.creative_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.LEAF_LITTER);
                        output.accept(ModBlocks.PINE_LITTER);
                        output.accept(ModItems.PINE_CONE);
                        output.accept(ModItems.PINE_NUTS);
                        output.accept(ModItems.TOASTED_PINE_NUTS);
                        output.accept(ModItems.TWIG);
                        output.accept(ModBlocks.TWIG_LADDER);
                        output.accept(ModItems.FOREST_SNACK);
                        output.accept(ModItems.FLAX_SEED);
                        output.accept(ModItems.FLAX_FIBER);
                        output.accept(ModItems.FLAX_FLOWER);
                        output.accept(ModItems.LINEN_THREAD);
                        output.accept(ModItems.LINEN_CLOTH);

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
