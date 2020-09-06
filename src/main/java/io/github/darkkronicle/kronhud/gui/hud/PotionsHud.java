package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class PotionsHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "potionshud");


    public PotionsHud() {
        super(60, 200);
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        ArrayList<StatusEffectInstance> effects = new ArrayList<>(client.player.getStatusEffects());
        if (!effects.isEmpty()) {
            StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
            int lastY = 1;
            DrawPosition pos = getScaledPos();
            for (int i = 0; i < effects.size(); i++) {
                StatusEffectInstance effect = effects.get(i);
                StatusEffect type = effect.getEffectType();
                if (type == StatusEffects.NIGHT_VISION) {
                    continue;
                }
                if (i > 8) {
                    break;
                }
                Sprite sprite = statusEffectSpriteManager.getSprite(type);
                this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
                DrawableHelper.drawSprite(matrices, pos.getX() + 1, pos.getY() + lastY, 0, 18, 18, sprite);
                client.textRenderer.drawWithShadow(matrices, StatusEffectUtil.durationToString(effect, 1), pos.getX() + 20, pos.getY() + 6 + lastY, Colors.WHITE.color().color());
                lastY = lastY + 20;

            }
        }
        matrices.pop();

    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.SELECTOR_BLUE.color().withAlpha(100).color());
        } else {
            rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());
        StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.SPEED);
        StatusEffect type = effect.getEffectType();
        Sprite sprite = statusEffectSpriteManager.getSprite(type);
        this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
        DrawableHelper.drawSprite(matrices, pos.getX() + 1, pos.getY() + 1, 0, 18, 18, sprite);
        client.textRenderer.drawWithShadow(matrices, StatusEffectUtil.durationToString(effect, 1), pos.getX() + 20, pos.getY() + 7, Colors.WHITE.color().color());
        hovered = false;
        matrices.pop();
    }

    @Override
    public boolean moveable() {
        return true;
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.potionsHudStorage;
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.potionshud");
    }

    public static class Storage extends AbstractStorage {
        public Storage() {
            x = 0;
            y = 0.5F;
            scale = 1;
        }
    }
}
