package org.sooshka.filtermychat.lang;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("FilterMyChat");
    private static final String LANG_PATH = "assets/filtermychat/lang/";
    private static final Gson gson = new Gson();
    
    private static Map<String, String> translations = new HashMap<>();
    private static String currentLanguage = "ru_ru";
    
    public static void loadLanguage(String language) {
        currentLanguage = language;
        translations.clear();
        
        LOGGER.info("Loading language: {}", language);
        
        try {
            String langFile = LANG_PATH + language + ".json";
            try (Reader reader = new InputStreamReader(
                    LanguageManager.class.getClassLoader().getResourceAsStream(langFile))) {
                
                if (reader != null) {
                    JsonObject langData = gson.fromJson(reader, JsonObject.class);
                    for (String key : langData.keySet()) {
                        translations.put(key, langData.get(key).getAsString());
                    }
                    LOGGER.info("Loaded {} translations for language: {}", translations.size(), language);
                } else {
                    LOGGER.warn("Language file not found: {}", langFile);
                    loadDefaultLanguage();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load language: {}", language, e);
            loadDefaultLanguage();
        }
    }
    
    private static void loadDefaultLanguage() {
        LOGGER.info("Loading default language: ru_ru");
        loadLanguage("ru_ru");
    }
    
    public static String getTranslation(String key) {
        return translations.getOrDefault(key, key);
    }
    
    public static Text getTranslatedText(String key) {
        return Text.literal(getTranslation(key));
    }
    
    public static String getCurrentLanguage() {
        return currentLanguage;
    }
    
    public static void setLanguage(String language) {
        if (!currentLanguage.equals(language)) {
            loadLanguage(language);
        }
    }
    
    public static String[] getAvailableLanguages() {
        return new String[]{"ru_ru", "en_us"};
    }
    
    public static String getLanguageDisplayName(String language) {
        switch (language) {
            case "ru_ru":
                return "Русский";
            case "en_us":
                return "English";
            default:
                return language;
        }
    }
} 