package de.artemis.floraexpansion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.fml.i18n.FMLTranslations;
import net.neoforged.neoforge.client.gui.LoadingErrorScreen;

final class ClientSmokeStartup {
    static boolean atTitle(Minecraft minecraft) {
        if (minecraft.gui.overlay() != null) return false;
        if (minecraft.gui.screen() instanceof AccessibilityOnboardingScreen onboarding) {
            onboarding.onClose();
            return false;
        }
        if (minecraft.gui.screen() instanceof LoadingErrorScreen loading) {
            String label = FMLTranslations.parseMessage("fml.button.continue.launch");
            var button = loading.children().stream().filter(child -> child instanceof Button candidate
                    && candidate.active && candidate.getMessage().getString().equals(label)).findFirst();
            if (button.isEmpty()) throw new IllegalStateException("Client startup has loading errors");
            ((Button) button.get()).onPress(null);
            return false;
        }
        return minecraft.gui.screen() instanceof TitleScreen;
    }
    private ClientSmokeStartup() {}
}
