package ensa.paint.tools;

import java.awt.Graphics2D;
import java.awt.Polygon;
import ensa.paint.model.DrawingModel;

/**
 * Outil pour tracer des triangles.
 */
public class TriangleTool extends ShapeTool {

    @Override
    protected void renderShape(Graphics2D g2, DrawingModel model, int x1, int y1, int x2, int y2) {
        int x = getMinX(x1, x2);
        int y = getMinY(y1, y2);
        int w = getWidth(x1, x2);
        int h = getHeight(y1, y2);

        int[] xPoints = { x + w / 2, x + w, x };
        int[] yPoints = { y, y + h, y + h };

        applyShape(g2, model, new Polygon(xPoints, yPoints, 3));
    }
}
