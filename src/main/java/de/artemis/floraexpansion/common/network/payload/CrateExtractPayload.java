package de.artemis.floraexpansion.common.network.payload;
import net.minecraft.core.BlockPos;
public record CrateExtractPayload(BlockPos pos, boolean fullStack) {}
