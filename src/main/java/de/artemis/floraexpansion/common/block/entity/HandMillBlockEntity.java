package de.artemis.floraexpansion.common.block.entity;

import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.inventory.HandMillMenu;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class HandMillBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    private static final int[] INPUT_SLOTS = {0};
    private static final int[] OUTPUT_SLOTS = {1};
    private String activeRecipe = "";
    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int progress;
    private long nextGrindingSoundTick;
    private final ContainerData data = new ContainerData() {
        public int get(int index) { return index == 0 ? progress : index == 1 ? getRequiredTurns() : 0; }
        public void set(int index, int value) { if (index == 0) progress = value; }
        public int getCount() { return 2; }
    };

    public HandMillBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.HAND_MILL.get(), pos, state); }
    public int getProgress() { return progress; }
    private java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<GrindingRecipe>> findRecipe() {
        return level instanceof net.minecraft.server.level.ServerLevel server
                ? server.recipeAccess().getRecipeFor(ModRecipes.GRINDING.get(), new SingleRecipeInput(items.get(0)), server)
                : java.util.Optional.empty();
    }
    public int getRequiredTurns() { return findRecipe().map(recipe -> recipe.value().turns()).orElse(0); }

    public boolean crank(Player player) {
        if (level == null || level.isClientSide()) return false;
        var crankState = level.getBlockState(worldPosition.above());
        if (!crankState.is(ModBlocks.HAND_CRANK.get()) || !HandCrankBlock.isOnMill(crankState, level, worldPosition.above())) return false;
        var match = findRecipe();
        if (match.isEmpty()) {
            progress = 0;
            activeRecipe = "";
            setChanged();
            player.displayClientMessage(Component.translatable("message.floraexpansion.hand_mill.empty"), true);
            return false;
        }
        var holder = match.get();
        var recipe = holder.value();
        String recipeId = holder.id().identifier().toString();
        if (!activeRecipe.equals(recipeId)) {
            progress = 0;
            activeRecipe = recipeId;
        }
        // Clamp saved progress if a datapack changes the number of turns.
        progress = Math.min(progress, recipe.turns() - 1);
        var result = recipe.assemble(new SingleRecipeInput(items.get(0)), level.registryAccess());
        var output = items.get(1);
        if (result.getCount() > getMaxStackSize(result) || !output.isEmpty() && (!ItemStack.isSameItemSameComponents(output, result) || output.getCount() + result.getCount() > Math.min(getMaxStackSize(output), result.getMaxStackSize()))) {
            player.displayClientMessage(Component.translatable("message.floraexpansion.hand_mill.output_full"), true);
            return false;
        }
        progress++;
        emitGrindingParticles();
        if (level.getGameTime() >= nextGrindingSoundTick) {
            level.playSound(null, worldPosition, SoundEvents.BASALT_HIT, SoundSource.BLOCKS,
                    0.3F, 0.75F + level.getRandom().nextFloat() * 0.1F);
            // Repeated clicks should not stack several stone sounds over the same turn.
            nextGrindingSoundTick = level.getGameTime() + HandCrankBlockEntity.ROTATION_TICKS;
        }
        if (progress == recipe.turns()) {
            if (items.get(0).is(de.artemis.floraexpansion.common.registry.ModItems.RAW_SALT.get())
                    && result.is(de.artemis.floraexpansion.common.registry.ModItems.SALT.get()))
                de.artemis.floraexpansion.common.advancement.FloraActionTrigger.fire(player, "grind_salt");
            items.get(0).shrink(1);
            if (output.isEmpty()) items.set(1, result);
            else output.grow(result.getCount());
            progress = 0;
        }
        setChanged();
        return true;
    }

    private void emitGrindingParticles() {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server)) return;
        // Snapshot before consuming input, including the last item of a completed batch.
        var particle = new net.minecraft.core.particles.ItemParticleOption(net.minecraft.core.particles.ParticleTypes.ITEM,
                items.get(0).copyWithCount(1));
        var random = server.getRandom();
        for (var side : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            for (int chip = 0; chip < 2; chip++) {
                double tangent = (random.nextDouble() - 0.5) * 0.24;
                double x = worldPosition.getX() + 0.5 + side.getStepX() * 0.57 + side.getStepZ() * tangent;
                double y = worldPosition.getY() + 0.48 + random.nextDouble() * 0.12;
                double z = worldPosition.getZ() + 0.5 + side.getStepZ() * 0.57 - side.getStepX() * tangent;
                // Spawn clear of the runner's swept edge, then drift away from the wheel.
                server.sendParticles(particle, x, y, z, 0,
                        side.getStepX() * 0.045, 0.025 + random.nextDouble() * 0.025, side.getStepZ() * 0.045, 1);
            }
        }
    }
    public float getMillstoneAngle(float partialTick) {
        return level != null && level.getBlockEntity(worldPosition.above()) instanceof HandCrankBlockEntity crank && crank.isOnMill()
                ? crank.getAngle(partialTick) : 0;
    }
    public float getCrankAngle(float partialTick) { return getMillstoneAngle(partialTick); }
    @Override public int getContainerSize() { return 2; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int count) {
        var removed = ContainerHelper.removeItem(items, slot, count);
        if (slot == 0 && items.get(0).isEmpty()) progress = 0;
        if (!removed.isEmpty()) setChanged();
        return removed;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        var removed = ContainerHelper.takeItem(items, slot);
        if (slot == 0) progress = 0;
        return removed;
    }
    @Override public void setItem(int slot, ItemStack stack) {
        if (slot == 0 && (!ItemStack.isSameItemSameComponents(items.get(0), stack) || stack.isEmpty())) progress = 0;
        stack.limitSize(getMaxStackSize(stack));
        items.set(slot, stack);
        setChanged();
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == 0 && ModRecipes.isGrindingInput(level, stack); }
    @Override public int[] getSlotsForFace(Direction direction) { return direction == Direction.DOWN ? OUTPUT_SLOTS : INPUT_SLOTS; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @org.jspecify.annotations.Nullable Direction direction) {
        return direction != Direction.DOWN && canPlaceItem(slot, stack);
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return slot == 1 && direction == Direction.DOWN;
    }
    @Override public boolean canTakeItem(Container destination, int slot, ItemStack stack) { return slot == 1; }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.clear(); progress = 0; setChanged(); }
    @Override public Component getDisplayName() { return Component.translatable("block.floraexpansion.hand_mill"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new HandMillMenu(id, inventory, this, data);
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("progress", progress);
        output.putString("active_recipe", activeRecipe);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        // Preserve input from saves made with the former direct-interaction mill.
        if (items.get(0).isEmpty()) items.set(0, input.read("input", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        activeRecipe = input.getStringOr("active_recipe", "" );
        progress = items.get(0).isEmpty() ? 0 : Math.clamp(input.getIntOr("progress", 0), 0, GrindingRecipe.MAX_TURNS - 1);
    }
}
