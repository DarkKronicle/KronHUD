package io.github.darkkronicle.kronhud.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.experimental.Accessors;

/*
 * Stores a basic rectangle.
 */
@Value
@AllArgsConstructor
@Accessors(fluent = true)
public class Rectangle {

    int x;
    int y;
    int width;
    int height;

    public Rectangle offset(DrawPosition offset) {
        return new Rectangle(x + offset.x, y + offset.y, width, height);
    }
    public Rectangle offset(int x, int y) {
        return new Rectangle(this.x + x, this.y + y, width, height);
    }

}
