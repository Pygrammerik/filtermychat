package org.sooshka.filtermychat.filter;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FilterCategory {
    @SerializedName("category")
    private String category;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("enabled")
    private boolean enabled;
    
    @SerializedName("words")
    private List<String> words;
    
    public FilterCategory() {}
    
    public FilterCategory(String category, String name, String description, boolean enabled, List<String> words) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.words = words;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public List<String> getWords() {
        return words;
    }
    
    public void setWords(List<String> words) {
        this.words = words;
    }
    
    public boolean containsWord(String word) {
        if (!enabled || words == null) {
            return false;
        }
        
        String lowerWord = word.toLowerCase();
        
        // Для категории ссылок используем более строгую проверку
        if ("links".equals(category)) {
            return words.stream()
                    .anyMatch(filterWord -> {
                        String lowerFilter = filterWord.toLowerCase();
                        // Проверяем, содержит ли слово фильтр или начинается с него
                        return lowerWord.contains(lowerFilter) || 
                               lowerWord.startsWith(lowerFilter) ||
                               lowerWord.endsWith(lowerFilter);
                    });
        }
        
        // Для остальных категорий используем обычную проверку
        return words.stream()
                .anyMatch(filterWord -> lowerWord.contains(filterWord.toLowerCase()));
    }
} 