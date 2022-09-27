
package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.hud.HudManager;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.Rectangle;
import io.github.darkkronicle.kronhud.util.SnappingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HudEditScreen extends Screen {
    private boolean mouseDown = false;
    private AbstractHudEntry current = null;
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
        addDrawableChild(new ButtonWidget(width / 2 - 50, height - 50 - 22, 100, 20,
                getSnappingButtonText(), (button) -> {
            snapEnabled = !snapEnabled;
            updateSnapState();
            button.setMessage(getSnappingButtonText());
        }));
        addDrawableChild(new ButtonWidget(width / 2 - 50, height - 50 , 100, 20,
                Text.translatable("button.kronhud.configuration"),
                (button) -> client.setScreen(new ConfigScreen(this, getTabs()))));
    }

    private Text getSnappingButtonText() {
        return Text.translatable(snapEnabled ? "button.kronhud.snapping.on" : "button.kronhud.snapping.off");
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
        KronHUD.storageHandler.saveDefaultHandling();
        if (parent != null) {
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

    private List<Tab> getTabs(){
        List<Tab> tabs = new ArrayList<>();

        KronHUD.hudManager.getEntries().forEach(abstractHudEntry -> {
            tabs.add(Tab.ofOptions(abstractHudEntry.getId(), abstractHudEntry.getName(), abstractHudEntry.getOptions()));
        });

        return tabs;
    }
}
