package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ExampleModClient implements ClientModInitializer {
    // Korrekte Standard-Identifier für das Hardcore-Herz
    private static final Identifier HARDCORE_HEART = Identifier.of("minecraft", "textures/gui/sprites/hud/heart/hardcore_full.png");
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;
            if (client.player.isCreative() || client.player.isSpectator()) return;

            float health = client.player.getHealth();
            if (health <= 6.0f && health > 0) {
                float missingHealth = 6.0f - health;
                float speedMultiplier = 1.0f + (missingHealth * 0.4f);
                double shake = Math.sin(client.player.age * 0.8 * speedMultiplier) * (1.5 + missingHealth * 0.5);

                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();

                int heartX = (width / 2) + 12;
                int heartY = (height / 2) - 4 + (int) shake;
                
                // Nutzt den sicheren Texture-Render für GUI Sprites
                drawContext.drawGuiTexture(HARDCORE_HEART, heartX, heartY, 9, 9);

                TextRenderer font = client.textRenderer;
                String warningText = "§c!";
                int textX = (width / 2) - 82;
                int textY = height - 40 + (int) (shake * 1.2);
                drawContext.drawTextWithShadow(font, warningText, textX, textY, 0xFF0000);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MinecraftClient clientInstance = MinecraftClient.getInstance();
            if (clientInstance.player == null || clientInstance.world == null) return;
            if (clientInstance.player.isCreative() || clientInstance.player.isSpectator()) return;

            float health = clientInstance.player.getHealth();
            if (health <= 6.0f && health > 0) {
                float missingHealth = 6.0f - health;
                int interval = Math.max(10, (int) (40 - (missingHealth * 6.0f)));

                tickCounter++;
                if (tickCounter >= interval) {
                    tickCounter = 0;
                    float pitch = 0.8f + (missingHealth * 0.1f);
                    clientInstance.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_HURT, pitch, 1.0f));
                }
            } else {
                tickCounter = 0;
            }
        });
    }
}
