package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.item.CactusArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class CactusArmorEvents {
    private static final float RETALIATE_CHANCE_PER_PIECE = 0.15F;
    private static final float BASE_RETALIATE_DAMAGE = 1.0F;
    private static final float THREE_PLUS_BONUS_DAMAGE = 1.0F;

    private CactusArmorEvents() {
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        if (!CactusArmorItem.isCactusArmor(left)) {
            return;
        }

        ItemStack output = event.getOutput();
        if (output.isEmpty()) {
            return;
        }

        ItemEnchantments enchants = output.get(DataComponents.ENCHANTMENTS);
        if (enchants == null || enchants.isEmpty()) {
            return;
        }

        if (!CactusArmorItem.hasOnlySupportedEnchantments(output, enchants)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (event.getNewDamage() <= 0.0F) {
            return;
        }

        DamageSource source = event.getSource();
        if (source.is(DamageTypes.THORNS)) {
            return;
        }

        LivingEntity victim = event.getEntity();
        Entity sourceEntity = source.getEntity();
        Entity directEntity = source.getDirectEntity();

        if (!(sourceEntity instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker == victim) {
            return;
        }

        if (directEntity != attacker) {
            return;
        }

        int pieceCount = countCactusArmorPieces(victim);
        if (pieceCount <= 0) {
            return;
        }

        float procChance = pieceCount * RETALIATE_CHANCE_PER_PIECE;
        if (serverLevel.getRandom().nextFloat() >= procChance) {
            return;
        }

        float retaliateDamage = BASE_RETALIATE_DAMAGE;
        if (pieceCount >= 3) {
            retaliateDamage += THREE_PLUS_BONUS_DAMAGE;
        }

        boolean hitLanded = attacker.hurtServer(serverLevel, victim.damageSources().thorns(victim), retaliateDamage);
        if (!hitLanded) {
            return;
        }

        int fireAspectLevel = getHighestFireAspectLevel(serverLevel, victim);
        if (fireAspectLevel > 0) {
            attacker.igniteForSeconds(2.0F * fireAspectLevel);
        }
    }

    private static int countCactusArmorPieces(LivingEntity entity) {
        int count = 0;

        if (CactusArmorItem.isCactusArmor(entity.getItemBySlot(EquipmentSlot.HEAD))) count++;
        if (CactusArmorItem.isCactusArmor(entity.getItemBySlot(EquipmentSlot.CHEST))) count++;
        if (CactusArmorItem.isCactusArmor(entity.getItemBySlot(EquipmentSlot.LEGS))) count++;
        if (CactusArmorItem.isCactusArmor(entity.getItemBySlot(EquipmentSlot.FEET))) count++;

        return count;
    }

    private static int getHighestFireAspectLevel(ServerLevel level, LivingEntity entity) {
        HolderLookup.RegistryLookup<Enchantment> lookup =
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder.Reference<Enchantment> fireAspect = lookup.getOrThrow(Enchantments.FIRE_ASPECT);

        int highest = 0;
        highest = Math.max(highest, getLevel(fireAspect, entity.getItemBySlot(EquipmentSlot.HEAD)));
        highest = Math.max(highest, getLevel(fireAspect, entity.getItemBySlot(EquipmentSlot.CHEST)));
        highest = Math.max(highest, getLevel(fireAspect, entity.getItemBySlot(EquipmentSlot.LEGS)));
        highest = Math.max(highest, getLevel(fireAspect, entity.getItemBySlot(EquipmentSlot.FEET)));

        return highest;
    }

    private static int getLevel(Holder<Enchantment> enchantment, ItemStack stack) {
        if (!CactusArmorItem.isCactusArmor(stack)) {
            return 0;
        }
        return EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
    }
}
