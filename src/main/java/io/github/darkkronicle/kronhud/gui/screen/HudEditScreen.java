
package io.github.darkkronicle.kronhud.gui.screen;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.Optional;

public class HudEditScreen extends GuiBase {
    private boolean mouseDown = false;
    private AbstractHudEntry current = null;
    private DrawPosition offset = null;
    private SnappingHelper snap;
    private boolean snapEnabled;
    private MinecraftClient client;
    private Screen parent;

    public HudEditScreen(Screen parent) {
        this.parent = parent;
        this.title = StringUtils.translate("screen.kronhud.set");
        client = MinecraftClient.getInstance();
        Window window = client.getWindow();
        snapEnabled = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new ButtonGeneric(width / 2 - 50, height - 50 - 22, 100, 20,
                getSnappingButtonText()), (button, mouseButton) -> {
            snapEnabled = !snapEnabled;
            updateSnapState();
            button.setDisplayString(getSnappingButtonText());
        });
        addButton(new ButtonGeneric(width / 2 - 50, height - 50 , 100, 20,
                StringUtils.translate("button.kronhud.configuration")), (button, mouseButton) -> {
            client.setScreen(new ConfigScreen(this));
        });
    }

    private String getSnappingButtonText() {
        return StringUtils.translate(snapEnabled ? "button.kronhud.snapping.on" : "button.kronhud.snapping.off");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        Optional<AbstractHudEntry> entry = KronHUD.hudManager.getEntryXY(mouseX, mouseY);
        entry.ifPresent(abstractHudEntry -> abstractHudEntry.setHovered(true));
        KronHUD.hudManager.renderPlaceholder(matrices);
        if (mouseDown && snap != null) {
            snap.renderSnaps(matrices);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        Optional<AbstractHudEntry> entry = KronHUD.hudManager.getEntryXY((int) Math.round(mouseX), (int) Math.round(mouseY));
        if (button == 0) {
            mouseDown = true;
            if (entry.isPresent()) {
                current = entry.get();
                offset = new DrawPosition((int) Math.round(mouseX - current.getX()), (int) Math.round(mouseY - current.getY()));
                updateSnapState();
                return true;
            } else {
                current = null;
            }
        } else if (button == 1) {
            entry.ifPresent(abstractHudEntry -> client.setScreen(new HudConfigScreen(entry.get(), this)));
        }
        return false;
    }

    private void updateSnapState() {
        if (snapEnabled && current != null) {
            List<Rectangle> bounds = KronHUD.hudManager.getAllBounds();
            bounds.remove(current.getScaledBounds());
            snap = new SnappingHelper(bounds, current.getScaledBounds());
        }
        else if(snap != null) {
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
    protected void closeGui(boolean showParent) {
        KronHUD.storageHandler.saveDefaultHandling();
        if(showParent) {
            client.setScreen(parent);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (current != null) {
            current.setXY((int) mouseX - offset.x(), (int) mouseY - offset.y());
            if (snap != null) {
                Integer snapX, snapY;
                snap.setCurrent(current.getScaledBounds());
                if ((snapX = snap.getCurrentXSnap()) != null) {
                    current.setX(snapX);
                }
                if ((snapY = snap.getCurrentYSnap()) != null) {
                    current.setY(snapY);
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