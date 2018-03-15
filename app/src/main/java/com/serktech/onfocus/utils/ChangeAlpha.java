package com.serktech.onfocus.utils;

import android.graphics.Color;

/**
 * transparency
 */

public class ChangeAlpha {
    /** Change color transparency based on percentage */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }
}
