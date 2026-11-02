# Pebbles

Current implementation:

- One pebble item for collecting, placing and throwing, retaining the existing registry ID.
- Up to five stones on a surface, with the original scattered-pebble textures and original thin cutout appearance.
- Empty-hand use collects one stone; sneak-use collects the pile. Recovered items go into other inventory slots so the active hand remains empty.
- Placement and collection support fresh water and brine without draining sources. Flowing brine is not converted into a source.
- Air use throws; sneak-use against a block also throws. Failed normal placement does not accidentally throw.
- Throws have an eight-tick cooldown, speed- and angle-dependent water skips, modest configurable damage and at most one recoverable item. Glancing impacts retain more speed; hops shrink as energy is lost. Slow or steep impacts sink, and fast shallow throws can exceed three skips. There is no fixed skip-count limit; the three-skip advancement still triggers on the third bounce. Creative throws do not create recoverable items. Dispensers launch pebbles.
- Irregular patches occur along rivers, shallow shores and rocky terrain. Small rocky beds use ordinary gravel and preserve existing plants and non-ground blocks.
- Legacy scattered-pebble inventory items remain placeable and convert one-for-one into pebbles.

Packed pebbles and their building variants have been removed. There are no packed-pebble crafting, grinding or stonecutting recipes, textures, models, loot tables or creative entries. Old packed-pebble IDs resolve to vanilla gravel for save compatibility.

Potted Glasswort retains its salt surface.

Runtime checks cover placement/collection, protection, legacy IDs, water identity and support loss, exact drops, actual skipping trajectories, projectile recovery, dispensers and shoreline generation. New natural distribution affects newly generated chunks.
