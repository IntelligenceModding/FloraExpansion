# Flora Expansion Datapacks

This document explains Flora Expansion's datapack customization on Minecraft 26.3: storage and gathering tags, plant soils, world generation, harvest rewards, recipes, trades, and item providers.

## Where Files Go

Flora Expansion uses the mod id `floraexpansion`.

In a datapack, crate storage rules go in this exact file:

```text
data/floraexpansion/tags/item/crate_storable.json
```

Basket gathering rules go in:

```text
data/floraexpansion/tags/block/basket_gatherable.json
```

Keep the `floraexpansion` namespace and the `crate_storable` filename when changing the crate rules. Additional tags referenced by this file can use your own namespace:

```text
data/<your_namespace>/tags/item/<your_tag>.json
data/<your_namespace>/tags/block/<your_tag>.json
```

Install and enable the datapack, then use `/reload` after changing JSON or restart the server. In multiplayer, install the datapack on the server.

## IDs

| ID | Type | Meaning |
| --- | --- | --- |
| `floraexpansion:crate` | Block/item | The crate itself. |
| `floraexpansion:crate_storable` | Item tag | Items that can be deposited into crates. |
| `floraexpansion:basket` | Item | The harvesting and gathering basket. |
| `floraexpansion:basket_gatherable` | Block tag | Plants the basket can gather by removing the block. |

Item tags take item IDs, block tags take block IDs, and biome tags take biome IDs. A leading `#` references another tag of the same kind. For example, `minecraft:bone_meal` adds bone meal to an item tag, while `#c:seeds` adds everything in the common seeds tag.

## Crate Storage Tag

Crates accept only items in `floraexpansion:crate_storable`. Each crate still stores one item type with matching components at a time.

### Default Contents

The default tag includes seeds, crops, saplings, leaves, flowers used by bees, fruit, berries, mushrooms, fertilizer, farming tools, animal feed/products, and Flora Expansion's harvests and farming supplies.

It references standard `c:` and `minecraft:` tags, so other mods' items are included when those mods add them to the referenced tags. Items outside those categories can be added explicitly.

The full shipped selection is in [`crate_storable.json`](src/main/resources/data/floraexpansion/tags/item/crate_storable.json).

### Basic Structure

| Field | Default | Meaning |
| --- | --- | --- |
| `replace` | `false` | Keeps entries from lower-priority packs when false; replaces them when true. |
| `values` | Required | Item IDs, item tag references, or optional entry objects to include. |

### Adding Items and Tags

This example extends the default selection with a modded fertilizer and crop tag:

```json
{
  "replace": false,
  "values": [
    "yourmod:fertilizer",
    "#yourmod:crops"
  ]
}
```

Replace `yourmod:fertilizer` and `#yourmod:crops` with existing item and tag IDs from your modpack. With `replace` set to `false`, the entries are added to the selection supplied by lower-priority packs.

### Replacing the Defaults

Set `replace` to `true` to supply your own complete selection. For example, this allows only wheat seeds and bone meal:

```json
{
  "replace": true,
  "values": [
    "minecraft:wheat_seeds",
    "minecraft:bone_meal"
  ]
}
```

Datapack priority still applies: a higher-priority pack can add more entries or replace the selection again.

### Optional Mod Compatibility

Use `required: false` for entries that should be skipped when their item or tag does not exist. This lets one datapack support optional mods:

```json
{
  "replace": false,
  "values": [
    {"id": "yourmod:fertilizer", "required": false},
    {"id": "#yourmod:crops", "required": false}
  ]
}
```

Normal string entries are required. Check the server log for missing IDs or invalid tag JSON if changes do not load.

## Storage Behavior

The tag restriction applies to hand deposits, bulk deposits, baskets, menu clicks, shift-click merging, and normal inventory/automation insertion. Reloading the datapack updates the selection without rebuilding the mod.

Crate contents use a pixel-aligned fill composed from the stored item's texture and tint. Added items do not need a separate crate fill texture. The visible fill surface rises in whole block-pixel steps with the stored amount. Resource packs can change its appearance through the item's normal model texture and tint.

Items already stored before an update or tag change are retained when loading, saving, or moving a packed crate. They can be withdrawn, but further deposits of items excluded by the current tag are rejected.

## Basket Gathering Tag

`floraexpansion:basket_gatherable` is a **block tag**, separate from the crate's item tag. Its defaults include mushrooms, Nether fungi and roots, flowers, saplings, grasses, ferns, dead bushes, melons, and pumpkins. The optional common `c:mushrooms` block tag includes modded mushrooms that use it.

