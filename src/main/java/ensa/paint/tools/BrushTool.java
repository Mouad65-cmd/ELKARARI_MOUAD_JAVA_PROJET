package ensa.paint.tools;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import ensa.paint.model.DrawingModel;

/**
 * Outil Pinceau avec rendu lissé et épaisseur configurable.
 */
public class BrushTool extends AbstractTool {

    private int lastX;
    private int lastY;

    @Override
    public void onMouseDown(int x, int y, DrawingModel model, boolean isRightClick) {
        super.onMouseDown(x, y, model, isRightClick);
        model.saveSnapshot();
        lastX = x;
        lastY = y;

        Graphics2D g2 = model.getCanvasImage().createGraphics();
        setupGraphics(g2);
        g2.setColor(getDrawColor(model, isRightClick));
        float width = Math.max(3.0f, model.getStrokeWidth() * 1.5f);
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x, y, x, y);
        g2.dispose();
        model.fireCanvasChanged();
    }

    @Override
    public void onMouseDrag(int x, int y, DrawingModel model, boolean isRightClick) {
        if (!isDrawing) return;
        Graphics2D g2 = model.getCanvasImage().createGraphics();
        setupGraphics(g2);
        g2.setColor(getDrawColor(model, isRightClick));
        float width = Math.max(3.0f, model.getStrokeWidth() * 1.5f);
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(lastX, lastY, x, y);
        g2.dispose();

        lastX = x;
        lastY = y;
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
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }
}
