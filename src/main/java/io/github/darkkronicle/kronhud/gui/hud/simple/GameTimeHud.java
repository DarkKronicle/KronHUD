package io.github.darkkronicle.kronhud.gui.hud.simple;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.gui.layout.Justification;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GameTimeHud extends TextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "gametimehud");
    private final KronBoolean isAmPm = new KronBoolean("is12hour", ID.getPath(), true, this::updateFormatter);
    private final KronBoolean sleepDisplay = new KronBoolean("showsleep", ID.getPath(), false);
    private final KronBoolean clockDisplay = new KronBoolean("showclock", ID.getPath(), false);
    private final Identifier bedTexture = Identifier.of("kronhud", "textures/gui/icons/classic_red_bed.png");
    private DateFormat formatter;

    public GameTimeHud() {
        super(79, 20, true);
    }

    private int calculateWidth() {
        int width;
        if (sleepDisplay.getValue() && clockDisplay.getValue()) {
            width = 79;
        } else if (sleepDisplay.getValue() || clockDisplay.getValue()) {
            width = 79 - 16;
        } else {
            width = 79 - 32;
        }
        if (!isAmPm.getValue()) {
            width -= 16;
        }
        return width;
    }

    private void updateWidth() {
        int calcWidth = calculateWidth();
        if (calcWidth != getWidth()) {
            setWidth(calcWidth);
            onBoundsUpdate();
        }
    }

    public Identifier getId() {
        return ID;
    }

    @Override
    public void renderComponent(DrawContext context, float delta) {
        updateWidth();
        DrawPosition pos = getPos();
        Text time = Text.literal(formatTime(getDayTicks()));
        drawString(
                context,
                client.textRenderer,
                time,
                pos.x() + Justification.RIGHT.getXOffset(time, getWidth() - 2),
                pos.y() + 6,
                textColor.getValue().color(),
                shadow.getValue()
        );
        if (clockDisplay.getValue()) {
            RenderUtil.drawItem(
                    context,
                    new ItemStack(Items.CLOCK),
                    pos.x() + 1 + Justification.LEFT.getXOffset(16, getWidth() - 2),
                    pos.y() + 2
            );
        }
        if (sleepDisplay.getValue()) {
            int offset = 0;
            if (clockDisplay.getValue()) {
                offset += 16;
            }
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1,
                    canPlayerSleep() ? 1 : 0.5f
            );
            context.drawTexture(
                    bedTexture,
                    pos.x() + offset + 1 + Justification.LEFT.getXOffset(16, getWidth() - 2),
                    pos.y() + 2,
                    0, 0, 16, 16, 16, 16
            );
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    @Override
    public void renderPlaceholderComponent(DrawContext context, float delta) {
        updateWidth();
        DrawPosition pos = getPos();
        Text time = Text.literal(formatTime(0));
        drawString(
                context,
                client.textRenderer,
                time,
                pos.x() + Justification.RIGHT.getXOffset(time, getWidth() - 2),
                pos.y() + 6,
                textColor.getValue().color(),
                shadow.getValue()
        );
        if (clockDisplay.getValue()) {
            RenderUtil.drawItem(
                    context,
                    new ItemStack(Items.CLOCK),
                    pos.x() + 1 + Justification.LEFT.getXOffset(16, getWidth() - 2),
                    pos.y() + 2
            );
        }
        if (sleepDisplay.getValue()) {
            int offset = 0;
            if (clockDisplay.getValue()) {
                offset += 16;
            }
            context.drawTexture(
                    bedTexture,
                    pos.x() + offset + 1 + Justification.LEFT.getXOffset(16, getWidth() - 4),
                    pos.y() + 2,
                    0, 0, 16, 16, 16, 16
            );
        }
    }

    private String formatTime(long currentTick) {
        if (formatter == null) {
            updateFormatter(isAmPm.getValue());
        }
        int hour = (int) (24 * (currentTick / 24000.0));
        hour = (hour + 6) % 24;
        int minute = (int) (60 * ((currentTick % 1000) / 1000.0));
        Date time = new Calendar.Builder()
                .setTimeOfDay(hour, minute, 0)
                .build()
                .getTime();
        return formatter.format(time);
    }

    private long getDayTicks() {
        return MinecraftClient.getInstance().world.getTimeOfDay() % 24000;
    }

    public void updateFormatter(boolean value) {
        if (value) {
            formatter = new SimpleDateFormat("hh:mm a");
        } else {
            formatter = new SimpleDateFormat("HH:mm");
        }
    }

    private boolean canPlayerSleep() {
        if (MinecraftClient.getInstance().world.getRegistryKey() != World.OVERWORLD) {
            return false;
        } else if (MinecraftClient.getInstance().world.isThundering()) {
            return true;
        } else if (getDayTicks() >= 12542) {
            return true;
        } else if (getDayTicks() >= 12010 && MinecraftClient.getInstance().world.isRaining()) {
            return true;
        }
        return false;
    }

    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(isAmPm);
        options.add(clockDisplay);
        options.add(sleepDisplay);
        return options;
    }
}