Right-click a tagged plant with a basket to gather its normal loot into the basket and remove the plant. Tall plants can be gathered from either half. Drops that do not fit are left in the world. Plants that normally need shears still use their normal loot rules; tagging them does not turn the basket into shears.

To add modded plants, create `data/floraexpansion/tags/block/basket_gatherable.json` in your datapack:

```json
{
  "replace": false,
  "values": [
    {"id": "yourmod:forest_mushroom", "required": false},
    {"id": "#yourmod:gatherable_plants", "required": false}
  ]
}
```

Replace these example IDs with your pack's blocks and block tags. `replace: true` replaces the default gathering selection, using the same priority rules as the crate tag. Use `/reload` after editing.

The basket tries a plant's existing right-click harvest interaction before removing a gatherable plant. Crops, fruit-bearing leaves, and berry bushes retain their dedicated harvest and maturity checks, even if included in this tag. Block entities are excluded. Gathering also respects building permissions and canceled block-break events.

## Plant Soil Tags

These are block tags under `data/floraexpansion/tags/block/`. Extend or replace their `values` using the same JSON format as basket gathering.

| Tag | Used for |
| --- | --- |
| `blueberry_soils` | Small and large blueberry bush support, including generation. |
| `wild_strawberry_soils` | Wild strawberry support, including generation. |
| `wild_flax_soils` | Wild flax support, including generation. |
| `crop_soils` | Cultivated flax and strawberry support; defaults to farmland. |
| `cactus_soils` | Small cactus support and cactus generation soils. |
| `giant_cactus_growth_soils` | Ground on which shoots grow into giant cacti; also included in generation support. |
| `cactus_stem_supports` | Cactus trunk and stem blocks that support an existing giant cactus stem. |
| `giant_cactus_supports` | Giant cactus generation support; includes cactus soils and cactus trunk blocks. |

Defaults match the previous soil checks. See the [shipped block tags](src/main/resources/data/floraexpansion/tags/block) for the complete selections. Adding a soil controls support and survival; light, space, growth, and normal farmland mechanics still apply.

## World Generation

### Biome Selection

Each feature's biome selection is now a block-independent biome tag under `data/floraexpansion/tags/worldgen/biome/`:

- `floraexpansion:has_prickly_pear_cactus`
- `floraexpansion:has_small_cactus`
- `floraexpansion:has_fallen_giant_cactus`
- `floraexpansion:has_pebble_clusters`
- `floraexpansion:has_needle_litter`
- `floraexpansion:has_blueberry_bushes`
- `floraexpansion:has_wild_flax`
- `floraexpansion:has_wild_strawberries`
- `floraexpansion:has_orchard_cherry_trees`
- `floraexpansion:has_apple_trees_sparse`
- `floraexpansion:has_apple_trees_forest`
- `floraexpansion:has_fancy_apple_trees_rare`
- `floraexpansion:has_fancy_apple_trees_jungle`
- `floraexpansion:has_apple_trees_dark_forest`
- `floraexpansion:has_giant_cactus`

For example, put this in `data/floraexpansion/tags/worldgen/biome/has_blueberry_bushes.json` to add a modded biome:

```json
{
  "replace": false,
  "values": [
    {"id": "yourmod:woodland", "required": false}
  ]
}
```

### Frequency and Disabling Features

Placement frequency is already stored in `data/floraexpansion/worldgen/placed_feature/*.json`. For example, [giant_cactus_placed.json](src/generated/resources/data/floraexpansion/worldgen/placed_feature/giant_cactus_placed.json) uses a rarity filter with `chance: 8`, meaning roughly one placement attempt in eight eligible chunks. Larger rarity values make attempts less frequent; actual successful placement also depends on terrain and the feature.

Copy the existing placed-feature JSON into your datapack at the same path and edit the desired placement values. Retain its other placement filters.

Biome modifiers live under `data/floraexpansion/neoforge/biome_modifier/`. Override a specific modifier with the following to disable that generation:

```json
{
  "type": "neoforge:none"
}
```

Restart the world/server after changing world-generation registries. Changes apply to newly generated terrain; existing blocks and chunks are retained.

### Feature Dimensions and Fruit Leaves

Feature definitions are in `data/floraexpansion/worldgen/feature/`. Supported optional fields are:

