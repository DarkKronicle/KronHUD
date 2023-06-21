package io.github.darkkronicle.kronhud.util;

import io.github.darkkronicle.darkkore.util.Color;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class SnappingHelper {
    private final int distance = 4;
    private final HashSet<Integer> x = new HashSet<>();
    private final HashSet<Integer> y = new HashSet<>();
    private static final Color LINE_COLOR = ColorUtil.SELECTOR_BLUE;
    @Setter
    private Rectangle current;
    private final MinecraftClient client;

    public SnappingHelper(List<Rectangle> rects, Rectangle current) {
        addAllRects(rects);
        this.current = current;
        this.client = MinecraftClient.getInstance();
    }

    public static Optional<Integer> getNearby(int pos, HashSet<Integer> set, int distance) {
        for (Integer integer : set) {
            if (integer - distance <= pos && integer + distance >= pos) {
                return Optional.of(integer);
            }
        }
        return Optional.empty();
    }

    public void addAllRects(List<Rectangle> rects) {
        for (Rectangle rect : rects) {
            addRect(rect);
        }
    }

    public void addRect(Rectangle rect) {
        x.add(rect.x());
        x.add(rect.x() + rect.width());
        y.add(rect.y());
        y.add(rect.y() + rect.height());
    }

    public void renderSnaps(DrawContext context) {
        Integer curx, cury;
        if ((curx = getRawXSnap()) != null) {
            DrawUtil.fillRect(context, new Rectangle(curx, 0, 1, client.getWindow().getScaledHeight()),
                    LINE_COLOR);
        }
        if ((cury = getRawYSnap()) != null) {
            DrawUtil.fillRect(context, new Rectangle(0, cury, client.getWindow().getScaledWidth(), 1),
                    LINE_COLOR);
        }
        // renderAll(context);

    }

    public void renderAll(DrawContext context) {
        for (Integer xval : x) {
            DrawUtil.fillRect(context, new Rectangle(xval, 0, 1, client.getWindow().getScaledHeight()),
                    ColorUtil.WHITE);
        }
        for (Integer yval : y) {
            DrawUtil.fillRect(context, new Rectangle(0, yval, client.getWindow().getScaledWidth(), 1), ColorUtil.WHITE);
        }
    }

    public Integer getCurrentXSnap() {
        Integer xSnap = getNearby(current.x(), x, distance).orElse(null);
        if (xSnap != null) {
            return xSnap;
        } else if ((xSnap = getNearby(current.x() + current.width(), x, distance).orElse(null)) != null) {
            return xSnap - current.width();
        } else if ((xSnap = getHalfXSnap()) != null) {
            return xSnap - (current.width() / 2);
        }
        return null;
    }

    public Integer getRawXSnap() {
        Integer xSnap = getNearby(current.x(), x, distance).orElse(null);
        if (xSnap != null) {
            return xSnap;
        } else if ((xSnap = getNearby(current.x() + current.width(), x, distance).orElse(null)) != null) {
            return xSnap;
        } else if ((xSnap = getHalfXSnap()) != null) {
            return xSnap;
        }
        return null;
    }

    public Integer getCurrentYSnap() {
        Integer ySnap = getNearby(current.y(), y, distance).orElse(null);
        if (ySnap != null) {
            return ySnap;
        } else if ((ySnap = getNearby(current.y() + current.height(), y, distance).orElse(null)) != null) {
            return ySnap - current.height();
        } else if ((ySnap = getHalfYSnap()) != null) {
            return ySnap - (current.height() / 2);
        }
        return null;
    }

    public Integer getHalfYSnap() {
        int height = client.getWindow().getScaledHeight() / 2;
        int pos = current.y() + Math.round((float) current.height() / 2);
        if (height - distance <= pos && height + distance >= pos) {
            return height;
        }
        return null;
    }

    public Integer getHalfXSnap() {
        int width = client.getWindow().getScaledWidth() / 2;
        int pos = current.x() + Math.round((float) current.width() / 2);
        if (width - distance <= pos && width + distance >= pos) {
            return width;
        }
        return null;
    }

    public Integer getRawYSnap() {
        Integer ySnap = getNearby(current.y(), y, distance).orElse(null);
        if (ySnap != null) {
            return ySnap;
        } else if ((ySnap = getNearby(current.y() + current.height(), y, distance).orElse(null)) != null) {
            return ySnap;
        } else if ((ySnap = getHalfYSnap()) != null) {
            return ySnap;
        }
        return null;
    }

}
