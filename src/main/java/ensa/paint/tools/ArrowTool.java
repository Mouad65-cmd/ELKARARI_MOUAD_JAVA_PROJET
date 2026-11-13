package ensa.paint.tools;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import ensa.paint.model.DrawingModel;

/**
 * Outil pour tracer des flèches.
 */
public class ArrowTool extends ShapeTool {

    @Override
    protected void renderShape(Graphics2D g2, DrawingModel model, int x1, int y1, int x2, int y2) {
        int x = getMinX(x1, x2);
        int y = getMinY(y1, y2);
        int w = getWidth(x1, x2);
        int h = getHeight(y1, y2);

        if (w < 4 || h < 4) return;

        Path2D.Double path = new Path2D.Double();
        // Flèche pointant de gauche à droite
        float shaftTop = y + h * 0.3f;
        float shaftBottom = y + h * 0.7f;
        float headStart = x + w * 0.6f;

        path.moveTo(x, shaftTop);
        path.lineTo(headStart, shaftTop);
        path.lineTo(headStart, y);
        path.lineTo(x + w, y + h * 0.5f);
        path.lineTo(headStart, y + h);
        path.lineTo(headStart, shaftBottom);
        path.lineTo(x, shaftBottom);
        path.closePath();

        applyShape(g2, model, path);
    }
}
