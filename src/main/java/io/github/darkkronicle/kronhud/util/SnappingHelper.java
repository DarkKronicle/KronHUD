package io.github.darkkronicle.kronhud.util;

import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleColor;
import io.github.darkkronicle.polish.util.SimpleRectangle;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class SnappingHelper {
    private final int distance = 4;
    private SimpleColor lineColor = Colors.SELECTOR_BLUE.color();
    @Setter
    private SimpleRectangle current;

    private final HashSet<Integer> x = new HashSet<>();
    private final HashSet<Integer> y = new HashSet<>();
    private MinecraftClient client;

    public SnappingHelper(List<SimpleRectangle> rects, SimpleRectangle current) {
        addAllRects(rects);
        this.current = current;
        this.client = MinecraftClient.getInstance();
    }

    public void addAllRects(List<SimpleRectangle> rects) {
        for (SimpleRectangle rect : rects) {
            addRect(rect);
        }
    }

    public void addRect(SimpleRectangle rect) {
        x.add(rect.x());
        x.add(rect.x() + rect.width());
        y.add(rect.y());
        y.add(rect.y() + rect.height());
    }

    public void renderSnaps(MatrixStack matrices) {
        Integer curx, cury;
        if ((curx = getRawXSnap()) != null) {
            DrawUtil.rect(matrices, curx, 0, 1, client.getWindow().getScaledHeight(), lineColor.color());
        }
        if ((cury = getRawYSnap()) != null) {
            DrawUtil.rect(matrices, 0, cury, client.getWindow().getScaledWidth(), 1, lineColor.color());
        }
       // renderAll(matrices);

    }

    public void renderAll(MatrixStack matrices) {
        for (Integer xval : x) {
            DrawUtil.rect(matrices, xval, 0, 1, client.getWindow().getScaledHeight(), Colors.WHITE.color().color());
        }
        for (Integer yval : y) {
            DrawUtil.rect(matrices, 0, yval, client.getWindow().getScaledWidth(), 1, Colors.WHITE.color().color());
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

    public static Optional<Integer> getNearby(int pos, HashSet<Integer> set, int distance) {
        for (Integer integer : set) {
            if (integer - distance <= pos && integer + distance >= pos) {
                return Optional.of(integer);
            }
        }
        return Optional.empty();
    }

}