| Feature/decorator type | Fields | Defaults | Limits |
| --- | --- | --- | --- |
| `floraexpansion:giant_cactus_feature` | `min_height`, `max_height` | `4`, `7` | Both 4–16; maximum at least minimum. |
| `floraexpansion:pebble_cluster_feature` | `min_radius`, `max_radius` | `1`, `3` | Both 1–8; maximum at least minimum. |
| `floraexpansion:apple_leaves_decorator` | `replacement_divisor`, `max_replacements` | `10`, `8` | Divisor 1–256; maximum 0–256. |
| `floraexpansion:orchard_cherry_leaves_decorator` | `replacement_divisor`, `max_replacements` | `2`, `30` | Divisor 1–256; maximum 0–256. |

The leaf decorators replace approximately one in `replacement_divisor` eligible exposed leaves, up to `max_replacements`. A maximum of zero disables replacement. Decorator fields belong inside the relevant tree feature's decorator entry.

For example, override `data/floraexpansion/worldgen/feature/giant_cactus.json` with:

```json
{
  "type": "floraexpansion:giant_cactus_feature",
  "min_height": 6,
  "max_height": 10
}
```

Height fields control the trunk; arms retain their existing shape rules. Omitted fields retain the original settings. Invalid ranges are rejected by the normal data codec.

## Harvest Rewards

Right-click harvests use ordinary loot tables under `data/floraexpansion/loot_table/harvest/`. Hand and basket harvests share these tables.

| Table filename | Default rewards |
| --- | --- |
| `apple_leaves.json` | One apple at full ripeness. |
| `orchard_cherry_leaves.json` | One cherry at age 1; 2–3 at age 2; 3–4 at age 3. |
| `blueberry_bush.json` | 1–2 blueberries when ripe. |
| `large_blueberry_bush.json` | 1–2 blueberries at age 2; either 2 or 4 at age 3. |
| `needle_litter.json` | One cone per segment, plus 0 to twice the segment count minus one twigs. |
| `prickly_pear_cactus.json` | 1–2 prickly pears when ripe. |
| `small_cactus.json` | One cactus juice for bottle harvesting. |
| `giant_cactus_stem.json` | 1–2 cactus slices per stem harvested with an axe. |
| `cactus_stripping.json` | One spine with a 20% base chance, plus 10 percentage points per Fortune level. |

These use Minecraft 26.3's block loot context: block state, origin, tool, and optional player. State-based `condition` entries select rewards by maturity or segment count. Multiple reward items are supported, and basket overflow is dropped normally.

For example, this replaces `data/floraexpansion/loot_table/harvest/apple_leaves.json` with three apples per harvest:

```json
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:apple",
          "modifier": [
            {"type": "minecraft:set_count", "count": 3}
          ]
        }
      ]
    }
  ]
}
```

The block still enforces maturity and resets after harvesting. An empty reward table still completes the harvest; it does not leave the plant ripe for repeated free attempts. Crop replanting and ordinary block-breaking loot continue to use their existing block loot tables.

## Recipes and Ingredient Tags

Recipes remain ordinary files under `data/floraexpansion/recipe/`. Textile recipes and their unlock conditions use these item tags:

`mixed_berries.json` requires one blueberry, one strawberry, and one extra ingredient from `c:foods/berry`, producing three servings. Extend that common tag to admit additional berries. `strawberry_juice.json` uses three strawberries and a glass bottle, matching the other bottled fruit juices. The cactus mosaic stair and slab recipes use cactus mosaic blocks and follow the normal wooden stair/slab yields.

The four jam recipes are `blueberry_jam.json`, `strawberry_jam.json`, `cherry_jam.json`, and `apple_jam.json`. Each makes one jam from four matching fruits, one sugar, and one glass jar. Eating any jam returns the same glass jar.

Empty and filled jars are also placeable blocks using the existing item IDs. Their ordinary block loot tables are `loot_table/blocks/glass_jar.json`, `blueberry_jam.json`, `strawberry_jam.json`, `cherry_jam.json`, and `apple_jam.json`; each returns the corresponding intact jar. Removing the supporting block also drops the jar. These placements do not use block entities.

Filled jar block states use `portions=1..4`; existing saved jars without this property default to four. Right-clicking eats one portion when hungry, and the last leaves the empty jar block. The four portions together retain the original whole-jar food value. Broken partial jars carry their portion count in Minecraft's `minecraft:block_state` item component and reduced food values in `minecraft:food`; placing or eating those items cannot restore missing portions. Full jars continue to stack with existing full-jar items.

