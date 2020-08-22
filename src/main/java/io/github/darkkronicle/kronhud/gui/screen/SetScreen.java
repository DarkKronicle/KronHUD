package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.SnappingHelper;
import io.github.darkkronicle.polish.gui.widgets.CleanButton;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleRectangle;
import io.github.darkkronicle.polish.util.WidgetManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.List;
import java.util.Optional;

public class SetScreen extends Screen {
    private boolean mouseDown = false;
    private AbstractHudEntry current = null;
    private DrawPosition offset = null;
    private SnappingHelper snap;
    private WidgetManager manager;
    private MinecraftClient client;

    public SetScreen() {
        super(new LiteralText("Set up Screen"));
        client = MinecraftClient.getInstance();
        Window window = client.getWindow();
        manager = new WidgetManager(this, children);
        manager.add(new CleanButton((window.getScaledWidth() / 2 ) - 50, window.getScaledHeight() - 50, 100, 15, Colors.BLACK.color().withAlpha(150), new LiteralText("Configuration"), cleanButton -> client.openScreen(ConfigScreen.getScreen())));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        DrawUtil.rect(matrices, 0, 0, width, height, Colors.BLACK.color().withAlpha(150).color());
        Optional<AbstractHudEntry> entry = KronHUD.hudManager.getEntryXY(mouseX, mouseY);
        entry.ifPresent(abstractHudEntry -> abstractHudEntry.setHovered(true));
        KronHUD.hudManager.renderPlaceholder(matrices);
        if (mouseDown && snap != null) {
            snap.renderSnaps(matrices);
        }
        manager.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Optional<AbstractHudEntry> entry = KronHUD.hudManager.getEntryXY((int) Math.round(mouseX), (int) Math.round(mouseY));
        mouseDown = true;
        if (entry.isPresent()) {
            current = entry.get();
            offset = new DrawPosition((int)Math.round(mouseX - current.getX()), (int)Math.round(mouseY - current.getY()));
            List<SimpleRectangle> bounds = KronHUD.hudManager.getAllBounds();
            bounds.remove(current.getBounds());
            snap = new SnappingHelper(bounds, current.getBounds());
            return true;
        } else {
            current = null;
        }
        manager.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        current = null;
        snap = null;
        mouseDown = false;
        return false;
    }

    @Override
    public void onClose() {
        KronHUD.storageHandler.saveDefaultHandling();
        super.onClose();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (current != null) {
            current.setXY((int) mouseX - offset.getX(), (int) mouseY - offset.getY());
            if (snap != null) {
                Integer snapX, snapY;
                snap.setCurrent(current.getBounds());
                if ((snapX = snap.getCurrentXSnap()) != null) {
                    current.setX(snapX);
                }
                if ((snapY = snap.getCurrentYSnap()) != null) {
                    current.setY(snapY);
                }
            }
            return true;
        }
        manager.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return false;
    }

}
