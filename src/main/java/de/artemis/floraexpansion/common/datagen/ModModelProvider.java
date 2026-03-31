package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.CactusThornBlock;
import de.artemis.floraexpansion.common.block.FruitingCherryLeavesBlock;
import de.artemis.floraexpansion.common.block.FruitingOakLeavesBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.NotNull;

public class ModModelProvider extends ModelProvider {
    private static final TextureSlot LEAVES = TextureSlot.create("leaves");
    private static final TextureSlot OVERLAY = TextureSlot.create("overlay");

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
        itemModels.generateFlatItem(ModItems.PINE_CONE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PEBBLES.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PINE_NUTS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.TOASTED_PINE_NUTS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.TWIG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FOREST_SNACK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SWEET_BERRY_MIX.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FLAX_SEED.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FLAX_FIBER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FLAX_FLOWER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.LINEN_THREAD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.LINEN_CLOTH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CHERRIES.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CHERRY_JUICE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.APPLE_JUICE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_SIGN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_HANGING_SIGN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_BOAT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_CHEST_BOAT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_HELMET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_CHESTPLATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_LEGGINGS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_BOOTS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PRICKLY_PEAR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_SLICE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CACTUS_JUICE.get(), ModelTemplates.FLAT_ITEM);
    }

    private void registerSimpleBlockModels(BlockModelGenerators blockModels) {
        blockModels.createTrivialCube(ModBlocks.LINEN_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.PEBBLE_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.CACTUS_PLANKS.get());
        blockModels.createTrivialCube(ModBlocks.CACTUS_MOSAIC.get());

        blockModels.createRotatedPillarWithHorizontalVariant(
                ModBlocks.FLAX_BALE.get(),
                TexturedModel.COLUMN_ALT,
                TexturedModel.COLUMN_HORIZONTAL_ALT
        );

        blockModels.family(ModBlocks.CACTUS_PLANKS.get())
                .stairs(ModBlocks.CACTUS_STAIRS.get())
                .slab(ModBlocks.CACTUS_SLAB.get())
                .fence(ModBlocks.CACTUS_FENCE.get())
                .fenceGate(ModBlocks.CACTUS_FENCE_GATE.get())
                .button(ModBlocks.CACTUS_BUTTON.get())
                .pressurePlate(ModBlocks.CACTUS_PRESSURE_PLATE.get())
                .door(ModBlocks.CACTUS_DOOR.get())
                .trapdoor(ModBlocks.CACTUS_TRAPDOOR.get());

        blockModels.createHangingSign(
                ModBlocks.CACTUS_PLANKS.get(),
                ModBlocks.CACTUS_HANGING_SIGN.get(),
                ModBlocks.CACTUS_WALL_HANGING_SIGN.get()
        );
    }

    private void registerSpecialBlockModels(BlockModelGenerators blockModels) {
        createCrossBlockState(blockModels, ModBlocks.APPLE_CORE.get(), modBlock("planted_apple_core"));
        createPottedPlantBlockState(blockModels, ModBlocks.POTTED_APPLE_CORE.get(), modBlock("planted_apple_core"));

        createCrossBlockState(blockModels, ModBlocks.CHERRY_PIT.get(), modBlock("cherry_pit"));
        createPottedPlantBlockState(blockModels, ModBlocks.POTTED_CHERRY_PIT.get(), modBlock("cherry_pit"));

        createFruitingOakLeaves(blockModels);
        createFruitingCherryLeaves(blockModels);

        createRotatedPillar(
                blockModels,
                ModBlocks.GIANT_CACTUS_BASE.get(),
                modBlock("giant_cactus_base_side"),
                modBlock("giant_cactus_base_top")
        );

        createRotatedPillar(
                blockModels,
                ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get(),
                modBlock("stripped_giant_cactus_base_side"),
                modBlock("stripped_giant_cactus_base_top")
        );

        createRotatedPillar(
                blockModels,
                ModBlocks.GIANT_CACTUS_WOOD.get(),
                modBlock("giant_cactus_base_side"),
                modBlock("giant_cactus_base_side")
        );

        createRotatedPillar(
                blockModels,
                ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(),
                modBlock("stripped_giant_cactus_base_side"),
                modBlock("stripped_giant_cactus_base_side")
        );

        createGiantCactusStem(blockModels);
        createCactusThorn(blockModels);
        createCrossBlockState(blockModels, ModBlocks.CACTUS_FLOWER.get(), modBlock("cactus_flower"));
    }

    private void createFruitingOakLeaves(BlockModelGenerators blockModels) {
        Identifier stage0 = createOakLeavesBaseModel(blockModels, "fruiting_oak_leaves_stage0");
        Identifier stage1 = createOakLeavesOverlayModel(
                blockModels,
                "fruiting_oak_leaves_stage1",
                modBlock("fruiting_oak_leaves_stage1_overlay")
        );
        Identifier stage2 = createOakLeavesOverlayModel(
                blockModels,
                "fruiting_oak_leaves_stage2",
                modBlock("fruiting_oak_leaves_stage2_overlay")
        );

        Variant v0 = new Variant(stage0);
        Variant v1 = new Variant(stage1);
        Variant v2 = new Variant(stage2);

        blockModels.blockStateOutput.accept(
                MultiPartGenerator.multiPart(ModBlocks.FRUITING_OAK_LEAVES.get())
                        .with(
                                BlockModelGenerators.condition().term(FruitingOakLeavesBlock.AGE, 0),
                                BlockModelGenerators.variant(v0)
                        )
                        .with(
                                BlockModelGenerators.condition().term(FruitingOakLeavesBlock.AGE, 1),
                                BlockModelGenerators.variant(v1)
                        )
                        .with(
                                BlockModelGenerators.condition().term(FruitingOakLeavesBlock.AGE, 2),
                                BlockModelGenerators.variant(v2)
                        )
        );
    }

    private void createFruitingCherryLeaves(BlockModelGenerators blockModels) {
        Identifier stage0 = createCherryLeavesBaseModel(blockModels, "fruiting_cherry_leaves_stage0");
        Identifier stage1 = createCherryLeavesOverlayModel(
                blockModels,
                "fruiting_cherry_leaves_stage1",
                modBlock("fruiting_cherry_leaves_stage1_overlay")
        );
        Identifier stage2 = createCherryLeavesOverlayModel(
                blockModels,
                "fruiting_cherry_leaves_stage2",
                modBlock("fruiting_cherry_leaves_stage2_overlay")
        );
        Identifier stage3 = createCherryLeavesOverlayModel(
                blockModels,
                "fruiting_cherry_leaves_stage3",
                modBlock("fruiting_cherry_leaves_stage3_overlay")
        );

        Variant v0 = new Variant(stage0);
        Variant v1 = new Variant(stage1);
        Variant v2 = new Variant(stage2);
        Variant v3 = new Variant(stage3);

        blockModels.blockStateOutput.accept(
                MultiPartGenerator.multiPart(ModBlocks.FRUITING_CHERRY_LEAVES.get())
                        .with(
                                BlockModelGenerators.condition().term(FruitingCherryLeavesBlock.AGE, 0),
                                BlockModelGenerators.variant(v0)
                        )
                        .with(
                                BlockModelGenerators.condition().term(FruitingCherryLeavesBlock.AGE, 1),
                                BlockModelGenerators.variant(v1)
                        )
                        .with(
                                BlockModelGenerators.condition().term(FruitingCherryLeavesBlock.AGE, 2),
                                BlockModelGenerators.variant(v2)
                        )
                        .with(
                                BlockModelGenerators.condition().term(FruitingCherryLeavesBlock.AGE, 3),
                                BlockModelGenerators.variant(v3)
                        )
        );
    }

    private void createRotatedPillar(BlockModelGenerators blockModels, RotatedPillarBlock block, Identifier side, Identifier end) {
        TexturedModel.Provider vertical = TexturedModel.createDefault(
                ignored -> new TextureMapping()
                        .put(TextureSlot.SIDE, side)
                        .put(TextureSlot.END, end)
                        .put(TextureSlot.PARTICLE, side),
                ModelTemplates.CUBE_COLUMN
        );

        TexturedModel.Provider horizontal = TexturedModel.createDefault(
                ignored -> new TextureMapping()
                        .put(TextureSlot.SIDE, side)
                        .put(TextureSlot.END, end)
                        .put(TextureSlot.PARTICLE, side),
                ModelTemplates.CUBE_COLUMN_HORIZONTAL
        );

        blockModels.createRotatedPillarWithHorizontalVariant(block, vertical, horizontal);
    }

    private void createGiantCactusStem(BlockModelGenerators blockModels) {
        TexturedModel.Provider provider = TexturedModel.createDefault(
                ignored -> new TextureMapping()
                        .put(TextureSlot.SIDE, modBlock("giant_cactus_stem_side"))
                        .put(TextureSlot.TOP, modBlock("giant_cactus_stem_top"))
                        .put(TextureSlot.BOTTOM, modBlock("giant_cactus_stem_bottom"))
                        .put(TextureSlot.PARTICLE, modBlock("giant_cactus_stem_top")),
                ModelTemplates.CUBE_BOTTOM_TOP.extend()
                        .renderType("cutout")
                        .build()
        );

        blockModels.createTrivialBlock(ModBlocks.GIANT_CACTUS_STEM.get(), provider);
    }

    private void createCactusThorn(BlockModelGenerators blockModels) {
        Identifier model = ModelTemplates.CROSS.create(
                ModelLocationUtils.getModelLocation(ModBlocks.CACTUS_THORN.get()),
                new TextureMapping()
                        .put(TextureSlot.CROSS, modBlock("cactus_thorn"))
                        .put(TextureSlot.PARTICLE, modBlock("cactus_thorn")),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.CACTUS_THORN.get(), BlockModelGenerators.plainVariant(model))
                        .with(PropertyDispatch.modify(CactusThornBlock.FACE)
                                .select(AttachFace.FLOOR, BlockModelGenerators.NOP)
                                .select(AttachFace.CEILING, BlockModelGenerators.X_ROT_180)
                                .select(AttachFace.WALL, BlockModelGenerators.X_ROT_90))
                        .with(PropertyDispatch.modify(CactusThornBlock.FACING)
                                .select(Direction.NORTH, BlockModelGenerators.NOP)
                                .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
                                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                                .select(Direction.WEST, BlockModelGenerators.Y_ROT_270))
        );
    }

    private void createCrossBlockState(BlockModelGenerators blockModels, Block block, Identifier texture) {
        Identifier model = ModelTemplates.CROSS.create(
                ModelLocationUtils.getModelLocation(block),
                new TextureMapping()
                        .put(TextureSlot.CROSS, texture)
                        .put(TextureSlot.PARTICLE, texture),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(model))
        );
    }

    private void createPottedPlantBlockState(BlockModelGenerators blockModels, Block block, Identifier plantTexture) {
        Identifier model = ModelTemplates.FLOWER_POT_CROSS.create(
                ModelLocationUtils.getModelLocation(block),
                new TextureMapping()
                        .put(TextureSlot.PLANT, plantTexture),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(model))
        );
    }

    private Identifier createOakLeavesBaseModel(BlockModelGenerators blockModels, String name) {
        return ExtendedModelTemplateBuilder.builder()
                .renderType("cutout_mipped")
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(LEAVES)
                .element(e -> e
                        .from(0, 0, 0)
                        .to(16, 16, 16)
                        .face(Direction.DOWN, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.UP, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.NORTH, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.SOUTH, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.WEST, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.EAST, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0)))
                .build()
                .create(
                        modModel(name),
                        new TextureMapping()
                                .put(TextureSlot.PARTICLE, mcBlock("oak_leaves"))
                                .put(LEAVES, mcBlock("oak_leaves")),
                        blockModels.modelOutput
                );
    }

    private Identifier createOakLeavesOverlayModel(BlockModelGenerators blockModels, String name, Identifier overlayTexture) {
        return ExtendedModelTemplateBuilder.builder()
                .renderType("cutout_mipped")
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(LEAVES)
                .requiredTextureSlot(OVERLAY)
                .element(e -> e
                        .from(0, 0, 0)
                        .to(16, 16, 16)
                        .face(Direction.DOWN, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.UP, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.NORTH, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.SOUTH, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.WEST, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0))
                        .face(Direction.EAST, f -> f.texture(LEAVES).uvs(0, 0, 16, 16).tintindex(0)))
                .element(e -> e
                        .from(-0.01F, -0.01F, -0.01F)
                        .to(16.01F, 16.01F, 16.01F)
                        .face(Direction.NORTH, f -> f.texture(OVERLAY).uvs(0, 0, 16, 16))
                        .face(Direction.SOUTH, f -> f.texture(OVERLAY).uvs(0, 0, 16, 16))
                        .face(Direction.WEST, f -> f.texture(OVERLAY).uvs(0, 0, 16, 16))
                        .face(Direction.EAST, f -> f.texture(OVERLAY).uvs(0, 0, 16, 16)))
                .build()
                .create(
                        modModel(name),
                        new TextureMapping()
                                .put(TextureSlot.PARTICLE, mcBlock("oak_leaves"))
                                .put(LEAVES, mcBlock("oak_leaves"))
                                .put(OVERLAY, overlayTexture),
                        blockModels.modelOutput
                );
    }

    private Identifier createCherryLeavesBaseModel(BlockModelGenerators blockModels, String name) {
        return ExtendedModelTemplateBuilder.builder()
                .renderType("cutout_mipped")
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(LEAVES)
                .element(e -> e
                        .from(0, 0, 0)
                        .to(16, 16, 16)
                        .face(Direction.DOWN, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.UP, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.NORTH, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.SOUTH, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.WEST, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.EAST, f -> f.texture(LEAVES).uvs(0, 0, 16, 16)))
                .build()
                .create(
                        modModel(name),
                        new TextureMapping()
                                .put(TextureSlot.PARTICLE, mcBlock("cherry_leaves"))
                                .put(LEAVES, mcBlock("cherry_leaves")),
                        blockModels.modelOutput
                );
    }

    private Identifier createCherryLeavesOverlayModel(BlockModelGenerators blockModels, String name, Identifier overlayTexture) {
        return ExtendedModelTemplateBuilder.builder()
                .renderType("cutout_mipped")
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(LEAVES)
                .requiredTextureSlot(OVERLAY)
                .element(e -> e
                        .from(0, 0, 0)
                        .to(16, 16, 16)
                        .face(Direction.DOWN, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.UP, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.NORTH, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.SOUTH, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.WEST, f -> f.texture(LEAVES).uvs(0, 0, 16, 16))
                        .face(Direction.EAST, f -> f.texture(LEAVES).uvs(0, 0, 16, 16)))
                .element(e -> e
                        .from(-0.01F, -0.01F, -0.01F)
                        .to(16.01F, 16.01F, 16.01F)
                        .face(Direction.NORTH, f -> f.texture(OVERLAY).uvs(0, 0, 16, 16))
                        .face(Direction.SOUTH, f -> f.texture(OVERLAY).uvs(0, 0, 16, 16))
                        .face(Direction.WEST, f -> f.texture(OVERLAY).uvs(0, 0, 16, 16))
                        .face(Direction.EAST, f -> f.texture(OVERLAY).uvs(0, 0, 16, 16)))
                .build()
                .create(
                        modModel(name),
                        new TextureMapping()
                                .put(TextureSlot.PARTICLE, mcBlock("cherry_leaves"))
                                .put(LEAVES, mcBlock("cherry_leaves"))
                                .put(OVERLAY, overlayTexture),
                        blockModels.modelOutput
                );
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
}