# Flora Expansion advancements

The mod has one visible **Flora Expansion** tab with 39 connected advancements. Obtaining any mod item or entering Salt Caves reveals its root. Recipe-book unlock advancements remain separate.

The tree follows the mod's survival activities:

- **Foraging:** twigs/cones/nuts → basket → successful basket harvest; toasted nuts → trail mix; fruit → planting an orchard, jams, baking, and juices.
- **Pebbles:** collect a pebble → skip the same stone three times.
- **Flax:** fiber → thread → fabric → linen furnishings. Thread also leads to crates → tying a crate containing supplies.
- **Wooden buckets:** make a bucket → carry a living passenger.
- **Desert:** prickly pears/giant cactus → cactus planks → boats; a second path rewards wearing all four pieces of spiked leather armor.
- **Salt:** raw salt → finish grinding salt → salting tub → collect a finished batch → eat both cured foods.
- **Salt Caves:** explore the biome → collect brine → glasswort → eat salted glasswort. Parallel paths cover salt spikes, brushing natural deposits and uncovering a find, and playing Into the Caves.

Parents control the vanilla advancement layout and visibility. They do not force players to repeat actions in an arbitrary order: criteria are tracked immediately, including before a parent is completed. Collections such as the four jams accumulate across acquisitions; players need not carry every item at once.

Most milestones use vanilla inventory, consumption, placement, location, equipment, and block-interaction criteria. Inventory milestones accept traded and automated products. Action descriptions refer to the actual task: an empty mill, an unfinished batch, undoing an unsalted ingredient, tying an empty crate, and brushing player-placed salt do not qualify.

Three optional challenges grant modest vanilla XP rewards: three pebble skips (25), drinking all five juices (50), and eating both cured foods (50). Other tasks/goals have no item or XP rewards. The tab uses vanilla frames, background, toasts, and announcements.

## Implementation and customization

`ModAdvancementProvider` bootstraps the native advancement registry through `GatherDataRegistryEntriesEvent`, generates `data/floraexpansion/advancement/nature/*.json`, and supplies English translation text to `ModLanguageProvider`. Stable advancement IDs preserve progress across releases. Normal datapacks can replace the generated definitions.

The `floraexpansion:action` criterion supports an `action` string and an optional standard player loot predicate. Available actions are `basket_harvest`, `skipping_stones`, `tie_crate`, `grind_salt`, `salt_food`, `brush_salt`, and `salt_find`. Each fires only after a successful server operation, for the player responsible. A find requires a nonempty archaeology loot result from natural salt. Automated salting output can still satisfy food-consumption milestones; the hands-on salting milestone requires collecting a batch by hand.

`AdvancementProgressionTest` checks the loaded tree, native criterion behavior, accumulated collections, challenge rewards, real mill/tub/crate/basket interactions, worn armor, jukebox playback, orchard planting, three physical water skips, action isolation, and revocation. Existing pebble and salt-brushing regression tests cover the associated mechanics and natural-only archaeology behavior.
