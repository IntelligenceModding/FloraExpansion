package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.advancement.FloraActionTrigger;
import de.artemis.floraexpansion.common.registry.ModBiomes;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.*;
import net.minecraft.advancements.criterion.*;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import java.util.*;

public final class ModAdvancementProvider {
    public static AdvancementSubProvider tree() { return new Tree(); }

    public record Milestone(String id, String parent, String icon, String title, String description, AdvancementType type, int experience) {}
    private static Milestone task(String id, String parent, String icon, String title, String description) {
        return new Milestone(id, parent, icon, title, description, AdvancementType.TASK, 0);
    }
    private static Milestone goal(String id, String parent, String icon, String title, String description) {
        return new Milestone(id, parent, icon, title, description, AdvancementType.GOAL, 0);
    }
    private static Milestone challenge(String id, String parent, String icon, String title, String description, int xp) {
        return new Milestone(id, parent, icon, title, description, AdvancementType.CHALLENGE, xp);
    }
    public static final List<Milestone> MILESTONES = List.of(
        task("root", "", "flax_flower", "Flora Expansion", "Discover the little things nature has to offer"),
        task("foraging", "root", "spruce_cone", "Underfoot", "Gather a twig, spruce cone, or spruce nuts"),
        task("basket", "foraging", "basket", "A Helping Handle", "Make a basket for your harvest"),
        goal("basket_harvest", "basket", "basket", "Gather Round", "Harvest or gather a plant using a basket"),
        task("toasted_nuts", "foraging", "toasted_spruce_nuts", "A Little Toasty", "Eat toasted spruce nuts"),
        task("trail_mix", "toasted_nuts", "trail_mix", "Happy Trails", "Eat some trail mix"),
        task("fruit", "foraging", "blueberries", "Ripe for the Picking", "Gather blueberries, strawberries, or cherries"),
        task("orchard", "fruit", "cherry_pit", "From Little Seeds", "Plant both a cherry pit and an apple core"),
        task("jam", "fruit", "blueberry_jam", "In a Jam", "Obtain a jar of fruit jam"),
        goal("all_jams", "jam", "strawberry_jam", "Preserve the Harvest", "Obtain blueberry, strawberry, cherry, and apple jam"),
        task("baking", "fruit", "blueberry_pie", "Something Sweet", "Obtain a blueberry pie and a strawberry cake"),
        challenge("all_juices", "fruit", "cherry_juice", "Flight of the Orchard", "Drink blueberry, strawberry, cherry, apple, and cactus juice", 50),
        task("pebbles", "root", "pebbles", "Pocket Stones", "Pick up a pebble"),
        challenge("skipping_stones", "pebbles", "pebbles", "One, Two, Three!", "Skip the same pebble across water three times", 25),
        task("flax", "root", "flax_fiber", "A Useful Little Flower", "Harvest flax fiber"),
        task("thread", "flax", "linen_thread", "A Common Thread", "Spin flax fiber into linen thread"),
        task("fabric", "thread", "linen_fabric", "Cut from the Same Cloth", "Make linen fabric"),
        goal("linen", "fabric", "linen_bale", "Home Spun", "Obtain a linen bale and linen carpet"),
        task("crate", "thread", "crate", "Room for More", "Make a crate for your farming supplies"),
        goal("tie_crate", "crate", "linen_thread", "All Tied Up", "Tie a nonempty crate with linen thread"),
        task("wooden_bucket", "root", "wooden_bucket", "Wood You Believe It?", "Make a wooden bucket"),
        goal("wooden_passenger", "wooden_bucket", "axolotl_wooden_bucket", "Precious Cargo", "Carry a fish, axolotl, or tadpole in a wooden bucket"),
        task("desert", "root", "prickly_pear", "Desert Provisions", "Gather a prickly pear or a giant cactus stem"),
        task("cactus_wood", "desert", "cactus_planks", "A Prickly Proposition", "Turn cactus wood into planks"),
        goal("cactus_boat", "cactus_wood", "cactus_boat", "Desert Drifter", "Make a cactus boat or cactus chest boat"),
        goal("spiked_armor", "desert", "spiked_leather_chestplate", "Handle with Care", "Wear a complete set of spiked leather armor"),
        task("raw_salt", "root", "raw_salt", "Worth Your Salt", "Collect raw salt"),
        task("grind_salt", "raw_salt", "hand_mill", "Salt of the Earth", "Finish grinding raw salt into salt with a hand mill"),
        task("salting_tub", "grind_salt", "salting_tub", "A Pinch of Patience", "Make a salting tub"),
        goal("salt_food", "salting_tub", "salted_raw_fish", "Seasoned Hands", "Collect a finished batch from a salting tub"),
        challenge("cured_food", "salt_food", "cured_meat", "Built to Last", "Eat both cured meat and cured fish", 50),
        task("salt_caves", "raw_salt", "crystalline_salt", "Beneath the Brine", "Explore the Salt Caves biome"),
        task("brine", "salt_caves", "salt_water_bucket", "A Different Kind of Spring", "Collect salt water in an iron or wooden bucket"),
        task("salt_spike", "salt_caves", "salt_spike", "Crystal Clear", "Collect a salt spike"),
        task("glasswort", "brine", "glasswort", "Life Finds a Way", "Collect glasswort from beside salt water"),
        task("salted_glasswort", "glasswort", "salted_glasswort", "Salad with a Pinch", "Eat salted glasswort"),
        task("brush_salt", "salt_caves", "minecraft:brush", "Layers of History", "Brush a layer from a naturally generated salt deposit"),
        goal("salt_find", "brush_salt", "minecraft:angler_pottery_sherd", "Preserved in Salt", "Uncover a find while brushing natural salt"),
        goal("cave_music", "salt_caves", "music_disc_into_the_caves", "Into the Caves", "Play Umplix's Into the Caves in a jukebox")
    );

