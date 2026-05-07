package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, FloraExpansion.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        //Miscellaneous
        add("floraexpansion.creative_tab", "Flora Expansion");

        //JEI
        add("jei.floraexpansion.leaf_litter.info.1", "Right-click with an empty main hand to gather twigs from leaf litter.");
        add("jei.floraexpansion.leaf_litter.info.2", "Thicker patches can yield more twigs.");
        add("jei.floraexpansion.leaf_litter.info.3", "Bonemeal increases the patch up to 4 layers instead of spreading to a new block.");
        add("jei.floraexpansion.leaf_litter.info.4", "Using bonemeal on a full 4-layer patch drops another leaf litter block.");
        add("jei.floraexpansion.pine_litter.info.1", "Right-click with an empty main hand to gather pine cones and twigs from pine litter.");
        add("jei.floraexpansion.pine_litter.info.2", "Thicker patches can yield more pine cones and more twigs.");
        add("jei.floraexpansion.pine_litter.info.3", "Bonemeal increases the patch up to 4 layers instead of spreading to a new block.");
        add("jei.floraexpansion.pine_litter.info.4", "Using bonemeal on a full 4-layer patch drops another pine litter block.");
        add("jei.floraexpansion.pine_cone.info.1", "Pine cones can be thrown.");
        add("jei.floraexpansion.pine_cone.info.2", "Thrown pine cones deal a small amount of damage and briefly slow targets.");
        add("jei.floraexpansion.pine_cone.info.3", "On impact, they sometimes drop 1 or 2 pine nuts.");
        add("jei.floraexpansion.pine_cone.info.4", "Burning pine cones can drop toasted pine nuts instead.");
        add("jei.floraexpansion.flax_seed.info.1", "Flax grows into a two-block crop once it reaches its taller stage.");
        add("jei.floraexpansion.flax_seed.info.2", "Right-click the upper half with shears to harvest the crop without replanting.");
        add("jei.floraexpansion.flax_seed.info.3", "Shearing resets flax back to a small lower-half plant so it can regrow faster.");
        add("jei.floraexpansion.flax_seed.info.4", "Bonemeal can advance flax growth and helps it reach the harvestable tall stage sooner.");
        add("jei.floraexpansion.pebble_patch.info.1", "Place more pebble patches onto the same block to build up the patch.");
        add("jei.floraexpansion.pebble_patch.info.2", "Right-click with an empty main hand to pick the pebbles back up.");
        add("jei.floraexpansion.pebble_patch.info.3", "A fuller patch returns more pebbles.");
        add("jei.floraexpansion.pebbles.info.1", "Pebbles can be thrown.");
        add("jei.floraexpansion.pebbles.info.2", "Thrown pebbles deal damage and usually drop themselves again on impact.");
        add("jei.floraexpansion.fruiting_cherry_leaves.info.1", "Fruiting cherry leaves slowly ripen over time.");
        add("jei.floraexpansion.fruiting_cherry_leaves.info.2", "Right-click by hand to harvest cherries once fruit has grown.");
        add("jei.floraexpansion.fruiting_cherry_leaves.info.3", "Riper leaves yield more cherries.");
        add("jei.floraexpansion.fruiting_cherry_leaves.info.4", "Bonemeal advances the fruiting stage.");
        add("jei.floraexpansion.cherries.info.1", "Eating cherries has a 50% chance to leave behind a cherry pit.");
        add("jei.floraexpansion.cherries.info.2", "Cherry pits can be planted to grow cherry trees.");
        add("jei.floraexpansion.cherry_pit.info.1", "Cherry pits are planted like saplings.");
        add("jei.floraexpansion.cherry_pit.info.2", "They can also come from eating cherries.");
        add("jei.floraexpansion.bottled_juice.info.1", "Drinking this returns an empty glass bottle.");
        add("jei.floraexpansion.bottled_juice.info.2", "The bottle is given back to the inventory or dropped nearby if there is no space.");
        add("jei.floraexpansion.fruiting_oak_leaves.info.1", "Fruiting oak leaves slowly ripen apples over time.");
        add("jei.floraexpansion.fruiting_oak_leaves.info.2", "Right-click by hand to harvest an apple once the leaves are fully ripe.");
        add("jei.floraexpansion.fruiting_oak_leaves.info.3", "Bonemeal advances the fruiting stage.");
        add("jei.floraexpansion.apple_core.info.1", "Apple cores are planted like saplings.");
        add("jei.floraexpansion.apple_core.info.2", "Eating apples has a 50% chance to leave behind an apple core.");
        add("jei.floraexpansion.giant_cactus_base.info.1", "Use an axe to strip giant cactus base blocks.");
        add("jei.floraexpansion.giant_cactus_base.info.2", "Stripping can knock loose a cactus thorn.");
        add("jei.floraexpansion.giant_cactus_wood.info.1", "Use an axe to strip giant cactus wood blocks.");
        add("jei.floraexpansion.giant_cactus_wood.info.2", "Stripping can knock loose a cactus thorn.");
        add("jei.floraexpansion.giant_cactus_stem.info.1", "Touching giant cactus stems deals cactus damage.");
        add("jei.floraexpansion.giant_cactus_stem.info.2", "Use an axe on the stem to harvest the whole connected stem column into cactus slices.");
        add("jei.floraexpansion.giant_cactus_stem.info.3", "Stems can keep growing upward and may produce cactus flowers at the top.");
        add("jei.floraexpansion.cactus_thorn.info.1", "Cactus thorns damage living entities that pass through them.");
        add("jei.floraexpansion.cactus_thorn.info.2", "They can be attached to floors, walls, or ceilings if the supporting block is sturdy enough.");
        add("jei.floraexpansion.cactus_flower.info.1", "Cactus flowers can grow into giant cacti on sand or suitable terracotta.");
        add("jei.floraexpansion.cactus_flower.info.2", "They can transform on their own over time.");
        add("jei.floraexpansion.cactus_flower.info.3", "Bonemeal can also trigger giant cactus growth when there is enough room.");
        add("jei.floraexpansion.cactus_armor.info.1", "Cactus armor can retaliate against direct melee attackers.");
        add("jei.floraexpansion.cactus_armor.info.2", "Wearing more pieces increases the chance, and 3 or more pieces hit harder.");
        add("jei.floraexpansion.cactus_armor.info.3", "Fire Aspect on cactus armor can ignite attackers, but Thorns cannot be applied.");
        add("jei.floraexpansion.cactus_cluster.info.1", "Place more cactus clusters onto the same block to build up the cluster.");
        add("jei.floraexpansion.cactus_cluster.info.2", "Use a glass bottle to extract cactus juice.");
        add("jei.floraexpansion.cactus_cluster.info.3", "Each bottle use reduces the cluster by one stage.");
        add("jei.floraexpansion.opuntia_cactus.info.1", "Opuntia cacti slowly ripen prickly pears over time.");
        add("jei.floraexpansion.opuntia_cactus.info.2", "Right-click to harvest ripe fruit without breaking the plant.");
        add("jei.floraexpansion.opuntia_cactus.info.3", "Bonemeal ripens the plant immediately.");
        add("jei.floraexpansion.prickly_pear.info.1", "Prickly pears are the fruit harvested from opuntia cacti.");
        add("jei.floraexpansion.prickly_pear.info.2", "The item can also be planted to place a new opuntia cactus.");

        //Blocks
        add(ModBlocks.PINE_LITTER.get(), "Pine Litter");
        add(ModBlocks.LEAF_LITTER.get(), "Leaf Litter");
        add(ModBlocks.TWIG_LADDER.get(), "Twig Ladder");
        add(ModBlocks.FLAX_CROP.get(), "Flax Crop");
        add(ModBlocks.LINEN_CARPET.get(), "Linen Carpet");
        add(ModBlocks.PEBBLE_PATCH.get(), "Pebble Patch");
        add(ModBlocks.PEBBLE_BLOCK.get(), "Pebble Block");
        add(ModBlocks.LINEN_BLOCK.get(), "Linen Block");
        add(ModBlocks.FLAX_BALE.get(), "Flax Bale");
        add(ModBlocks.CHERRY_PIT.get(), "Cherry Pit");
        add(ModBlocks.FRUITING_CHERRY_LEAVES.get(), "Fruiting Cherry Leaves");
        add(ModBlocks.POTTED_CHERRY_PIT.get(), "Potted Cherry Pit");
        add(ModBlocks.APPLE_CORE.get(), "Apple Core");
        add(ModBlocks.POTTED_APPLE_CORE.get(), "Potted Apple Core");
        add(ModBlocks.FRUITING_OAK_LEAVES.get(), "Fruiting Oak Leaves");
        add(ModBlocks.GIANT_CACTUS_BASE.get(), "Giant Cactus Base");
        add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get(), "Stripped Giant Cactus Base");
        add(ModBlocks.GIANT_CACTUS_STEM.get(), "Giant Cactus Stem");
        add(ModBlocks.POTTED_GIANT_CACTUS_STEM.get(), "Potted Giant Cactus Stem");
        add(ModBlocks.CACTUS_THORN.get(), "Cactus Thorn");
        add(ModBlocks.CACTUS_FLOWER.get(), "Cactus Flower");
        add(ModBlocks.GIANT_CACTUS_WOOD.get(), "Giant Cactus Wood");
        add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(), "Stripped Giant Cactus Wood");
        add(ModBlocks.CACTUS_PLANKS.get(), "Cactus Planks");
        add(ModBlocks.CACTUS_MOSAIC.get(), "Cactus Mosaic");
        add(ModBlocks.CACTUS_STAIRS.get(), "Cactus Stairs");
        add(ModBlocks.CACTUS_SLAB.get(), "Cactus Slab");
        add(ModBlocks.CACTUS_FENCE.get(), "Cactus Fence");
        add(ModBlocks.CACTUS_FENCE_GATE.get(), "Cactus Fence Gate");
        add(ModBlocks.CACTUS_BUTTON.get(), "Cactus Button");
        add(ModBlocks.CACTUS_PRESSURE_PLATE.get(), "Cactus Pressure Plate");
        add(ModBlocks.CACTUS_DOOR.get(), "Cactus Door");
        add(ModBlocks.CACTUS_TRAPDOOR.get(), "Cactus Trapdoor");
        add(ModBlocks.DESERT_MOSS.get(), "Desert Moss");
        add(ModBlocks.CACTUS_CLUSTER.get(), "Cactus Cluster");
        add(ModBlocks.OPUNTIA_CACTUS.get(), "Opuntia Cactus");

        //Items
        add(ModItems.PINE_CONE.get(), "Pine Cone");
        add(ModItems.PINE_NUTS.get(), "Pine Nuts");
        add(ModItems.TOASTED_PINE_NUTS.get(), "Toasted Pine Nuts");
        add(ModItems.TWIG.get(), "Twig");
        add(ModItems.FOREST_SNACK.get(), "Forest Snack");
        add(ModItems.FLAX_SEED.get(), "Flax Seed");
        add(ModItems.FLAX_FIBER.get(), "Flax Fiber");
        add(ModItems.FLAX_FLOWER.get(), "Flax Flower");
        add(ModItems.LINEN_THREAD.get(), "Linen Thread");
        add(ModItems.LINEN_CLOTH.get(), "Linen Cloth");
        add(ModItems.PEBBLES.get(), "Pebbles");
        add(ModItems.CHERRIES.get(), "Cherries");
        add(ModItems.SWEET_BERRY_MIX.get(), "Sweet Berry Mix");
        add(ModItems.CHERRY_JUICE.get(), "Cherry Juice");
        add(ModItems.APPLE_JUICE.get(), "Apple Juice");
        add(ModItems.CACTUS_SIGN.get(), "Cactus Sign");
        add(ModItems.CACTUS_HANGING_SIGN.get(), "Cactus Hanging Sign");
        add(ModItems.CACTUS_BOAT.get(), "Cactus Boat");
        add(ModItems.CACTUS_CHEST_BOAT.get(), "Cactus Chest Boat");
        add(ModItems.CACTUS_HELMET.get(), "Cactus Helmet");
        add(ModItems.CACTUS_CHESTPLATE.get(), "Cactus Chestplate");
        add(ModItems.CACTUS_LEGGINGS.get(), "Cactus Leggings");
        add(ModItems.CACTUS_BOOTS.get(), "Cactus Boots");
        add(ModItems.CACTUS_SLICE.get(), "Cactus Slice");
        add(ModItems.CACTUS_JUICE.get(), "Cactus Juice");
        add(ModItems.PRICKLY_PEAR.get(), "Prickly Pear");
    }
}

