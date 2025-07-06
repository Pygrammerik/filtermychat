package org.sooshka.filtermychat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModConfig {
    private static final String CONFIG_FILE = "filtermychat_config.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path configPath;
    
    private boolean firstRun = true;
    private Map<String, Boolean> categorySettings = new HashMap<>();
    
    public ModConfig() {
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        initializeDefaultSettings();
    }
    
    private void initializeDefaultSettings() {
        categorySettings.put("swearing", true);
        categorySettings.put("links", true);
        categorySettings.put("trolling", true);
    }
    
    public void load() {
        File configFile = configPath.toFile();
        
        System.out.println("FilterMyChat: Loading config from: " + configFile.getAbsolutePath());
        
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = gson.fromJson(reader, ConfigData.class);
                if (data != null) {
                    this.firstRun = data.firstRun;
                    this.categorySettings = data.categorySettings != null ? data.categorySettings : new HashMap<>();
                    System.out.println("FilterMyChat: Config loaded - firstRun: " + this.firstRun);
                }
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        } else {
            System.out.println("FilterMyChat: Config file does not exist, using defaults - firstRun: " + this.firstRun);
        }
    }
    
    public void save() {
        try {
            File configFile = configPath.toFile();
            configFile.getParentFile().mkdirs();
            
            ConfigData data = new ConfigData();
            data.firstRun = this.firstRun;
            data.categorySettings = this.categorySettings;
            
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(data, writer);
                System.out.println("FilterMyChat: Config saved - firstRun: " + this.firstRun);
            }
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
    
    public boolean isFirstRun() {
        return firstRun;
    }
    
    public void setFirstRun(boolean firstRun) {
        System.out.println("FilterMyChat: Setting firstRun from " + this.firstRun + " to " + firstRun);
        this.firstRun = firstRun;
        save();
    }
    
    public boolean isCategoryEnabled(String category) {
        return categorySettings.getOrDefault(category, true);
    }
    
    public void setCategoryEnabled(String category, boolean enabled) {
        categorySettings.put(category, enabled);
        save();
    }
    
    public Map<String, Boolean> getCategorySettings() {
        return new HashMap<>(categorySettings);
    }
    
    private static class ConfigData {
        public boolean firstRun = true;
        public Map<String, Boolean> categorySettings = new HashMap<>();
    }
} 