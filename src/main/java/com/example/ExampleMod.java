package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ExampleMod implements ClientModInitializer {
    private static final Identifier HARDCORE_HEART = Identifier.of("minecraft", "textures/gui/sprites/hud/heart/hardcore_full.png");
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        // 1. HUD Rendering (Herz neben Crosshair & Ausrufezeichen über Rüstung)
        HudRenderCallback.EVENT.register((drawContext, tickCounterRef) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.player.isCreative() || client.player.isSpectator()) return;

            float health = client.player.getHealth(); // 6.0 = 3 Herzen
            if (health <= 6.0f && health > 0) {
                float missingHealth = 6.0f - health; // 0 bis 5
                float speedMultiplier = 1.0f + (missingHealth * 0.4f);
                
                // Wackel-Offset berechnen
                double shake = Math.sin(client.player.age * 0.8 * speedMultiplier) * (1.5 + missingHealth * 0.5);

                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();

                // Hardcore-Herz rechts neben dem Fadenkreuz
                int heartX = (width / 2) + 12;
                int heartY = (height / 2) - 4 + (int) shake;
                drawContext.drawTexture(HARDCORE_HEART, heartX, heartY, 0, 0, 9, 9, 9, 9);

                // Rotes Ausrufezeichen über der Rüstungsleiste
                TextRenderer font = client.textRenderer;
                String warningText = "§c!";
                int textX = (width / 2) - 82;
                int textY = height - 49 + (int) (shake * 1.2);
                drawContext.drawTextWithShadow(font, warningText, textX, textY, 0xFF0000);
            }
        });

        // 2. Sound-System (Herzklopfen wird lauter & schneller)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            float health = client.player.getHealth();

            if (health <= 6.0f && health > 0) {
                tickCounter++;
                int interval = Math.max(5, (int) (20 - ((6.0f - health) * 3)));
                float volume = 0.3f + ((6.0f - health) * 0.15f);

                if (tickCounter % interval == 0) {
                    client.getSoundManager().play(
                        PositionedSoundInstance.master(SoundEvents.ENTITY_WARDEN_HEARTBEAT, 0.8f + (volume * 0.2f), volume)
                    );
                }
            } else {
                tickCounter = 0;
            }
        });
    }
}
