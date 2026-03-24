package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.FruitingOakLeavesBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FloraExpansion.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        saplingBlock(ModBlocks.APPLE_CORE.get(), "planted_apple_core");
        pottedPlantBlock(ModBlocks.POTTED_APPLE_CORE.get(), "planted_apple_core");
        fruitingOakLeavesBlock();
    }

    private void fruitingOakLeavesBlock() {
        Block block = ModBlocks.FRUITING_OAK_LEAVES.get();

        BlockModelBuilder stage0 = models()
                .withExistingParent("fruiting_oak_leaves_stage0", mcLoc("block/leaves"))
                .texture("all", modLoc("block/fruiting_oak_leaves_stage0"));

        BlockModelBuilder stage1 = models()
                .withExistingParent("fruiting_oak_leaves_stage1", mcLoc("block/leaves"))
                .texture("all", modLoc("block/fruiting_oak_leaves_stage1"));

        getVariantBuilder(block).forAllStates(state -> {
            int age = state.getValue(FruitingOakLeavesBlock.AGE);
            ModelFile model = age == 1 ? stage1 : stage0;

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .build();
        });

        simpleBlockItem(block, new ModelFile.UncheckedModelFile(modLoc("block/fruiting_oak_leaves_stage1")));
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
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}