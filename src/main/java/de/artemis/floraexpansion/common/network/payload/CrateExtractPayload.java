package de.artemis.floraexpansion.common.network.payload;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CrateExtractPayload(BlockPos pos, boolean fullStack) implements CustomPacketPayload {
    public static final Type<CrateExtractPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "crate_extract"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CrateExtractPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CrateExtractPayload::pos,
            ByteBufCodecs.BOOL,
            CrateExtractPayload::fullStack,
            CrateExtractPayload::new
    );

    @Override
    public Type<CrateExtractPayload> type() {
        return TYPE;
    }
}
