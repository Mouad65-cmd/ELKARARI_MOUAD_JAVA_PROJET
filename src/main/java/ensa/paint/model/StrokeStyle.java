package ensa.paint.model;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * Styles de tracé pour le dessin (Continu, Tirets, Pointillés).
 */
public enum StrokeStyle {
    SOLID("toolbar.style.solid"),
    DASHED("toolbar.style.dashed"),
    DOTTED("toolbar.style.dotted");

    private final String i18nKey;

    StrokeStyle(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String getI18nKey() {
        return i18nKey;
    }

    public Stroke createStroke(float width) {
        switch (this) {
            case DASHED:
                return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        10.0f, new float[]{width * 3.0f, width * 2.0f}, 0.0f);
            case DOTTED:
                return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        10.0f, new float[]{1.0f, width * 2.0f}, 0.0f);
            case SOLID:
            default:
                return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        }
    }
}
