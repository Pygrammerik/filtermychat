package org.sooshka.filtermychat.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.sooshka.filtermychat.lang.LanguageManager;

public class WelcomeScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final String TITLE_KEY = "filtermychat.welcome.title";
    private static final String SUBTITLE_KEY = "filtermychat.welcome.subtitle";
    private static final String NEXT_KEY = "filtermychat.welcome.next";
    
    public WelcomeScreen() {
        super(Text.empty());
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Кнопка "Дальше"
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(getTranslationWithFallback(NEXT_KEY, "Дальше")),
            button -> this.client.setScreen(new SettingsScreen(this))
        ).dimensions(centerX - BUTTON_WIDTH / 2, centerY + 20, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Рендерим заголовок по центру
        drawCenteredText(context, this.textRenderer, Text.literal(getTranslationWithFallback(TITLE_KEY, "Добро пожаловать!")), this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        drawCenteredText(context, this.textRenderer, Text.literal(getTranslationWithFallback(SUBTITLE_KEY, "Выполните первоначальную настройку.")), this.width / 2, this.height / 2 - 20, 0xCCCCCC);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int y, int color) {
        int width = textRenderer.getWidth(text);
        context.drawText(textRenderer, text, centerX - width / 2, y, color, false);
    }
    
    private String getTranslationWithFallback(String key, String fallback) {
        String translation = org.sooshka.filtermychat.lang.LanguageManager.getTranslation(key);
        return translation.equals(key) ? fallback : translation;
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
} 