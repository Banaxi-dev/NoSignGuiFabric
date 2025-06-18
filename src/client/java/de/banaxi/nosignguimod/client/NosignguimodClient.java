package de.banaxi.nosignguimod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class NosignguimodClient implements ClientModInitializer {
    public static boolean guiDisabled = false;
    private static KeyBinding toggleGuiKey;

    @Override
    public void onInitializeClient() {
        // Command "/nosigngui"
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("nosigngui").executes(ctx -> {
                    toggleGui();
                    return 1;
                })
        ));

        // Keybinding registrieren (default: X)
        toggleGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nosignguimod.toggle",               // translation key, für Controls-Optionen
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,                         // Default-Taste: X
                "category.nosignguimod"                  // Kategorie in den Controls
        ));

        // Tick-Event für Keybind und GUI schließen
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleGuiKey.wasPressed()) {
                toggleGui();
            }

            if (guiDisabled && (client.currentScreen instanceof SignEditScreen || client.currentScreen instanceof HangingSignEditScreen)) {
                client.setScreen(null);
            }
        });
    }

    private void toggleGui() {
        guiDisabled = !guiDisabled;
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("SignGui is now " + (guiDisabled ? "§cdeactivated" : "§aactivated")),
                    false
            );
        }
    }
}
