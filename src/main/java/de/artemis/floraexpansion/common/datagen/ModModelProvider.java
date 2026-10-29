package de.artemis.floraexpansion.common.datagen;

import com.google.gson.JsonObject;

import static de.artemis.floraexpansion.common.datagen.ModelJsonTemplates.*;import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.DesertMossBlock;
import de.artemis.floraexpansion.common.block.OrchardCherryLeavesBlock;
import de.artemis.floraexpansion.common.block.AppleLeavesBlock;
import de.artemis.floraexpansion.common.block.FlaxBlock;
import de.artemis.floraexpansion.common.block.GiantCactusShootBlock;
import de.artemis.floraexpansion.common.block.CactusWoodBlock;
import de.artemis.floraexpansion.common.block.LargeBlueberryBushBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.block.PricklyPearCactusBlock;
import de.artemis.floraexpansion.common.block.ScatteredPebblesBlock;
import de.artemis.floraexpansion.common.block.NeedleLitterBlock;
import de.artemis.floraexpansion.common.block.SmallBlueberryBushBlock;
import de.artemis.floraexpansion.common.block.StrawberryCropBlock;
import de.artemis.floraexpansion.common.block.StrawberryCakeBlock;
import de.artemis.floraexpansion.common.block.JamJarBlock;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.properties.numeric.CustomModelDataProperty;
import net.minecraft.client.renderer.item.properties.select.ItemBlockState;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

