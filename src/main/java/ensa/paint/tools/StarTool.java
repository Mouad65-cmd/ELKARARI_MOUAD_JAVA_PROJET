package ensa.paint.tools;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import ensa.paint.model.DrawingModel;

/**
 * Outil pour tracer des étoiles à 5 branches.
 */
public class StarTool extends ShapeTool {

    @Override
    protected void renderShape(Graphics2D g2, DrawingModel model, int x1, int y1, int x2, int y2) {
        int x = getMinX(x1, x2);
        int y = getMinY(y1, y2);
        int w = getWidth(x1, x2);
        int h = getHeight(y1, y2);

        if (w < 2 || h < 2) return;

        double centerX = x + w / 2.0;
        double centerY = y + h / 2.0;
        double rxOuter = w / 2.0;
        double ryOuter = h / 2.0;
        double rxInner = rxOuter * 0.45;
        double ryInner = ryOuter * 0.45;

        Path2D.Double path = new Path2D.Double();
        int points = 10;
        double angleStep = Math.PI / 5.0; // 36°
        double startAngle = -Math.PI / 2.0; // Pointe vers le haut

        for (int i = 0; i < points; i++) {
            double angle = startAngle + i * angleStep;
            double rx = (i % 2 == 0) ? rxOuter : rxInner;
            double ry = (i % 2 == 0) ? ryOuter : ryInner;
            double px = centerX + rx * Math.cos(angle);
            double py = centerY + ry * Math.sin(angle);

            if (i == 0) {
                path.moveTo(px, py);
            } else {
                path.lineTo(px, py);
            }
        }
        path.closePath();

        applyShape(g2, model, path);
    }
}
