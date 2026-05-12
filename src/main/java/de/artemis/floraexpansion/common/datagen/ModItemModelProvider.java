package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FloraExpansion.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.PINE_CONE.get());
        basicItem(ModItems.PINE_NUTS.get());
        basicItem(ModItems.TOASTED_PINE_NUTS.get());
        basicItem(ModItems.TWIG.get());
        basicItem(ModItems.FOREST_SNACK.get());
        basicItem(ModItems.FLAX_SEED.get());
        basicItem(ModItems.BLUEBERRIES.get());
        basicItem(ModItems.BLUEBERRY_COOKIE.get());
        basicItem(ModItems.BLUEBERRY_PIE.get());
        basicItem(ModItems.BLUEBERRY_PIE_SLICE.get());
        basicItem(ModItems.BLUEBERRY_JUICE.get());
        vanillaTextureItem(ModItems.BASKET.get(), "bundle");
        basicItem(ModItems.STRAWBERRY.get());
        basicItem(ModItems.STRAWBERRY_JAM.get());
        basicItem(ModBlocks.STRAWBERRY_CAKE.get().asItem());
        basicItem(ModItems.WOODEN_BUCKET.get());
        sharedTextureItem(ModItems.WOODEN_WATER_BUCKET.get(), "water_wooden_bucket");
        sharedTextureItem(ModItems.WOODEN_LAVA_BUCKET.get(), "lava_wooden_bucket");
        sharedTextureItem(ModItems.WOODEN_POWDER_SNOW_BUCKET.get(), "powder_snow_wooden_bucket");
        sharedTextureItem(ModItems.WOODEN_MILK_BUCKET.get(), "milk_wooden_bucket");
        basicItem(ModItems.COD_WOODEN_BUCKET.get());
        basicItem(ModItems.SALMON_WOODEN_BUCKET.get());
        basicItem(ModItems.PUFFERFISH_WOODEN_BUCKET.get());
        basicItem(ModItems.TROPICAL_FISH_WOODEN_BUCKET.get());
        basicItem(ModItems.AXOLOTL_WOODEN_BUCKET.get());
        basicItem(ModItems.TADPOLE_WOODEN_BUCKET.get());
        basicItem(ModItems.FLAX_FLOWER.get());
        basicItem(ModItems.FLAX_FIBER.get());
        basicItem(ModItems.LINEN_THREAD.get());
        basicItem(ModItems.LINEN_CLOTH.get());
        basicItem(ModItems.PEBBLES.get());
        basicItem(ModItems.CHERRIES.get());
        basicItem(ModItems.SWEET_BERRY_MIX.get());
        basicItem(ModItems.CHERRY_JUICE.get());
        basicItem(ModItems.APPLE_JUICE.get());
        basicItem(ModItems.CACTUS_SIGN.get());
        basicItem(ModItems.CACTUS_HANGING_SIGN.get());
        basicItem(ModItems.CACTUS_BOAT.get());
        basicItem(ModItems.CACTUS_CHEST_BOAT.get());
        basicItem(ModItems.CACTUS_SLICE.get());
        basicItem(ModItems.CACTUS_JUICE.get());

        basicItem(ModBlocks.CACTUS_DOOR.get().asItem());
        basicItem(ModBlocks.APPLE_CORE.get().asItem());
        basicItem(ModBlocks.CACTUS_THORN.get().asItem());
        basicItem(ModBlocks.CACTUS_CLUSTER.get().asItem());
        blockTextureItem(ModBlocks.LARGE_BLUEBERRY_BUSH, "large_blueberry_bush_stage3");

        blockTextureItem(ModBlocks.CACTUS_FLOWER, "giant_cactus_blossom_0");
    }

    private void blockTextureItem(DeferredBlock<? extends Block> block) {
        String name = block.getId().getPath();
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + name));
    }

    private void blockTextureItem(DeferredBlock<? extends Block> block, String textureName) {
        String name = block.getId().getPath();
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + textureName));
    }

    private void blockItem(DeferredBlock<? extends Block> block) {
        String name = block.getId().getPath();
        withExistingParent(name, modLoc("block/" + name));
    }

    private void sharedTextureItem(Item item, String textureName) {
        String name = item.builtInRegistryHolder().key().location().getPath();
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + textureName));
    }

    private void vanillaTextureItem(Item item, String textureName) {
        String name = item.builtInRegistryHolder().key().location().getPath();
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/" + textureName));
    }
}

