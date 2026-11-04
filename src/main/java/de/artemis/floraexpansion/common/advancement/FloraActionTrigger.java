package de.artemis.floraexpansion.common.advancement;

import org.jetbrains.annotations.NotNull;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
public final class FloraActionTrigger extends SimpleCriterionTrigger<FloraActionTrigger.Instance> {
 private static final ResourceLocation ID=ResourceLocation.fromNamespaceAndPath("floraexpansion","action");
 private static final FloraActionTrigger ACTION=CriteriaTriggers.register(new FloraActionTrigger());
 public static void register(net.minecraftforge.eventbus.api.IEventBus bus){}
 @NotNull public ResourceLocation getId(){return ID;}
 public static void fire(Player player,String action){if(player instanceof ServerPlayer server)ACTION.trigger(server,i->i.action.equals(action));}
 public static Instance criterion(String action){return new Instance(ContextAwarePredicate.ANY,action);}
 @NotNull protected Instance createInstance(@NotNull com.google.gson.JsonObject json,@NotNull ContextAwarePredicate player,@NotNull DeserializationContext context){return new Instance(player,net.minecraft.util.GsonHelper.getAsString(json,"action"));}
 public static final class Instance extends AbstractCriterionTriggerInstance {
  private final String action;
  public Instance(ContextAwarePredicate player,String action){super(ID,player);this.action=action;}
  @NotNull public com.google.gson.JsonObject serializeToJson(@NotNull SerializationContext context){var json=super.serializeToJson(context);json.addProperty("action",action);return json;}
 }
}
