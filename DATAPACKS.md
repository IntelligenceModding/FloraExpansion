# Datapack customization

Use datapacks to change accepted items, soils, recipes, generation, rewards and trades. For growth rates, combat and interaction switches, use [CONFIG.md](CONFIG.md).

## Create and load a pack

Create `<world>/datapacks/my_pack/pack.mcmeta` for Minecraft 26.2:

```json
{
  "pack": {
    "description": "My Flora Expansion changes",
    "min_format": [107, 1],
    "max_format": [107, 1]
  }
}
```

Put a `data/` directory beside it. In multiplayer, install the pack on the server. Run `/reload` for recipes, tags and loot; restart for world-generation changes, which affect new chunks only. Use `/datapack list` to check that the pack is enabled.

To override a file, copy its exact path from [main data](src/main/resources/data/floraexpansion) or [generated data](src/generated/resources/data/floraexpansion). New entries can use your own namespace. Paths below are relative to `data/floraexpansion/` unless another namespace is shown.

## Change accepted items, plants and soils

For example, let crates accept an optional mod's fertilizer and crops by adding `tags/item/crate_storable.json`:

```json
{
  "replace": false,
  "values": [
    {"id": "yourmod:fertilizer", "required": false},
    {"id": "#yourmod:crops", "required": false}
  ]
}
```

Replace the example IDs with yours. `replace: false` adds entries; `true` replaces the tag's lower-priority contents. `#` references another tag of the same kind; `required: false` tolerates an absent mod.

| Edit | To change |
| --- | --- |
| `tags/item/crate_storable.json` | What crates accept; removed items remain withdrawable. |
| `tags/block/basket_gatherable.json` | Blocks baskets collect using normal block loot. Inventory blocks remain excluded. |
| `tags/item/linen_fibers.json`, `linen_threads.json` | Ingredients accepted in textile recipes. |
| `tags/item/curable_meat.json`, `curable_fish.json`, `toasted_spruce_nuts.json` | Ingredients accepted by the corresponding salting recipes. |
| `tags/item/spiked_leather_armor_repairable.json` | Armor repair materials. |
| `tags/block/blueberry_soils.json`, `wild_strawberry_soils.json`, `wild_flax_soils.json` | Wild plant soils. |
| `tags/block/crop_soils.json` | Cultivated flax/strawberry soils. |
| `tags/block/cactus_soils.json`, `giant_cactus_growth_soils.json` | Cactus planting and shoot-growth soils. |
| `tags/block/cactus_stem_supports.json`, `giant_cactus_supports.json` | Stem support and giant-cactus generation support. |
| `tags/block/glasswort_plantable_on.json` | Glasswort soils; nearby brine and sturdy support are still required. |
| `tags/block/pebble_ground.json` | Ground on which pebble patches generate. |

Use item IDs in item tags and block IDs in block tags. Adding a soil does not bypass light, space or water requirements.

## Add grinding or salting recipes

Add files under `data/<namespace>/recipe/`, or copy an existing recipe to change it. Ingredients accept item IDs, item-tag references such as `"#minecraft:logs"`, or ingredient lists.

For a four-turn mill recipe:

```json
{
  "type": "floraexpansion:grinding",
  "ingredient": "floraexpansion:raw_salt",
  "result": {"id": "floraexpansion:salt", "count": 1},
  "turns": 4
}
```

The mill consumes one input per operation. `turns` accepts `1–32767` and defaults to `4`.

For a salting batch:

```json
{
  "type": "floraexpansion:salting",
  "ingredient": "#floraexpansion:curable_fish",
  "salt": "floraexpansion:salt",
  "processing_time": 100,
  "result": {"id": "floraexpansion:salted_raw_fish", "count": 4}
}
```

The tub consumes four matching food items and one matching `salt` item. `processing_time` accepts `1–72000` ticks and defaults to `100` (five seconds). `result` is the entire batch, not the output per food item. Both recipe types accept output components and counts of `1–99`, but the output must fit in one stack. Sealed tub batches retain their saved output and remaining time after a reload.

Copy [existing salting recipes](src/generated/resources/data/floraexpansion/recipe/salting) to change a particular food. Reload and check the result in JEI if installed.

## Change harvests and brushing finds

Copy a loot table and edit its entries, weights or `minecraft:set_count` values:

| Path | Affects |
| --- | --- |
| `loot_table/harvest/*.json` | Right-click harvests, including basket harvests. |
| `loot_table/blocks/*.json` | Block-breaking drops. |
| `loot_table/archaeology/salt_deposit.json` | Extra finds from brushing natural salt. |

For example, set the count in [apple harvest loot](src/main/resources/data/floraexpansion/loot_table/harvest/apple_leaves.json) to `3` for three apples. Keep maturity conditions when rewards should require ripe plants.

For salt finds, edit [the archaeology table](src/main/resources/data/floraexpansion/loot_table/archaeology/salt_deposit.json). Its weights total `100`, so the music disc's weight `1` gives a 1% chance per natural layer. Increasing its weight changes that total; reduce another weight to keep it at `100`. Keep the `natural=true` condition. Player-placed and evaporated salt cannot produce finds; the guaranteed raw-salt drop is handled separately.

