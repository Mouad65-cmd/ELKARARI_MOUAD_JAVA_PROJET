package ensa.paint.model;

/**
 * Énumération des outils de dessin disponibles.
 */
public enum ToolType {
    // Outils libres et utilitaires
    PENCIL("tools.pencil"),
    BRUSH("tools.brush"),
    ERASER("tools.eraser"),
    BUCKET("tools.bucket"),
    PICKER("tools.picker"),
    TEXT("tools.text"),
    SELECTION("tools.selection"),

    // Formes géométriques
    LINE("shapes.line"),
    RECTANGLE("shapes.rectangle"),
    ROUNDED_RECTANGLE("shapes.rounded_rectangle"),
    ELLIPSE("shapes.ellipse"),
    TRIANGLE("shapes.triangle"),
    STAR("shapes.star"),
    ARROW("shapes.arrow");

    private final String i18nKey;

    ToolType(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String getI18nKey() {
        return i18nKey;
    }

    public boolean isShape() {
        return this == LINE || this == RECTANGLE || this == ROUNDED_RECTANGLE ||
               this == ELLIPSE || this == TRIANGLE || this == STAR || this == ARROW;
    }
}
