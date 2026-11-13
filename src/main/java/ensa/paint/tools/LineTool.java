package ensa.paint.tools;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import ensa.paint.model.DrawingModel;

/**
 * Outil pour tracer des lignes droites.
 */
public class LineTool extends ShapeTool {

    @Override
    protected void renderShape(Graphics2D g2, DrawingModel model, int x1, int y1, int x2, int y2) {
        g2.setColor(getDrawColor(model, isRightClick));
        g2.setStroke(model.getStrokeStyle().createStroke(model.getStrokeWidth()));
        g2.draw(new Line2D.Float(x1, y1, x2, y2));
    }
}
