package ensa.paint.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import ensa.paint.model.DrawingModel;
import ensa.paint.model.FillMode;

/**
 * Classe de base Template Method pour toutes les formes géométriques.
 * Gère la prévisualisation en temps réel et le tracé final sur le canvas.
 */
public abstract class ShapeTool extends AbstractTool {

    @Override
    public void onMouseDown(int x, int y, DrawingModel model, boolean isRightClick) {
        super.onMouseDown(x, y, model, isRightClick);
        // Ne modifie pas le canvas tout de suite, seulement la prévisualisation
    }

    @Override
    public void onMouseDrag(int x, int y, DrawingModel model, boolean isRightClick) {
        super.onMouseDrag(x, y, model, isRightClick);
        // Le panneau de dessin redessinera la prévisualisation
    }

    @Override
    public void onMouseUp(int x, int y, DrawingModel model, boolean isRightClick) {
        if (!isDrawing) return;
        super.onMouseUp(x, y, model, isRightClick);

        // Si le clic n'a pas bougé, ignorer ou tracer un point
        if (Math.abs(startX - currentX) < 1 && Math.abs(startY - currentY) < 1) {
            return;
        }

        model.saveSnapshot();
        Graphics2D g2 = model.getCanvasImage().createGraphics();
        setupGraphics(g2);
        renderShape(g2, model, startX, startY, currentX, currentY);
        g2.dispose();

        model.setModified(true);
        model.fireCanvasChanged();
    }

    @Override
    public void drawPreview(Graphics2D g2, DrawingModel model) {
        if (!isDrawing) return;
        setupGraphics(g2);
        renderShape(g2, model, startX, startY, currentX, currentY);
    }

    /**
     * Effectue le tracé et/ou remplissage de la forme selon le FillMode sélectionné.
     */
    protected void applyShape(Graphics2D g2, DrawingModel model, Shape shape) {
        FillMode mode = model.getFillMode();
        Color strokeColor = getDrawColor(model, isRightClick);
        Color fillColor = getFillColor(model, isRightClick);

        if (mode == FillMode.FILL_ONLY || mode == FillMode.BOTH) {
            g2.setColor(fillColor);
            g2.fill(shape);
        }

        if (mode == FillMode.STROKE_ONLY || mode == FillMode.BOTH) {
            g2.setColor(strokeColor);
            g2.setStroke(model.getStrokeStyle().createStroke(model.getStrokeWidth()));
            g2.draw(shape);
        }
    }

    /**
     * Méthode abstraite que chaque forme concrète doit implémenter.
     */
    protected abstract void renderShape(Graphics2D g2, DrawingModel model, int x1, int y1, int x2, int y2);

    protected static int getMinX(int x1, int x2) { return Math.min(x1, x2); }
    protected static int getMinY(int y1, int y2) { return Math.min(y1, y2); }
    protected static int getWidth(int x1, int x2) { return Math.abs(x1 - x2); }
    protected static int getHeight(int y1, int y2) { return Math.abs(y1 - y2); }
}
