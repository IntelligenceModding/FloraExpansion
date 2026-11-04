package de.artemis.floraexpansion.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.DoubleValue cropGrowth = BUILDER
            .comment("Natural flax and strawberry growth multiplier; 0 disables natural growth.")
            .translation("floraexpansion.configuration.server.growth.crops")
            .defineInRange("growth.crops", 1.0, 0.0, 16.0);
    public static final ForgeConfigSpec.DoubleValue berryGrowth = BUILDER
            .comment("Natural blueberry growth multiplier.")
            .translation("floraexpansion.configuration.server.growth.berries")
            .defineInRange("growth.berries", 1.0, 0.0, 16.0);
    public static final ForgeConfigSpec.DoubleValue fruitGrowth = BUILDER
            .comment("Natural apple and cherry ripening multiplier.")
            .translation("floraexpansion.configuration.server.growth.fruit")
            .defineInRange("growth.fruit", 1.0, 0.0, 16.0);
    public static final ForgeConfigSpec.DoubleValue cactusGrowth = BUILDER
            .comment("Natural cactus fruit and flowering growth multiplier.")
            .translation("floraexpansion.configuration.server.growth.cactus")
            .defineInRange("growth.cactus", 1.0, 0.0, 16.0);
    public static final ForgeConfigSpec.DoubleValue appleCoreChance = BUILDER
            .comment("Chance to receive an apple core after eating an apple.")
            .translation("floraexpansion.configuration.server.remainders.appleCoreChance")
            .defineInRange("remainders.appleCoreChance", 0.5, 0.0, 1.0);
    public static final ForgeConfigSpec.DoubleValue cherryPitChance = BUILDER
            .comment("Chance to receive a cherry pit after eating cherries.")
            .translation("floraexpansion.configuration.server.remainders.cherryPitChance")
            .defineInRange("remainders.cherryPitChance", 0.5, 0.0, 1.0);
    public static final ForgeConfigSpec.DoubleValue coneDamage = BUILDER
            .comment("Damage dealt by a thrown spruce cone.")
            .translation("floraexpansion.configuration.server.combat.coneDamage")
            .defineInRange("combat.coneDamage", 1.0, 0.0, 100.0);
    public static final ForgeConfigSpec.DoubleValue pebbleDamage = BUILDER
            .comment("Damage dealt by a thrown pebble.")
            .translation("floraexpansion.configuration.server.combat.pebbleDamage")
            .defineInRange("combat.pebbleDamage", 2.0, 0.0, 100.0);
    public static final ForgeConfigSpec.IntValue coneSlownessTicks = BUILDER
            .comment("Slowness duration in ticks; 0 disables the effect.")
            .translation("floraexpansion.configuration.server.combat.coneSlownessTicks")
            .defineInRange("combat.coneSlownessTicks", 60, 0, 12000);
    public static final ForgeConfigSpec.DoubleValue armorChance = BUILDER
            .comment("Retaliation chance per spiked leather piece; total chance is capped at 1.")
            .translation("floraexpansion.configuration.server.combat.armorChancePerPiece")
            .defineInRange("combat.armorChancePerPiece", 0.15, 0.0, 1.0);
    public static final ForgeConfigSpec.DoubleValue armorDamage = BUILDER
            .comment("Base spiked leather retaliation damage.")
            .translation("floraexpansion.configuration.server.combat.armorDamage")
            .defineInRange("combat.armorDamage", 1.0, 0.0, 100.0);
    public static final ForgeConfigSpec.DoubleValue armorBonus = BUILDER
            .comment("Additional retaliation damage with at least three pieces.")
            .translation("floraexpansion.configuration.server.combat.armorBonusDamage")
            .defineInRange("combat.armorBonusDamage", 1.0, 0.0, 100.0);
    public static final ForgeConfigSpec.BooleanValue lavaEnabled = BUILDER
            .comment("Allow newly filled wooden buckets to carry lava.")
            .translation("floraexpansion.configuration.server.woodenBuckets.allowLava")
            .define("woodenBuckets.allowLava", true);
    public static final ForgeConfigSpec.IntValue lavaMinTicks = BUILDER
            .comment("Minimum lifetime of newly filled lava buckets, in ticks.")
            .translation("floraexpansion.configuration.server.woodenBuckets.lavaMinTicks")
            .defineInRange("woodenBuckets.lavaMinTicks", 100, 1, 72000);
    public static final ForgeConfigSpec.IntValue lavaMaxTicks = BUILDER
            .comment("Maximum lifetime in ticks; values below the minimum are treated as the minimum.")
            .translation("floraexpansion.configuration.server.woodenBuckets.lavaMaxTicks")
            .defineInRange("woodenBuckets.lavaMaxTicks", 200, 1, 72000);
    public static final ForgeConfigSpec.BooleanValue basketCrops = BUILDER
            .comment("Allow basket harvesting of mature crops and fruit plants.")
            .translation("floraexpansion.configuration.server.basket.harvestCrops")
            .define("basket.harvestCrops", true);
    public static final ForgeConfigSpec.BooleanValue basketGathering = BUILDER
            .comment("Allow gathering plants in the basket_gatherable block tag.")
            .translation("floraexpansion.configuration.server.basket.gatherPlants")
            .define("basket.gatherPlants", true);
    public static final ForgeConfigSpec.BooleanValue basketDeposits = BUILDER
            .comment("Allow depositing basket contents into containers.")
            .translation("floraexpansion.configuration.server.basket.depositIntoContainers")
            .define("basket.depositIntoContainers", true);
    public static final ForgeConfigSpec.BooleanValue crateBulk = BUILDER
            .comment("Allow double-click deposits of matching inventory items.")
            .translation("floraexpansion.configuration.server.crate.bulkDeposits")
            .define("crate.bulkDeposits", true);
    public static final ForgeConfigSpec.BooleanValue crateExtraction = BUILDER
            .comment("Allow the left-click crate extraction shortcut.")
            .translation("floraexpansion.configuration.server.crate.quickExtraction")
            .define("crate.quickExtraction", true);
    public static final ForgeConfigSpec.IntValue crateDoubleClickTicks = BUILDER
            .comment("Maximum ticks between clicks for a bulk deposit.")
            .translation("floraexpansion.configuration.server.crate.doubleClickTicks")
            .defineInRange("crate.doubleClickTicks", 8, 1, 40);
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    private ServerConfig() {}
}
