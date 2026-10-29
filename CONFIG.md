# Flora Expansion Config

Flora Expansion has a server-owned gameplay config and a client config for local presentation.

The gameplay config uses NeoForge's `SYNCED` type on Minecraft 26.3. Its default location is:

```text
config/floraexpansion-synced.toml
```

For a specific world, place the same filename in:

```text
<world>/syncedconfig/floraexpansion-synced.toml
```

The local client config is:

```text
config/floraexpansion-client.toml
```

## In-game Config Screens

Open **Mods → Flora Expansion → Config**. Client Settings controls local presentation. Gameplay Settings becomes available after a world loads. NeoForge disables gameplay editing when connected to a remote server, or while a singleplayer world is published to LAN.

The screens use NeoForge's normal configuration UI and validation. Use its editing, reset, and save controls. Gameplay values are read from the current loaded config rather than cached copies; NeoForge handles synchronization and unloading when leaving a server.

## Server Config Sections

| Section | What it controls |
| --- | --- |
| `growth` | Natural crop, berry, fruit, and cactus growth probabilities. |
| `remainders` | Apple-core and cherry-pit rewards after eating fruit. |
| `combat` | Throwable damage, cone slowness, and spiked armor retaliation. |
| `woodenBuckets` | New wooden lava-bucket filling and lifetime. |
| `basket` | Harvesting, wild gathering, and container deposits. |
| `crate` | Bulk deposits, quick extraction, and double-click timing. |

## Server Defaults and Ranges

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `growth.crops` | `1` | `0–16` | Natural flax and strawberry growth multiplier; 0 disables natural growth. |
| `growth.berries` | `1` | `0–16` | Natural blueberry growth multiplier. |
| `growth.fruit` | `1` | `0–16` | Natural apple and cherry ripening multiplier. |
| `growth.cactus` | `1` | `0–16` | Natural cactus fruit and flowering growth multiplier. |
| `remainders.appleCoreChance` | `0.5` | `0–1` | Chance to receive an apple core after eating an apple. |
| `remainders.cherryPitChance` | `0.5` | `0–1` | Chance to receive a cherry pit after eating cherries. |
| `combat.coneDamage` | `1` | `0–100` | Damage dealt by a thrown spruce cone. |
| `combat.pebbleDamage` | `2` | `0–100` | Damage dealt by a thrown pebble. |
| `combat.coneSlownessTicks` | `60` | `0–12000` | Slowness duration in ticks; 0 disables the effect. |
| `combat.armorChancePerPiece` | `0.15` | `0–1` | Retaliation chance per spiked leather piece; total chance is capped at 1. |
| `combat.armorDamage` | `1` | `0–100` | Base spiked leather retaliation damage. |
| `combat.armorBonusDamage` | `1` | `0–100` | Additional retaliation damage with at least three pieces. |
| `woodenBuckets.allowLava` | `true` | Boolean | Allow newly filled wooden buckets to carry lava. |
| `woodenBuckets.lavaMinTicks` | `100` | `1–72000` | Minimum lifetime of newly filled lava buckets, in ticks. |
| `woodenBuckets.lavaMaxTicks` | `200` | `1–72000` | Maximum lifetime in ticks; values below the minimum are treated as the minimum. |
| `basket.harvestCrops` | `true` | Boolean | Allow basket harvesting of mature crops and fruit plants. |
| `basket.gatherPlants` | `true` | Boolean | Allow gathering plants in the basket_gatherable block tag. |
| `basket.depositIntoContainers` | `true` | Boolean | Allow depositing basket contents into containers. |
| `crate.bulkDeposits` | `true` | Boolean | Allow double-click deposits of matching inventory items. |
| `crate.quickExtraction` | `true` | Boolean | Allow the left-click crate extraction shortcut. |
| `crate.doubleClickTicks` | `8` | `1–40` | Maximum ticks between clicks for a bulk deposit. |

Growth values multiply the existing natural-growth probability, capped at one growth action per eligible random tick. `1` keeps normal behavior; `0` stops natural growth for that family. Existing maturity, light, soil, space, and world random-tick rules still apply. Bone meal is unchanged. Growth attempts participate in NeoForge crop-growth events.

Damage is measured in health points, and durations use ticks (normally 20 ticks per second). A cone slowness duration of `0` disables its effect. Armor retaliation chance is capped at 100% after combining equipped pieces.

If the maximum lava lifetime is below the minimum, the effective maximum is clamped to the minimum. Changing either setting affects newly filled buckets. Previously filled buckets retain their saved expiration times. Disabling lava filling prevents new lava pickup without deleting existing filled buckets.

Disabling basket or crate shortcuts does not delete stored items or disable access to their storage menus. Disallowed actions are checked on the server, including direct crate-extraction requests from clients.

## Client Defaults and Ranges

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `particles.needles` | `1` | `0–1` | Needle litter decorative particle intensity. |
| `particles.flax` | `1` | `0–1` | Decorative flax flower particle intensity. |
| `particles.fruit` | `1` | `0–1` | Decorative falling fruit particle intensity; does not change harvests. |
| `particles.impactCount` | `8` | `0–8` | Number of item fragments shown when a throwable hits something. |
| `rendering.crateFill` | `true` | Boolean | Show the contents of open crates. |
| `rendering.crateFillDistance` | `64` | `8–128` | Maximum distance at which crate contents are shown. |
| `interface.scrollSensitivity` | `1` | `0.1–10` | Crate mouse-wheel scrolling sensitivity. |
| `interface.interactionHints` | `false` | Boolean | Show basket and crate control hints. |
| `interface.quickExtraction` | `true` | Boolean | Use the crate left-click shortcut when permitted by the server. |
| `interface.tooltipDetail` | `COMPACT` | `COMPACT`, `EXPANDED`, `ON_SHIFT` | Compact previews, full previews, or full previews while Shift is held. |

Particle intensities are fractions from `0` to `1`. These preferences only change visuals; they do not change item drops, growth, or damage. Needle particle intensity does not disable footstep sounds.

Crate-fill settings affect rendering only. The fill keeps its block-pixel scale regardless of distance. Scroll sensitivity does not change storage capacity or transfer amounts. Local quick extraction can be turned off independently; turning it on cannot enable extraction prohibited by the server.

## Server Authority

The server's gameplay config and datapacks decide world generation, growth, rewards, inventory transfers, and combat. A joining player's local gameplay file is not an override for the server.

Client messages request an interaction, such as extracting from a crate. The server checks its own settings, the loaded target, access, distance, lock state, and actual stored contents. Clients do not supply reward counts, damage values, or replacement config values.

`LOCAL` and `STARTUP` configs are not used for these gameplay rules. All blocks and items remain registered regardless of settings.

## Datapacks

World generation frequency, biome membership, soils, harvest rewards, recipes, trades, crate eligibility, basket gathering, fuel, and composting are configured through datapacks. Their locations and examples are in [DATAPACKS.md](DATAPACKS.md). These controls are separate from the gameplay TOML settings.

Tags, recipes, harvest tables, and reloadable provider data use the normal server datapack reload process. Restart the world/server after changing world-generation registries. Existing generated chunks are not regenerated.

## Verification

The opt-in regression suite is run with Java 25:

```text
gradlew runGameTestServer -PportTests
gradlew runGameTestServer -PportTests -PcustomizationOverrides
gradlew runClient -PportTests -PclientSmokeTest
```

The second command enables a real test datapack that replaces storage/gathering tags, soils, biome selection, a textile ingredient, composting, and harvest rewards. That test pack and all test classes are excluded from release builds.
