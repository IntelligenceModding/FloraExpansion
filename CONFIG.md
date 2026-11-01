# Configuration

Open **Mods > Flora Expansion > Config**, or edit the TOML files below. Restart after editing files directly.

| File | When to use it |
| --- | --- |
| `config/floraexpansion-client.toml` | Change your display and controls. |
| `config/floraexpansion-synced.toml` | Set gameplay defaults on the server or in singleplayer. |
| `<world>/syncedconfig/floraexpansion-synced.toml` | Override one world; copy the default file here first. |

Gameplay settings load when the server/world starts and are sent to connected players. On a remote server, the server owner controls them.

## Change growth and seed returns

For twice the natural flax/strawberry growth chance and a 25% apple-core chance:

```toml
[growth]
crops = 2.0

[remainders]
appleCoreChance = 0.25
```

Edit existing sections rather than adding duplicates. `growth.crops` means `crops` under `[growth]`.

| Keys | Default | Values |
| --- | --- | --- |
| `growth.crops`, `growth.berries`, `growth.fruit`, `growth.cactus` | `1.0` each | `0–16`: flax/strawberries, blueberries, apple/cherry ripening, cactus fruit/flowers, respectively. |
| `remainders.appleCoreChance`, `remainders.cherryPitChance` | `0.5` each | `0–1`: chance after eating the corresponding fruit. |

Growth `0` stops natural growth; `1` is normal. Soil, light and space checks still apply. Bone meal is unchanged.

## Balance combat

Edit `[combat]`. Damage uses health points (`2` = one heart); `20` ticks normally equals one second.

| Key | Default | Values / effect |
| --- | --- | --- |
| `coneDamage` | `1.0` | `0–100` damage per cone hit. |
| `pebbleDamage` | `2.0` | `0–100` damage per pebble hit. |
| `coneSlownessTicks` | `60` | `0–12000`; `0` disables slowness. |
| `armorChancePerPiece` | `0.15` | `0–1`; total retaliation chance is capped at 100%. |
| `armorDamage` | `1.0` | `0–100` base retaliation damage. |
| `armorBonusDamage` | `1.0` | `0–100` extra damage with at least three spiked leather pieces. |

## Restrict wooden lava buckets

Disable new lava fills with:

```toml
[woodenBuckets]
allowLava = false
```

To change the lifetime instead, edit `lavaMinTicks` and `lavaMaxTicks` in the same section. Defaults are `100` and `200` (5–10 seconds); each accepts `1–72000`. A maximum below the minimum uses the minimum. Existing filled buckets retain their saved expiry.

## Change basket and crate controls

Set these gameplay switches to `false` to disable an interaction. All default to `true`.

| Key | Interaction |
| --- | --- |
| `basket.harvestCrops` | Harvest mature crops and fruit plants. |
| `basket.gatherPlants` | Collect blocks in the basket gathering tag. |
| `basket.depositIntoContainers` | Empty baskets into containers. |
| `crate.bulkDeposits` | Double-click to deposit matching inventory items. |
| `crate.quickExtraction` | Left-click to extract items. |

Set `crate.doubleClickTicks` to change the double-click window: default `8`, range `1–40` ticks. Change accepted items/plants through [datapack tags](DATAPACKS.md#change-accepted-items-plants-and-soils).

## Reduce effects or change local controls

Edit `floraexpansion-client.toml`. These settings affect only your client.

| Key | Default | How to change it |
| --- | --- | --- |
| `particles.needles`, `particles.flax`, `particles.fruit` | `1.0` each | Choose `0–1`; `0` hides the corresponding decorative particles. |
| `particles.impactCount` | `8` | Choose `0–8` throwable-impact fragments. |
| `rendering.crateFill` | `true` | Set `false` to hide crate contents. |
| `rendering.crateFillDistance` | `64.0` | Choose `8–128` blocks to limit content rendering distance. |
| `interface.scrollSensitivity` | `1.0` | Choose `0.1–10` for crate mouse-wheel sensitivity. |
| `interface.interactionHints` | `false` | Set `true` to show basket/crate control hints. |
| `interface.quickExtraction` | `true` | Set `false` to disable your crate shortcut; the server must also permit it. |
| `interface.tooltipDetail` | `"COMPACT"` | Use `"EXPANDED"` for full details or `"ON_SHIFT"` for full details while holding Shift. |

For recipes, world generation, loot, trades, fuel and composting, use [DATAPACKS.md](DATAPACKS.md).
