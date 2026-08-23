package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client == null || client.player == null || client.world == null) {
                return;
            }

            if (client.player.isCreative() || client.player.isSpectator()) {
                return;
            }

            try {
                float health = client.player.getHealth();
                if (health <= 6.0f && health > 0) {
                    float missingHealth = 6.0f - health;
                    float speedMultiplier = 1.0f + (missingHealth * 0.4f);
                    double shake = Math.sin(client.player.age * 0.8 * speedMultiplier) * (1.5 + missingHealth * 0.5);

                    int width = client.getWindow().getScaledWidth();
                    int height = client.getWindow().getScaledHeight();

                    TextRenderer font = client.textRenderer;

                    String heartIcon = "§c❤";
                    String warningText = "§c!";

                    int heartX = (width / 2) + 12;
                    int heartY = (height / 2) - 4 + (int) shake;

                    int textX = (width / 2) - 82;
                    int textY = height - 40 + (int) (shake * 1.2);

                    drawContext.drawTextWithShadow(font, heartIcon, heartX, heartY, 0xFF0000);
                    drawContext.drawTextWithShadow(font, warningText, textX, textY, 0xFF0000);
                }
            } catch (Throwable t) {
                // Verhindert Spiel-Crashes
            }
        });
    }
}
