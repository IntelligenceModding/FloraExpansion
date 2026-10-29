package de.artemis.floraexpansion.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.DoubleValue needleParticles = BUILDER
            .comment("Needle litter decorative particle intensity.")
            .translation("floraexpansion.configuration.client.particles.needles")
            .defineInRange("particles.needles", 1.0, 0.0, 1.0);
    public static final ModConfigSpec.DoubleValue flaxParticles = BUILDER
            .comment("Decorative flax flower particle intensity.")
            .translation("floraexpansion.configuration.client.particles.flax")
            .defineInRange("particles.flax", 1.0, 0.0, 1.0);
    public static final ModConfigSpec.DoubleValue fruitParticles = BUILDER
            .comment("Decorative falling fruit particle intensity; does not change harvests.")
            .translation("floraexpansion.configuration.client.particles.fruit")
            .defineInRange("particles.fruit", 1.0, 0.0, 1.0);
    public static final ModConfigSpec.IntValue impactParticles = BUILDER
            .comment("Number of item fragments shown when a throwable hits something.")
            .translation("floraexpansion.configuration.client.particles.impactCount")
            .defineInRange("particles.impactCount", 8, 0, 8);
    public static final ModConfigSpec.BooleanValue crateFill = BUILDER
            .comment("Show the contents of open crates.")
            .translation("floraexpansion.configuration.client.rendering.crateFill")
            .define("rendering.crateFill", true);
    public static final ModConfigSpec.DoubleValue crateFillDistance = BUILDER
            .comment("Maximum distance at which crate contents are shown.")
            .translation("floraexpansion.configuration.client.rendering.crateFillDistance")
            .defineInRange("rendering.crateFillDistance", 64.0, 8.0, 128.0);
    public static final ModConfigSpec.DoubleValue scrollSensitivity = BUILDER
            .comment("Crate mouse-wheel scrolling sensitivity.")
            .translation("floraexpansion.configuration.client.interface.scrollSensitivity")
            .defineInRange("interface.scrollSensitivity", 1.0, 0.1, 10.0);
    public static final ModConfigSpec.BooleanValue interactionHints = BUILDER
            .comment("Show basket and crate control hints.")
            .translation("floraexpansion.configuration.client.interface.interactionHints")
            .define("interface.interactionHints", false);
    public static final ModConfigSpec.BooleanValue quickExtraction = BUILDER
            .comment("Use the crate left-click shortcut when permitted by the server.")
            .translation("floraexpansion.configuration.client.interface.quickExtraction")
            .define("interface.quickExtraction", true);
    public enum TooltipDetail { COMPACT, EXPANDED, ON_SHIFT }
    public static final ModConfigSpec.EnumValue<TooltipDetail> tooltipDetail = BUILDER
            .comment("Amount of basket and packed-crate information to show.")
            .translation("floraexpansion.configuration.client.interface.tooltipDetail")
            .defineEnum("interface.tooltipDetail", TooltipDetail.COMPACT);
    public static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean expanded(net.minecraft.world.item.TooltipFlag flag) {
        if (!SPEC.isLoaded()) return false;
        return flag.shouldDisplayAllInformation() || tooltipDetail.get() == TooltipDetail.EXPANDED || tooltipDetail.get() == TooltipDetail.ON_SHIFT && flag.hasShiftDown();
    }
    public static boolean hints() { return SPEC.isLoaded() && interactionHints.get(); }
    private ClientConfig() {}
}

