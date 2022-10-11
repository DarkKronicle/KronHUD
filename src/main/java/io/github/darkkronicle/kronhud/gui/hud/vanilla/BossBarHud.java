package io.github.darkkronicle.kronhud.gui.hud.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.mixins.AccessorBossBarHud;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BossBarHud extends TextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "bossbarhud");
    private static final Identifier BARS_TEXTURE = new Identifier("textures/gui/bars.png");
    private final BossBar placeholder = new CustomBossBar(Text.literal("Boss bar"), BossBar.Color.WHITE, BossBar.Style.PROGRESS);
    private final BossBar placeholder2 = Util.make(() -> {
        BossBar boss = new CustomBossBar(Text.literal("More boss bars..."), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
        boss.setPercent(0.45F);
        return boss;
    });

    private Map<UUID, ClientBossBar> bossBars;
    private final KronBoolean text = new KronBoolean("text", ID.getPath(), true);
    private final KronBoolean bar = new KronBoolean("bar", ID.getPath(), true);
    // TODO custom color

    public BossBarHud() {
        super(184, 80, false);
    }

    public void setBossBars() {
        bossBars = ((AccessorBossBarHud) client.inGameHud.getBossBarHud()).getBossBars();
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        setBossBars();
        if (this.bossBars.isEmpty()) {
            return;
        }
        DrawPosition scaledPos = getPos();
        int by = 12;
        for (ClientBossBar bossBar : bossBars.values()) {
            renderBossBar(matrices, scaledPos.x(), by + scaledPos.y(), bossBar);
            by = by + 19;
            if (by > getHeight()) {
                break;
            }
        }
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        renderBossBar(matrices, pos.x(), pos.y() + 12, placeholder);
        renderBossBar(matrices, pos.x(), pos.y() + 31, placeholder2);
    }

    private void renderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar) {
        RenderSystem.setShaderTexture(0, BARS_TEXTURE);
        if (bar.getValue()) {
            DrawableHelper.drawTexture(matrices, x, y, 0, bossBar.getColor().ordinal() * 5 * 2, 182, 5, 256, 256);
            if (bossBar.getStyle() != BossBar.Style.PROGRESS) {
                DrawableHelper.drawTexture(matrices, x, y, 0, 80 + (bossBar.getStyle().ordinal() - 1) * 5 * 2, 182, 5, 256, 256);
            }

            int i = (int) (bossBar.getPercent() * 183.0F);
            if (i > 0) {
                DrawableHelper.drawTexture(matrices, x, y, 0, bossBar.getColor().ordinal() * 5 * 2 + 5, i, 5, 256, 256);
                if (bossBar.getStyle() != BossBar.Style.PROGRESS) {
                    DrawableHelper.drawTexture(matrices, x, y, 0, 80 + (bossBar.getStyle().ordinal() - 1) * 5 * 2 + 5, i, 5, 256, 256);
                }
            }
        }
        if (text.getValue()) {
            Text text = bossBar.getName();
            float textX = x + ((float) getWidth() / 2) - ((float) client.textRenderer.getWidth(text) / 2);
            float textY = y - 9;
            if (shadow.getValue()) {
                client.textRenderer.drawWithShadow(matrices, text, textX, textY, textColor.getValue().color());
            } else {
                client.textRenderer.draw(matrices, text, textX, textY, textColor.getValue().color());
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(text);
        options.add(bar);
        return options;
    }

    public static class CustomBossBar extends BossBar {
        public CustomBossBar(Text name, Color color, Style style) {
            super(MathHelper.randomUuid(), name, color, style);
        }
    }
}