    public static String translation(String id, String part) { return "advancements.floraexpansion." + id + "." + part; }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path); }
    private static Identifier itemId(String path) { return path.contains(":") ? Identifier.parse(path) : id(path); }

    private static final class Tree implements AdvancementSubProvider {
        private HolderLookup.Provider lookup;
 @Override public void generate(HolderLookup.Provider lookup, java.util.function.Consumer<AdvancementHolder> output) { this.lookup = lookup;
            var items = lookup.lookupOrThrow(Registries.ITEM);
            var parents = new HashMap<String, AdvancementHolder>();
            for (var milestone : MILESTONES) {
                var builder = Advancement.Builder.advancement();
                var icon = items.getOrThrow(net.minecraft.resources.ResourceKey.create(Registries.ITEM, itemId(milestone.icon()))).value();
                var title = Component.translatable(translation(milestone.id(), "title"));
                var description = Component.translatable(translation(milestone.id(), "description"));
                if (milestone.parent().isEmpty()) builder.display(icon, title, description,
                        Identifier.withDefaultNamespace("gui/advancements/backgrounds/husbandry"), milestone.type(), false, false, false);
                else builder.display(icon, title, description, null, milestone.type(), true, true, false);
                if (!milestone.parent().isEmpty()) builder.parent(Objects.requireNonNull(parents.get(milestone.parent())));
                if (milestone.experience() > 0) builder.rewards(AdvancementRewards.Builder.experience(milestone.experience()));
                criteria(milestone.id(), builder, lookup, items);
                parents.put(milestone.id(), builder.save(output, id("nature/" + milestone.id()).toString()));
            }
        }

        private static ItemPredicate.Builder predicate(HolderGetter<Item> items, String... names) {
            return ItemPredicate.Builder.item().of(items, Arrays.stream(names).map(name -> items.getOrThrow(
                    net.minecraft.resources.ResourceKey.create(Registries.ITEM, itemId(name))).value()).toArray(Item[]::new));
        }
        private static void inventory(Advancement.Builder builder, HolderGetter<Item> items, String... names) {
            builder.addCriterion("obtained", InventoryChangeTrigger.TriggerInstance.hasItems(predicate(items, names)));
        }
        private static void collectAll(Advancement.Builder builder, HolderGetter<Item> items, String... names) {
            for (String name : names) builder.addCriterion(name, InventoryChangeTrigger.TriggerInstance.hasItems(predicate(items, name)));
        }
        private static void eat(Advancement.Builder builder, HolderGetter<Item> items, String... names) {
            for (String name : names) builder.addCriterion(name, ConsumeItemTrigger.TriggerInstance.usedItem(predicate(items, name)));
        }
        private static void criteria(String name, Advancement.Builder builder, HolderLookup.Provider lookup, HolderGetter<Item> items) {
            switch (name) {
                case "root" -> {
                    var modItems = net.minecraft.core.registries.BuiltInRegistries.ITEM.stream().filter(item -> item.builtInRegistryHolder().key().identifier().getNamespace().equals(FloraExpansion.MODID))
                            .toArray(Item[]::new);
                    builder.addCriterion("discovery", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, modItems)));
                    builder.addCriterion("cave", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location().setBiomes(
                            HolderSet.direct(lookup.lookupOrThrow(Registries.BIOME).getOrThrow(ModBiomes.SALT_CAVES)))));
                    builder.requirements(AdvancementRequirements.Strategy.OR);
                }
                case "foraging" -> inventory(builder, items, "twig", "spruce_cone", "spruce_nuts");
                case "basket", "pebbles", "linen", "crate", "wooden_bucket", "salting_tub", "raw_salt", "salt_spike", "glasswort" -> {
                    if (name.equals("linen")) collectAll(builder, items, "linen_bale", "linen_carpet");
                    else inventory(builder, items, name);
                }
                case "fruit" -> inventory(builder, items, "blueberries", "strawberry", "cherries");
                case "toasted_nuts" -> eat(builder, items, "toasted_spruce_nuts");
                case "trail_mix" -> eat(builder, items, "trail_mix");
                case "jam" -> inventory(builder, items, "blueberry_jam", "strawberry_jam", "cherry_jam", "apple_jam");
                case "all_jams" -> collectAll(builder, items, "blueberry_jam", "strawberry_jam", "cherry_jam", "apple_jam");
                case "baking" -> collectAll(builder, items, "blueberry_pie", "strawberry_cake");
                case "all_juices" -> eat(builder, items, "blueberry_juice", "strawberry_juice", "cherry_juice", "apple_juice", "cactus_juice");
                case "flax" -> inventory(builder, items, "flax_fiber");
                case "thread" -> inventory(builder, items, "linen_thread");
                case "fabric" -> inventory(builder, items, "linen_fabric");
                case "wooden_passenger" -> inventory(builder, items, "cod_wooden_bucket", "salmon_wooden_bucket", "pufferfish_wooden_bucket", "tropical_fish_wooden_bucket", "axolotl_wooden_bucket", "tadpole_wooden_bucket");
                case "desert" -> inventory(builder, items, "prickly_pear", "giant_cactus_stem");
                case "cactus_wood" -> inventory(builder, items, "cactus_planks");
                case "cactus_boat" -> inventory(builder, items, "cactus_boat", "cactus_chest_boat");
                case "brine" -> inventory(builder, items, "salt_water_bucket", "wooden_salt_water_bucket");
                case "cured_food" -> eat(builder, items, "cured_meat", "cured_fish");
                case "salted_glasswort" -> eat(builder, items, "salted_glasswort");
                case "orchard" -> {
                    var blocks = lookup.lookupOrThrow(Registries.BLOCK);
                    for (String sapling : List.of("cherry_pit", "apple_core")) builder.addCriterion(sapling, ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(
                            blocks.getOrThrow(net.minecraft.resources.ResourceKey.create(Registries.BLOCK, id(sapling))).value()));
                }
                case "salt_caves" -> builder.addCriterion("explore", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location()
                        .setBiomes(HolderSet.direct(lookup.lookupOrThrow(Registries.BIOME).getOrThrow(ModBiomes.SALT_CAVES)))));
                case "spiked_armor" -> builder.addCriterion("wear", PlayerTrigger.TriggerInstance.located(EntityPredicate.Builder.entity().equipment(
                        EntityEquipmentPredicate.Builder.equipment().head(predicate(items, "spiked_leather_helmet"))
                                .chest(predicate(items, "spiked_leather_chestplate")).legs(predicate(items, "spiked_leather_leggings"))
                                .feet(predicate(items, "spiked_leather_boots")))));
                case "cave_music" -> builder.addCriterion("play", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(lookup.lookupOrThrow(Registries.BLOCK), Blocks.JUKEBOX)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(net.minecraft.world.level.block.JukeboxBlock.HAS_RECORD, true))),
                        predicate(items, "music_disc_into_the_caves")));
                case "basket_harvest", "skipping_stones", "tie_crate", "grind_salt", "salt_food", "brush_salt", "salt_find" ->
                        builder.addCriterion("performed", FloraActionTrigger.criterion(name));
                default -> throw new IllegalArgumentException("Missing advancement criteria: " + name);
            }
        }
    }
}
