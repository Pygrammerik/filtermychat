package org.sooshka.filtermychat.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.sooshka.filtermychat.Filtermychat;
import org.sooshka.filtermychat.config.ModConfig;
import org.sooshka.filtermychat.lang.LanguageManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.HashMap;
import java.util.Map;

public class SettingsScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 24;
    private static final int CHECKBOX_WIDTH = 200;
    private static final int CHECKBOX_HEIGHT = 24;
    private static final int CARD_WIDTH = 220;
    private static final int CARD_HEIGHT = 80;
    private static final int CARD_PADDING = 10;
    
    private static final String TITLE_KEY = "filtermychat.settings.title";
    
    private final Map<String, ButtonWidget> checkboxes = new HashMap<>();
    private final boolean isFirstRun;
    private final Screen parent;
    
    public SettingsScreen() {
        super(Text.empty());
        this.isFirstRun = Filtermychat.getConfig().isFirstRun();
        this.parent = null;
    }
    
    public SettingsScreen(Screen parent) {
        super(Text.empty());
        this.isFirstRun = Filtermychat.getConfig().isFirstRun();
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 100;
        
        // Карточки для категорий по центру в ряд
        int totalWidth = CARD_WIDTH * 3 + 40; // 3 карточки + отступы
        int startX = centerX - totalWidth / 2;
        
        addCategoryCard("swearing", 
                       getTranslationWithFallback("filtermychat.category.swearing", "Мат и нецензурная лексика"),
                       getTranslationWithFallback("filtermychat.category.swearing.desc", "Фильтрует нецензурные выражения"), 
                       startX, startY);
        
        addCategoryCard("links", 
                       getTranslationWithFallback("filtermychat.category.links", "Ссылки"),
                       getTranslationWithFallback("filtermychat.category.links.desc", "Блокирует ссылки на внешние ресурсы"), 
                       startX + CARD_WIDTH + 20, startY);
        
        addCategoryCard("trolling", 
                       getTranslationWithFallback("filtermychat.category.trolling", "Троллинг и провокации"),
                       getTranslationWithFallback("filtermychat.category.trolling.desc", "Фильтрует провокационные сообщения"), 
                       startX + (CARD_WIDTH + 20) * 2, startY);
        
        // Кнопка выбора языка
        this.addDrawableChild(new GradientButtonWidget(
            centerX - BUTTON_WIDTH / 2, 
            startY + CARD_HEIGHT + 20, 
            BUTTON_WIDTH, 
            BUTTON_HEIGHT,
            Text.literal("Language: " + LanguageManager.getLanguageDisplayName(LanguageManager.getCurrentLanguage())),
            button -> openLanguageSelector()
        ));
        
        // Кнопка сохранения с градиентом
        String buttonText = isFirstRun ? 
            getTranslationWithFallback("filtermychat.settings.finish", "Завершить настройку") : 
            getTranslationWithFallback("filtermychat.settings.save", "Сохранить настройки");
        this.addDrawableChild(new GradientButtonWidget(
            centerX - BUTTON_WIDTH / 2, 
            startY + CARD_HEIGHT + 50, 
            BUTTON_WIDTH, 
            BUTTON_HEIGHT,
            Text.literal(buttonText),
            button -> saveSettings()
        ));
    }
    
    private String getTranslationWithFallback(String key, String fallback) {
        String translation = LanguageManager.getTranslation(key);
        return translation.equals(key) ? fallback : translation;
    }
    
    private void openLanguageSelector() {
        String[] languages = LanguageManager.getAvailableLanguages();
        String currentLang = LanguageManager.getCurrentLanguage();
        
        // Переключаем на следующий язык
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(currentLang)) {
                String nextLang = languages[(i + 1) % languages.length];
                LanguageManager.setLanguage(nextLang);
                // Пересоздаем экран для применения нового языка
                this.client.setScreen(new SettingsScreen(this.parent));
                break;
            }
        }
    }
    
    private void addCategoryCard(String category, String name, String description, int x, int y) {
        boolean enabled = Filtermychat.getConfig().isCategoryEnabled(category);
        
        // Создаем красивую кнопку-карточку
        ButtonWidget cardButton = new CategoryCardWidget(
            x, y, CARD_WIDTH, CARD_HEIGHT,
            Text.literal(name),
            Text.literal(description),
            enabled,
            button -> {
                boolean newState = !Filtermychat.getConfig().isCategoryEnabled(category);
                Filtermychat.getConfig().setCategoryEnabled(category, newState);
                Filtermychat.getFilterManager().setCategoryEnabled(category, newState);
                ((CategoryCardWidget) button).setEnabled(newState);
            }
        );
        
        checkboxes.put(category, cardButton);
        this.addDrawableChild(cardButton);
    }
    
    private void saveSettings() {
        if (isFirstRun) {
            // Немедленно сбрасываем флаг и сохраняем
            Filtermychat.getConfig().setFirstRun(false);
            Filtermychat.getConfig().save();
            
            // Принудительно обновляем конфигурацию в памяти
            try {
                Filtermychat.getConfig().load();
            } catch (Exception e) {
                // Игнорируем ошибки загрузки
            }
            
            // Закрываем экран
            this.client.setScreen(null);
        } else {
            Filtermychat.getConfig().save();
            if (parent != null) {
                this.client.setScreen(parent);
            } else {
                this.client.setScreen(null);
            }
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Красивый градиентный фон
        renderGradientBackground(context, mouseX, mouseY, delta);
        
        // Рендерим заголовок с тенью
        String title = getTranslationWithFallback(TITLE_KEY, "Настройки фильтрации чата");
        drawTitleWithShadow(context, Text.literal(title), this.width / 2, 30);
        
        // Подзаголовок
        String subtitleKey = isFirstRun ? "filtermychat.settings.subtitle.first" : "filtermychat.settings.subtitle.normal";
        String subtitleText = isFirstRun ? 
            getTranslationWithFallback(subtitleKey, "Настройте фильтры для комфортной игры") :
            getTranslationWithFallback(subtitleKey, "Управление фильтрацией чата");
        drawCenteredText(context, this.textRenderer, Text.literal(subtitleText), this.width / 2, 55, 0xCCCCCC);
        
        // Подпись автора в правом нижнем углу
        String authorText = getTranslationWithFallback("filtermychat.settings.author", "Made by Sooshka");
        int authorWidth = this.textRenderer.getWidth(authorText);
        context.drawText(this.textRenderer, authorText, 
                        this.width - authorWidth - 10, this.height - 20, 0x888888, false);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderGradientBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Темный градиентный фон
        context.fillGradient(0, 0, this.width, this.height, 0xFF1a1a1a, 0xFF2d2d2d);
        
        // Добавляем декоративные элементы
        for (int i = 0; i < 5; i++) {
            int x = (int) (Math.sin(System.currentTimeMillis() * 0.001 + i) * 100 + this.width / 2);
            int y = (int) (Math.cos(System.currentTimeMillis() * 0.001 + i) * 50 + this.height / 2);
            context.fill(x, y, x + 2, y + 2, 0x33FFFFFF);
        }
    }
    
    private void drawTitleWithShadow(DrawContext context, Text title, int centerX, int y) {
        // Тень
        drawCenteredText(context, this.textRenderer, title, centerX + 1, y + 1, 0x66000000);
        // Основной текст
        drawCenteredText(context, this.textRenderer, title, centerX, y, 0xFFFFFF);
    }
    
    private void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int y, int color) {
        int width = textRenderer.getWidth(text);
        context.drawText(textRenderer, text, centerX - width / 2, y, color, false);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    // Кастомный виджет для карточки категории
    private static class CategoryCardWidget extends ButtonWidget {
        private final Text description;
        private boolean enabled;
        
        public CategoryCardWidget(int x, int y, int width, int height, Text title, Text description, 
                                boolean enabled, PressAction onPress) {
            super(x, y, width, height, title, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.description = description;
            this.enabled = enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        
        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            // Фон карточки с градиентом
            int backgroundColor = enabled ? 0xFF2d5a2d : 0xFF5a2d2d;
            int borderColor = enabled ? 0xFF4CAF50 : 0xFFF44336;
            
            if (this.isHovered()) {
                backgroundColor = enabled ? 0xFF3d6a3d : 0xFF6a3d3d;
                borderColor = enabled ? 0xFF66BB6A : 0xFFEF5350;
            }
            
            // Рамка
            context.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, borderColor);
            // Фон
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, backgroundColor);
            
            // Иконка (упрощенная версия без текстуры)
            context.fill(this.getX() + 5, this.getY() + 5, this.getX() + 21, this.getY() + 21, 0xFFFFFFFF);
            
            // Заголовок
            context.drawText(MinecraftClient.getInstance().textRenderer, this.getMessage(), 
                           this.getX() + 25, this.getY() + 8, 0xFFFFFF, false);
            
            // Описание
            context.drawText(MinecraftClient.getInstance().textRenderer, this.description, 
                           this.getX() + 5, this.getY() + 25, 0xCCCCCC, false);
            
            // Статус
            String status = enabled ? 
                LanguageManager.getTranslation("filtermychat.status.enabled").equals("filtermychat.status.enabled") ? "✓ Включено" : LanguageManager.getTranslation("filtermychat.status.enabled") : 
                LanguageManager.getTranslation("filtermychat.status.disabled").equals("filtermychat.status.disabled") ? "✗ Отключено" : LanguageManager.getTranslation("filtermychat.status.disabled");
            int statusColor = enabled ? 0xFF4CAF50 : 0xFFF44336;
            context.drawText(MinecraftClient.getInstance().textRenderer, status, 
                           this.getX() + 5, this.getY() + 40, statusColor, false);
        }
    }
    
    // Кастомный виджет для кнопки с градиентом
    private static class GradientButtonWidget extends ButtonWidget {
        public GradientButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }
        
        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            // Градиентный фон
            int color1 = this.isHovered() ? 0xFF4CAF50 : 0xFF2E7D32;
            int color2 = this.isHovered() ? 0xFF66BB6A : 0xFF388E3C;
            
            context.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color1, color2);
            
            // Рамка
            context.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, 0xFF4CAF50);
            
            // Текст
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int textWidth = textRenderer.getWidth(this.getMessage());
            context.drawText(textRenderer, this.getMessage(), 
                           this.getX() + (this.width - textWidth) / 2, 
                           this.getY() + (this.height - 8) / 2, 0xFFFFFF, false);
        }
    }
} 