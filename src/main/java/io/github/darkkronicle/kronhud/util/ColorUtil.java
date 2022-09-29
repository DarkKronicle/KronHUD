package io.github.darkkronicle.kronhud.util;

import io.github.darkkronicle.darkkore.util.Color;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ColorUtil {

    public static Color parse(String color) {
        try {
            return new Color(Integer.parseInt(color));
        } catch (NumberFormatException ignored) {
        }

        if (color.startsWith("#")) {
            color = color.substring(1);
        } else if(color.startsWith("0x")) {
            color = color.substring(2);
        } if(color.length() == 6) {
            color = "FF" + color;
        } else if (color.length() != 8) {
            return ERROR;
        }
        try {
            return new Color(Integer.valueOf(color.substring(2, 4), 16),
                    Integer.valueOf(color.substring(4, 6), 16),
                    Integer.valueOf(color.substring(6, 8), 16),
                    Integer.valueOf(color.substring(0, 2), 16));
        } catch (NumberFormatException error) {
            return ERROR;
        }
    }


    public static Color WHITE = new Color(255, 255, 255, 255);
    public static Color BLACK = new Color(0, 0, 0, 255);
    public static Color GRAY = new Color(128, 128, 128, 255);
    public static Color DARK_GRAY = new Color(49, 51, 53, 255);
    public static Color SELECTOR_RED = new Color(191, 34, 34, 255);
    public static Color SELECTOR_GREEN = new Color(53, 219, 103, 255);
    public static Color SELECTOR_BLUE = new Color(51, 153, 255, 255);
    public static Color ERROR = new Color(255, 0, 255, 255);

    /**
     * Blends two {@link Color}s based off of a percentage.
     *
     * @param original   color to start the blend with
     * @param blend      color that when fully blended, will be this
     * @param percentage the percentage to blend
     * @return the simple color
     */
    public Color blend(Color original, Color blend, float percentage) {
        if (percentage >= 1) {
            return blend;
        }
        if (percentage <= 0) {
            return original;
        }
        int red = blendInt(original.red(), blend.red(), percentage);
        int green = blendInt(original.green(), blend.green(), percentage);
        int blue = blendInt(original.blue(), blend.blue(), percentage);
        int alpha = blendInt(original.alpha(), blend.alpha(), percentage);
        return new Color(red, green, blue, alpha);
    }

    /**
     * Blends two ints together based off of a percent.
     *
     * @param start   starting int
     * @param end     end int
     * @param percent percent to blend
     * @return the blended int
     */
    public int blendInt(int start, int end, float percent) {
        if (percent <= 0) {
            return start;
        }
        if (start == end || percent >= 1) {
            return end;
        }
        int dif = end - start;
        int add = Math.round((float) dif * percent);
        return start + (add);
    }

}
