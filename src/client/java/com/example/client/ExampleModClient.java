package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            try {
                MinecraftClient client = MinecraftClient.getInstance();

                // Strikte Prüfungen: Nur im Spiel rendern, nicht im Ladebildschirm
                if (client == null || client.player == null || client.world == null || client.currentScreen != null) {
                    return;
                }

                if (client.player.isCreative() || client.player.isSpectator()) {
                    return;
                }

                float health = client.player.getHealth();
                if (health <= 6.0f && health > 0) {
                    float missingHealth = 6.0f - health;
                    float speedMultiplier = 1.0f + (missingHealth * 0.4f);
                    double shake = Math.sin(client.player.age * 0.8 * speedMultiplier) * (1.5 + missingHealth * 0.5);

                    int width = drawContext.getScaledWindowWidth();
                    int height = drawContext.getScaledWindowHeight();

                    TextRenderer font = client.textRenderer;
                    if (font == null) return;

                    String heartIcon = "§c❤";
                    String warningText = "§c!";

                    int heartX = (width / 2) + 12;
                    int heartY = (height / 2) - 4 + (int) shake;

                    int textX = (width / 2) - 82;
                    int textY = height - 40 + (int) (shake * 1.2);

                    drawContext.drawTextWithShadow(font, heartIcon, heartX, heartY, 0xFF0000);
                    drawContext.drawTextWithShadow(font, warningText, textX, textY, 0xFF0000);
                }
            } catch (Throwable ignored) {
                // Fängt jeden Rendering-Fehler ab
            }
        });
    }
}
