package de.artemis.floraexpansion.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddItemModifier extends LootModifier {

    public static final Codec<AddItemModifier> CODEC = RecordCodecBuilder.create(inst ->
            LootModifier.codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("min_count", 1).forGetter(m -> m.minCount),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("max_count", 1).forGetter(m -> m.maxCount)
            )).apply(inst, AddItemModifier::new));

    private final Item item;
    private final int minCount;
    private final int maxCount;

    public AddItemModifier(LootItemCondition[] conditionsIn, Item item) {
        this(conditionsIn, item, 1, 1);
    }

    public AddItemModifier(LootItemCondition[] conditionsIn, Item item, int minCount, int maxCount) {
        super(conditionsIn);
        this.item = item;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext lootContext) {
        RandomSource random = lootContext.getRandom();
        int count = this.minCount >= this.maxCount
                ? this.minCount
                : random.nextInt(this.maxCount - this.minCount + 1) + this.minCount;

        generatedLoot.add(new ItemStack(this.item, count));
        return generatedLoot;
    }

    @Override
    public @NotNull Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
