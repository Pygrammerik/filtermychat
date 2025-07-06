package org.sooshka.filtermychat.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.sooshka.filtermychat.Filtermychat;
import org.sooshka.filtermychat.client.gui.WelcomeScreen;

public class FiltermychatClient implements ClientModInitializer {
    private static boolean welcomeScreenShown = false;

    @Override
    public void onInitializeClient() {
        // Регистрируем обработчик тиков для проверки первого запуска
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }
    
    private void onClientTick(MinecraftClient client) {
        // Проверяем, нужно ли показать экран приветствия
        if (client.player != null && Filtermychat.getConfig().isFirstRun() && !welcomeScreenShown) {
            welcomeScreenShown = true;
            client.setScreen(new WelcomeScreen());
        }
        
        // Сбрасываем флаг, если это больше не первый запуск
        if (!Filtermychat.getConfig().isFirstRun()) {
            welcomeScreenShown = false;
        }
    }
}
