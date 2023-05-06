package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.kronhud.config.DefaultOptions;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronOptionList;
import io.github.darkkronicle.kronhud.gui.component.DynamicallyPositionable;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;
import io.github.darkkronicle.kronhud.gui.layout.CardinalOrder;
import io.github.darkkronicle.kronhud.util.Rectangle;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PotionsHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "potionshud");

    private final KronOptionList<AnchorPoint> anchor = DefaultOptions.getAnchorPoint(AnchorPoint.TOP_LEFT);

    private final KronOptionList<CardinalOrder> order = DefaultOptions.getCardinalOrder(CardinalOrder.TOP_DOWN);

    private final KronBoolean iconsOnly = new KronBoolean("iconsonly", ID.getPath(), false);

    private final KronBoolean showName = new KronBoolean("showname", ID.getPath(), false);

    public PotionsHud() {
        super(50, 200, false);
    }

    private int calculateWidth(List<StatusEffectInstance> effects) {
        if (order.getValue().isXAxis()) {
            if (iconsOnly.getValue()) {
                return 20 * effects.size() + 2;
            }
            if (showName.getValue()) {
                float totalWidth = 0;
                for (StatusEffectInstance effect : effects) {
                    int potionWidth = client.textRenderer.getWidth(generatePotionText(effect));
                    totalWidth += potionWidth / 1.25f + 22;
                }
                return (int)Math.floor(totalWidth + 2);
            }
            return 50 * effects.size() + 2;
        } else {
            if (iconsOnly.getValue()) {
                return 20;
            }
            if (showName.getValue()) {
                int maxWidth = 0;
                for (StatusEffectInstance effect : effects) {
                    int potionWidth = client.textRenderer.getWidth(generatePotionText(effect));
                    if (maxWidth < potionWidth) maxWidth = potionWidth;
                }
                return (int)Math.floor(maxWidth / 1.25f + 24);
            }
            return 50;
        }
    }

    private int calculateHeight(List<StatusEffectInstance> effects) {
        if (order.getValue().isXAxis()) {
            return 22;
        } else {
            return 20 * effects.size() + 2;
        }
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        List<StatusEffectInstance> effects = new ArrayList<>(client.player.getStatusEffects());
        if (effects.isEmpty()) {
            return;
        }
        renderEffects(matrices, effects);
    }

    private void renderEffects(MatrixStack matrices, List<StatusEffectInstance> effects) {
        int calcWidth = calculateWidth(effects);
        int calcHeight = calculateHeight(effects);
        boolean changed = false;
        if (calcWidth != width) {
            setWidth(calcWidth);
            changed = true;
        }
        if (calcHeight != height) {
            setHeight(calcHeight);
            changed = true;
        }
        if (changed) {
            onBoundsUpdate();
        }
        int lastPos = 0;
        CardinalOrder direction = order.getValue();

        Rectangle bounds = getBounds();
        int x = bounds.x();
        int y = bounds.y();
        for (int i = 0; i < effects.size(); i++) {
            StatusEffectInstance effect = effects.get(direction.getDirection() == -1 ? i : effects.size() - i - 1);
            if (direction.isXAxis()) {
                int potionWidth = renderPotion(matrices, effect, x + lastPos + 1, y + 1);
                lastPos += (iconsOnly.getValue() ? 20 : showName.getValue() ? potionWidth / 1.25f + 22 : 50);
            } else {
                renderPotion(matrices, effect, x + 1, y + 1 + lastPos);
                lastPos += 20;
            }
        }
    }

    private int renderPotion(MatrixStack matrices, StatusEffectInstance effect, int x, int y) {
        StatusEffect type = effect.getEffectType();
        Sprite sprite = client.getStatusEffectSpriteManager().getSprite(type);
        int renderWidth = 0;

        RenderSystem.setShaderTexture(0, sprite.getAtlasId());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        DrawableHelper.drawSprite(matrices, x, y, 0, 18, 18, sprite);

        if (!iconsOnly.getValue()) {
            if (showName.getValue()) {
                String potionTexts = generatePotionText(effect);

                MatrixStack textMatrix = new MatrixStack();

                textMatrix.scale(getScale() / 1.25f, getScale() / 1.25f, 1);

                drawString(textMatrix, client.textRenderer, Text.of(potionTexts), (x + 19) * 1.25f, (y + 2) * 1.25f, textColor.getValue().color(), shadow.getValue());
                renderWidth = client.textRenderer.getWidth(Text.of(potionTexts));

                drawString(textMatrix, client.textRenderer, StatusEffectUtil.durationToString(effect, 1), (x + 19) * 1.25f, (y + 10) * 1.25f,
                        textColor.getValue().color(), shadow.getValue()
                );
            } else {
                drawString(matrices, client.textRenderer, StatusEffectUtil.durationToString(effect, 1), x + 19, showName.getValue() ? y + 10 : y + 5,
                        textColor.getValue().color(), shadow.getValue()
                );
            }
        }
        return renderWidth;
    }

    private String generatePotionText(StatusEffectInstance effect) {
        StatusEffect type = effect.getEffectType();
        String potionName = I18n.translate(type.getTranslationKey());
        int amplifier = effect.getAmplifier();
        if (I18n.hasTranslation("potion.potency." + amplifier)) {
            String translation = I18n.translate("potion.potency." + amplifier);
            return potionName + " " + translation;
        } else {
            return potionName;
        }
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.SPEED);
        StatusEffectInstance jump = new StatusEffectInstance(StatusEffects.JUMP_BOOST);
        StatusEffectInstance haste = new StatusEffectInstance(StatusEffects.HASTE);
        renderEffects(matrices, List.of(effect, jump, haste));
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(anchor);
        options.add(order);
        options.add(iconsOnly);
        options.add(showName);
        return options;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public AnchorPoint getAnchor() {
        return anchor.getValue();
    }
}
