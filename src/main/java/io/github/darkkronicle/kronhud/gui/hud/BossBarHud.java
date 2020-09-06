package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.mixins.AccessorBossBarHud;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.UUID;

public class BossBarHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "bossbarhud");
    private static final Identifier BARS_TEXTURE = new Identifier("textures/gui/bars.png");
    private final BossBar placeholder = new CustomBossBar(new LiteralText("Boss bar"), BossBar.Color.WHITE, BossBar.Style.PROGRESS);
    private final BossBar placeholder2 = Util.make(() -> {
        BossBar boss = new CustomBossBar(new LiteralText("More boss bars..."), BossBar.Color.PURPLE, BossBar.Style.NOTCHED_6);
        boss.setPercent(0.45F);
        return boss;
    });

    private Map<UUID, ClientBossBar> bossBars;
    private final MinecraftClient client;

    public BossBarHud() {
        super(184, 80);
        client = MinecraftClient.getInstance();
    }

    public void setBossBars() {
        bossBars = ((AccessorBossBarHud) client.inGameHud.getBossBarHud()).getBossBars();
    }


    @Override
    public void render(MatrixStack matrices) {
        setBossBars();
        if (this.bossBars.isEmpty()) {
            return;
        }
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition scaledPos = getScaledPos();

        int by = 12;
        for (ClientBossBar bossBar : bossBars.values()) {
            renderBossBar(matrices, scaledPos.getX(), by + scaledPos.getY(), bossBar);
            by = by + 19;
            if (by > height) {
                break;
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
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        DrawUtil.outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());
        renderBossBar(matrices, pos.getX(), pos.getY() + 12, placeholder);
        renderBossBar(matrices, pos.getX(), pos.getY() + 31, placeholder2);
        hovered = false;
        matrices.pop();
    }

    private void renderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar) {
        this.client.getTextureManager().bindTexture(BARS_TEXTURE);
        if (getStorage().bar) {
            DrawableHelper.drawTexture(matrices, x, y, 0, bossBar.getColor().ordinal() * 5 * 2, 182, 5, 256, 256);
            if (bossBar.getOverlay() != BossBar.Style.PROGRESS) {
                DrawableHelper.drawTexture(matrices, x, y, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2, 182, 5, 256, 256);
            }

            int i = (int) (bossBar.getPercent() * 183.0F);
            if (i > 0) {
                DrawableHelper.drawTexture(matrices, x, y, 0, bossBar.getColor().ordinal() * 5 * 2 + 5, i, 5, 256, 256);
                if (bossBar.getOverlay() != BossBar.Style.PROGRESS) {
                    DrawableHelper.drawTexture(matrices, x, y, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5, 256, 256);
                }
            }
        }
        if (getStorage().text) {
            Text text = bossBar.getName();
            client.textRenderer.drawWithShadow(matrices, text, x + ((float) width / 2) - ((float) client.textRenderer.getWidth(text) / 2), y - 9, Colors.WHITE.color().color());
        }
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public boolean moveable() {
        return true;
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.bossBarHudStorage;
    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.bossbarhud");
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    public static class Storage extends AbstractStorage {
        public boolean text;
        public boolean bar;

        public Storage() {
            x = 0.5F;
            y = 0F;
            scale = 1F;
            enabled = true;
            text = true;
            bar = true;
        }
    }

    public static class CustomBossBar extends BossBar {
        public CustomBossBar(Text name, Color color, Style style) {
            super(MathHelper.randomUuid(), name, color, style);
        }
    }
}