@SuppressWarnings({"deprecation", "unused"})
public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, FloraExpansion.MODID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        registerItemModels(blockModels, itemModels);
        registerSimpleBlockModels(blockModels);
        registerSpecialBlockModels(blockModels);
    }

    private void registerItemModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        registerCrateItemModel(blockModels);

        itemModels.generateFlatItem(ModBlocks.APPLE_CORE.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.SMALL_CACTUS.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.CACTUS_SPINE.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.CHERRY_PIT.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.NEEDLE_LITTER.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.SCATTERED_PEBBLES.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.STRAWBERRY_CAKE.get().asItem(), ModelTemplates.FLAT_ITEM);
        blockModels.registerSimpleFlatItemModel(ModBlocks.TWIG_LADDER.get());
        blockModels.registerSimpleItemModel(ModBlocks.ORCHARD_CHERRY_LEAVES.get(), modModel("orchard_cherry_leaves_stage3"));
        blockModels.itemModelOutput.accept(ModBlocks.APPLE_LEAVES.get().asItem(),
                ItemModelUtils.tintedModel(modItemModel("apple_leaves"),
                        ItemModelUtils.constantTint(FoliageColor.FOLIAGE_DEFAULT)));
        registerGeneratedItemWithTexture(blockModels, itemModels, ModBlocks.LARGE_BLUEBERRY_BUSH.get().asItem(), "floraexpansion:block/large_blueberry_bush_stage3");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModBlocks.DESERT_MOSS.get().asItem(), "floraexpansion:block/desert_moss_2");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModBlocks.FLOWERING_DESERT_MOSS.get().asItem(), "floraexpansion:item/flowering_desert_moss");

        getFlatItems().forEach(item -> itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM));
        registerJamItemModels(blockModels, itemModels);
        registerExistingItemModel(blockModels, ModItems.GLASS_JAR.get(), "glass_jar");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.BASKET.get(), "minecraft:item/bundle");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.WOODEN_WATER_BUCKET.get(), "floraexpansion:item/water_wooden_bucket");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.WOODEN_LAVA_BUCKET.get(), "floraexpansion:item/lava_wooden_bucket");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.WOODEN_POWDER_SNOW_BUCKET.get(), "floraexpansion:item/powder_snow_wooden_bucket");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.WOODEN_MILK_BUCKET.get(), "floraexpansion:item/milk_wooden_bucket");
    }

    private void registerSimpleBlockModels(BlockModelGenerators blockModels) {
        createJarModels(blockModels);
        createAppleCoreModels(blockModels);
        createBlueberryBushModels(blockModels);
        createCactusWoodFamilyModels(blockModels);
        createCherryPitModels(blockModels);
        createCrateModels(blockModels);
        createDesertMossModels(blockModels);
        createFlaxBaleModels(blockModels);
        createOrchardCherryLeavesModels(blockModels);
        createAppleLeavesModels(blockModels);
        createCactusLogModels(blockModels);
        createGiantCactusShootModels(blockModels);
        createGiantCactusStemModels(blockModels);
        createCactusWoodModels(blockModels);
        createPricklyPearCactusModels(blockModels);
        createPackedPebblesModels(blockModels);
        createStrawberryCakeModels(blockModels);
        createStrawberryCropModels(blockModels);
        blockModels.createFullAndCarpetBlocks(ModBlocks.LINEN_BALE.get(), ModBlocks.LINEN_CARPET.get());
        createTwigLadderBlockModel(blockModels);
        blockModels.createNonTemplateHorizontalBlock(ModBlocks.TWIG_LADDER.get());
    }

    private void registerSpecialBlockModels(BlockModelGenerators blockModels) {
        createBlueberryBushBlockState(blockModels);
        createSmallCactusBlockState(blockModels);
        createCactusSpineModels(blockModels);
        createCrateBlockState(blockModels);
        createDesertMossBlockState(blockModels);
        createFlaxBlockState(blockModels);
        createOrchardCherryLeavesBlockState(blockModels);
        createAppleLeavesBlockState(blockModels);
        createGiantCactusShootBlockState(blockModels);
        createCactusWoodBlockState(blockModels, ModBlocks.CACTUS_WOOD.get(), "cactus_wood");
        createCactusWoodBlockState(blockModels, ModBlocks.STRIPPED_CACTUS_WOOD.get(), "stripped_cactus_wood");
        createPricklyPearCactusBlockState(blockModels);
        createScatteredPebblesBlockState(blockModels);
        createNeedleLitterBlockState(blockModels);
        createStrawberryCakeBlockState(blockModels);
        createStrawberryCropBlockState(blockModels);
    }

    private void createJarModels(BlockModelGenerators blockModels) {
        for (Block jar : new Block[]{ModBlocks.GLASS_JAR.get(), ModBlocks.BLUEBERRY_JAM.get(),
                ModBlocks.STRAWBERRY_JAM.get(), ModBlocks.CHERRY_JAM.get(), ModBlocks.APPLE_JAM.get()}) {
            String name = jar.builtInRegistryHolder().key().identifier().getPath();
            if (jar instanceof JamJarBlock) {
                var variants = PropertyDispatch.initial(JamJarBlock.PORTIONS);
                for (int portions = 1; portions <= JamJarBlock.MAX_PORTIONS; portions++) {
                    int amount = portions;
                    Identifier model = modModel(name + (portions == JamJarBlock.MAX_PORTIONS ? "" : "_" + portions));
                    blockModels.modelOutput.accept(model, () -> createJarModelJson(name, amount));
                    variants.select(portions, BlockModelGenerators.plainVariant(model));
                }
                blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(jar).with(variants));
            } else {
                Identifier model = modModel(name);
                blockModels.modelOutput.accept(model, () -> createJarModelJson(null, 0));
                blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(jar, BlockModelGenerators.plainVariant(model)));
            }
        }
    }

    private void registerCrateItemModel(BlockModelGenerators blockModels) {
        blockModels.itemModelOutput.accept(ModBlocks.CRATE.get().asItem(),
                ItemModelUtils.rangeSelect(
                        new CustomModelDataProperty(0),
                        ItemModelUtils.plainModel(modItemModel("crate")),
                        ItemModelUtils.override(ItemModelUtils.plainModel(modItemModel("crate_packed")), 1.0F)
                ));
    }

    private void registerJamItemModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        for (Item jam : new Item[]{ModItems.BLUEBERRY_JAM.get(), ModItems.STRAWBERRY_JAM.get(),
                ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get()}) {
            String name = jam.builtInRegistryHolder().key().identifier().getPath();
            Identifier full = modItemModel(name);
            if (jam != ModItems.BLUEBERRY_JAM.get()) {
                itemModels.modelOutput.accept(full, () -> createGeneratedItemModelJson("floraexpansion:item/" + name));
            }
            var cases = new java.util.ArrayList<SelectItemModel.SwitchCase<String>>();
            for (int portions = 1; portions < JamJarBlock.MAX_PORTIONS; portions++) {
                String variant = name + "_portion_" + portions;
                Identifier model = modItemModel(variant);
                itemModels.modelOutput.accept(model, () -> createGeneratedItemModelJson("floraexpansion:item/" + variant));
                cases.add(ItemModelUtils.when(Integer.toString(portions), ItemModelUtils.plainModel(model)));
            }
            blockModels.itemModelOutput.accept(jam, ItemModelUtils.select(new ItemBlockState(JamJarBlock.PORTIONS.getName()),
                    ItemModelUtils.plainModel(full), cases));
        }
    }

    private void registerExistingItemModel(BlockModelGenerators blockModels, Item item, String modelName) {
        blockModels.registerSimpleItemModel(item, modItemModel(modelName));
    }

    private void registerGeneratedItemWithTexture(BlockModelGenerators blockModels, ItemModelGenerators itemModels, Item item, String texture) {
        Identifier model = modItemModel(item.builtInRegistryHolder().key().identifier().getPath());
        itemModels.modelOutput.accept(model, () -> createGeneratedItemModelJson(texture));
        blockModels.registerSimpleItemModel(item, model);
    }

    private void createBlueberryBushBlockState(BlockModelGenerators blockModels) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.BLUEBERRY_BUSH.get())
                        .with(PropertyDispatch.initial(SmallBlueberryBushBlock.AGE)
                                .generate(age -> BlockModelGenerators.plainVariant(modModel("small_blueberry_bush_stage" + age))))
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.LARGE_BLUEBERRY_BUSH.get())
                        .with(PropertyDispatch.initial(LargeBlueberryBushBlock.AGE)
                                .generate(age -> BlockModelGenerators.plainVariant(modModel("large_blueberry_bush_stage" + age))))
        );
    }

    private void createCrateBlockState(BlockModelGenerators blockModels) {
        MultiVariant open = BlockModelGenerators.plainVariant(modModel("crate"));
        MultiVariant closed = BlockModelGenerators.plainVariant(mcBlock("barrel"));
        MultiVariant packed = BlockModelGenerators.plainVariant(modModel("crate_packed"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.CRATE.get())
                        .with(PropertyDispatch.initial(CrateBlock.PACKED, CrateBlock.POWERED)
                                .select(false, false, open)
                                .select(false, true, closed)
                                .select(true, false, packed)
                                .select(true, true, packed))
        );
    }

    private void createSmallCactusBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.SMALL_CACTUS.get());

        for (int pickles = 1; pickles <= 4; pickles++) {
            generator.with(
                    BlockModelGenerators.condition().term(de.artemis.floraexpansion.common.block.SmallCactusBlock.PICKLES, pickles),
                    BlockModelGenerators.plainVariant(modModel("small_cactus_" + pickles))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createDesertMossBlockState(BlockModelGenerators blockModels) {
        for (Block moss : java.util.List.of(ModBlocks.DESERT_MOSS.get(), ModBlocks.FLOWERING_DESERT_MOSS.get())) {
            MultiPartGenerator generator = MultiPartGenerator.multiPart(moss);
            String name = moss.builtInRegistryHolder().key().identifier().getPath();
            for (int variant = 0; variant <= 3; variant++) {
                generator.with(
                        BlockModelGenerators.condition().term(DesertMossBlock.VARIANT, variant),
                        BlockModelGenerators.plainVariant(modModel(name + "_" + variant))
                );
            }
            blockModels.blockStateOutput.accept(generator);
        }
    }

    private void createFlaxBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.FLAX.get());

        for (int age = 0; age <= FlaxBlock.AGE.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(4); age++) {
            generator.with(
                    BlockModelGenerators.condition()
                            .term(FlaxBlock.AGE, age)
                            .term(FlaxBlock.HALF, DoubleBlockHalf.LOWER),
                    BlockModelGenerators.plainVariant(modModel(getFlaxLowerModel(age)))
            );
            generator.with(
                    BlockModelGenerators.condition()
                            .term(FlaxBlock.AGE, age)
                            .term(FlaxBlock.HALF, DoubleBlockHalf.UPPER),
                    BlockModelGenerators.plainVariant(modModel(getFlaxUpperModel(age)))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createCactusSpineModels(BlockModelGenerators blockModels) {
        Identifier model = modModel("cactus_spine");

        blockModels.modelOutput.accept(model, () -> createCutoutCrossModelJson("floraexpansion:block/cactus_spine"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.CACTUS_SPINE.get(), BlockModelGenerators.plainVariant(model))
                        .with(PropertyDispatch.modify(de.artemis.floraexpansion.common.block.CactusSpineBlock.FACE)
                                .select(AttachFace.FLOOR, BlockModelGenerators.NOP)
                                .select(AttachFace.CEILING, BlockModelGenerators.X_ROT_180)
                                .select(AttachFace.WALL, BlockModelGenerators.X_ROT_90))
                        .with(PropertyDispatch.modify(de.artemis.floraexpansion.common.block.CactusSpineBlock.FACING)
                                .select(Direction.NORTH, BlockModelGenerators.NOP)
                                .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
                                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                                .select(Direction.WEST, BlockModelGenerators.Y_ROT_270))
        );
    }

    private void createNeedleLitterBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.NEEDLE_LITTER.get());

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (int segment = NeedleLitterBlock.MIN_SEGMENTS; segment <= NeedleLitterBlock.MAX_SEGMENTS; segment++) {
                for (int amount = segment; amount <= NeedleLitterBlock.MAX_SEGMENTS; amount++) {
                    generator.with(
                            BlockModelGenerators.condition()
                                    .term(NeedleLitterBlock.FACING, facing)
                                    .term(NeedleLitterBlock.AMOUNT, amount),
                            createNeedleLitterVariant(segment, facing)
                    );
                }
            }
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private MultiVariant createNeedleLitterVariant(int segment, Direction facing) {
        MultiVariant variant = BlockModelGenerators.plainVariant(modModel("needle_litter_" + segment));

        return switch (facing) {
            case NORTH -> variant;
            case EAST -> variant.with(BlockModelGenerators.Y_ROT_90);
            case SOUTH -> variant.with(BlockModelGenerators.Y_ROT_180);
            case WEST -> variant.with(BlockModelGenerators.Y_ROT_270);
            default -> throw new IllegalStateException("Unexpected horizontal direction: " + facing);
        };
    }

    private static String getFlaxLowerModel(int age) {
        return switch (age) {
            case 0 -> "flax_stage0";
            case 1 -> "flax_stage1_bottom";
            case 2 -> "flax_stage2_bottom";
            case 3 -> "flax_stage3_bottom";
            case 4 -> "flax_stage4_bottom";
            default -> throw new IllegalStateException("Unexpected flax age: " + age);
        };
    }

    private static String getFlaxUpperModel(int age) {
        return switch (age) {
            case 0, 1 -> "flax_stage1_top";
            case 2 -> "flax_stage2_top";
            case 3 -> "flax_stage3_top";
            case 4 -> "flax_stage4_top";
            default -> throw new IllegalStateException("Unexpected flax age: " + age);
        };
    }

    private void createScatteredPebblesBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.SCATTERED_PEBBLES.get());

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (int amount = 1; amount <= 5; amount++) {
                generator.with(
                        BlockModelGenerators.condition()
                                .term(ScatteredPebblesBlock.FACING, facing)
                                .term(ScatteredPebblesBlock.AMOUNT, amount),
                        createScatteredPebblesVariant(amount, facing)
                );
            }
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createOrchardCherryLeavesBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.ORCHARD_CHERRY_LEAVES.get());

        for (int age = 0; age <= OrchardCherryLeavesBlock.MAX_AGE; age++) {
            generator.with(
                    BlockModelGenerators.condition().term(OrchardCherryLeavesBlock.AGE, age),
                    BlockModelGenerators.plainVariant(modModel("orchard_cherry_leaves_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createAppleLeavesBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.APPLE_LEAVES.get());

        for (int age = 0; age <= AppleLeavesBlock.MAX_AGE; age++) {
            generator.with(
                    BlockModelGenerators.condition().term(AppleLeavesBlock.AGE, age),
                    BlockModelGenerators.plainVariant(modModel("apple_leaves_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createPricklyPearCactusBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.PRICKLY_PEAR_CACTUS.get());

        for (int age = 0; age <= PricklyPearCactusBlock.MAX_AGE; age++) {
            generator.with(
                    BlockModelGenerators.condition().term(PricklyPearCactusBlock.AGE, age),
                    BlockModelGenerators.plainVariant(modModel("prickly_pear_cactus_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createGiantCactusShootBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.GIANT_CACTUS_SHOOT.get());
        String[] textures = {"giant_cactus_shoot_0", "giant_cactus_shoot_1", "giant_cactus_shoot_3"};

        for (int variant = 0; variant < textures.length; variant++) {
            generator.with(
                    BlockModelGenerators.condition().term(GiantCactusShootBlock.VARIANT, variant),
                    BlockModelGenerators.plainVariant(modModel("giant_cactus_shoot_variant_" + variant))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createCactusWoodBlockState(BlockModelGenerators blockModels, Block block, String modelName) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

        for (boolean generated : new boolean[]{false, true}) {
            generator.with(
                    BlockModelGenerators.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                            .term(CactusWoodBlock.GENERATED, generated),
                    BlockModelGenerators.plainVariant(modModel(modelName))
            );
            generator.with(
                    BlockModelGenerators.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.X)
                            .term(CactusWoodBlock.GENERATED, generated),
                    BlockModelGenerators.plainVariant(modModel(modelName + "_horizontal"))
                            .with(BlockModelGenerators.X_ROT_90)
                            .with(BlockModelGenerators.Y_ROT_90)
            );
            generator.with(
                    BlockModelGenerators.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                            .term(CactusWoodBlock.GENERATED, generated),
                    BlockModelGenerators.plainVariant(modModel(modelName + "_horizontal"))
                            .with(BlockModelGenerators.X_ROT_90)
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private MultiVariant createScatteredPebblesVariant(int amount, Direction facing) {
        MultiVariant variant = BlockModelGenerators.plainVariant(modModel("scattered_pebbles_" + (amount - 1)));

        return switch (facing) {
            case NORTH -> variant;
            case EAST -> variant.with(BlockModelGenerators.Y_ROT_90);
            case SOUTH -> variant.with(BlockModelGenerators.Y_ROT_180);
            case WEST -> variant.with(BlockModelGenerators.Y_ROT_270);
            default -> throw new IllegalStateException("Unexpected horizontal direction: " + facing);
        };
    }

    private void createTwigLadderBlockModel(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("twig_ladder"), () -> {
            JsonObject model = new JsonObject();
            JsonObject textures = new JsonObject();
            model.addProperty("parent", "minecraft:block/ladder");
            model.addProperty("render_type", "cutout");

            textures.addProperty("particle", "floraexpansion:block/twig_ladder");
            textures.addProperty("texture", "floraexpansion:block/twig_ladder");
            model.add("textures", textures);
            return model;
        });
    }

    private void createCherryPitModels(BlockModelGenerators blockModels) {
        Identifier cherryPitModel = modModel("cherry_pit");
        Identifier pottedCherryPitModel = modModel("potted_cherry_pit");

        blockModels.modelOutput.accept(cherryPitModel, () -> createCutoutCrossModelJson("floraexpansion:block/planted_cherry_pit"));
        blockModels.modelOutput.accept(pottedCherryPitModel, () -> createCutoutPottedPlantModelJson("floraexpansion:block/planted_cherry_pit"));

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.CHERRY_PIT.get(), BlockModelGenerators.plainVariant(cherryPitModel))
        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.POTTED_CHERRY_PIT.get(), BlockModelGenerators.plainVariant(pottedCherryPitModel))
        );
    }

    private void createBlueberryBushModels(BlockModelGenerators blockModels) {
        for (int age = 0; age <= 3; age++) {
            String modelName = "small_blueberry_bush_stage" + age;
            String textureName = "small_blueberry_bush_stage" + age;
            blockModels.modelOutput.accept(modModel(modelName), () ->
                    createCutoutCrossModelJson("floraexpansion:block/" + textureName));
        }
    }

    private void createCrateModels(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("crate"), () -> createComposterCrateModelJson(
                "minecraft:block/barrel_bottom",
                "minecraft:block/barrel_top",
                "minecraft:block/barrel_side"
        ));
    }

    private void createCactusWoodFamilyModels(BlockModelGenerators blockModels) {
        Identifier cactusPlanksModel = ModelTemplates.CUBE_ALL.create(
                ModBlocks.CACTUS_PLANKS.get(),
                TextureMapping.cube(modBlockMaterial("cactus_planks")),
                blockModels.modelOutput
        );
        Identifier cactusMosaicModel = ModelTemplates.CUBE_ALL.create(
                ModBlocks.CACTUS_MOSAIC.get(),
                TextureMapping.cube(modBlockMaterial("cactus_mosaic")),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.CACTUS_PLANKS.get(), BlockModelGenerators.plainVariant(cactusPlanksModel))
        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.CACTUS_MOSAIC.get(), BlockModelGenerators.plainVariant(cactusMosaicModel))
        );
        blockModels.registerSimpleItemModel(ModBlocks.CACTUS_PLANKS.get(), cactusPlanksModel);
        blockModels.registerSimpleItemModel(ModBlocks.CACTUS_MOSAIC.get(), cactusMosaicModel);

        BlockFamily family = new BlockFamily.Builder(ModBlocks.CACTUS_PLANKS.get())
                .stairs(ModBlocks.CACTUS_STAIRS.get())
                .slab(ModBlocks.CACTUS_SLAB.get())
                .fence(ModBlocks.CACTUS_FENCE.get())
                .fenceGate(ModBlocks.CACTUS_FENCE_GATE.get())
                .button(ModBlocks.CACTUS_BUTTON.get())
                .pressurePlate(ModBlocks.CACTUS_PRESSURE_PLATE.get())
                .getFamily();
        blockModels.familyWithExistingFullBlock(ModBlocks.CACTUS_PLANKS.get()).generateFor(family);
        BlockFamily mosaicFamily = new BlockFamily.Builder(ModBlocks.CACTUS_MOSAIC.get())
                .stairs(ModBlocks.CACTUS_MOSAIC_STAIRS.get()).slab(ModBlocks.CACTUS_MOSAIC_SLAB.get()).getFamily();
        blockModels.familyWithExistingFullBlock(ModBlocks.CACTUS_MOSAIC.get()).generateFor(mosaicFamily);
        createCactusDoorModels(blockModels);
        createCactusTrapdoorModels(blockModels);

        createCactusSignModels(blockModels);
    }

    private void createCactusSignModels(BlockModelGenerators blockModels) {
        TextureMapping standing = new TextureMapping().put(TextureSlot.ALL, modBlockMaterial("cactus_sign"))
                .put(TextureSlot.PARTICLE, modBlockMaterial("cactus_planks"));
        TextureMapping hanging = new TextureMapping().put(TextureSlot.ALL, modBlockMaterial("cactus_hanging_sign"))
                .put(TextureSlot.PARTICLE, modBlockMaterial("stripped_cactus_log_side"));
        var standingTemplates = java.util.List.of(ModelTemplates.SIGN_ROT_0, ModelTemplates.SIGN_ROT_1,
                ModelTemplates.SIGN_ROT_2, ModelTemplates.SIGN_ROT_3);
        var hangingTemplates = java.util.List.of(ModelTemplates.HANGING_SIGN_ROT_0, ModelTemplates.HANGING_SIGN_ROT_1,
                ModelTemplates.HANGING_SIGN_ROT_2, ModelTemplates.HANGING_SIGN_ROT_3);
        var attachedTemplates = java.util.List.of(ModelTemplates.ATTACHED_HANGING_SIGN_ROT_0, ModelTemplates.ATTACHED_HANGING_SIGN_ROT_1,
                ModelTemplates.ATTACHED_HANGING_SIGN_ROT_2, ModelTemplates.ATTACHED_HANGING_SIGN_ROT_3);
        MultiVariant[] signs = new MultiVariant[4], hangingSigns = new MultiVariant[4], attachedSigns = new MultiVariant[4];
        for (int rotation = 0; rotation < 4; rotation++) {
            signs[rotation] = BlockModelGenerators.plainVariant(standingTemplates.get(rotation)
                    .create(modModel("cactus_sign_rot_" + rotation), standing, blockModels.modelOutput));
            hangingSigns[rotation] = BlockModelGenerators.plainVariant(hangingTemplates.get(rotation)
                    .create(modModel("cactus_hanging_sign_rot_" + rotation), hanging, blockModels.modelOutput));
            attachedSigns[rotation] = BlockModelGenerators.plainVariant(attachedTemplates.get(rotation)
                    .create(modModel("cactus_hanging_sign_attached_rot_" + rotation), hanging, blockModels.modelOutput));
        }
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSign(ModBlocks.CACTUS_SIGN.get(),
                signs[0], signs[1], signs[2], signs[3]));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createHangingSign(ModBlocks.CACTUS_HANGING_SIGN.get(),
                hangingSigns[0], hangingSigns[1], hangingSigns[2], hangingSigns[3],
                attachedSigns[0], attachedSigns[1], attachedSigns[2], attachedSigns[3]));
        var wallSign = BlockModelGenerators.plainVariant(ModelTemplates.WALL_SIGN.create(
                modModel("cactus_wall_sign"), standing, blockModels.modelOutput));
        var wallHangingSign = BlockModelGenerators.plainVariant(ModelTemplates.WALL_HANGING_SIGN.create(
                modModel("cactus_wall_hanging_sign"), hanging, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.CACTUS_WALL_SIGN.get(), wallSign)
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.CACTUS_WALL_HANGING_SIGN.get(), wallHangingSign)
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));
    }

    private void createAppleCoreModels(BlockModelGenerators blockModels) {
        Identifier appleCoreModel = modModel("apple_core");
        Identifier pottedAppleCoreModel = modModel("potted_apple_core");

        blockModels.modelOutput.accept(appleCoreModel, () -> createCutoutCrossModelJson("floraexpansion:block/planted_apple_core"));
        blockModels.modelOutput.accept(pottedAppleCoreModel, () -> createCutoutPottedPlantModelJson("floraexpansion:block/planted_apple_core"));

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.APPLE_CORE.get(), BlockModelGenerators.plainVariant(appleCoreModel))
        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.POTTED_APPLE_CORE.get(), BlockModelGenerators.plainVariant(pottedAppleCoreModel))
        );
    }

    private void createDesertMossModels(BlockModelGenerators blockModels) {
        for (int variant = 0; variant <= 3; variant++) {
            String modelName = "desert_moss_" + variant;
            String texture = "floraexpansion:block/desert_moss_" + variant;
            blockModels.modelOutput.accept(modModel(modelName), () -> createGroundPlaneModelJson(texture));
            int flowerVariant = variant;
            blockModels.modelOutput.accept(modModel("flowering_" + modelName),
                    () -> createFloweringDesertMossModelJson(texture, flowerVariant));
        }
    }

    private void createOrchardCherryLeavesModels(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("orchard_cherry_leaves_stage0"), () ->
                createLeavesModelJson("floraexpansion:block/orchard_cherry_leaves_stage0"));
        for (int age = 1; age <= OrchardCherryLeavesBlock.MAX_AGE; age++) {
            String stageTexture = "floraexpansion:block/orchard_cherry_leaves_stage" + age;
            blockModels.modelOutput.accept(modModel("orchard_cherry_leaves_stage" + age), () ->
                    createLeavesModelJson(stageTexture));
        }
    }

    private void createAppleLeavesModels(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("apple_leaves_stage0"), () ->
                createOakLeavesModelJson());
        blockModels.modelOutput.accept(modModel("apple_leaves_stage1"), () ->
                createOakLeavesWithSideOverlayModelJson("floraexpansion:block/apple_leaves_stage1_overlay"));
        blockModels.modelOutput.accept(modModel("apple_leaves_stage2"), () ->
                createOakLeavesWithSideOverlayModelJson("floraexpansion:block/apple_leaves_stage2_overlay"));
    }

    private void createCactusLogModels(BlockModelGenerators blockModels) {
        createRotatedPillarModels(
                blockModels,
                ModBlocks.CACTUS_LOG.get(),
                "cactus_log",
                "cactus_log_horizontal",
                "floraexpansion:block/cactus_log_top",
                "floraexpansion:block/cactus_log_side"
        );
        createRotatedPillarModels(
                blockModels,
                ModBlocks.STRIPPED_CACTUS_LOG.get(),
                "stripped_cactus_log",
                "stripped_cactus_log_horizontal",
                "floraexpansion:block/stripped_cactus_log_top",
                "floraexpansion:block/stripped_cactus_log_side"
        );
    }

    private void createGiantCactusStemModels(BlockModelGenerators blockModels) {
        Identifier giantCactusStemModel = modModel("giant_cactus_stem");
        Identifier pottedGiantCactusStemModel = modModel("potted_giant_cactus_stem");

        blockModels.modelOutput.accept(giantCactusStemModel, () -> createInsetCactusModelJson(
                "floraexpansion:block/giant_cactus_stem_bottom",
                "floraexpansion:block/giant_cactus_stem_top",
                "floraexpansion:block/giant_cactus_stem_side"
        ));
        blockModels.modelOutput.accept(pottedGiantCactusStemModel, () -> createPottedCactusModelJson(
                "floraexpansion:block/giant_cactus_stem_side",
                "floraexpansion:block/giant_cactus_stem_top"
        ));

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.GIANT_CACTUS_STEM.get(), BlockModelGenerators.plainVariant(giantCactusStemModel))
        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.POTTED_GIANT_CACTUS_STEM.get(), BlockModelGenerators.plainVariant(pottedGiantCactusStemModel))
        );
        blockModels.registerSimpleItemModel(ModBlocks.GIANT_CACTUS_STEM.get(), giantCactusStemModel);
    }

    private void createGiantCactusShootModels(BlockModelGenerators blockModels) {
        Identifier giantCactusShootItemModel = modModel("giant_cactus_shoot_item");
        String[] textures = {"giant_cactus_shoot_0", "giant_cactus_shoot_1", "giant_cactus_shoot_3"};

        for (int variant = 0; variant < textures.length; variant++) {
            String modelName = "giant_cactus_shoot_variant_" + variant;
            String texture = "floraexpansion:block/" + textures[variant];
            blockModels.modelOutput.accept(
                    modModel(modelName),
                    () -> createCutoutCrossModelJson(texture)
            );
        }
        blockModels.modelOutput.accept(giantCactusShootItemModel, () ->
                createGeneratedItemModelJson("floraexpansion:block/giant_cactus_shoot_0"));
        blockModels.registerSimpleItemModel(ModBlocks.GIANT_CACTUS_SHOOT.get(), giantCactusShootItemModel);
    }

    private void createCactusWoodModels(BlockModelGenerators blockModels) {
        createWoodPillarModels(
                blockModels,
                ModBlocks.CACTUS_WOOD.get(),
                "cactus_wood",
                "cactus_wood_horizontal",
                "floraexpansion:block/cactus_log_side"
        );
        createWoodPillarModels(
                blockModels,
                ModBlocks.STRIPPED_CACTUS_WOOD.get(),
                "stripped_cactus_wood",
                "stripped_cactus_wood_horizontal",
                "floraexpansion:block/stripped_cactus_log_side"
        );
    }

    private void createPricklyPearCactusModels(BlockModelGenerators blockModels) {
        for (int age = 0; age <= PricklyPearCactusBlock.MAX_AGE; age++) {
            String texture = "prickly_pear_cactus_stage" + age;
            blockModels.modelOutput.accept(modModel(texture), () ->
                    createCutoutCrossModelJson("floraexpansion:block/" + texture));
        }
    }

    private void createStrawberryCakeModels(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("strawberry_cake"), () -> createCakeModelJson("minecraft:block/cake"));
        for (int bites = 1; bites <= 6; bites++) {
            int slice = bites;
            blockModels.modelOutput.accept(modModel("strawberry_cake_slice" + bites), () -> createCakeModelJson("minecraft:block/cake_slice" + slice));
        }
        for (int bites = 0; bites <= 6; bites++) {
            int slice = bites;
            blockModels.modelOutput.accept(modModel("strawberry_cake_slice" + bites + "_half"),
                    () -> createHalfSliceCakeModelJson(slice));
        }
    }

    private void createStrawberryCropModels(BlockModelGenerators blockModels) {
        for (int age = 0; age <= StrawberryCropBlock.AGE.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(3); age++) {
            String texture = "strawberry_plant_stage" + age;
            blockModels.modelOutput.accept(modModel(texture), () ->
                    createCutoutCrossModelJson("floraexpansion:block/" + texture));
        }
    }

    private void createStrawberryCakeBlockState(BlockModelGenerators blockModels) {
        var variants = PropertyDispatch.initial(BlockStateProperties.BITES, StrawberryCakeBlock.HALF_SLICE);
        for (int bites = 0; bites <= 6; bites++) {
            String whole = bites == 0 ? "strawberry_cake" : "strawberry_cake_slice" + bites;
            variants.select(bites, false, BlockModelGenerators.plainVariant(modModel(whole)));
            variants.select(bites, true, BlockModelGenerators.plainVariant(modModel("strawberry_cake_slice" + bites + "_half")));
        }
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.STRAWBERRY_CAKE.get()).with(variants));

        ModBlocks.STRAWBERRY_CANDLE_CAKES.forEach(cake -> createStrawberryCakeWithCandleBlockState(blockModels, cake.get()));
    }

    private void createStrawberryCakeWithCandleBlockState(BlockModelGenerators blockModels, Block candleCake) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(candleCake,
                BlockModelGenerators.plainVariant(modModel("strawberry_cake"))));
    }

    private void createStrawberryCropBlockState(BlockModelGenerators blockModels) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.STRAWBERRY_PLANT.get())
                        .with(PropertyDispatch.initial(StrawberryCropBlock.AGE, StrawberryCropBlock.WILD)
                                .generate((age, wild) -> BlockModelGenerators.plainVariant(modModel("strawberry_plant_stage" + age))))
        );
    }

    private void createFlaxBaleModels(BlockModelGenerators blockModels) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.END, modBlockMaterial("flax_bale_top"))
                .put(TextureSlot.SIDE, modBlockMaterial("flax_bale_side"));

        Identifier verticalModel = ModelTemplates.CUBE_COLUMN.create(ModBlocks.FLAX_BALE.get(), textures, blockModels.modelOutput);
        Identifier horizontalModel = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(ModBlocks.FLAX_BALE.get(), textures, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
                        ModBlocks.FLAX_BALE.get(),
                        BlockModelGenerators.plainVariant(verticalModel),
                        BlockModelGenerators.plainVariant(horizontalModel)
                )
        );
        blockModels.registerSimpleItemModel(ModBlocks.FLAX_BALE.get(), verticalModel);
    }

    private void createRotatedPillarModels(BlockModelGenerators blockModels,
                                           Block block,
                                           String verticalModelName,
                                           String horizontalModelName,
                                           String endTexture,
                                           String sideTexture) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.END, new Material(Identifier.parse(endTexture)))
                .put(TextureSlot.SIDE, new Material(Identifier.parse(sideTexture)));

        Identifier verticalModel = ModelTemplates.CUBE_COLUMN.create(
                modModel(verticalModelName),
                textures,
                blockModels.modelOutput
        );
        Identifier horizontalModel = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
                modModel(horizontalModelName),
                textures,
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
                        block,
                        BlockModelGenerators.plainVariant(verticalModel),
                        BlockModelGenerators.plainVariant(horizontalModel)
                )
        );
        blockModels.registerSimpleItemModel(block, verticalModel);
    }

    private void createWoodPillarModels(BlockModelGenerators blockModels,
                                        Block block,
                                        String verticalModelName,
                                        String horizontalModelName,
                                        String texture) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.END, new Material(Identifier.parse(texture)))
                .put(TextureSlot.SIDE, new Material(Identifier.parse(texture)));

        Identifier verticalModel = ModelTemplates.CUBE_COLUMN.create(
                modModel(verticalModelName),
                textures,
                blockModels.modelOutput
        );
        ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
                modModel(horizontalModelName),
                textures,
                blockModels.modelOutput
        );

        blockModels.registerSimpleItemModel(block, verticalModel);
    }

    private void createPackedPebblesModels(BlockModelGenerators blockModels) {
        Identifier model = ModelTemplates.CUBE_ALL.create(
                ModBlocks.PACKED_PEBBLES.get(),
                new TextureMapping().put(TextureSlot.ALL, modBlockMaterial("packed_pebbles")),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.PACKED_PEBBLES.get(), BlockModelGenerators.plainVariant(model)));
        blockModels.registerSimpleItemModel(ModBlocks.PACKED_PEBBLES.get(), model);
    }

    private void createCactusDoorModels(BlockModelGenerators blockModels) {
        MultiVariant bottomLeft = createCactusDoorModel(blockModels, "bottom_left");
        MultiVariant bottomLeftOpen = createCactusDoorModel(blockModels, "bottom_left_open");
        MultiVariant bottomRight = createCactusDoorModel(blockModels, "bottom_right");
        MultiVariant bottomRightOpen = createCactusDoorModel(blockModels, "bottom_right_open");
        MultiVariant topLeft = createCactusDoorModel(blockModels, "top_left");
        MultiVariant topLeftOpen = createCactusDoorModel(blockModels, "top_left_open");
        MultiVariant topRight = createCactusDoorModel(blockModels, "top_right");
        MultiVariant topRightOpen = createCactusDoorModel(blockModels, "top_right_open");

        blockModels.blockStateOutput.accept(BlockModelGenerators.createDoor(
                ModBlocks.CACTUS_DOOR.get(),
                bottomLeft,
                bottomLeftOpen,
                bottomRight,
                bottomRightOpen,
                topLeft,
                topLeftOpen,
                topRight,
                topRightOpen
        ));
        blockModels.registerSimpleFlatItemModel(ModBlocks.CACTUS_DOOR.get().asItem());
    }

    private void createCactusTrapdoorModels(BlockModelGenerators blockModels) {
        Identifier bottomModel = createCactusTrapdoorModel(blockModels, "bottom", "minecraft:block/template_orientable_trapdoor_bottom");
        MultiVariant bottom = BlockModelGenerators.plainVariant(bottomModel);
        MultiVariant top = BlockModelGenerators.plainVariant(createCactusTrapdoorModel(blockModels, "top", "minecraft:block/template_orientable_trapdoor_top"));
        MultiVariant open = BlockModelGenerators.plainVariant(createCactusTrapdoorModel(blockModels, "open", "minecraft:block/template_orientable_trapdoor_open"));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createOrientableTrapdoor(ModBlocks.CACTUS_TRAPDOOR.get(), top, bottom, open));
        blockModels.registerSimpleItemModel(ModBlocks.CACTUS_TRAPDOOR.get(), bottomModel);
    }

    private MultiVariant createCactusDoorModel(BlockModelGenerators blockModels, String variant) {
        Identifier model = modModel("cactus_door_" + variant);
        blockModels.modelOutput.accept(model, () -> createDoorModelJson(
                "minecraft:block/door_" + variant,
                "floraexpansion:block/cactus_door_bottom",
                "floraexpansion:block/cactus_door_top"
        ));
        return BlockModelGenerators.plainVariant(model);
    }

    private Identifier createCactusTrapdoorModel(BlockModelGenerators blockModels, String variant, String parent) {
        Identifier model = modModel("cactus_trapdoor_" + variant);
        blockModels.modelOutput.accept(model, () -> createTrapdoorModelJson(parent));
        return model;
    }

    private static Identifier modBlock(String path) {
        return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "block/" + path);
    }

    private static Material modBlockMaterial(String path) {
        return new Material(modBlock(path));
    }

    private static Identifier mcBlock(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", "block/" + path);
    }

    private static Identifier modModel(String path) {
        return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "block/" + path);
    }

    private static Identifier modItemModel(String path) {
        return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "item/" + path);
    }

    @Override
    protected @NonNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream();
    }

    @Override
    protected @NonNull Stream<? extends Holder<Item>> getKnownItems() {
        return ModItems.ITEMS.getEntries().stream()
                .filter(item -> !item.is(ModItems.SPIKED_LEATHER_HELMET)
                        && !item.is(ModItems.SPIKED_LEATHER_CHESTPLATE)
                        && !item.is(ModItems.SPIKED_LEATHER_LEGGINGS)
                        && !item.is(ModItems.SPIKED_LEATHER_BOOTS));
    }

    private static Stream<Item> getFlatItems() {
        return Stream.of(
                ModItems.SPRUCE_CONE.get(),
                ModItems.PEBBLES.get(),
                ModItems.SPRUCE_NUTS.get(),
                ModItems.TOASTED_SPRUCE_NUTS.get(),
                ModItems.TWIG.get(),
                ModItems.TRAIL_MIX.get(),
                ModItems.MIXED_BERRIES.get(),
                ModItems.BLUEBERRIES.get(),
                ModItems.BLUEBERRY_COOKIE.get(),
                ModItems.BLUEBERRY_PIE.get(),
                ModItems.SLICE_OF_BLUEBERRY_PIE.get(),
                ModItems.BLUEBERRY_JUICE.get(),
                ModItems.STRAWBERRY.get(),
                ModItems.STRAWBERRY_JUICE.get(),
                ModItems.WOODEN_BUCKET.get(),
                ModItems.COD_WOODEN_BUCKET.get(),
                ModItems.SALMON_WOODEN_BUCKET.get(),
                ModItems.PUFFERFISH_WOODEN_BUCKET.get(),
                ModItems.TROPICAL_FISH_WOODEN_BUCKET.get(),
                ModItems.AXOLOTL_WOODEN_BUCKET.get(),
                ModItems.TADPOLE_WOODEN_BUCKET.get(),
                ModItems.FLAX_SEEDS.get(),
                ModItems.FLAX_FIBER.get(),
                ModItems.FLAX_FLOWER.get(),
                ModItems.LINEN_THREAD.get(),
                ModItems.LINEN_FABRIC.get(),
                ModItems.CHERRIES.get(),
                ModItems.CHERRY_JUICE.get(),
                ModItems.APPLE_JUICE.get(),
                ModItems.PRICKLY_PEAR.get(),
                ModItems.CACTUS_SIGN.get(),
                ModItems.CACTUS_HANGING_SIGN.get(),
                ModItems.CACTUS_BOAT.get(),
                ModItems.CACTUS_CHEST_BOAT.get(),
                ModItems.CACTUS_SLICE.get(),
                ModItems.CACTUS_JUICE.get()
        );
    }
}