To adjust added chest/block/archaeology rewards, copy a file from `loot_modifiers/` and edit `item`, `min_count`, `max_count` or `condition`. Generated chest contents remain unchanged. Keep the `floraexpansion:cake_candles` dynamic entry in candle-cake loot to return saved candle colors.

## Change or disable generation

| Edit | To change |
| --- | --- |
| `tags/worldgen/biome/has_<feature>.json` | Eligible biomes; use the tag format above with biome IDs. |
| `worldgen/placed_feature/*.json` | Attempt counts, rarity, heights and placement filters. |
| `worldgen/configured_feature/*.json` | Feature parameters and tree decorators. |
| `neoforge/biome_modifier/*.json` | Features added to tagged biomes. |

For example, add `yourmod:woodland` to `tags/worldgen/biome/has_blueberry_bushes.json` to allow bushes there. To reduce frequency, raise a placed feature's `minecraft:rarity_filter` `chance`; `8` means roughly one attempt per eight eligible chunks. Preserve its other filters: an attempt still needs suitable terrain.

To disable deposits, create `neoforge/biome_modifier/add_salt_deposit.json` containing:

```json
{"type": "neoforge:none"}
```

Apply the same override to other matching biome modifiers. Surface brine springs are added directly to `worldgen/biome/salt_caves.json`; to disable them, remove `floraexpansion:rooted_brine_spring_placed` from that biome's feature list instead.

These custom parameters can be edited in feature definitions or tree decorator entries:

| Type | Fields (defaults) | Limits |
| --- | --- | --- |
| `floraexpansion:giant_cactus_feature` | `min_height` (`4`), `max_height` (`7`) | `4–16`; maximum ≥ minimum. |
| `floraexpansion:pebble_cluster_feature` | `min_radius` (`1`), `max_radius` (`3`) | `1–8`; maximum ≥ minimum. |
| `floraexpansion:apple_leaves_decorator` | `replacement_divisor` (`10`), `max_replacements` (`8`), `exposed_only` (`true`) | Divisor `1–256`; maximum ≥ `0`; boolean. |
| `floraexpansion:orchard_cherry_leaves_decorator` | `replacement_divisor` (`2`), `max_replacements` (`30`), `exposed_only` (`true`) | Same limits. |

A larger leaf divisor reduces replacements; maximum `0` disables them. Set `exposed_only: false` to include interior leaves.

Surface springs sample actual cave layers in one out of four chunks, using `rarity_filter` and `count_on_every_layer` in `worldgen/placed_feature/rooted_brine_spring_placed.json`. The spring builder rejects nearby existing pools in the terrain safely available during decoration. This keeps the surface formations sparse while avoiding the old large full-height retry count. Adjust the rarity chance to change frequency, and the upward-search settings in `worldgen/configured_feature/rooted_brine_spring.json` to change cave-to-surface behavior. Keep solid support in `allowed_tree_position` so intermediate caves do not abort the search. Edit `tags/block/brine_spring_root_replaceable.json` to change which blocks become the mineral trail.

Edit `worldgen/biome/salt_caves.json` for ambience, spawns and carvers. The biome's climate selection is defined in code; changing `has_salt_cave.json` does not relocate it. The lining also checks the actual Salt Caves biome.

For calcite-ore processing recipes, use `c:ores/<mineral>` or `c:ores_in_ground/calcite`; `c:ore_bearing_ground/calcite` identifies the host block. These tags exist for both blocks and items.

## Adjust fuel and composting

Minecraft 26.2 uses NeoForge data maps for these settings. Override `data/neoforge/data_maps/item/furnace_fuels.json` or `data/neoforge/data_maps/item/compostables.json`. The defaults are generated by [ModDataMapProvider](src/main/java/de/artemis/floraexpansion/common/datagen/ModDataMapProvider.java).

For example, set `"floraexpansion:spruce_cone": {"burn_time": 200}` under `"values"` in the fuel map. Compost entries use `{"chance": 0.3}`. These maps reload with datapacks.

## Change villager offers

Copy a file from `villager_trade/` and edit its `wants`, `gives`, `max_uses`, `xp` or discount fields. For example, change `wants.count` in [the flax-seed offer](src/main/resources/data/floraexpansion/villager_trade/farmer/1/flax_seeds_emerald.json) to change how many seeds buy an emerald.

Add an offer's ID to its pool tag under `data/minecraft/tags/villager_trade/`, such as `farmer/level_1.json`. To remove offers, replace that pool tag with the entries you want to retain; this also replaces other lower-priority additions. Existing saved offers do not change.

## Change advancement requirements or rewards

Copy a file from [advancement/nature](src/generated/resources/data/floraexpansion/advancement/nature) and edit its `criteria`, `requirements`, `parent`, `display` or `rewards`. Keep the same file ID to retain saved progress where criteria still match.

For a successful mod interaction, use this criterion inside `criteria`:

```json
{
  "performed": {
    "trigger": "floraexpansion:action",
    "conditions": {"action": "salt_food"}
  }
}
```

Available actions are `basket_harvest`, `skipping_stones`, `tie_crate`, `grind_salt`, `salt_food`, `brush_salt` and `salt_find`. `conditions.player` accepts a standard player loot predicate. Match criterion names in `requirements`; parents control layout, not the order in which criteria can be earned.
