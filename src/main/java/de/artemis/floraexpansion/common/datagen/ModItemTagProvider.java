package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends IntrinsicHolderTagsProvider<Item> {
    public ModItemTagProvider(PackOutput output, CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider, net.minecraftforge.common.data.ExistingFileHelper existingFiles) {
        super(output, Registries.ITEM, lookupProvider, item -> item.builtInRegistryHolder().key(), FloraExpansion.MODID, existingFiles);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:hidden_from_recipe_viewers"))).add(blockItemKey(ModBlocks.SCATTERED_PEBBLES));
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:foods"))).add(blockItemKey(ModBlocks.GLASSWORT), ModItems.SALTED_GLASSWORT.get());
        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("minecraft:music_discs"))).add(ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
        tag(Tags.Items.ORE_RATES_DENSE).add(blockItemKey(ModBlocks.CALCITE_COPPER_ORE),
                blockItemKey(ModBlocks.CALCITE_LAPIS_ORE), blockItemKey(ModBlocks.CALCITE_REDSTONE_ORE));
        tag(Tags.Items.ORE_RATES_SINGULAR).add(blockItemKey(ModBlocks.CALCITE_COAL_ORE), blockItemKey(ModBlocks.CALCITE_IRON_ORE),
                blockItemKey(ModBlocks.CALCITE_GOLD_ORE), blockItemKey(ModBlocks.CALCITE_EMERALD_ORE), blockItemKey(ModBlocks.CALCITE_DIAMOND_ORE));
        tag(ItemTags.COAL_ORES).add(blockItemKey(ModBlocks.CALCITE_COAL_ORE));
        tag(ItemTags.IRON_ORES).add(blockItemKey(ModBlocks.CALCITE_IRON_ORE));
        tag(ItemTags.COPPER_ORES).add(blockItemKey(ModBlocks.CALCITE_COPPER_ORE));
        tag(ItemTags.GOLD_ORES).add(blockItemKey(ModBlocks.CALCITE_GOLD_ORE));
        tag(ItemTags.REDSTONE_ORES).add(blockItemKey(ModBlocks.CALCITE_REDSTONE_ORE));
        tag(ItemTags.EMERALD_ORES).add(blockItemKey(ModBlocks.CALCITE_EMERALD_ORE));
        tag(ItemTags.LAPIS_ORES).add(blockItemKey(ModBlocks.CALCITE_LAPIS_ORE));
        tag(ItemTags.DIAMOND_ORES).add(blockItemKey(ModBlocks.CALCITE_DIAMOND_ORE));
        tag(ItemTags.STAIRS).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_STAIRS));
        tag(ItemTags.SLABS).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_SLAB));
        tag(ItemTags.WALLS).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_WALL));
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:foods"))).add(ModItems.SALTED_SPRUCE_NUTS.get(), ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(),
                ModItems.CURED_MEAT.get(), ModItems.CURED_FISH.get());
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("minecraft:flowers"))).add(blockItemKey(ModBlocks.FLOWERING_DESERT_MOSS));
        tag(ItemTags.WOODEN_STAIRS).add(blockItemKey(ModBlocks.CACTUS_STAIRS)).add(blockItemKey(ModBlocks.CACTUS_MOSAIC_STAIRS));
        tag(ItemTags.WOODEN_SLABS).add(blockItemKey(ModBlocks.CACTUS_SLAB)).add(blockItemKey(ModBlocks.CACTUS_MOSAIC_SLAB));
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:seeds")))
                .add(ModItems.SPRUCE_NUTS.get())
                .add(ModItems.FLAX_SEEDS.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:seeds")))
                .add(ModItems.SPRUCE_NUTS.get())
                .add(ModItems.FLAX_SEEDS.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("minecraft:parrot_poisonous_food")))
                .add(ModItems.TOASTED_SPRUCE_NUTS.get());

        tag(ItemTags.DAMPENS_VIBRATIONS)
                .add(blockItemKey(ModBlocks.NEEDLE_LITTER))
                .add(blockItemKey(ModBlocks.LINEN_BALE))
                .add(blockItemKey(ModBlocks.LINEN_CARPET));

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.FLAX_SEEDS.get())
                .add(ModItems.STRAWBERRY.get());

        tag(ItemTags.SAPLINGS)
                .add(blockItemKey(ModBlocks.APPLE_CORE))
                .add(blockItemKey(ModBlocks.CHERRY_PIT));

        tag(ItemTags.LOGS_THAT_BURN)
                .add(blockItemKey(ModBlocks.STRIPPED_CACTUS_LOG))
                .add(blockItemKey(ModBlocks.CACTUS_LOG))
                .add(blockItemKey(ModBlocks.STRIPPED_CACTUS_WOOD))
                .add(blockItemKey(ModBlocks.CACTUS_WOOD));

        tag(ItemTags.PLANKS)
                .add(blockItemKey(ModBlocks.CACTUS_PLANKS));

        tag(ItemTags.BOATS)
                .add(ModItems.CACTUS_BOAT.get());

        tag(ItemTags.CHEST_BOATS)
                .add(ModItems.CACTUS_CHEST_BOAT.get());

        tag(ItemTags.SIGNS)
                .add(ModItems.CACTUS_SIGN.get());

        tag(ItemTags.HANGING_SIGNS)
                .add(ModItems.CACTUS_HANGING_SIGN.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:enchantable/armor")))
                .add(ModItems.SPIKED_LEATHER_HELMET.get())
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.get())
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.get())
                .add(ModItems.SPIKED_LEATHER_BOOTS.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:enchantable/fire_aspect")))
                .add(ModItems.SPIKED_LEATHER_HELMET.get())
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.get())
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.get())
                .add(ModItems.SPIKED_LEATHER_BOOTS.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:enchantable/head_armor")))
                .add(ModItems.SPIKED_LEATHER_HELMET.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:enchantable/chest_armor")))
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:enchantable/leg_armor")))
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:enchantable/foot_armor")))
                .add(ModItems.SPIKED_LEATHER_BOOTS.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:armors/helmets")))
                .add(ModItems.SPIKED_LEATHER_HELMET.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:armors/chestplates")))
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:armors/leggings")))
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:armors/boots")))
                .add(ModItems.SPIKED_LEATHER_BOOTS.get());

        tag(ItemTags.FOX_FOOD)
                .add(ModItems.PRICKLY_PEAR.get())
                .add(net.minecraft.world.item.Items.SWEET_BERRIES, net.minecraft.world.item.Items.GLOW_BERRIES).add(ModItems.BLUEBERRIES.get())
                .add(ModItems.STRAWBERRY.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:foods")))
                .add(net.minecraft.world.item.Items.SWEET_BERRIES, net.minecraft.world.item.Items.GLOW_BERRIES).add(ModItems.BLUEBERRIES.get())
                .add(ModItems.BLUEBERRY_COOKIE.get())
                .add(ModItems.BLUEBERRY_PIE.get())
                .add(ModItems.SLICE_OF_BLUEBERRY_PIE.get())
                .add(ModItems.BLUEBERRY_JUICE.get())
                .add(ModItems.STRAWBERRY_JUICE.get())
                .add(ModItems.STRAWBERRY.get())
                .add(ModItems.STRAWBERRY_JAM.get())
                .add(ModItems.BLUEBERRY_JAM.get())
                .add(ModItems.CHERRY_JAM.get())
                .add(ModItems.APPLE_JAM.get())
                .add(ModItems.SPRUCE_NUTS.get())
                .add(ModItems.TOASTED_SPRUCE_NUTS.get())
                .add(ModItems.CHERRIES.get())
                .add(ModItems.MIXED_BERRIES.get())
                .add(ModItems.CHERRY_JUICE.get())
                .add(ModItems.CACTUS_SLICE.get())
                .add(ModItems.APPLE_JUICE.get())
                .add(ModItems.CACTUS_JUICE.get())
                .add(ModItems.TRAIL_MIX.get())
                .add(ModItems.PRICKLY_PEAR.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:string")))
                .add(ModItems.LINEN_THREAD.get());

        tag(Tags.Items.SEEDS)
                .add(ModItems.FLAX_SEEDS.get())
                .add(ModItems.STRAWBERRY.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:foods/berries")))
                .add(ModItems.PRICKLY_PEAR.get())
                .add(net.minecraft.world.item.Items.SWEET_BERRIES, net.minecraft.world.item.Items.GLOW_BERRIES).add(ModItems.BLUEBERRIES.get())
                .add(ModItems.STRAWBERRY.get());

        tag(Tags.Items.CROPS).add(ModItems.FLAX_FIBER.get()).add(ModItems.FLAX_FLOWER.get());
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:foods/fruits"))).add(net.minecraft.world.item.Items.APPLE, net.minecraft.world.item.Items.MELON_SLICE, net.minecraft.world.item.Items.CHORUS_FRUIT).add(ModItems.CHERRIES.get()).add(ModItems.PRICKLY_PEAR.get());

        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:animal_foods")))
                .add(ModItems.PRICKLY_PEAR.get());
    }

    private static Item blockItemKey(RegistryObject<? extends Block> block) {
        return block.get().asItem();
    }
}
