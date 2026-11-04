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
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;


import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@SuppressWarnings({"deprecation", "unused"})
public class ModModelProvider implements net.minecraft.data.DataProvider {
    private final PackOutput output;
    public ModModelProvider(PackOutput output) { this.output = output; }
    @NotNull @Override public String getName() { return "Flora Expansion block and item models"; }
    @NotNull @Override public java.util.concurrent.CompletableFuture<?> run(@NotNull net.minecraft.data.CachedOutput cache) {
        var states = new java.util.LinkedHashMap<Block, net.minecraft.data.models.blockstates.BlockStateGenerator>();
        var models = new java.util.LinkedHashMap<ResourceLocation, java.util.function.Supplier<com.google.gson.JsonElement>>();
        java.util.function.BiConsumer<ResourceLocation, java.util.function.Supplier<com.google.gson.JsonElement>> save = (id, json) -> {
            if (models.putIfAbsent(id, json) != null) throw new IllegalStateException("Duplicate model: " + id);
        };
        var blocks = new BlockModelGenerators(generator -> {
            if (states.putIfAbsent(generator.getBlock(), generator) != null) throw new IllegalStateException("Duplicate blockstate: " + generator.getBlock());
        }, save, item -> {});
        var items = new ItemModelGenerators(save);
        registerModels(blocks, items);
        for (var entry : ModBlocks.BLOCKS.getEntries()) {
            var block = entry.get(); var item = block.asItem();
            if (item == net.minecraft.world.item.Items.AIR) continue;
            var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).withPrefix("item/");
            if (!models.containsKey(id) && !java.util.Set.of("item/glasswort", "item/glass_jar", "item/salt_spike", "item/leaf_litter").contains(id.getPath())) {
                var parent = java.util.Objects.requireNonNull(entry.getId()).withPrefix("block/");
                models.put(id, () -> { var json=new JsonObject();json.addProperty("parent", parent.toString());return json; });
            }
        }
        var statePaths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        var modelPaths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        var writes = new java.util.ArrayList<java.util.concurrent.CompletableFuture<?>>();
        states.forEach((block, json) -> writes.add(net.minecraft.data.DataProvider.saveStable(cache, json.get(), statePaths.json(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block)))));
        models.forEach((id, json) -> writes.add(net.minecraft.data.DataProvider.saveStable(cache, json.get(), modelPaths.json(id))));
        return java.util.concurrent.CompletableFuture.allOf(writes.toArray(java.util.concurrent.CompletableFuture[]::new));
    }
    private static BlockModelGenerators.BlockFamilyProvider familyWithExistingFullBlock(BlockModelGenerators generators, Block block) {
        var family = generators.new BlockFamilyProvider(TextureMapping.cube(block));
        family.fullBlock = net.minecraft.data.models.model.ModelLocationUtils.getModelLocation(block);
        return family;
    }
    private static Variant plainVariant(ResourceLocation model) { return Variant.variant().with(VariantProperties.MODEL, model); }
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        registerItemModels(blockModels, itemModels);
        registerSimpleBlockModels(blockModels);
        registerSpecialBlockModels(blockModels);
    }

    private void registerItemModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.MUSIC_DISC_INTO_THE_CAVES.get(), "floraexpansion:item/music_disc_into_the_caves");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.SALT_WATER_BUCKET.get(), "floraexpansion:item/salt_water_bucket");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.WOODEN_SALT_WATER_BUCKET.get(), "floraexpansion:item/wooden_salt_water_bucket");
        for (Item item : java.util.List.of(ModItems.SALTED_GLASSWORT.get(), ModItems.SALTED_SPRUCE_NUTS.get(),
                ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(), ModItems.SALTED_RAW_MEAT.get(), ModItems.SALTED_RAW_FISH.get(),
                ModItems.CURED_MEAT.get(), ModItems.CURED_FISH.get())) {
            registerGeneratedItemWithTexture(blockModels, itemModels, item,
                    "floraexpansion:item/" + item.builtInRegistryHolder().key().location().getPath());
        }
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.SALT.get(), "floraexpansion:item/salt");
        registerGeneratedItemWithTexture(blockModels, itemModels, ModItems.RAW_SALT.get(), "floraexpansion:item/raw_salt");
        blockModels.delegateItemModel(ModBlocks.CRYSTALLINE_SALT.get(), modModel("crystalline_salt"));
        blockModels.delegateItemModel(ModBlocks.SMOOTH_SALT.get(), modModel("smooth_salt"));
        blockModels.delegateItemModel(ModBlocks.CUT_SALT.get(), modModel("cut_salt"));
        blockModels.delegateItemModel(ModBlocks.CHISELED_SALT.get(), modModel("chiseled_salt"));
        blockModels.delegateItemModel(ModBlocks.SALTING_TUB.get(), modModel("salting_tub"));
        blockModels.delegateItemModel(ModBlocks.HAND_MILL.get(), modModel("hand_mill_item"));
        blockModels.delegateItemModel(ModBlocks.HAND_CRANK.get(), modModel("hand_crank_item"));
        registerCrateItemModel(blockModels);

        itemModels.generateFlatItem(ModBlocks.APPLE_CORE.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.SMALL_CACTUS.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.CACTUS_SPINE.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.CHERRY_PIT.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.NEEDLE_LITTER.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.SCATTERED_PEBBLES.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModBlocks.STRAWBERRY_CAKE.get().asItem(), ModelTemplates.FLAT_ITEM);
        blockModels.createSimpleFlatItemModel(ModBlocks.TWIG_LADDER.get());
        blockModels.delegateItemModel(ModBlocks.ORCHARD_CHERRY_LEAVES.get(), modModel("orchard_cherry_leaves_stage3"));
        blockModels.delegateItemModel(ModBlocks.APPLE_LEAVES.get(), modModel("apple_leaves_stage2"));
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
        createStrawberryCakeModels(blockModels);
        createStrawberryCropModels(blockModels);
        blockModels.createFullAndCarpetBlocks(ModBlocks.LINEN_BALE.get(), ModBlocks.LINEN_CARPET.get());
        createTwigLadderBlockModel(blockModels);
        blockModels.createNonTemplateHorizontalBlock(ModBlocks.TWIG_LADDER.get());
    }

    private void registerSpecialBlockModels(BlockModelGenerators blockModels) {
        createSaltWaterModels(blockModels);
        blockModels.modelOutput.accept(modModel("salting_tub"), SaltingTubModel::create);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.SALTING_TUB.get(),
                plainVariant(modModel("salting_tub"))));
        blockModels.modelOutput.accept(modModel("hand_mill"), ModelJsonTemplates::createHandMillModelJson);
        blockModels.modelOutput.accept(modModel("hand_mill_item"), ModelJsonTemplates::createHandMillItemModelJson);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.HAND_MILL.get(),
                plainVariant(modModel("hand_mill"))));
        blockModels.modelOutput.accept(modModel("hand_crank"), ModelJsonTemplates::createHandCrankBlockModelJson);
        blockModels.modelOutput.accept(modModel("hand_crank_item"), () -> createHandCrankModelJson(0));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.HAND_CRANK.get(),
                plainVariant(modModel("hand_crank"))));
        blockModels.new BlockFamilyProvider(TextureMapping.cube(modBlockMaterial("smooth_salt")))
                .fullBlock(ModBlocks.SMOOTH_SALT.get(), ModelTemplates.CUBE_ALL);
        blockModels.modelOutput.accept(modModel("cut_salt"), () -> {
            var json = new com.google.gson.JsonObject();
            json.addProperty("parent", "minecraft:block/cube_column");
            var textures = new com.google.gson.JsonObject();
            textures.addProperty("side", "floraexpansion:block/cut_salt");
            textures.addProperty("end", "floraexpansion:block/smooth_salt");
            json.add("textures", textures);
            return json;
        });
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.CUT_SALT.get(),
                plainVariant(modModel("cut_salt"))));
        blockModels.modelOutput.accept(modModel("chiseled_salt"), () -> {
            var json = new com.google.gson.JsonObject();
            json.addProperty("parent", "minecraft:block/cube_column");
            var textures = new com.google.gson.JsonObject();
            textures.addProperty("side", "floraexpansion:block/chiseled_salt");
            textures.addProperty("end", "floraexpansion:block/smooth_salt");
            json.add("textures", textures);
            return json;
        });
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.CHISELED_SALT.get(),
                plainVariant(modModel("chiseled_salt"))));
        blockModels.new BlockFamilyProvider(TextureMapping.cube(modBlockMaterial("crystalline_salt")))
                .fullBlock(ModBlocks.CRYSTALLINE_SALT.get(), ModelTemplates.CUBE_ALL)
                .stairs(ModBlocks.CRYSTALLINE_SALT_STAIRS.get()).slab(ModBlocks.CRYSTALLINE_SALT_SLAB.get()).wall(ModBlocks.CRYSTALLINE_SALT_WALL.get());
        var rawStates = PropertyDispatch.property(de.artemis.floraexpansion.common.block.SaltDepositBlock.LAYERS);
        for (int layers = 1; layers <= 4; layers++) {
            int height = layers * 4;
            var model = modModel("salt_deposit_" + layers);
            blockModels.modelOutput.accept(model, () -> {
                var json = com.google.gson.JsonParser.parseString("{\"parent\":\"minecraft:block/block\",\"textures\":{\"particle\":\"floraexpansion:block/salt_deposit\",\"all\":\"floraexpansion:block/salt_deposit\"},\"elements\":[{\"from\":[0,0,0],\"to\":[16," + height + ",16],\"faces\":{}}]}").getAsJsonObject();
                var faces = json.getAsJsonArray("elements").get(0).getAsJsonObject().getAsJsonObject("faces");
                for (String face : java.util.List.of("up", "down", "north", "south", "east", "west")) {
                    var data = new com.google.gson.JsonObject(); data.addProperty("texture", "#all"); faces.add(face, data);
                }
                return json;
            });
            rawStates.select(layers, plainVariant(model));
        }
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.SALT_DEPOSIT.get()).with(rawStates));
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

    private void createSaltWaterModels(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modModel("salt_water"), () -> {
            var model = new JsonObject();
            var textures = new JsonObject();
            textures.addProperty("particle", "minecraft:block/water_still");
            model.add("textures", textures);
            return model;
        });
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.SALT_WATER.get(),
                plainVariant(modModel("salt_water"))));
        var states = PropertyDispatch.property(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL);
        var templates = new net.minecraft.data.models.model.ModelTemplate[]{ModelTemplates.CAULDRON_LEVEL1,
                ModelTemplates.CAULDRON_LEVEL2, ModelTemplates.CAULDRON_FULL};
        for (int fill=1; fill<=3; fill++) {
            var model = templates[fill-1].createWithSuffix(ModBlocks.SALT_WATER_CAULDRON.get(), "_level" + fill,
                    TextureMapping.cauldron(ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still")), blockModels.modelOutput);
            states.select(fill, plainVariant(model));
        }
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.SALT_WATER_CAULDRON.get()).with(states));
    }

    private void createJarModels(BlockModelGenerators blockModels) {
        for (Block jar : new Block[]{ModBlocks.GLASS_JAR.get(), ModBlocks.BLUEBERRY_JAM.get(),
                ModBlocks.STRAWBERRY_JAM.get(), ModBlocks.CHERRY_JAM.get(), ModBlocks.APPLE_JAM.get()}) {
            String name = jar.builtInRegistryHolder().key().location().getPath();
            if (jar instanceof JamJarBlock) {
                var variants = PropertyDispatch.property(JamJarBlock.PORTIONS);
                for (int portions = 1; portions <= JamJarBlock.MAX_PORTIONS; portions++) {
                    int amount = portions;
                    ResourceLocation model = modModel(name + (portions == JamJarBlock.MAX_PORTIONS ? "" : "_" + portions));
                    blockModels.modelOutput.accept(model, () -> createJarModelJson(name, amount));
                    variants.select(portions, plainVariant(model));
                }
                blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(jar).with(variants));
            } else {
                ResourceLocation model = modModel(name);
                blockModels.modelOutput.accept(model, () -> createJarModelJson(null, 0));
                blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(jar, plainVariant(model)));
            }
        }
    }

    private void registerCrateItemModel(BlockModelGenerators blockModels) {
        blockModels.modelOutput.accept(modItemModel("crate"), () -> {
            var json = new JsonObject();
            json.addProperty("parent", "floraexpansion:block/crate");
            var overrides = new com.google.gson.JsonArray(); var entry = new JsonObject(); var predicate = new JsonObject();
            predicate.addProperty("minecraft:custom_model_data", 1);entry.add("predicate", predicate);entry.addProperty("model", modItemModel("crate_packed").toString());overrides.add(entry);json.add("overrides", overrides);return json;
        });
    }

    private void registerJamItemModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        for (Item jam : new Item[]{ModItems.BLUEBERRY_JAM.get(), ModItems.STRAWBERRY_JAM.get(), ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get()}) {
            String name = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(jam).getPath();
            itemModels.output.accept(modItemModel(name), () -> {
                var json = createGeneratedItemModelJson("floraexpansion:item/" + name);
                var overrides = new com.google.gson.JsonArray();
                for (int portions = 3; portions >= 1; portions--) {
                    var entry = new JsonObject();
                    var predicate = new JsonObject();
                    predicate.addProperty("floraexpansion:eaten_portions", 4 - portions);
                    entry.add("predicate", predicate);
                    entry.addProperty("model", modItemModel(name + "_portion_" + portions).toString());
                    overrides.add(entry);
                }
                json.add("overrides", overrides);
                return json;
            });
            for (int portions=1;portions<4;portions++) {
                String variant=name+"_portion_"+portions;
                itemModels.output.accept(modItemModel(variant), () -> createGeneratedItemModelJson("floraexpansion:item/"+variant));
            }
        }
    }
    private void registerExistingItemModel(BlockModelGenerators blockModels, Item item, String modelName) {

    }

    private void registerGeneratedItemWithTexture(BlockModelGenerators blockModels, ItemModelGenerators itemModels, Item item, String texture) {
        ResourceLocation model = modItemModel(item.builtInRegistryHolder().key().location().getPath());
        itemModels.output.accept(model, () -> createGeneratedItemModelJson(texture));

    }

    private void createBlueberryBushBlockState(BlockModelGenerators blockModels) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.BLUEBERRY_BUSH.get())
                        .with(PropertyDispatch.property(SmallBlueberryBushBlock.AGE)
                                .generate(age -> plainVariant(modModel("small_blueberry_bush_stage" + age))))
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.LARGE_BLUEBERRY_BUSH.get())
                        .with(PropertyDispatch.property(LargeBlueberryBushBlock.AGE)
                                .generate(age -> plainVariant(modModel("large_blueberry_bush_stage" + age))))
        );
    }

    private void createCrateBlockState(BlockModelGenerators blockModels) {
        Variant open = plainVariant(modModel("crate"));
        Variant closed = plainVariant(mcBlock("barrel"));
        Variant packed = plainVariant(modModel("crate_packed"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.CRATE.get())
                        .with(PropertyDispatch.properties(CrateBlock.PACKED, CrateBlock.POWERED)
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
                    net.minecraft.data.models.blockstates.Condition.condition().term(de.artemis.floraexpansion.common.block.SmallCactusBlock.PICKLES, pickles),
                    plainVariant(modModel("small_cactus_" + pickles))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createDesertMossBlockState(BlockModelGenerators blockModels) {
        for (Block moss : java.util.List.of(ModBlocks.DESERT_MOSS.get(), ModBlocks.FLOWERING_DESERT_MOSS.get())) {
            MultiPartGenerator generator = MultiPartGenerator.multiPart(moss);
            String name = moss.builtInRegistryHolder().key().location().getPath();
            for (int variant = 0; variant <= 3; variant++) {
                generator.with(
                        net.minecraft.data.models.blockstates.Condition.condition().term(DesertMossBlock.VARIANT, variant),
                        plainVariant(modModel(name + "_" + variant))
                );
            }
            blockModels.blockStateOutput.accept(generator);
        }
    }

    private void createFlaxBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.FLAX.get());

        for (int age = 0; age <= FlaxBlock.AGE.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(4); age++) {
            generator.with(
                    net.minecraft.data.models.blockstates.Condition.condition()
                            .term(FlaxBlock.AGE, age)
                            .term(FlaxBlock.HALF, DoubleBlockHalf.LOWER),
                    plainVariant(modModel(getFlaxLowerModel(age)))
            );
            generator.with(
                    net.minecraft.data.models.blockstates.Condition.condition()
                            .term(FlaxBlock.AGE, age)
                            .term(FlaxBlock.HALF, DoubleBlockHalf.UPPER),
                    plainVariant(modModel(getFlaxUpperModel(age)))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createCactusSpineModels(BlockModelGenerators blockModels) {
        ResourceLocation model = modModel("cactus_spine");

        blockModels.modelOutput.accept(model, () -> createCutoutCrossModelJson("floraexpansion:block/cactus_spine"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.CACTUS_SPINE.get(), plainVariant(model))
                        .with(PropertyDispatch.property(de.artemis.floraexpansion.common.block.CactusSpineBlock.FACE)
                                .select(AttachFace.FLOOR, Variant.variant())
                                .select(AttachFace.CEILING, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.WALL, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)))
                        .with(PropertyDispatch.property(de.artemis.floraexpansion.common.block.CactusSpineBlock.FACING)
                                .select(Direction.NORTH, Variant.variant())
                                .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))
        );
    }

    private void createNeedleLitterBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.NEEDLE_LITTER.get());

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (int segment = NeedleLitterBlock.MIN_SEGMENTS; segment <= NeedleLitterBlock.MAX_SEGMENTS; segment++) {
                for (int amount = segment; amount <= NeedleLitterBlock.MAX_SEGMENTS; amount++) {
                    generator.with(
                            net.minecraft.data.models.blockstates.Condition.condition()
                                    .term(NeedleLitterBlock.FACING, facing)
                                    .term(NeedleLitterBlock.AMOUNT, amount),
                            createNeedleLitterVariant(segment, facing)
                    );
                }
            }
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private Variant createNeedleLitterVariant(int segment, Direction facing) {
        Variant variant = plainVariant(modModel("needle_litter_" + segment));

        return switch (facing) {
            case NORTH -> variant;
            case EAST -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            case SOUTH -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
            case WEST -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
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
                        net.minecraft.data.models.blockstates.Condition.condition()
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
                    net.minecraft.data.models.blockstates.Condition.condition().term(OrchardCherryLeavesBlock.AGE, age),
                    plainVariant(modModel("orchard_cherry_leaves_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createAppleLeavesBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.APPLE_LEAVES.get());

        for (int age = 0; age <= AppleLeavesBlock.MAX_AGE; age++) {
            generator.with(
                    net.minecraft.data.models.blockstates.Condition.condition().term(AppleLeavesBlock.AGE, age),
                    plainVariant(modModel("apple_leaves_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createPricklyPearCactusBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.PRICKLY_PEAR_CACTUS.get());

        for (int age = 0; age <= PricklyPearCactusBlock.MAX_AGE; age++) {
            generator.with(
                    net.minecraft.data.models.blockstates.Condition.condition().term(PricklyPearCactusBlock.AGE, age),
                    plainVariant(modModel("prickly_pear_cactus_stage" + age))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createGiantCactusShootBlockState(BlockModelGenerators blockModels) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.GIANT_CACTUS_SHOOT.get());

        for (int variant = 0; variant < 3; variant++) {
            generator.with(
                    net.minecraft.data.models.blockstates.Condition.condition().term(GiantCactusShootBlock.VARIANT, variant),
                    plainVariant(modModel("giant_cactus_shoot_variant_" + variant))
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private void createCactusWoodBlockState(BlockModelGenerators blockModels, Block block, String modelName) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

        for (boolean generated : new boolean[]{false, true}) {
            generator.with(
                    net.minecraft.data.models.blockstates.Condition.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                            .term(CactusWoodBlock.GENERATED, generated),
                    plainVariant(modModel(modelName))
            );
            generator.with(
                    net.minecraft.data.models.blockstates.Condition.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.X)
                            .term(CactusWoodBlock.GENERATED, generated),
                    plainVariant(modModel(modelName + "_horizontal"))
                            .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
            );
            generator.with(
                    net.minecraft.data.models.blockstates.Condition.condition()
                            .term(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                            .term(CactusWoodBlock.GENERATED, generated),
                    plainVariant(modModel(modelName + "_horizontal"))
                            .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
            );
        }

        blockModels.blockStateOutput.accept(generator);
    }

    private Variant createScatteredPebblesVariant(int amount, Direction facing) {
        Variant variant = plainVariant(modModel("scattered_pebbles_" + (amount - 1)));

        return switch (facing) {
            case NORTH -> variant;
            case EAST -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            case SOUTH -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
            case WEST -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
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
        ResourceLocation cherryPitModel = modModel("cherry_pit");
        ResourceLocation pottedCherryPitModel = modModel("potted_cherry_pit");

        blockModels.modelOutput.accept(cherryPitModel, () -> createCutoutCrossModelJson("floraexpansion:block/planted_cherry_pit"));
        blockModels.modelOutput.accept(pottedCherryPitModel, () -> createCutoutPottedPlantModelJson("floraexpansion:block/planted_cherry_pit"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.CHERRY_PIT.get(), plainVariant(cherryPitModel))
        );
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.POTTED_CHERRY_PIT.get(), plainVariant(pottedCherryPitModel))
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
        ResourceLocation cactusPlanksModel = ModelTemplates.CUBE_ALL.create(
                ModBlocks.CACTUS_PLANKS.get(),
                TextureMapping.cube(modBlockMaterial("cactus_planks")),
                blockModels.modelOutput
        );
        ResourceLocation cactusMosaicModel = ModelTemplates.CUBE_ALL.create(
                ModBlocks.CACTUS_MOSAIC.get(),
                TextureMapping.cube(modBlockMaterial("cactus_mosaic")),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.CACTUS_PLANKS.get(), plainVariant(cactusPlanksModel))
        );
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.CACTUS_MOSAIC.get(), plainVariant(cactusMosaicModel))
        );
        blockModels.delegateItemModel(ModBlocks.CACTUS_PLANKS.get(), cactusPlanksModel);
        blockModels.delegateItemModel(ModBlocks.CACTUS_MOSAIC.get(), cactusMosaicModel);

        BlockFamily family = new BlockFamily.Builder(ModBlocks.CACTUS_PLANKS.get())
                .stairs(ModBlocks.CACTUS_STAIRS.get())
                .slab(ModBlocks.CACTUS_SLAB.get())
                .fence(ModBlocks.CACTUS_FENCE.get())
                .fenceGate(ModBlocks.CACTUS_FENCE_GATE.get())
                .button(ModBlocks.CACTUS_BUTTON.get())
                .pressurePlate(ModBlocks.CACTUS_PRESSURE_PLATE.get())
                .getFamily();
        familyWithExistingFullBlock(blockModels, ModBlocks.CACTUS_PLANKS.get()).generateFor(family);
        BlockFamily mosaicFamily = new BlockFamily.Builder(ModBlocks.CACTUS_MOSAIC.get())
                .stairs(ModBlocks.CACTUS_MOSAIC_STAIRS.get()).slab(ModBlocks.CACTUS_MOSAIC_SLAB.get()).getFamily();
        familyWithExistingFullBlock(blockModels, ModBlocks.CACTUS_MOSAIC.get()).generateFor(mosaicFamily);
        createCactusDoorModels(blockModels);
        createCactusTrapdoorModels(blockModels);

        createCactusSignModels(blockModels);
    }

    private void createCactusSignModels(BlockModelGenerators blockModels) {
        // In 26.1 the sign board is rendered by the block entity, with particle-only block models.
        var model = plainVariant(ModelTemplates.PARTICLE_ONLY.create(ModBlocks.CACTUS_SIGN.get(), TextureMapping.cube(ModBlocks.CACTUS_PLANKS.get()), blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.CACTUS_SIGN.get(), model));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.CACTUS_WALL_SIGN.get(), model));

        var hanging = plainVariant(ModelTemplates.PARTICLE_ONLY.create(ModBlocks.CACTUS_HANGING_SIGN.get(), new TextureMapping().put(TextureSlot.PARTICLE, modBlockMaterial("stripped_cactus_log_side")), blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.CACTUS_HANGING_SIGN.get(), hanging));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.CACTUS_WALL_HANGING_SIGN.get(), hanging));
    }

    private void createAppleCoreModels(BlockModelGenerators blockModels) {
        ResourceLocation appleCoreModel = modModel("apple_core");
        ResourceLocation pottedAppleCoreModel = modModel("potted_apple_core");

        blockModels.modelOutput.accept(appleCoreModel, () -> createCutoutCrossModelJson("floraexpansion:block/planted_apple_core"));
        blockModels.modelOutput.accept(pottedAppleCoreModel, () -> createCutoutPottedPlantModelJson("floraexpansion:block/planted_apple_core"));

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.APPLE_CORE.get(), plainVariant(appleCoreModel))
        );
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.POTTED_APPLE_CORE.get(), plainVariant(pottedAppleCoreModel))
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
        ResourceLocation giantCactusStemModel = modModel("giant_cactus_stem");
        ResourceLocation pottedGiantCactusStemModel = modModel("potted_giant_cactus_stem");

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
                MultiVariantGenerator.multiVariant(ModBlocks.GIANT_CACTUS_STEM.get(), plainVariant(giantCactusStemModel))
        );
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.POTTED_GIANT_CACTUS_STEM.get(), plainVariant(pottedGiantCactusStemModel))
        );
        blockModels.delegateItemModel(ModBlocks.GIANT_CACTUS_STEM.get(), giantCactusStemModel);
    }

    private void createGiantCactusShootModels(BlockModelGenerators blockModels) {
        ResourceLocation giantCactusShootItemModel = modModel("giant_cactus_shoot_item");
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
        blockModels.delegateItemModel(ModBlocks.GIANT_CACTUS_SHOOT.get(), giantCactusShootItemModel);
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
        var variants = PropertyDispatch.properties(BlockStateProperties.BITES, StrawberryCakeBlock.HALF_SLICE);
        for (int bites = 0; bites <= 6; bites++) {
            String whole = bites == 0 ? "strawberry_cake" : "strawberry_cake_slice" + bites;
            variants.select(bites, false, plainVariant(modModel(whole)));
            variants.select(bites, true, plainVariant(modModel("strawberry_cake_slice" + bites + "_half")));
        }
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.STRAWBERRY_CAKE.get()).with(variants));

        ModBlocks.STRAWBERRY_CANDLE_CAKES.forEach(cake -> createStrawberryCakeWithCandleBlockState(blockModels, cake.get()));
    }

    private void createStrawberryCakeWithCandleBlockState(BlockModelGenerators blockModels, Block candleCake) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(candleCake,
                plainVariant(modModel("strawberry_cake"))));
    }

    private void createStrawberryCropBlockState(BlockModelGenerators blockModels) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.STRAWBERRY_PLANT.get())
                        .with(PropertyDispatch.properties(StrawberryCropBlock.AGE, StrawberryCropBlock.WILD)
                                .generate((age, wild) -> plainVariant(modModel("strawberry_plant_stage" + age))))
        );
    }

    private void createFlaxBaleModels(BlockModelGenerators blockModels) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.END, modBlockMaterial("flax_bale_top"))
                .put(TextureSlot.SIDE, modBlockMaterial("flax_bale_side"));

        ResourceLocation verticalModel = ModelTemplates.CUBE_COLUMN.create(ModBlocks.FLAX_BALE.get(), textures, blockModels.modelOutput);
        ResourceLocation horizontalModel = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(ModBlocks.FLAX_BALE.get(), textures, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
                        ModBlocks.FLAX_BALE.get(),
                        verticalModel,
                        horizontalModel
                )
        );
        blockModels.delegateItemModel(ModBlocks.FLAX_BALE.get(), verticalModel);
    }

    private void createRotatedPillarModels(BlockModelGenerators blockModels,
                                           Block block,
                                           String verticalModelName,
                                           String horizontalModelName,
                                           String endTexture,
                                           String sideTexture) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.END, ResourceLocation.parse(endTexture))
                .put(TextureSlot.SIDE, ResourceLocation.parse(sideTexture));

        ResourceLocation verticalModel = ModelTemplates.CUBE_COLUMN.create(
                modModel(verticalModelName),
                textures,
                blockModels.modelOutput
        );
        ResourceLocation horizontalModel = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
                modModel(horizontalModelName),
                textures,
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
                        block,
                        verticalModel,
                        horizontalModel
                )
        );
        blockModels.delegateItemModel(block, verticalModel);
    }

    private void createWoodPillarModels(BlockModelGenerators blockModels,
                                        Block block,
                                        String verticalModelName,
                                        String horizontalModelName,
                                        String texture) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.END, ResourceLocation.parse(texture))
                .put(TextureSlot.SIDE, ResourceLocation.parse(texture));

        ResourceLocation verticalModel = ModelTemplates.CUBE_COLUMN.create(
                modModel(verticalModelName),
                textures,
                blockModels.modelOutput
        );
        ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
                modModel(horizontalModelName),
                textures,
                blockModels.modelOutput
        );

        blockModels.delegateItemModel(block, verticalModel);
    }


    private void createCactusDoorModels(BlockModelGenerators blockModels) {
        ResourceLocation bottomLeft = createCactusDoorModel(blockModels, "bottom_left");
        ResourceLocation bottomLeftOpen = createCactusDoorModel(blockModels, "bottom_left_open");
        ResourceLocation bottomRight = createCactusDoorModel(blockModels, "bottom_right");
        ResourceLocation bottomRightOpen = createCactusDoorModel(blockModels, "bottom_right_open");
        ResourceLocation topLeft = createCactusDoorModel(blockModels, "top_left");
        ResourceLocation topLeftOpen = createCactusDoorModel(blockModels, "top_left_open");
        ResourceLocation topRight = createCactusDoorModel(blockModels, "top_right");
        ResourceLocation topRightOpen = createCactusDoorModel(blockModels, "top_right_open");

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
        blockModels.createSimpleFlatItemModel(ModBlocks.CACTUS_DOOR.get().asItem());
    }

    private void createCactusTrapdoorModels(BlockModelGenerators blockModels) {
        ResourceLocation bottomModel = createCactusTrapdoorModel(blockModels, "bottom", "minecraft:block/template_orientable_trapdoor_bottom");
        ResourceLocation bottom = bottomModel;
        ResourceLocation top = createCactusTrapdoorModel(blockModels, "top", "minecraft:block/template_orientable_trapdoor_top");
        ResourceLocation open = createCactusTrapdoorModel(blockModels, "open", "minecraft:block/template_orientable_trapdoor_open");

        blockModels.blockStateOutput.accept(BlockModelGenerators.createOrientableTrapdoor(ModBlocks.CACTUS_TRAPDOOR.get(), top, bottom, open));
        blockModels.delegateItemModel(ModBlocks.CACTUS_TRAPDOOR.get(), bottomModel);
    }

    private ResourceLocation createCactusDoorModel(BlockModelGenerators blockModels, String variant) {
        ResourceLocation model = modModel("cactus_door_" + variant);
        blockModels.modelOutput.accept(model, () -> createDoorModelJson(
                "minecraft:block/door_" + variant,
                "floraexpansion:block/cactus_door_bottom",
                "floraexpansion:block/cactus_door_top"
        ));
        return model;
    }

    private ResourceLocation createCactusTrapdoorModel(BlockModelGenerators blockModels, String variant, String parent) {
        ResourceLocation model = modModel("cactus_trapdoor_" + variant);
        blockModels.modelOutput.accept(model, () -> createTrapdoorModelJson(parent));
        return model;
    }

    private static ResourceLocation modBlock(String path) {
        return ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "block/" + path);
    }

    private static ResourceLocation modBlockMaterial(String path) {
        return modBlock(path);
    }

    private static ResourceLocation mcBlock(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "block/" + path);
    }

    private static ResourceLocation modModel(String path) {
        return ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "block/" + path);
    }

    private static ResourceLocation modItemModel(String path) {
        return ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "item/" + path);
    }

    protected @NotNull Stream<? extends net.minecraftforge.registries.RegistryObject<Block>> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().filter(block -> !block.equals(ModBlocks.SALT_SPIKE) && !block.equals(ModBlocks.GLASSWORT) && !block.equals(ModBlocks.POTTED_GLASSWORT))
                .filter(block -> ModBlocks.CALCITE_ORES.stream().noneMatch(ore -> block.equals(ore)));
    }

    protected @NotNull Stream<? extends net.minecraftforge.registries.RegistryObject<Item>> getKnownItems() {
        return ModItems.ITEMS.getEntries().stream()
                .filter(item -> ModBlocks.CALCITE_ORES.stream().noneMatch(ore -> item.get() == ore.get().asItem()))
                .filter(item -> item.get() != ModBlocks.SALT_SPIKE.get().asItem()
                        && item.get() != ModBlocks.GLASSWORT.get().asItem()
                        && !item.equals(ModItems.SPIKED_LEATHER_HELMET)
                        && !item.equals(ModItems.SPIKED_LEATHER_CHESTPLATE)
                        && !item.equals(ModItems.SPIKED_LEATHER_LEGGINGS)
                        && !item.equals(ModItems.SPIKED_LEATHER_BOOTS));
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
