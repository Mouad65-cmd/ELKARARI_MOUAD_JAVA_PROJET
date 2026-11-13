package ensa.paint.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import ensa.paint.model.DrawingModel;
import ensa.paint.model.FillMode;

/**
 * Classe abstraite de base implémentant les fonctionnalités partagées par les outils.
 */
public abstract class AbstractTool implements Tool {

    protected int startX;
    protected int startY;
    protected int currentX;
    protected int currentY;
    protected boolean isDrawing = false;
    protected boolean isRightClick = false;

    @Override
    public void onMouseDown(int x, int y, DrawingModel model, boolean isRightClick) {
        this.startX = x;
        this.startY = y;
        this.currentX = x;
        this.currentY = y;
        this.isRightClick = isRightClick;
        this.isDrawing = true;
    }

    @Override
    public void onMouseDrag(int x, int y, DrawingModel model, boolean isRightClick) {
        this.currentX = x;
        this.currentY = y;
    }

    @Override
    public void onMouseUp(int x, int y, DrawingModel model, boolean isRightClick) {
        this.currentX = x;
        this.currentY = y;
        this.isDrawing = false;
    }

    @Override
    public void drawPreview(Graphics2D g2, DrawingModel model) {
        // Optionnel : surchargé par les outils affichant une prévisualisation
    }

    @Override
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }

    protected Color getDrawColor(DrawingModel model, boolean rightClick) {
        return rightClick ? model.getSecondaryColor() : model.getPrimaryColor();
    }

    protected Color getFillColor(DrawingModel model, boolean rightClick) {
        return rightClick ? model.getPrimaryColor() : model.getSecondaryColor();
    }

    protected void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
