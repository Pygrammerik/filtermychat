package org.sooshka.filtermychat.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.sooshka.filtermychat.Filtermychat;

@Mixin(ChatHud.class)
public class ChatMixin {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, net.minecraft.network.message.MessageSignatureData signature, net.minecraft.client.gui.hud.MessageIndicator indicator, CallbackInfo ci) {
        // Отладочная информация - проверяем, что миксин срабатывает
        System.out.println("FilterMyChat: Mixin triggered! Message: " + (message != null ? message.getString() : "null"));
        
        // Проверяем, что сообщение не null
        if (message == null) {
            System.out.println("FilterMyChat: Message is null, skipping");
            return;
        }
        
        // Получаем текст сообщения
        String messageText = message.getString();
        
        // Пропускаем пустые сообщения
        if (messageText == null || messageText.trim().isEmpty()) {
            System.out.println("FilterMyChat: Message is empty, skipping");
            return;
        }
        
        // Пропускаем системные сообщения (начинающиеся с [ или §)
        if (messageText.startsWith("[") || messageText.startsWith("§")) {
            System.out.println("FilterMyChat: System message, skipping: " + messageText);
            return;
        }
        
        // Проверяем, что FilterManager инициализирован
        if (Filtermychat.getFilterManager() == null) {
            System.out.println("FilterMyChat: FilterManager is null!");
            return;
        }
        
        // Отладочная информация
        System.out.println("FilterMyChat: Processing message: " + messageText);
        
        // Фильтруем сообщение
        String filteredText = Filtermychat.getFilterManager().filterMessage(messageText);
        
        // Если сообщение было изменено, заменяем его
        if (!messageText.equals(filteredText)) {
            System.out.println("FilterMyChat: Message filtered from '" + messageText + "' to '" + filteredText + "'");
            
            // Отменяем оригинальное сообщение
            ci.cancel();
            
            // Добавляем отфильтрованное сообщение
            ChatHud chatHud = (ChatHud) (Object) this;
            Text filteredMessage = Text.literal(filteredText);
            
            // Используем правильный метод для добавления сообщения
            try {
                // В Minecraft 1.21.3 используем addMessage с MessageSignatureData и MessageIndicator
                chatHud.addMessage(filteredMessage, signature, indicator);
                System.out.println("FilterMyChat: Filtered message added successfully");
            } catch (Exception e) {
                // Если не получилось, просто логируем
                System.out.println("FilterMyChat: Failed to add filtered message: " + e.getMessage());
            }
        } else {
            System.out.println("FilterMyChat: Message not filtered: " + messageText);
        }
    }
} 