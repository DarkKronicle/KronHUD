
package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.settings.DarkKoreConfig;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.gui.HudManager;
import io.github.darkkronicle.kronhud.gui.component.HudEntry;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.Rectangle;
import io.github.darkkronicle.kronhud.util.SnappingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class HudEditScreen extends Screen {
    private boolean mouseDown = false;
    private HudEntry current = null;
    private DrawPosition offset = null;
    private SnappingHelper snap;
    private boolean snapEnabled;
    private final MinecraftClient client;
    private final Screen parent;

    public HudEditScreen(Screen parent) {
        super(Text.translatable("screen.kronhud.set"));
        this.parent = parent;
        client = MinecraftClient.getInstance();
        snapEnabled = true;
    }

    @Override
    public void init() {
        super.init();
        addDrawableChild(new ButtonWidget.Builder(getSnappingButtonText(),
                (button) -> {
            snapEnabled = !snapEnabled;
            updateSnapState();
            button.setMessage(getSnappingButtonText());
        }).dimensions(width / 2 - 50, height - 50 - 22, 100, 20).build());
        addDrawableChild(
                new ButtonWidget.Builder(Text.translatable("button.kronhud.configuration"),
                (button) -> {
                    HudConfigScreen screen = new HudConfigScreen();
                    screen.setParent(this);
                    client.setScreen(screen);
                }).dimensions(width / 2 - 50, height - 50 , 100, 20).build());
    }

    private Text getSnappingButtonText() {
        return Text.translatable(snapEnabled ? "button.kronhud.snapping.on" : "button.kronhud.snapping.off");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtil.fill(context, 0, 0, width, height, DarkKoreConfig.getInstance().screenBackgroundColor.getValue());
        super.render(context, mouseX, mouseY, delta);
        Optional<HudEntry> entry = HudManager.getInstance().getEntryXY(mouseX, mouseY);
        entry.ifPresent(abstractHudEntry -> abstractHudEntry.setHovered(true));
        HudManager.getInstance().renderPlaceholder(context, delta);
        if (mouseDown && snap != null) {
            snap.renderSnaps(context);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        Optional<HudEntry> entry = HudManager.getInstance().getEntryXY((int) Math.round(mouseX), (int) Math.round(mouseY));
        if (button == 0) {
            mouseDown = true;
            if (entry.isPresent()) {
                current = entry.get();
                offset = new DrawPosition((int) Math.round(mouseX - current.getTruePos().x()), (int) Math.round(mouseY - current.getTruePos().y()));
                updateSnapState();
                return true;
            } else {
                current = null;
            }
        } else if (button == 1) {
            entry.ifPresent(abstractHudEntry -> client.setScreen(new EntryConfigScreen(entry.get())));
        }
        return false;
    }

    private void updateSnapState() {
        if (snapEnabled && current != null) {
            List<Rectangle> bounds = HudManager.getInstance().getAllBounds();
            bounds.remove(current.getTrueBounds());
            snap = new SnappingHelper(bounds, current.getTrueBounds());
        } else if (snap != null) {
            snap = null;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        current = null;
        snap = null;
        mouseDown = false;
        return false;
    }

    @Override
    public void removed() {
        ConfigHandler.getInstance().save();
        if (parent != null) {
            client.setScreen(parent);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (current != null) {
            current.setX((int) mouseX - offset.x() + current.offsetTrueWidth());
            current.setY((int) mouseY - offset.y() + current.offsetTrueHeight());
            if (snap != null) {
                Integer snapX, snapY;
                snap.setCurrent(current.getTrueBounds());
                if ((snapX = snap.getCurrentXSnap()) != null) {
                    current.setX(snapX + current.offsetTrueWidth());
                }
                if ((snapY = snap.getCurrentYSnap()) != null) {
                    current.setY(snapY + current.offsetTrueHeight());
                }
            }
            if (current.tickable()) {
                current.tick();
            }
            return true;
        }
        return false;
    }

}
