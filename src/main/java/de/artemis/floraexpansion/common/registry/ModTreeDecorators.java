package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import de.artemis.floraexpansion.common.worldgen.treedecorator.*;

public class ModTreeDecorators {

    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, FloraExpansion.MODID);

    @SuppressWarnings("Notnull")
    public static final DeferredHolder<TreeDecoratorType<?>, @org.jetbrains.annotations.NotNull TreeDecoratorType<@org.jetbrains.annotations.NotNull FruitingCherryLeavesDecorator>> FRUITING_CHERRY_LEAVES_DECORATOR =
            TREE_DECORATORS.register("fruiting_cherry_leaves_decorator",
                    () -> new TreeDecoratorType<>(FruitingCherryLeavesDecorator.CODEC));

    public static final DeferredHolder<TreeDecoratorType<?>, @org.jetbrains.annotations.NotNull TreeDecoratorType<@org.jetbrains.annotations.NotNull FruitingOakLeavesDecorator>> FRUITING_OAK_LEAVES_DECORATOR =
            TREE_DECORATORS.register("fruiting_oak_leaves_decorator",
                    () -> new TreeDecoratorType<>(FruitingOakLeavesDecorator.CODEC));

    public static void register(IEventBus eventBus) {
        TREE_DECORATORS.register(eventBus);
    }
}

