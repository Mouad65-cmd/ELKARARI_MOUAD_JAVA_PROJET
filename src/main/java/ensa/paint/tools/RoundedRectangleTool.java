package ensa.paint.tools;

import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import ensa.paint.model.DrawingModel;

/**
 * Outil pour tracer des rectangles à coins arrondis.
 */
public class RoundedRectangleTool extends ShapeTool {

    @Override
    protected void renderShape(Graphics2D g2, DrawingModel model, int x1, int y1, int x2, int y2) {
        int x = getMinX(x1, x2);
        int y = getMinY(y1, y2);
        int w = getWidth(x1, x2);
        int h = getHeight(y1, y2);

        float arc = Math.min(w, h) * 0.3f;
        applyShape(g2, model, new RoundRectangle2D.Float(x, y, w, h, arc, arc));
    }
}
