package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.CactusFlowerBlock;
import de.artemis.floraexpansion.common.block.CactusThornBlock;
import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.FruitingOakLeavesBlock;
import de.artemis.floraexpansion.common.block.LargeBlueberryBushBlock;
import de.artemis.floraexpansion.common.block.StrawberryCropBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FloraExpansion.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        crateBlock();
        strawberryCakeBlock();
        strawberryCandleCakeBlocks();
        smallBlueberryBushBlock();
        largeBlueberryBushBlock();
        strawberryCropBlock();
        saplingBlock(ModBlocks.APPLE_CORE.get(), "planted_apple_core");
        pottedPlantBlock(ModBlocks.POTTED_APPLE_CORE.get(), "planted_apple_core");
        fruitingOakLeavesBlock();
        giantCactusBaseBlock();
        giantCactusStemBlock();
        strippedGiantCactusBaseBlock();
        cactusThornBlock();
        cactusFlowerBlock();
        cactusWoodFromBaseTextures(ModBlocks.GIANT_CACTUS_WOOD, "giant_cactus_base_side");
        cactusWoodFromBaseTextures(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD, "stripped_giant_cactus_base_side");
        planksBlock(ModBlocks.CACTUS_PLANKS, "cactus_planks");
        planksBlock(ModBlocks.CACTUS_MOSAIC, "cactus_mosaic");
        stairsBlockFromTexture(ModBlocks.CACTUS_STAIRS, "cactus_planks");
        slabBlockFromTexture(ModBlocks.CACTUS_SLAB, "cactus_planks");
        fenceBlockFromTexture(ModBlocks.CACTUS_FENCE, "cactus_planks");
        fenceGateBlockFromTexture(ModBlocks.CACTUS_FENCE_GATE, "cactus_planks");
        buttonBlockFromTexture(ModBlocks.CACTUS_BUTTON, "cactus_planks");
        pressurePlateBlockFromTexture(ModBlocks.CACTUS_PRESSURE_PLATE, "cactus_planks");
        doorBlockFromTexture(ModBlocks.CACTUS_DOOR, "cactus_door_bottom", "cactus_door_top");
        trapdoorBlockFromTexture(ModBlocks.CACTUS_TRAPDOOR, "cactus_trapdoor");
        signBlocks();
        hangingSignBlocks();
    }

    private void crateBlock() {
        ModelFile openModel = models().withExistingParent("crate", mcLoc("block/composter"))
                .texture("particle", mcLoc("block/barrel_side"))
                .texture("top", mcLoc("block/barrel_top"))
                .texture("bottom", mcLoc("block/barrel_bottom"))
                .texture("side", mcLoc("block/barrel_side"))
                .texture("inside", mcLoc("block/barrel_bottom"));

        ModelFile closedModel = models().getExistingFile(mcLoc("block/barrel"));
        ModelFile packedModel = models().getExistingFile(modLoc("block/crate_packed"));

        getVariantBuilder(ModBlocks.CRATE.get()).forAllStates(state -> {
            ModelFile model;
            if (state.getValue(CrateBlock.PACKED)) {
                model = packedModel;
            } else if (state.getValue(CrateBlock.POWERED)) {
                model = closedModel;
            } else {
                model = openModel;
            }

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .build();
        });
    }

    private void strawberryCakeBlock() {
        ModelFile wholeCake = models().withExistingParent("strawberry_cake", mcLoc("block/cake"))
                .texture("particle", modLoc("block/strawberry_cake_side"))
                .texture("bottom", modLoc("block/strawberry_cake_bottom"))
                .texture("top", modLoc("block/strawberry_cake_top"))
                .texture("side", modLoc("block/strawberry_cake_side"));

        ModelFile[] sliceModels = new ModelFile[6];
        for (int bite = 1; bite <= 6; bite++) {
            sliceModels[bite - 1] = models().withExistingParent("strawberry_cake_slice" + bite, mcLoc("block/cake_slice" + bite))
                    .texture("particle", modLoc("block/strawberry_cake_side"))
                    .texture("bottom", modLoc("block/strawberry_cake_bottom"))
                    .texture("top", modLoc("block/strawberry_cake_top"))
                    .texture("side", modLoc("block/strawberry_cake_side"))
                    .texture("inside", modLoc("block/strawberry_cake_inner"));
        }

        getVariantBuilder(ModBlocks.STRAWBERRY_CAKE.get()).forAllStates(state -> {
            int bites = state.getValue(CakeBlock.BITES);
            ModelFile model = bites == 0 ? wholeCake : sliceModels[bites - 1];
            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void strawberryCandleCakeBlocks() {
        candleCakeBlock(ModBlocks.STRAWBERRY_CANDLE_CAKE, "candle");
        candleCakeBlock(ModBlocks.WHITE_STRAWBERRY_CANDLE_CAKE, "white_candle");
        candleCakeBlock(ModBlocks.ORANGE_STRAWBERRY_CANDLE_CAKE, "orange_candle");
        candleCakeBlock(ModBlocks.MAGENTA_STRAWBERRY_CANDLE_CAKE, "magenta_candle");
        candleCakeBlock(ModBlocks.LIGHT_BLUE_STRAWBERRY_CANDLE_CAKE, "light_blue_candle");
        candleCakeBlock(ModBlocks.YELLOW_STRAWBERRY_CANDLE_CAKE, "yellow_candle");
        candleCakeBlock(ModBlocks.LIME_STRAWBERRY_CANDLE_CAKE, "lime_candle");
        candleCakeBlock(ModBlocks.PINK_STRAWBERRY_CANDLE_CAKE, "pink_candle");
        candleCakeBlock(ModBlocks.GRAY_STRAWBERRY_CANDLE_CAKE, "gray_candle");
        candleCakeBlock(ModBlocks.LIGHT_GRAY_STRAWBERRY_CANDLE_CAKE, "light_gray_candle");
        candleCakeBlock(ModBlocks.CYAN_STRAWBERRY_CANDLE_CAKE, "cyan_candle");
        candleCakeBlock(ModBlocks.PURPLE_STRAWBERRY_CANDLE_CAKE, "purple_candle");
        candleCakeBlock(ModBlocks.BLUE_STRAWBERRY_CANDLE_CAKE, "blue_candle");
        candleCakeBlock(ModBlocks.BROWN_STRAWBERRY_CANDLE_CAKE, "brown_candle");
        candleCakeBlock(ModBlocks.GREEN_STRAWBERRY_CANDLE_CAKE, "green_candle");
        candleCakeBlock(ModBlocks.RED_STRAWBERRY_CANDLE_CAKE, "red_candle");
        candleCakeBlock(ModBlocks.BLACK_STRAWBERRY_CANDLE_CAKE, "black_candle");
    }

    private void candleCakeBlock(DeferredBlock<? extends Block> block, String candleTextureName) {
        String name = block.getId().getPath();
        ModelFile unlitModel = models().withExistingParent(name, mcLoc("block/template_cake_with_candle"))
                .texture("particle", modLoc("block/strawberry_cake_side"))
                .texture("bottom", modLoc("block/strawberry_cake_bottom"))
                .texture("top", modLoc("block/strawberry_cake_top"))
                .texture("side", modLoc("block/strawberry_cake_side"))
                .texture("candle", mcLoc("block/" + candleTextureName));
        ModelFile litModel = models().withExistingParent(name + "_lit", mcLoc("block/template_cake_with_candle"))
                .texture("particle", modLoc("block/strawberry_cake_side"))
                .texture("bottom", modLoc("block/strawberry_cake_bottom"))
                .texture("top", modLoc("block/strawberry_cake_top"))
                .texture("side", modLoc("block/strawberry_cake_side"))
                .texture("candle", mcLoc("block/" + candleTextureName + "_lit"));

        getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(CandleCakeBlock.LIT) ? litModel : unlitModel)
                .build());
    }

    private void smallBlueberryBushBlock() {
        BlockModelBuilder stage0 = models().cross("small_blueberry_bush_stage0", modLoc("block/small_blueberry_bush_stage0")).renderType("cutout");
        BlockModelBuilder stage1 = models().cross("small_blueberry_bush_stage1", modLoc("block/small_blueberry_bush_stage1")).renderType("cutout");
        BlockModelBuilder stage2 = models().cross("small_blueberry_bush_stage2", modLoc("block/small_blueberry_bush_stage2")).renderType("cutout");
        BlockModelBuilder stage3 = models().cross("small_blueberry_bush_stage3", modLoc("block/small_blueberry_bush_stage3")).renderType("cutout");

        getVariantBuilder(ModBlocks.BLUEBERRY_BUSH.get()).forAllStates(state -> {
            int age = state.getValue(net.minecraft.world.level.block.SweetBerryBushBlock.AGE);
            ModelFile model = switch (age) {
                case 1 -> stage1;
                case 2 -> stage2;
                case 3 -> stage3;
                default -> stage0;
            };

            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void largeBlueberryBushBlock() {
        ModelFile stage0 = models().getExistingFile(modLoc("block/large_blueberry_bush_stage0"));
        ModelFile stage1 = models().getExistingFile(modLoc("block/large_blueberry_bush_stage1"));
        ModelFile stage2 = models().getExistingFile(modLoc("block/large_blueberry_bush_stage2"));
        ModelFile stage3 = models().getExistingFile(modLoc("block/large_blueberry_bush_stage3"));

        getVariantBuilder(ModBlocks.LARGE_BLUEBERRY_BUSH.get()).forAllStates(state -> {
            int age = state.getValue(LargeBlueberryBushBlock.AGE);
            ModelFile model = switch (age) {
                case 1 -> stage1;
                case 2 -> stage2;
                case 3 -> stage3;
                default -> stage0;
            };

            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void strawberryCropBlock() {
        ModelFile stage0 = models().crop("strawberry_plant_stage0", modLoc("block/strawberry_plant_stage0")).renderType("cutout");
        ModelFile stage1 = models().crop("strawberry_plant_stage1", modLoc("block/strawberry_plant_stage1")).renderType("cutout");
        ModelFile stage2 = models().crop("strawberry_plant_stage2", modLoc("block/strawberry_plant_stage2")).renderType("cutout");
        ModelFile stage3 = models().crop("strawberry_plant_stage3", modLoc("block/strawberry_plant_stage3")).renderType("cutout");

        getVariantBuilder(ModBlocks.STRAWBERRY_PLANT.get()).forAllStates(state -> {
            int age = state.getValue(StrawberryCropBlock.AGE);
            ModelFile model = switch (age) {
                case 1 -> stage1;
                case 2 -> stage2;
                case 3 -> stage3;
                default -> stage0;
            };

            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void planksBlock(DeferredBlock<? extends Block> block, String textureName) {
        String name = block.getId().getPath();
        ModelFile model = models().cubeAll(name, modLoc("block/" + textureName));
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }

    private void stairsBlockFromTexture(DeferredBlock<? extends Block> block, String textureName) {
        stairsBlock((StairBlock) block.get(), modLoc("block/" + textureName));
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + block.getId().getPath())));
    }

    private void slabBlockFromTexture(DeferredBlock<? extends Block> block, String textureName) {
        slabBlock((SlabBlock) block.get(),
                modLoc("block/" + textureName),
                modLoc("block/" + textureName));
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + block.getId().getPath())));
    }

    private void fenceBlockFromTexture(DeferredBlock<? extends Block> block, String textureName) {
        String name = block.getId().getPath();
        fenceBlock((FenceBlock) block.get(), modLoc("block/" + textureName));
        ModelFile inventoryModel = models().fenceInventory(name + "_inventory", modLoc("block/" + textureName));
        simpleBlockItem(block.get(), inventoryModel);
    }

    private void fenceGateBlockFromTexture(DeferredBlock<? extends Block> block, String textureName) {
        fenceGateBlock((FenceGateBlock) block.get(), modLoc("block/" + textureName));
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + block.getId().getPath())));
    }

    private void buttonBlockFromTexture(DeferredBlock<? extends Block> block, String textureName) {
        String name = block.getId().getPath();
        buttonBlock((ButtonBlock) block.get(), modLoc("block/" + textureName));
        ModelFile inventoryModel = models().buttonInventory(name + "_inventory", modLoc("block/" + textureName));
        simpleBlockItem(block.get(), inventoryModel);
    }
    private void pressurePlateBlockFromTexture(DeferredBlock<? extends Block> block, String textureName) {
        pressurePlateBlock((PressurePlateBlock) block.get(), modLoc("block/" + textureName));
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + block.getId().getPath())));
    }

    private void doorBlockFromTexture(DeferredBlock<? extends Block> block, String bottom, String top) {
        doorBlockWithRenderType((DoorBlock) block.get(),
                modLoc("block/" + bottom),
                modLoc("block/" + top),
                "cutout");
    }

    private void trapdoorBlockFromTexture(DeferredBlock<? extends Block> block, String textureName) {
        trapdoorBlockWithRenderType((TrapDoorBlock) block.get(),
                modLoc("block/" + textureName),
                true,
                "cutout");
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + block.getId().getPath() + "_bottom")));
    }

    private void signBlocks() {
        signBlock(
                (StandingSignBlock) ModBlocks.CACTUS_SIGN.get(),
                (WallSignBlock) ModBlocks.CACTUS_WALL_SIGN.get(),
                modLoc("block/cactus_planks")
        );
    }

    private void hangingSignBlocks() {
        hangingSignBlock(
                (CeilingHangingSignBlock) ModBlocks.CACTUS_HANGING_SIGN.get(),
                (WallHangingSignBlock) ModBlocks.CACTUS_WALL_HANGING_SIGN.get(),
                modLoc("block/cactus_planks")
        );
    }

    private void cactusWoodFromBaseTextures(DeferredBlock<? extends Block> block, String baseTextureName) {
        String name = block.getId().getPath();

        ModelFile vertical = models().cubeColumn(name,
                modLoc("block/" + baseTextureName),
                modLoc("block/" + baseTextureName));

        ModelFile horizontal = models().cubeColumnHorizontal(name + "_horizontal",
                modLoc("block/" + baseTextureName),
                modLoc("block/" + baseTextureName));

        axisBlock((RotatedPillarBlock) block.get(), vertical, horizontal);
        simpleBlockItem(block.get(), vertical);
    }


    private void fruitingOakLeavesBlock() {
        Block block = ModBlocks.FRUITING_OAK_LEAVES.get();

        BlockModelBuilder stage0 = oakLeavesBaseModel("fruiting_oak_leaves_stage0");
        BlockModelBuilder stage1 = oakLeavesWithSideOverlayModel(
                "fruiting_oak_leaves_stage1",
                modLoc("block/fruiting_oak_leaves_stage1_overlay")
        );
        BlockModelBuilder stage2 = oakLeavesWithSideOverlayModel(
                "fruiting_oak_leaves_stage2",
                modLoc("block/fruiting_oak_leaves_stage2_overlay")
        );

        getVariantBuilder(block).forAllStates(state -> {
            int age = state.getValue(FruitingOakLeavesBlock.AGE);

            ModelFile model = switch (age) {
                case 1 -> stage1;
                case 2 -> stage2;
                default -> stage0;
            };

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .build();
        });
    }

    private void giantCactusBaseBlock() {
        axisBlock((net.minecraft.world.level.block.RotatedPillarBlock) ModBlocks.GIANT_CACTUS_BASE.get(),
                models().cubeColumn("giant_cactus_base",
                        modLoc("block/giant_cactus_base_side"),
                        modLoc("block/giant_cactus_base_top")),
                models().cubeColumnHorizontal("giant_cactus_base_horizontal",
                        modLoc("block/giant_cactus_base_side"),
                        modLoc("block/giant_cactus_base_top")));

        simpleBlockItem(ModBlocks.GIANT_CACTUS_BASE.get(),
                new net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile(modLoc("block/giant_cactus_base")));
    }

    private void strippedGiantCactusBaseBlock() {
        axisBlock((net.minecraft.world.level.block.RotatedPillarBlock) ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get(),
                models().cubeColumn("stripped_giant_cactus_base",
                        modLoc("block/stripped_giant_cactus_base_side"),
                        modLoc("block/stripped_giant_cactus_base_top")),
                models().cubeColumnHorizontal("stripped_giant_cactus_base_horizontal",
                        modLoc("block/stripped_giant_cactus_base_side"),
                        modLoc("block/stripped_giant_cactus_base_top")));

        simpleBlockItem(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get(),
                new net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile(modLoc("block/stripped_giant_cactus_base")));
    }

    private void giantCactusStemBlock() {
        ModelFile model = models()
                .withExistingParent("giant_cactus_stem", mcLoc("block/cactus"))
                .renderType("cutout")
                .texture("side", modLoc("block/giant_cactus_stem_side"))
                .texture("top", modLoc("block/giant_cactus_stem_top"))
                .texture("particle", modLoc("block/giant_cactus_stem_top"))
                .texture("bottom", modLoc("block/giant_cactus_stem_bottom"));

        simpleBlock(ModBlocks.GIANT_CACTUS_STEM.get(), model);
        simpleBlockItem(ModBlocks.GIANT_CACTUS_STEM.get(), model);
    }

    private void cactusThornBlock() {
        ModelFile model = models()
                .withExistingParent("cactus_thorn", mcLoc("block/nether_sprouts"))
                .renderType("cutout")
                .texture("cross", modLoc("block/cactus_thorn"));

        getVariantBuilder(ModBlocks.CACTUS_THORN.get()).forAllStates(state -> {
            int xRot = 0;
            int yRot = 0;

            switch (state.getValue(CactusThornBlock.FACE)) {
                case FLOOR -> {
                    xRot = 0;
                    yRot = ((int) state.getValue(CactusThornBlock.FACING).toYRot() + 180) % 360;
                }
                case CEILING -> {
                    xRot = 180;
                    yRot = ((int) state.getValue(CactusThornBlock.FACING).toYRot() + 180) % 360;
                }
                case WALL -> {
                    xRot = 90;
                    yRot = ((int) state.getValue(CactusThornBlock.FACING).toYRot() + 180) % 360;
                }
            }

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        });
    }

    private void cactusFlowerBlock() {
        BlockModelBuilder variant0 = models().cross("cactus_flower_variant_0", modLoc("block/giant_cactus_blossom_0")).renderType("cutout");
        BlockModelBuilder variant1 = models().cross("cactus_flower_variant_1", modLoc("block/giant_cactus_blossom_1")).renderType("cutout");
        BlockModelBuilder variant2 = models().cross("cactus_flower_variant_2", modLoc("block/giant_cactus_blossom_3")).renderType("cutout");

        getVariantBuilder(ModBlocks.CACTUS_FLOWER.get()).forAllStates(state -> {
            int variant = state.getValue(CactusFlowerBlock.VARIANT);
            ModelFile model = switch (variant) {
                case 1 -> variant1;
                case 2 -> variant2;
                default -> variant0;
            };

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .build();
        });
    }

    private void crossBlock(Block block, String textureName) {
        var model = models().cross(name(block), modLoc("block/" + textureName)).renderType("cutout");
        simpleBlock(block, model);
    }

    private BlockModelBuilder oakLeavesBaseModel(String name) {
        return models().getBuilder(name)
                .texture("leaves", mcLoc("block/oak_leaves"))
                .texture("particle", mcLoc("block/oak_leaves"))
                .renderType("cutout_mipped")
                .element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .end();
    }

    private BlockModelBuilder oakLeavesWithSideOverlayModel(String name, ResourceLocation overlayTexture) {
        return models().getBuilder(name)
                .texture("leaves", mcLoc("block/oak_leaves"))
                .texture("overlay", overlayTexture)
                .texture("particle", mcLoc("block/oak_leaves"))
                .renderType("cutout_mipped")

                .element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#leaves").tintindex(0).end()
                .end()

                .element()
                .from(-0.01F, -0.01F, -0.01F)
                .to(16.01F, 16.01F, 16.01F)
                .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#overlay").end()
                .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#overlay").end()
                .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#overlay").end()
                .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#overlay").end()
                .end();
    }

    private void saplingBlock(Block block, String textureName) {
        ModelFile model = models()
                .cross(name(block), modLoc("block/" + textureName))
                .renderType("cutout");

        simpleBlock(block, model);
    }

    private void pottedPlantBlock(Block block, String plantTextureName) {
        ModelFile model = models()
                .withExistingParent(name(block), mcLoc("block/flower_pot_cross"))
                .texture("plant", modLoc("block/" + plantTextureName))
                .texture("dirt", mcLoc("block/dirt"))
                .texture("flowerpot", mcLoc("block/flower_pot"));

        simpleBlock(block, model);
    }

    private String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}

