package de.artemis.floraexpansion.common.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.DesertMossBlock;
import de.artemis.floraexpansion.common.block.FruitingCherryLeavesBlock;
import de.artemis.floraexpansion.common.block.FruitingOakLeavesBlock;
import de.artemis.floraexpansion.common.block.FlaxCropBlock;
import de.artemis.floraexpansion.common.block.GiantCactusBlossomBlock;
import de.artemis.floraexpansion.common.block.GiantCactusWoodBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.block.CactusFruitPlantBlock;
import de.artemis.floraexpansion.common.block.PebblePatchBlock;
import de.artemis.floraexpansion.common.block.PineLitterBlock;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
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
        itemModels.generateFlatItem(ModBlocks.APPLE_CORE.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.CACTUS_CLUSTER.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.CACTUS_THORN.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.CHERRY_PIT.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.DESERT_MOSS.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.PINE_LITTER.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.PEBBLE_PATCH.get().asItem(), ModelTemplates.FLAT_ITEM);
        blockModels.registerSimpleFlatItemModel(ModBlocks.TWIG_LADDER.get());
        blockModels.registerSimpleItemModel(ModBlocks.FRUITING_CHERRY_LEAVES.get(), modModel("fruiting_cherry_leaves_item"));
        blockModels.registerSimpleItemModel(ModBlocks.FRUITING_OAK_LEAVES.get(), modModel("fruiting_oak_leaves_item"));
        getSimpleItems().forEach(item -> itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM));
    }

    private void registerSimpleBlockModels(BlockModelGenerators blockModels) {
        createAppleCoreModels(blockModels);
        createCactusWoodFamilyModels(blockModels);
        createCherryPitModels(blockModels);
        createDesertMossModels(blockModels);
        createFlaxBaleModels(blockModels);
        createFruitingCherryLeavesModels(blockModels);
        createFruitingCherryLeavesItemModel(blockModels);
        createFruitingOakLeavesModels(blockModels);
        createFruitingOakLeavesItemModel(blockModels);
        createGiantCactusBaseModels(blockModels);
        createGiantCactusBlossomModels(blockModels);
        createGiantCactusStemModels(blockModels);
        createGiantCactusWoodModels(blockModels);
        createOpuntiaCactusModels(blockModels);
        createPebbleBlockModels(blockModels);
        blockModels.createFullAndCarpetBlocks(ModBlocks.LINEN_BLOCK.get(), ModBlocks.LINEN_CARPET.get());
        createTwigLadderBlockModel(blockModels);
        blockModels.createNonTemplateHorizontalBlock(ModBlocks.TWIG_LADDER.get());
    }

    private void registerSpecialBlockModels(BlockModelGenerators blockModels) {
        createCactusClusterBlockState(blockModels);
        createCactusThornModels(blockModels);
        createDesertMossBlockState(blockModels);
        createFlaxCropBlockState(blockModels);
        createFruitingCherryLeavesBlockState(blockModels);
        createFruitingOakLeavesBlockState(blockModels);
        createGiantCactusBlossomBlockState(blockModels);
        createGiantCactusWoodBlockState(blockModels, ModBlocks.GIANT_CACTUS_WOOD.get(), "giant_cactus_wood");
        createGiantCactusWoodBlockState(blockModels, ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(), "stripped_giant_cactus_wood");
        createOpuntiaCactusBlockState(blockModels);
        createPebblePatchBlockState(blockModels);
        createPineLitterBlockState(blockModels);
    }

    private void createCactusClusterBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.CACTUS_CLUSTER.get());

        for (int pickles = 1; pickles <= 4; pickles++) {
            generator.with(
                    BlockModelGenerators.condition().term(de.artemis.floraexpansion.common.block.CactusClusterBlock.PICKLES, pickles),
                    BlockModelGenerators.plainVariant(modModel("cactus_cluster_" + pickles))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createDesertMossBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.DESERT_MOSS.get());

        for (int variant = 0; variant <= 3; variant++) {
            generator.with(
                    BlockModelGenerators.condition().term(DesertMossBlock.VARIANT, variant),
                    BlockModelGenerators.plainVariant(modModel("desert_moss_" + variant))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createFlaxCropBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.FLAX_CROP.get());

        for (int age = 0; age <= FlaxCropBlock.AGE.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(4); age++) {
            generator.with(
                    BlockModelGenerators.condition()
                            .term(FlaxCropBlock.AGE, age)
                            .term(FlaxCropBlock.HALF, DoubleBlockHalf.LOWER),
                    BlockModelGenerators.plainVariant(modModel(getFlaxCropLowerModel(age)))
            );
            generator.with(
                    BlockModelGenerators.condition()
                            .term(FlaxCropBlock.AGE, age)
                            .term(FlaxCropBlock.HALF, DoubleBlockHalf.UPPER),
                    BlockModelGenerators.plainVariant(modModel(getFlaxCropUpperModel(age)))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createCactusThornModels(BlockModelGenerators blockModels) {
        Identifier model = modModel("cactus_thorn");

        blockModels.modelOutput.accept(model, () -> createCutoutCrossModelJson("floraexpansion:block/cactus_thorn"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.CACTUS_THORN.get(), BlockModelGenerators.plainVariant(model))
                        .with(PropertyDispatch.modify(de.artemis.floraexpansion.common.block.CactusThornBlock.FACE)
                                .select(AttachFace.FLOOR, BlockModelGenerators.NOP)
                                .select(AttachFace.CEILING, BlockModelGenerators.X_ROT_180)
                                .select(AttachFace.WALL, BlockModelGenerators.X_ROT_90))
                        .with(PropertyDispatch.modify(de.artemis.floraexpansion.common.block.CactusThornBlock.FACING)
                                .select(Direction.NORTH, BlockModelGenerators.NOP)
                                .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
                                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                                .select(Direction.WEST, BlockModelGenerators.Y_ROT_270))
        );
    }

    private void createPineLitterBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.PINE_LITTER.get());

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (int segment = PineLitterBlock.MIN_SEGMENTS; segment <= PineLitterBlock.MAX_SEGMENTS; segment++) {
                for (int amount = segment; amount <= PineLitterBlock.MAX_SEGMENTS; amount++) {
                    generator.with(
                            BlockModelGenerators.condition()
                                    .term(PineLitterBlock.FACING, facing)
                                    .term(PineLitterBlock.AMOUNT, amount),
                            createPineLitterVariant(segment, facing)
                    );
                }
            }
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private MultiVariant createPineLitterVariant(int segment, Direction facing) {
        MultiVariant variant = BlockModelGenerators.plainVariant(modModel("pine_litter_" + segment));

        return switch (facing) {
            case NORTH -> variant;
            case EAST -> variant.with(BlockModelGenerators.Y_ROT_90);
            case SOUTH -> variant.with(BlockModelGenerators.Y_ROT_180);
            case WEST -> variant.with(BlockModelGenerators.Y_ROT_270);
            default -> throw new IllegalStateException("Unexpected horizontal direction: " + facing);
        };
    }

    private static String getFlaxCropLowerModel(int age) {
        return switch (age) {
            case 0 -> "flax_stage0";
            case 1 -> "flax_stage1_bottom";
            case 2 -> "flax_stage2_bottom";
            case 3 -> "flax_stage3_bottom";
            case 4 -> "flax_stage4_bottom";
            default -> throw new IllegalStateException("Unexpected flax crop age: " + age);
        };
    }

    private static String getFlaxCropUpperModel(int age) {
        return switch (age) {
            case 0, 1 -> "flax_stage1_top";
            case 2 -> "flax_stage2_top";
            case 3 -> "flax_stage3_top";
            case 4 -> "flax_stage4_top";
            default -> throw new IllegalStateException("Unexpected flax crop age: " + age);
        };
    }

    private void createPebblePatchBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.PEBBLE_PATCH.get());

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (int amount = 1; amount <= 5; amount++) {
                generator.with(
                        BlockModelGenerators.condition()
                                .term(PebblePatchBlock.FACING, facing)
                                .term(PebblePatchBlock.AMOUNT, amount),
                        createPebblePatchVariant(amount, facing)
                );
            }
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createFruitingCherryLeavesBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.FRUITING_CHERRY_LEAVES.get());

        for (int age = 0; age <= FruitingCherryLeavesBlock.MAX_AGE; age++) {
            generator.with(
                    BlockModelGenerators.condition().term(FruitingCherryLeavesBlock.AGE, age),
                    BlockModelGenerators.plainVariant(modModel("fruiting_cherry_leaves_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createFruitingOakLeavesBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.FRUITING_OAK_LEAVES.get());

        for (int age = 0; age <= FruitingOakLeavesBlock.MAX_AGE; age++) {
            generator.with(
                    BlockModelGenerators.condition().term(FruitingOakLeavesBlock.AGE, age),
                    BlockModelGenerators.plainVariant(modModel("fruiting_oak_leaves_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createOpuntiaCactusBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.OPUNTIA_CACTUS.get());

        for (int age = 0; age <= CactusFruitPlantBlock.MAX_AGE; age++) {
            generator.with(
                    BlockModelGenerators.condition().term(CactusFruitPlantBlock.AGE, age),
                    BlockModelGenerators.plainVariant(modModel("opuntia_cactus_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createGiantCactusBlossomBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.GIANT_CACTUS_BLOSSOM.get());
        String[] textures = {"giant_cactus_blossom_0", "giant_cactus_blossom_1", "giant_cactus_blossom_3"};

        for (int variant = 0; variant < textures.length; variant++) {
            generator.with(
                    BlockModelGenerators.condition().term(GiantCactusBlossomBlock.VARIANT, variant),
                    BlockModelGenerators.plainVariant(modModel("giant_cactus_blossom_variant_" + variant))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createGiantCactusWoodBlockState(BlockModelGenerators blockModels, Block block, String modelName) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

        for (boolean generated : new boolean[]{false, true}) {
            generator.with(
                    BlockModelGenerators.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                            .term(GiantCactusWoodBlock.GENERATED, generated),
                    BlockModelGenerators.plainVariant(modModel(modelName))
            );
            generator.with(
                    BlockModelGenerators.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.X)
                            .term(GiantCactusWoodBlock.GENERATED, generated),
                    BlockModelGenerators.plainVariant(modModel(modelName + "_horizontal"))
                            .with(BlockModelGenerators.X_ROT_90)
                            .with(BlockModelGenerators.Y_ROT_90)
            );
            generator.with(
                    BlockModelGenerators.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                            .term(GiantCactusWoodBlock.GENERATED, generated),
                    BlockModelGenerators.plainVariant(modModel(modelName + "_horizontal"))
                            .with(BlockModelGenerators.X_ROT_90)
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private MultiVariant createPebblePatchVariant(int amount, Direction facing) {
        MultiVariant variant = BlockModelGenerators.plainVariant(modModel("pebble_patch_" + (amount - 1)));

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

            textures.addProperty("particle", "floraexpansion:block/twig_ladder");
            textures.addProperty("texture", "floraexpansion:block/twig_ladder");
            model.add("textures", textures);
            return model;
        });
    }

    private void createCherryPitModels(BlockModelGenerators blockModels) {
        Identifier cherryPitModel = ModelTemplates.CROSS.create(
                ModBlocks.CHERRY_PIT.get(),
                TextureMapping.cross(modBlock("planted_cherry_pit")),
                blockModels.modelOutput
        );
        Identifier pottedCherryPitModel = ModelTemplates.FLOWER_POT_CROSS.create(
                ModBlocks.POTTED_CHERRY_PIT.get(),
                TextureMapping.plant(modBlock("planted_cherry_pit")),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.CHERRY_PIT.get(), BlockModelGenerators.plainVariant(cherryPitModel))
        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.POTTED_CHERRY_PIT.get(), BlockModelGenerators.plainVariant(pottedCherryPitModel))
        );
    }

    private void createCactusWoodFamilyModels(BlockModelGenerators blockModels) {
        Identifier cactusPlanksModel = ModelTemplates.CUBE_ALL.create(
                ModBlocks.CACTUS_PLANKS.get(),
                TextureMapping.cube(modBlock("cactus_planks")),
                blockModels.modelOutput
        );
        Identifier cactusMosaicModel = ModelTemplates.CUBE_ALL.create(
                ModBlocks.CACTUS_MOSAIC.get(),
                TextureMapping.cube(modBlock("cactus_mosaic")),
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
                .door(ModBlocks.CACTUS_DOOR.get())
                .trapdoor(ModBlocks.CACTUS_TRAPDOOR.get())
                .getFamily();
        blockModels.familyWithExistingFullBlock(ModBlocks.CACTUS_PLANKS.get()).generateFor(family);

        Identifier cactusSignModel = ModelTemplates.PARTICLE_ONLY.create(
                modModel("cactus_sign"),
                new TextureMapping().put(TextureSlot.PARTICLE, modBlock("cactus_planks")),
                blockModels.modelOutput
        );
        Identifier cactusHangingSignModel = ModelTemplates.PARTICLE_ONLY.create(
                modModel("cactus_hanging_sign"),
                new TextureMapping().put(TextureSlot.PARTICLE, modBlock("stripped_giant_cactus_base_side")),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.CACTUS_SIGN.get(), BlockModelGenerators.plainVariant(cactusSignModel))
        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.CACTUS_WALL_SIGN.get(), BlockModelGenerators.plainVariant(cactusSignModel))
        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.CACTUS_HANGING_SIGN.get(), BlockModelGenerators.plainVariant(cactusHangingSignModel))
        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(ModBlocks.CACTUS_WALL_HANGING_SIGN.get(), BlockModelGenerators.plainVariant(cactusHangingSignModel))
        );
    }

    private void createAppleCoreModels(BlockModelGenerators blockModels) {
        Identifier appleCoreModel = ModelTemplates.CROSS.create(
                ModBlocks.APPLE_CORE.get(),
                TextureMapping.cross(modBlock("planted_apple_core")),
                blockModels.modelOutput
        );
        Identifier pottedAppleCoreModel = ModelTemplates.FLOWER_POT_CROSS.create(
                ModBlocks.POTTED_APPLE_CORE.get(),
                TextureMapping.plant(modBlock("planted_apple_core")),
                blockModels.modelOutput
        );

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
        }
    }

    private void createFruitingCherryLeavesModels(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("fruiting_cherry_leaves_stage0"), () ->
                createCubeModelJson("floraexpansion:block/fruiting_cherry_leaves_stage0", false));
        for (int age = 1; age <= FruitingCherryLeavesBlock.MAX_AGE; age++) {
            String stageTexture = "floraexpansion:block/fruiting_cherry_leaves_stage" + age;
            blockModels.modelOutput.accept(modModel("fruiting_cherry_leaves_stage" + age), () ->
                    createCubeModelJson(stageTexture, false));
        }
    }

    private void createFruitingCherryLeavesItemModel(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("fruiting_cherry_leaves_item"), () ->
                createCubeModelJson("floraexpansion:block/fruiting_cherry_leaves_stage3", false));
    }

    private void createFruitingOakLeavesModels(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("fruiting_oak_leaves_stage0"), () ->
                createCubeModelJson("minecraft:block/oak_leaves", true));
        blockModels.modelOutput.accept(modModel("fruiting_oak_leaves_stage1"), () ->
                createLayeredCubeModelJson("minecraft:block/oak_leaves", true, "floraexpansion:block/fruiting_oak_leaves_stage1_overlay"));
        blockModels.modelOutput.accept(modModel("fruiting_oak_leaves_stage2"), () ->
                createLayeredCubeModelJson("minecraft:block/oak_leaves", true, "floraexpansion:block/fruiting_oak_leaves_stage2_overlay"));
    }

    private void createFruitingOakLeavesItemModel(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("fruiting_oak_leaves_item"), () ->
                createCubeModelJson("floraexpansion:block/fruiting_oak_leaves_stage2_item", false));
    }

    private void createGiantCactusBaseModels(BlockModelGenerators blockModels) {
        createRotatedPillarModels(
                blockModels,
                ModBlocks.GIANT_CACTUS_BASE.get(),
                "giant_cactus_base",
                "giant_cactus_base_horizontal",
                "floraexpansion:block/giant_cactus_base_top",
                "floraexpansion:block/giant_cactus_base_side"
        );
        createRotatedPillarModels(
                blockModels,
                ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get(),
                "stripped_giant_cactus_base",
                "stripped_giant_cactus_base_horizontal",
                "floraexpansion:block/stripped_giant_cactus_base_top",
                "floraexpansion:block/stripped_giant_cactus_base_side"
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

    private void createGiantCactusBlossomModels(BlockModelGenerators blockModels) {
        Identifier giantCactusBlossomItemModel = modModel("giant_cactus_blossom_item");
        String[] textures = {"giant_cactus_blossom_0", "giant_cactus_blossom_1", "giant_cactus_blossom_3"};

        for (int variant = 0; variant < textures.length; variant++) {
            String modelName = "giant_cactus_blossom_variant_" + variant;
            String texture = "floraexpansion:block/" + textures[variant];
            blockModels.modelOutput.accept(
                    modModel(modelName),
                    () -> createCutoutCrossModelJson(texture)
            );
        }
        blockModels.modelOutput.accept(giantCactusBlossomItemModel, () ->
                createGeneratedItemModelJson("floraexpansion:block/giant_cactus_blossom_0"));
        blockModels.registerSimpleItemModel(ModBlocks.GIANT_CACTUS_BLOSSOM.get(), giantCactusBlossomItemModel);
    }

    private void createGiantCactusWoodModels(BlockModelGenerators blockModels) {
        createWoodPillarModels(
                blockModels,
                ModBlocks.GIANT_CACTUS_WOOD.get(),
                "giant_cactus_wood",
                "giant_cactus_wood_horizontal",
                "floraexpansion:block/giant_cactus_base_side"
        );
        createWoodPillarModels(
                blockModels,
                ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(),
                "stripped_giant_cactus_wood",
                "stripped_giant_cactus_wood_horizontal",
                "floraexpansion:block/stripped_giant_cactus_base_side"
        );
    }

    private void createOpuntiaCactusModels(BlockModelGenerators blockModels) {
        for (int age = 0; age <= CactusFruitPlantBlock.MAX_AGE; age++) {
            String texture = "opuntia_cactus_stage" + age;
            ModelTemplates.CROSS.create(
                    modModel(texture),
                    TextureMapping.cross(modBlock(texture)),
                    blockModels.modelOutput
            );
        }
    }

    private void createFlaxBaleModels(BlockModelGenerators blockModels) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.END, modBlock("flax_bale_top"))
                .put(TextureSlot.SIDE, modBlock("flax_bale_side"));

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
                .put(TextureSlot.END, Identifier.parse(endTexture))
                .put(TextureSlot.SIDE, Identifier.parse(sideTexture));

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
                .put(TextureSlot.END, Identifier.parse(texture))
                .put(TextureSlot.SIDE, Identifier.parse(texture));

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

    private void createPebbleBlockModels(BlockModelGenerators blockModels) {
        Identifier model = ModelTemplates.CUBE_ALL.create(
                ModBlocks.PEBBLE_BLOCK.get(),
                new TextureMapping().put(TextureSlot.ALL, modBlock("pebble_block")),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.PEBBLE_BLOCK.get(), BlockModelGenerators.plainVariant(model)));
        blockModels.registerSimpleItemModel(ModBlocks.PEBBLE_BLOCK.get(), model);
    }

    private static JsonObject createCutoutCrossModelJson(String texture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:block/cross");
        model.addProperty("render_type", "cutout");
        textures.addProperty("cross", texture);
        textures.addProperty("particle", texture);
        model.add("textures", textures);
        return model;
    }

    private static JsonObject createGeneratedItemModelJson(String texture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:item/generated");
        textures.addProperty("layer0", texture);
        model.add("textures", textures);
        return model;
    }

    private static JsonObject createLayeredCubeModelJson(String baseTexture, boolean tintBase, String overlayTexture) {
        JsonObject model = createCubeModelJson(baseTexture, tintBase);
        model.getAsJsonArray("elements").add(createCubeElement("#overlay", false));

        JsonObject textures = model.getAsJsonObject("textures");
        textures.addProperty("overlay", overlayTexture);
        return model;
    }

    private static JsonObject createGroundPlaneModelJson(String texture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();
        JsonArray elements = new JsonArray();
        JsonObject element = new JsonObject();
        JsonArray from = new JsonArray();
        JsonArray to = new JsonArray();
        JsonObject faces = new JsonObject();

        model.addProperty("parent", "minecraft:block/block");
        model.addProperty("render_type", "cutout");

        textures.addProperty("particle", texture);
        textures.addProperty("texture", texture);
        model.add("textures", textures);

        from.add(0);
        from.add(0);
        from.add(0);
        element.add("from", from);

        to.add(16);
        to.add(0.1);
        to.add(16);
        element.add("to", to);

        faces.add("up", createFace("#texture", false, null));
        element.add("faces", faces);
        elements.add(element);
        model.add("elements", elements);
        return model;
    }

    private static JsonObject createInsetCactusModelJson(String bottomTexture, String topTexture, String sideTexture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();
        JsonArray elements = new JsonArray();

        model.addProperty("parent", "minecraft:block/block");
        model.addProperty("render_type", "cutout");
        textures.addProperty("particle", sideTexture);
        textures.addProperty("bottom", bottomTexture);
        textures.addProperty("top", topTexture);
        textures.addProperty("side", sideTexture);
        model.add("textures", textures);

        elements.add(createCactusCoreElement());
        elements.add(createCactusNorthSouthElement());
        elements.add(createCactusWestEastElement());
        model.add("elements", elements);
        return model;
    }

    private static JsonObject createCactusCoreElement() {
        JsonObject element = new JsonObject();
        JsonArray from = new JsonArray();
        JsonArray to = new JsonArray();
        JsonObject faces = new JsonObject();

        from.add(1);
        from.add(0);
        from.add(1);
        element.add("from", from);

        to.add(15);
        to.add(16);
        to.add(15);
        element.add("to", to);

        faces.add("down", createFace("#bottom", 1, 1, 15, 15, false, "down"));
        faces.add("up", createFace("#top", 1, 1, 15, 15, false, "up"));
        element.add("faces", faces);
        return element;
    }

    private static JsonObject createCactusNorthSouthElement() {
        JsonObject element = new JsonObject();
        JsonArray from = new JsonArray();
        JsonArray to = new JsonArray();
        JsonObject faces = new JsonObject();

        from.add(0);
        from.add(0);
        from.add(1);
        element.add("from", from);

        to.add(16);
        to.add(16);
        to.add(15);
        element.add("to", to);

        faces.add("north", createFace("#side", 0, 0, 16, 16, false, null));
        faces.add("south", createFace("#side", 0, 0, 16, 16, false, null));
        element.add("faces", faces);
        return element;
    }

    private static JsonObject createCactusWestEastElement() {
        JsonObject element = new JsonObject();
        JsonArray from = new JsonArray();
        JsonArray to = new JsonArray();
        JsonObject faces = new JsonObject();

        from.add(1);
        from.add(0);
        from.add(0);
        element.add("from", from);

        to.add(15);
        to.add(16);
        to.add(16);
        element.add("to", to);

        faces.add("west", createFace("#side", 0, 0, 16, 16, false, null));
        faces.add("east", createFace("#side", 0, 0, 16, 16, false, null));
        element.add("faces", faces);
        return element;
    }

    private static JsonObject createPottedCactusModelJson(String sideTexture, String topTexture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:block/potted_cactus");
        model.addProperty("render_type", "cutout");
        textures.addProperty("cactus", sideTexture);
        textures.addProperty("cactus_top", topTexture);
        textures.addProperty("flowerpot", "minecraft:block/flower_pot");
        model.add("textures", textures);
        return model;
    }

    private static JsonObject createCubeModelJson(String texture, boolean tint) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();
        JsonArray elements = new JsonArray();

        model.addProperty("parent", "minecraft:block/block");
        textures.addProperty("particle", texture);
        textures.addProperty("all", texture);
        model.add("textures", textures);
        elements.add(createCubeElement("#all", tint));
        model.add("elements", elements);
        return model;
    }

    private static JsonObject createCubeElement(String textureSlot, boolean tint) {
        JsonObject element = new JsonObject();
        JsonArray from = new JsonArray();
        JsonArray to = new JsonArray();
        JsonObject faces = new JsonObject();

        from.add(0);
        from.add(0);
        from.add(0);
        element.add("from", from);

        to.add(16);
        to.add(16);
        to.add(16);
        element.add("to", to);

        faces.add("down", createFace(textureSlot, tint, "down"));
        faces.add("up", createFace(textureSlot, tint, "up"));
        faces.add("north", createFace(textureSlot, tint, "north"));
        faces.add("south", createFace(textureSlot, tint, "south"));
        faces.add("west", createFace(textureSlot, tint, "west"));
        faces.add("east", createFace(textureSlot, tint, "east"));
        element.add("faces", faces);
        return element;
    }

    private static JsonObject createFace(String textureSlot, boolean tint, String cullface) {
        JsonObject face = new JsonObject();
        JsonArray uv = new JsonArray();

        uv.add(0);
        uv.add(0);
        uv.add(16);
        uv.add(16);
        face.add("uv", uv);
        face.addProperty("texture", textureSlot);

        if (cullface != null) {
            face.addProperty("cullface", cullface);
        }

        if (tint) {
            face.addProperty("tintindex", 0);
        }

        return face;
    }

    private static JsonObject createFace(String textureSlot,
                                         int u1,
                                         int v1,
                                         int u2,
                                         int v2,
                                         boolean tint,
                                         String cullface) {
        JsonObject face = new JsonObject();
        JsonArray uv = new JsonArray();

        uv.add(u1);
        uv.add(v1);
        uv.add(u2);
        uv.add(v2);
        face.add("uv", uv);
        face.addProperty("texture", textureSlot);

        if (cullface != null) {
            face.addProperty("cullface", cullface);
        }

        if (tint) {
            face.addProperty("tintindex", 0);
        }

        return face;
    }

    private static Identifier modBlock(String path) {
        return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "block/" + path);
    }

    private static Identifier mcBlock(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", "block/" + path);
    }

    private static Identifier modModel(String path) {
        return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "block/" + path);
    }

    @Override
    protected @NonNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().filter(x ->
                x.is(ModBlocks.APPLE_CORE)
                        || x.is(ModBlocks.POTTED_APPLE_CORE)
                        || x.is(ModBlocks.CHERRY_PIT)
                        || x.is(ModBlocks.POTTED_CHERRY_PIT)
                        || x.is(ModBlocks.PINE_LITTER)
                        || x.is(ModBlocks.FRUITING_CHERRY_LEAVES)
                        || x.is(ModBlocks.FRUITING_OAK_LEAVES)
                        || x.is(ModBlocks.PEBBLE_PATCH)
                        || x.is(ModBlocks.PEBBLE_BLOCK)
                        || x.is(ModBlocks.CACTUS_PLANKS)
                        || x.is(ModBlocks.CACTUS_MOSAIC)
                        || x.is(ModBlocks.CACTUS_STAIRS)
                        || x.is(ModBlocks.CACTUS_SLAB)
                        || x.is(ModBlocks.CACTUS_FENCE)
                        || x.is(ModBlocks.CACTUS_FENCE_GATE)
                        || x.is(ModBlocks.CACTUS_BUTTON)
                        || x.is(ModBlocks.CACTUS_PRESSURE_PLATE)
                        || x.is(ModBlocks.CACTUS_CLUSTER)
                        || x.is(ModBlocks.CACTUS_DOOR)
                        || x.is(ModBlocks.CACTUS_TRAPDOOR)
                        || x.is(ModBlocks.GIANT_CACTUS_BLOSSOM)
                        || x.is(ModBlocks.CACTUS_SIGN)
                        || x.is(ModBlocks.CACTUS_WALL_SIGN)
                        || x.is(ModBlocks.CACTUS_HANGING_SIGN)
                        || x.is(ModBlocks.CACTUS_WALL_HANGING_SIGN)
                        || x.is(ModBlocks.CACTUS_THORN)
                        || x.is(ModBlocks.DESERT_MOSS)
                        || x.is(ModBlocks.GIANT_CACTUS_BASE)
                        || x.is(ModBlocks.GIANT_CACTUS_STEM)
                        || x.is(ModBlocks.GIANT_CACTUS_WOOD)
                        || x.is(ModBlocks.OPUNTIA_CACTUS)
                        || x.is(ModBlocks.POTTED_GIANT_CACTUS_STEM)
                        || x.is(ModBlocks.STRIPPED_GIANT_CACTUS_BASE)
                        || x.is(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD)
                        || x.is(ModBlocks.TWIG_LADDER)
                        || x.is(ModBlocks.FLAX_CROP)
                        || x.is(ModBlocks.FLAX_BALE)
                        || x.is(ModBlocks.LINEN_BLOCK)
                        || x.is(ModBlocks.LINEN_CARPET));
    }

    @Override
    protected @NonNull Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.concat(
                Stream.of(
                        ModBlocks.APPLE_CORE.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CHERRY_PIT.get().asItem().builtInRegistryHolder(),
                        ModBlocks.PINE_LITTER.get().asItem().builtInRegistryHolder(),
                        ModBlocks.FRUITING_CHERRY_LEAVES.get().asItem().builtInRegistryHolder(),
                        ModBlocks.FRUITING_OAK_LEAVES.get().asItem().builtInRegistryHolder(),
                        ModBlocks.PEBBLE_PATCH.get().asItem().builtInRegistryHolder(),
                        ModBlocks.PEBBLE_BLOCK.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_PLANKS.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_MOSAIC.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_STAIRS.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_SLAB.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_FENCE.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_FENCE_GATE.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_BUTTON.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_PRESSURE_PLATE.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_CLUSTER.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_DOOR.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_TRAPDOOR.get().asItem().builtInRegistryHolder(),
                        ModBlocks.GIANT_CACTUS_BLOSSOM.get().asItem().builtInRegistryHolder(),
                        ModBlocks.CACTUS_THORN.get().asItem().builtInRegistryHolder(),
                        ModBlocks.DESERT_MOSS.get().asItem().builtInRegistryHolder(),
                        ModBlocks.GIANT_CACTUS_BASE.get().asItem().builtInRegistryHolder(),
                        ModBlocks.GIANT_CACTUS_STEM.get().asItem().builtInRegistryHolder(),
                        ModBlocks.GIANT_CACTUS_WOOD.get().asItem().builtInRegistryHolder(),
                        ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get().asItem().builtInRegistryHolder(),
                        ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get().asItem().builtInRegistryHolder(),
                        ModBlocks.TWIG_LADDER.get().asItem().builtInRegistryHolder(),
                        ModBlocks.FLAX_BALE.get().asItem().builtInRegistryHolder(),
                        ModBlocks.LINEN_BLOCK.get().asItem().builtInRegistryHolder(),
                        ModBlocks.LINEN_CARPET.get().asItem().builtInRegistryHolder()
                ),
                getSimpleItems().map(Item::builtInRegistryHolder)
        );
    }

    private static Stream<Item> getSimpleItems() {
        return Stream.of(
                ModItems.PINE_CONE.get(),
                ModItems.PEBBLES.get(),
                ModItems.PINE_NUTS.get(),
                ModItems.TOASTED_PINE_NUTS.get(),
                ModItems.TWIG.get(),
                ModItems.FOREST_SNACK.get(),
                ModItems.SWEET_BERRY_MIX.get(),
                ModItems.FLAX_SEED.get(),
                ModItems.FLAX_FIBER.get(),
                ModItems.FLAX_FLOWER.get(),
                ModItems.LINEN_THREAD.get(),
                ModItems.LINEN_CLOTH.get(),
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
