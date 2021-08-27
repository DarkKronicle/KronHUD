package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.config.IConfigBase;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.Color;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PotionsHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "potionshud");

    public PotionsHud() {
        super(60, 200);
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        scale(matrices);
        ArrayList<StatusEffectInstance> effects = new ArrayList<>(client.player.getStatusEffects());
        if (!effects.isEmpty()) {
            StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
            int lastY = 1;
            DrawPosition pos = getPos();
            for (int i = 0; i < effects.size(); i++) {
                StatusEffectInstance effect = effects.get(i);
                StatusEffect type = effect.getEffectType();
//                Removed - night vision appears in vanilla, just not with hideParticles flag.
//                if (type == StatusEffects.NIGHT_VISION) {
//                    continue;
//                }
                if (i > 8) {
                    break;
                }

                Sprite sprite = statusEffectSpriteManager.getSprite(type);
                RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
                RenderSystem.setShaderColor(1, 1, 1, 1);
                DrawableHelper.drawSprite(matrices, pos.x(), pos.y() + 1 + lastY, 0, 18, 18, sprite);
                drawString(matrices, client.textRenderer, StatusEffectUtil.durationToString(effect, 1),
                        pos.x() + 20, pos.y() + 6 + lastY, textColor.getColor().color(), shadow.getBooleanValue());

                lastY += 20;
            }
        }
        matrices.pop();

    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.SPEED);
        StatusEffect type = effect.getEffectType();
        Sprite sprite = statusEffectSpriteManager.getSprite(type);
        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        DrawableHelper.drawSprite(matrices, pos.x() + 1, pos.y() + 1, 0, 18, 18, sprite);
        drawString(matrices, client.textRenderer, StatusEffectUtil.durationToString(effect, 1), pos.x() + 20,
                pos.y() + 7, Color.WHITE.color(), shadow.getBooleanValue());
        hovered = false;
        matrices.pop();
    }

    @Override
    public void addConfigOptions(List<IConfigBase> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(shadow);
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

}
