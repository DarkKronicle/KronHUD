package io.github.darkkronicle.kronhud.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class DrawPosition {

    int x;
    int y;

    public DrawPosition subtract(int x, int y) {
        return new DrawPosition(this.x - x, this.y - y);
    }

    public DrawPosition subtract(DrawPosition position) {
        return new DrawPosition(position.x(), position.y());
    }

    public DrawPosition divide(float scale) {
        return new DrawPosition((int) (x / scale), (int) (y / scale));
    }

}
