package org.sooshka.filtermychat.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class ChatFilterManager {
    private static final String FILTER_PATH = "assets/filtermychat/filters/";
    private static final Logger LOGGER = LoggerFactory.getLogger("FilterMyChat");
    private final Map<String, FilterCategory> categories = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public void loadFilters() {
        categories.clear();
        
        LOGGER.info("Starting to load filter categories...");
        
        // Загружаем предопределенные категории
        loadFilterCategory("swearing.json");
        loadFilterCategory("links.json");
        loadFilterCategory("trolling.json");
        
        LOGGER.info("Loaded {} filter categories", categories.size());
        
        // Выводим информацию о загруженных категориях
        for (Map.Entry<String, FilterCategory> entry : categories.entrySet()) {
            FilterCategory category = entry.getValue();
            LOGGER.info("Category '{}': {} words, enabled: {}", 
                category.getName(), 
                category.getWords().size(), 
                category.isEnabled());
        }
    }
    
    private void loadFilterCategory(String filename) {
        try {
            LOGGER.info("Loading filter category from: {}", filename);
            
            // Пробуем разные пути
            String[] paths = {
                FILTER_PATH + filename,
                "assets/filtermychat/filters/" + filename,
                "/assets/filtermychat/filters/" + filename
            };
            
            FilterCategory category = null;
            for (String path : paths) {
                try (Reader reader = new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(path))) {
                    
                    if (reader != null) {
                        category = gson.fromJson(reader, FilterCategory.class);
                        LOGGER.info("Successfully loaded from path: {}", path);
                        break;
                    }
                } catch (Exception e) {
                    LOGGER.warn("Failed to load from path: {}", path);
                }
            }
            
            if (category != null) {
                categories.put(category.getCategory(), category);
                LOGGER.info("Loaded filter category: {} with {} words", category.getName(), category.getWords().size());
            } else {
                LOGGER.error("Failed to load filter category from {}", filename);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load filter category from {}", filename, e);
        }
    }
    
    public String filterMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return message;
        }
        
        LOGGER.info("Filtering message: '{}'", message);
        
        String filteredMessage = message;
        
        // Разбиваем сообщение на слова
        String[] words = message.split("\\s+");
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String filteredWord = filterWord(word);
            if (!word.equals(filteredWord)) {
                LOGGER.info("Filtered word '{}' to '{}'", word, filteredWord);
                filteredMessage = filteredMessage.replace(word, filteredWord);
            }
        }
        
        LOGGER.info("Final filtered message: '{}'", filteredMessage);
        return filteredMessage;
    }
    
    private String filterWord(String word) {
        // Убираем знаки препинания для проверки
        String cleanWord = word.replaceAll("[^\\wа-яё]", "");
        
        LOGGER.info("Checking word: '{}' (clean: '{}')", word, cleanWord);
        
        for (FilterCategory category : categories.values()) {
            if (category.isEnabled()) {
                // Проверяем как очищенное слово, так и оригинальное
                if (category.containsWord(cleanWord) || category.containsWord(word)) {
                    LOGGER.info("Word '{}' found in category '{}'", word, category.getName());
                    // Заменяем все символы на *
                    return "*".repeat(word.length());
                }
            }
        }
        
        return word;
    }
    
    public void setCategoryEnabled(String category, boolean enabled) {
        FilterCategory filterCategory = categories.get(category);
        if (filterCategory != null) {
            filterCategory.setEnabled(enabled);
            LOGGER.info("Category {} {}: {}", category, enabled ? "enabled" : "disabled", filterCategory.getName());
        }
    }
    
    public boolean isCategoryEnabled(String category) {
        FilterCategory filterCategory = categories.get(category);
        return filterCategory != null && filterCategory.isEnabled();
    }
    
    public Map<String, FilterCategory> getCategories() {
        return new HashMap<>(categories);
    }
    
    public FilterCategory getCategory(String category) {
        return categories.get(category);
    }
} 