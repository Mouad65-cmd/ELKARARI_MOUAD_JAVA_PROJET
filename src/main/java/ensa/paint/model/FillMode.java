package ensa.paint.model;

/**
 * Modes de remplissage pour les formes géométriques.
 */
public enum FillMode {
    STROKE_ONLY("toolbar.fill.stroke_only"),
    FILL_ONLY("toolbar.fill.fill_only"),
    BOTH("toolbar.fill.both");

    private final String i18nKey;

    FillMode(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String getI18nKey() {
        return i18nKey;
    }
}
