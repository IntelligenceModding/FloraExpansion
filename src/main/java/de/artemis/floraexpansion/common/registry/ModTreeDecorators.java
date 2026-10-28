package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import de.artemis.floraexpansion.common.worldgen.treedecorator.OrchardCherryLeavesDecorator;
import de.artemis.floraexpansion.common.worldgen.treedecorator.AppleLeavesDecorator;

public class ModTreeDecorators {

    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, FloraExpansion.MODID);

    @SuppressWarnings("Notnull")
    public static final DeferredHolder<TreeDecoratorType<?>, @org.jetbrains.annotations.NotNull TreeDecoratorType<@org.jetbrains.annotations.NotNull OrchardCherryLeavesDecorator>> ORCHARD_CHERRY_LEAVES_DECORATOR =
            TREE_DECORATORS.register("orchard_cherry_leaves_decorator",
                    () -> new TreeDecoratorType<>(OrchardCherryLeavesDecorator.CODEC));

    public static final DeferredHolder<TreeDecoratorType<?>, @org.jetbrains.annotations.NotNull TreeDecoratorType<@org.jetbrains.annotations.NotNull AppleLeavesDecorator>> APPLE_LEAVES_DECORATOR =
            TREE_DECORATORS.register("apple_leaves_decorator",
                    () -> new TreeDecoratorType<>(AppleLeavesDecorator.CODEC));

    public static void register(IEventBus eventBus) {
        TREE_DECORATORS.register(eventBus);
    }
}

