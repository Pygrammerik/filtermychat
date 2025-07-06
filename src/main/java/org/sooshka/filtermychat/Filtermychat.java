package org.sooshka.filtermychat;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sooshka.filtermychat.config.ModConfig;
import org.sooshka.filtermychat.filter.ChatFilterManager;

public class Filtermychat implements ModInitializer {
    public static final String MOD_ID = "filtermychat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ChatFilterManager filterManager;
    private static ModConfig config;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing FilterMyChat mod");

        // Инициализация конфигурации
        config = new ModConfig();
        config.load();

        // Инициализация менеджера фильтров
        filterManager = new ChatFilterManager();
        filterManager.loadFilters();

        LOGGER.info("FilterMyChat mod initialized successfully");
    }

    public static ChatFilterManager getFilterManager() {
        return filterManager;
    }

    public static ModConfig getConfig() {
        return config;
    }
}