Each blueberry, strawberry, cherry, or apple adds one portion to an empty jar or a jar of that same flavor. A full jar rejects additional fruit. This interaction uses fruit only; changing the jam crafting recipes does not change refilling. Hoppers can insert matching fruit from above or a side, one fruit per portion. They cannot extract portions or the jar. Refill input is a state-backed container like a composter, with no stored inventory or block entity.

| Tag | Default |
| --- | --- |
| `floraexpansion:linen_fibers` | Flax fiber. |
| `floraexpansion:linen_threads` | Linen thread. |

Add modded fibers or threads to these tags to allow substitutions without overriding each textile recipe. Tie/untie interactions still consume and return linen thread.

Flax seeds, berries, cherries, prickly pears, flax fiber, and flax flowers also participate in appropriate existing common item tags for other mods' recipes and integrations. Prefer common tags when their meaning matches your ingredient.

## Armor Repair

`data/floraexpansion/tags/item/spiked_leather_armor_repairable.json` supplies the spiked leather armor repair materials. It defaults to leather and can be extended or replaced. The old cactus armor repair tag forwards to it for compatibility.

## Composting and Furnace Fuel

Flora Expansion item components now reference mod-owned integer providers under `data/floraexpansion/context_int_provider/`. The shipped defaults copy Minecraft's original provider behavior.

- `compostable/always_add_one.json`
- `compostable/low.json`
- `compostable/low_medium.json`
- `compostable/medium.json`
- `compostable/medium_high.json`
- `cooking/time_boats.json`
- `cooking/time_dry_plants.json`
- `cooking/time_hanging_signs.json`
- `cooking/time_wood_blocks.json`
- `cooking/time_wood_items_extra_small.json`
- `cooking/time_wood_items_large.json`
- `cooking/time_wood_slabs.json`
- `cooking/time_wool_carpets.json`

Override a provider to change the Flora Expansion items that use that category, without changing vanilla items that reference `minecraft:` providers. A constant provider can be written as a JSON number; for example:

```json
200
```

Replacing a cooking provider with a constant also replaces its default furnace-specific adjustments. Copy the shipped provider JSON when you want to preserve those adjustments while changing its base value. Check the [component assignments](src/main/java/de/artemis/floraexpansion/common/event/ItemComponentEvents.java) for each item's category.

## Chest Loot, Block Extras, and Archaeology

Strawberry candle-cake loot tables under `data/floraexpansion/loot_table/blocks/` use a `minecraft:dynamic` entry named `floraexpansion:cake_candles` to return each candle's saved color. Keep this entry when customizing drops if the original candles should still be returned. It works for both breaking the cake and removing its candles by eating the first portion.

Existing global loot modifiers are under `data/floraexpansion/loot_modifiers/`. They add rewards to selected chest, block, and archaeology loot. Copy a shipped modifier at the same path to change its `item`, `min_count`, `max_count`, or `condition`.

These are the Minecraft 26.3 forms: conditions use a singular `condition` field with a typed predicate. Use the shipped JSON as the schema reference. Previously generated chest contents are not rewritten.

Ordinary block-breaking tables are under `data/floraexpansion/loot_table/blocks/`.

## Villager and Wandering Trader Offers

Trade entries are already data-driven under `data/floraexpansion/villager_trade/`. Membership in profession/level offer pools is supplied by the matching files under `data/minecraft/tags/villager_trade/`.

All four jam flavors have offers in the farmer level-3 and wandering-trader uncommon pools. Each offer starts at three emeralds for one full jar. Farmer offers allow 12 uses and give 10 XP; wandering trader offers allow two uses and give 1 XP. Their existing reputation discount factor is 0.05. Override the individual trade files or pool tags to adjust these choices.

For example, [flax_seeds_emerald.json](src/main/resources/data/floraexpansion/villager_trade/farmer/1/flax_seeds_emerald.json) defines a farmer offer. Copy existing trade JSON at the same path to change its costs, output, or offer properties. Use normal trade-tag merging/replacement to alter pool membership.

Existing saved villager offers are retained; edited definitions apply when offers are generated.

## Configuration and Authority

Probability multipliers, combat balance, bucket hazards, feature interaction switches, and local presentation preferences are documented in [CONFIG.md](CONFIG.md).

Gameplay datapacks are installed on the server/world. A player's resource pack can change appearance, but cannot change server generation frequency, rewards, or allowed items.
