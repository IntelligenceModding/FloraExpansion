package de.artemis.floraexpansion.common.worldgen.treedecorator;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModTreeDecorators {

    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, FloraExpansion.MODID);

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<CherryStoneLeafDecorator>> CHERRY_STONE_LEAF_DECORATOR =
            TREE_DECORATORS.register("cherry_stone_leaf_decorator",
                    () -> new TreeDecoratorType<>(CherryStoneLeafDecorator.CODEC));

    public static void register(IEventBus eventBus) {
        TREE_DECORATORS.register(eventBus);
    }
}