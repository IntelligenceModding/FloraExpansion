package de.artemis.floraexpansion;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;

/** Exposes the real JEI runtime only inside the opt-in client regression suite. */
@JeiPlugin
public final class SaltingJeiSmokePlugin implements IModPlugin {
    static IJeiRuntime runtime;
    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "test_salting_jei"); }
    @Override public void onRuntimeAvailable(IJeiRuntime jeiRuntime) { runtime = jeiRuntime; }
    @Override public void onRuntimeUnavailable() { runtime = null; }
}
