package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

final class SignRegressionTests {
    static void verify(GameTestHelper helper) {
        Block[] blocks = {ModBlocks.CACTUS_SIGN.get(), ModBlocks.CACTUS_WALL_SIGN.get(),
                ModBlocks.CACTUS_HANGING_SIGN.get(), ModBlocks.CACTUS_WALL_HANGING_SIGN.get()};
        for (int index = 0; index < blocks.length; index++) {
            var pos = helper.absolutePos(new BlockPos(1 + index, 2, 1));
            var state = blocks[index].defaultBlockState();
            var type = index < 2 ? ModBlockEntities.CACTUS_SIGN.get() : ModBlockEntities.CACTUS_HANGING_SIGN.get();
            var sign = (SignBlockEntity) type.create(pos, state);
            check(sign != null && sign.getType() == type && sign.typeHolder().value() == type
                    && sign.isValidBlockState(state), "Custom sign has incorrect type or invalid state");
            check((sign instanceof HangingSignBlockEntity) == (index >= 2), "Incorrect editor dispatch type");
            check(sign.getMaxTextLineWidth() == (index < 2 ? 90 : 60)
                    && sign.getTextLineHeight() == (index < 2 ? 10 : 9), "Incorrect sign text layout");
            sign.setLevel(helper.getLevel());
            var front = new SignText().setMessage(0, Component.literal("Cactus front"))
                    .setColor(DyeColor.BLUE).setHasGlowingText(true);
            var back = new SignText().setMessage(1, Component.literal("Saved back text"))
                    .setColor(DyeColor.RED);
            sign.setText(front, true);
            sign.setText(back, false);
            sign.setWaxed(true);
            var saved = sign.saveWithFullMetadata(helper.getLevel().registryAccess());
            check(saved.getString("id").orElseThrow().equals(index < 2 ? "floraexpansion:cactus_sign" : "floraexpansion:cactus_hanging_sign"),
                    "Custom sign saved under vanilla block entity ID");
            var restored = (SignBlockEntity) BlockEntity.loadStatic(pos, state, saved, helper.getLevel().registryAccess());
            check(restored != null && restored.getType() == type && restored.typeHolder().value() == type
                    && same(restored.getText(true), front) && same(restored.getText(false), back)
                    && restored.isWaxed(), "Sign text, colors, glow, wax, or type changed on save/load");
        }
    }

    private static boolean same(SignText a, SignText b) { return java.util.Arrays.equals(a.getMessages(false), b.getMessages(false)) && a.getColor() == b.getColor() && a.hasGlowingText() == b.hasGlowingText(); }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
}
