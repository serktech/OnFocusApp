package com.serktech.onfocus.ui.widget;

/**
 * Transparency
 */

public interface TranslucentListener {
    /**
     * Transparency monitoring
     *
     * @param alpha 0>1transparency
     */
    void onTranlucent(float alpha, int color, int textColor);
}
