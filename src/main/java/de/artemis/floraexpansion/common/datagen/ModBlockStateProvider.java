package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.CactusThornBlock;
import de.artemis.floraexpansion.common.block.FruitingOakLeavesBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
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
        saplingBlock(ModBlocks.APPLE_CORE.get(), "planted_apple_core");
        pottedPlantBlock(ModBlocks.POTTED_APPLE_CORE.get(), "planted_apple_core");
        fruitingOakLeavesBlock();
        giantCactusBaseBlock();
        giantCactusStemBlock();
        strippedGiantCactusBaseBlock();
        cactusThornBlock();
        crossBlock(ModBlocks.CACTUS_FLOWER.get(), "cactus_flower");
        cactusWoodFromBaseTextures(ModBlocks.GIANT_CACTUS_WOOD, "giant_cactus_base_side");
        cactusWoodFromBaseTextures(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD, "stripped_giant_cactus_base_side");
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