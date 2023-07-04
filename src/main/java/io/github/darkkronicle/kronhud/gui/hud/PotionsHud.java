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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PotionsHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "potionshud");

    private final KronOptionList<AnchorPoint> anchor = DefaultOptions.getAnchorPoint(AnchorPoint.TOP_LEFT);

    private final KronOptionList<CardinalOrder> order = DefaultOptions.getCardinalOrder(CardinalOrder.TOP_DOWN);

    private final KronBoolean iconsOnly = new KronBoolean("iconsonly", ID.getPath(), false);

    public PotionsHud() {
        super(50, 200, false);
    }

    private int calculateWidth(List<StatusEffectInstance> effects) {
        if (order.getValue().isXAxis()) {
            if (iconsOnly.getValue()) {
                return 20 * effects.size() + 2;
            }
            return 50 * effects.size() + 2;
        } else {
            if (iconsOnly.getValue()) {
                return 20;
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
    public void renderComponent(DrawContext context, float delta) {
        List<StatusEffectInstance> effects = new ArrayList<>(client.player.getStatusEffects());
        if (effects.isEmpty()) {
            return;
        }
        renderEffects(context, effects);
    }

    private void renderEffects(DrawContext context, List<StatusEffectInstance> effects) {
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
                renderPotion(context, effect, x + lastPos + 1, y + 1);
                lastPos += (iconsOnly.getValue() ? 20 : 50);
            } else {
                renderPotion(context, effect, x + 1, y + 1 + lastPos);
                lastPos += 20;
            }
        }
    }

    private void renderPotion(DrawContext context, StatusEffectInstance effect, int x, int y) {
        StatusEffect type = effect.getEffectType();
        Sprite sprite = client.getStatusEffectSpriteManager().getSprite(type);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        context.drawSprite(x, y, 0, 18, 18, sprite);
        if (!iconsOnly.getValue()) {
            drawString(context, client.textRenderer, StatusEffectUtil.getDurationText(effect, 1), x + 19, y + 5,
                    textColor.getValue().color(), shadow.getValue()
            );
        }
    }

    @Override
    public void renderPlaceholderComponent(DrawContext context, float delta) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.SPEED);
        StatusEffectInstance jump = new StatusEffectInstance(StatusEffects.JUMP_BOOST);
        StatusEffectInstance haste = new StatusEffectInstance(StatusEffects.HASTE);
        renderEffects(context, List.of(effect, jump, haste));
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(anchor);
        options.add(order);
        options.add(iconsOnly);
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
