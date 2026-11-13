package ensa.paint.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import ensa.paint.model.DrawingModel;

/**
 * Outil Gomme : efface en peignant avec la couleur secondaire (ou blanche).
 */
public class EraserTool extends AbstractTool {

    private int lastX;
    private int lastY;

    @Override
    public void onMouseDown(int x, int y, DrawingModel model, boolean isRightClick) {
        super.onMouseDown(x, y, model, isRightClick);
        model.saveSnapshot();
        lastX = x;
        lastY = y;

        erase(x, y, x, y, model);
    }

    @Override
    public void onMouseDrag(int x, int y, DrawingModel model, boolean isRightClick) {
        if (!isDrawing) return;
        erase(lastX, lastY, x, y, model);
        lastX = x;
        lastY = y;
    }

    private void erase(int x1, int y1, int x2, int y2, DrawingModel model) {
        Graphics2D g2 = model.getCanvasImage().createGraphics();
        setupGraphics(g2);
        // La gomme utilise la couleur secondaire (arrière-plan)
        Color eraseColor = model.getSecondaryColor() != null ? model.getSecondaryColor() : Color.WHITE;
        g2.setColor(eraseColor);
        float size = Math.max(8.0f, model.getStrokeWidth() * 3.0f);
        g2.setStroke(new BasicStroke(size, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g2.drawLine(x1, y1, x2, y2);
        g2.dispose();
        model.fireCanvasChanged();
    }

    @Override
    public void onMouseUp(int x, int y, DrawingModel model, boolean isRightClick) {
        super.onMouseUp(x, y, model, isRightClick);
        model.setModified(true);
        model.fireCanvasChanged();
    }

    @Override
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    }
}
