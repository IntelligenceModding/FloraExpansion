package de.artemis.floraexpansion.common.block.entity;

import de.artemis.floraexpansion.common.block.SaltingTubBlock;
import de.artemis.floraexpansion.common.util.SaltingTubParticleEffects;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import de.artemis.floraexpansion.common.recipe.SaltingRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.ArrayList;
import java.util.List;

public class SaltingTubBlockEntity extends BlockEntity implements net.minecraft.world.WorldlyContainer {
    public static final int CAPACITY = SaltingRecipe.MAX_LAYERS;
    private final List<ItemStack> food = new ArrayList<>();
    private boolean salted;
    private ItemStack result = ItemStack.EMPTY;
    private ItemStack coveringSalt = ItemStack.EMPTY;
    private int remainingTicks;
    private boolean ready;
    private net.minecraft.core.NonNullList<ItemStack> supplies = net.minecraft.core.NonNullList.withSize(2, ItemStack.EMPTY);

    public SaltingTubBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SALTING_TUB.get(), pos, state); }
    public int layers() { return food.size(); }
    public boolean isSalted() { return salted; }
    public boolean isReady() { return ready; }
    public int remainingTicks() { return remainingTicks; }
    public ItemStack displayFood() { return salted ? result.copy() : food.isEmpty() ? ItemStack.EMPTY : food.getLast().copy(); }

    public boolean addFood(ItemStack stack) {
        if (!canAddFood(stack)) return false;
        food.add(stack.copyWithCount(1));
        sync();
        emitAddingParticles(stack, SaltingTubParticleEffects.FOOD_COUNT);
        return true;
    }

    private boolean canAddFood(ItemStack stack) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server) || salted || food.size() >= CAPACITY || stack.isEmpty()) return false;
        var candidate = new ArrayList<>(food);
        candidate.add(stack.copyWithCount(1));
        return server.recipeAccess().recipeMap().byType(ModRecipes.SALTING.get()).stream()
                .anyMatch(holder -> holder.value().acceptsFood(candidate));
    }

    @Override public int getContainerSize() { return 3; }
    @Override public boolean isEmpty() { return supplies.stream().allMatch(ItemStack::isEmpty) && (!ready || result.isEmpty()); }
    @Override public ItemStack getItem(int slot) { return slot == 2 ? ready ? result : ItemStack.EMPTY : supplies.get(slot); }
    @Override public ItemStack removeItem(int slot, int count) {
        if (slot != 2 || !ready || count <= 0) return ItemStack.EMPTY;
        var removed = result.split(count);
        // Keep the batch state until the next tick so a failed hopper transfer can restore its stack.
        if (!removed.isEmpty()) sync();
        return removed;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 2 || !ready) return ItemStack.EMPTY;
        var removed = result;
        result = ItemStack.EMPTY;
        return removed;
    }
    @Override public void setItem(int slot, ItemStack stack) {
        if (slot == 2) { if (ready) result = stack; }
        else supplies.set(slot, stack);
        sync();
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 ? canAddFood(stack) : slot == 1 && !salted && ModRecipes.isSaltingSalt(level, stack);
    }
    @Override public int[] getSlotsForFace(net.minecraft.core.Direction face) {
        return face == net.minecraft.core.Direction.DOWN ? new int[]{2}
                : face == net.minecraft.core.Direction.UP ? new int[]{0, 1} : new int[]{1};
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction face) {
        return face != net.minecraft.core.Direction.DOWN && (face == net.minecraft.core.Direction.UP || slot == 1)
                && canPlaceItem(slot, stack);
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction face) {
        return face == net.minecraft.core.Direction.DOWN && slot == 2 && ready;
    }
    @Override public boolean canTakeItem(net.minecraft.world.Container destination, int slot, ItemStack stack) { return slot == 2 && ready; }
    @Override public boolean stillValid(net.minecraft.world.entity.player.Player player) { return net.minecraft.world.Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() {
        supplies.clear(); food.clear(); result = ItemStack.EMPTY; coveringSalt = ItemStack.EMPTY;
        salted = false; ready = false; remainingTicks = 0; sync();
    }

    public boolean salt() {
        return salt(new ItemStack(ModItems.SALT.get()));
    }

    public boolean salt(ItemStack salt) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server) || salted || food.isEmpty()) return false;
        var input = new SaltingRecipeInput(food, salt);
        var recipe = server.recipeAccess().getRecipeFor(ModRecipes.SALTING.get(), input, server);
        if (recipe.isEmpty()) return false;
        // Save the assembled stack so recipe reloads cannot change a finished batch.
        var assembled = recipe.get().value().assemble(input);
        if (assembled.isEmpty() || assembled.getCount() > assembled.getMaxStackSize()) return false;
        result = assembled;
        coveringSalt = salt.copyWithCount(1);
        remainingTicks = recipe.get().value().processingTime();
        ready = false;
        salted = true;
        sync();
        emitAddingParticles(new ItemStack(ModItems.RAW_SALT.get()), SaltingTubParticleEffects.SALT_COUNT);
        return true;
    }

    /** Sent only after an ingredient is accepted, including ingredients supplied by hoppers. */
    private void emitAddingParticles(ItemStack ingredient, int count) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server)) return;
        var particle = new net.minecraft.core.particles.ItemParticleOption(
                net.minecraft.core.particles.ParticleTypes.ITEM,
                net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(ingredient, 1));
        var random = server.getRandom();
        for (int i = 0; i < count; i++) {
            var emission = SaltingTubParticleEffects.addition(random);
            server.sendParticles(particle,
                    worldPosition.getX() + emission.x(), worldPosition.getY() + emission.y(),
                    worldPosition.getZ() + emission.z(), 0,
                    emission.vx(), emission.vy(), emission.vz(), 1);
        }
    }

    /** Collect finished food, or undo the last unsalted layer while sneaking. */
    public List<ItemStack> take(boolean undo) {
        if (level == null || level.isClientSide() || food.isEmpty() || salted && !ready || !salted && !undo) return List.of();
        var result = new ArrayList<ItemStack>();
        if (salted) {
            result.add(this.result.copy());
            this.result = ItemStack.EMPTY;
            food.clear();
            salted = false;
            ready = false;
            coveringSalt = ItemStack.EMPTY;
            remainingTicks = 0;
        } else result.add(food.removeLast());
        sync();
        return result;
    }

    public void dropContents() {
        if (level == null || level.isClientSide()) return;
        var drops = new ArrayList<ItemStack>();
        supplies.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy).forEach(drops::add);
        supplies.clear();
        if (ready) drops.add(result.copy());
        else {
            drops.addAll(food);
            if (salted && !coveringSalt.isEmpty()) drops.add(coveringSalt.copy());
        }
        food.clear();
        result = ItemStack.EMPTY;
        salted = false;
        ready = false;
        remainingTicks = 0;
        coveringSalt = ItemStack.EMPTY;
        setChanged();
        drops.forEach(stack -> Block.popResource(level, worldPosition, stack));
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, SaltingTubBlockEntity tub) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server)) return;
        if (tub.ready && tub.result.isEmpty()) tub.take(false);
        if (!tub.salted) {
            var input = tub.supplies.get(0);
            if (!input.isEmpty() && tub.addFood(input)) { input.shrink(1); tub.setChanged(); }
            var salt = tub.supplies.get(1);
            if (!salt.isEmpty() && tub.salt(salt)) { salt.shrink(1); tub.setChanged(); }
        }
        // Bring saved states from the former instant-salting tub into the new ready state.
        if (state.getValue(SaltingTubBlock.READY) != tub.ready) tub.sync();
        if (!tub.salted || tub.ready || tub.remainingTicks <= 0) return;
        tub.remainingTicks--;
        tub.setChanged();
        if (tub.remainingTicks == 0) {
            tub.ready = true;
            tub.sync();
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.COMPOSTER_READY, net.minecraft.sounds.SoundSource.BLOCKS, 1F, 1F);
            server.sendParticles(new net.minecraft.core.particles.DustParticleOptions(SaltingTubParticleEffects.FINISH_COLOR, SaltingTubParticleEffects.FINISH_SCALE),
                    pos.getX() + 0.5, pos.getY() + SaltingTubParticleEffects.FINISH_HEIGHT, pos.getZ() + 0.5,
                    SaltingTubParticleEffects.FINISH_COUNT, SaltingTubParticleEffects.FINISH_SPREAD_XZ,
                    SaltingTubParticleEffects.FINISH_SPREAD_Y, SaltingTubParticleEffects.FINISH_SPREAD_XZ, 0);
        }
    }

    @Override public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        dropContents();
        super.preRemoveSideEffects(pos, state);
    }

    private void sync() {
        setChanged();
        if (level == null) return;
        var oldState = getBlockState();
        var newState = oldState.setValue(SaltingTubBlock.LAYERS, food.size()).setValue(SaltingTubBlock.SALTED, salted)
                .setValue(SaltingTubBlock.READY, ready);
        level.setBlock(worldPosition, newState, Block.UPDATE_ALL);
        level.sendBlockUpdated(worldPosition, oldState, newState, Block.UPDATE_CLIENTS);
        level.updateNeighbourForOutputSignal(worldPosition, newState.getBlock());
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        net.minecraft.world.ContainerHelper.saveAllItems(output, supplies);
        output.store("food", ItemStack.CODEC.listOf(), food);
        output.putBoolean("salted", salted);
        output.store("result", ItemStack.OPTIONAL_CODEC, result);
        output.store("covering_salt", ItemStack.OPTIONAL_CODEC, coveringSalt);
        output.putInt("remaining_ticks", remainingTicks);
        output.putBoolean("ready", ready);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        supplies = net.minecraft.core.NonNullList.withSize(2, ItemStack.EMPTY);
        net.minecraft.world.ContainerHelper.loadAllItems(input, supplies);
        food.clear();
        input.read("food", ItemStack.CODEC.listOf()).orElse(List.of()).stream()
                .filter(stack -> !stack.isEmpty()).limit(CAPACITY).map(stack -> stack.copyWithCount(1)).forEach(food::add);
        salted = input.getBooleanOr("salted", false) && !food.isEmpty();
        var savedResult = input.read("result", ItemStack.OPTIONAL_CODEC);
        result = savedResult.orElse(ItemStack.EMPTY);
        // Older tubs saved the converted servings in the food layers themselves.
        if (salted && savedResult.isEmpty()) result = food.getFirst().copyWithCount(food.size());
        if (!salted) result = ItemStack.EMPTY;
        remainingTicks = salted ? Math.clamp(input.getIntOr("remaining_ticks", 0), 0, 72000) : 0;
        ready = salted && remainingTicks == 0;
        coveringSalt = salted ? input.read("covering_salt", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}

