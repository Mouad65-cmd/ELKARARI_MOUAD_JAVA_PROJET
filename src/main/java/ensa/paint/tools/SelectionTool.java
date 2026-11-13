package ensa.paint.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import ensa.paint.model.DrawingModel;

/**
 * Outil Sélection Rectangulaire : permet de sélectionner, déplacer,
 * copier, couper et manipuler une zone du dessin.
 */
public class SelectionTool extends AbstractTool {

    private boolean isMoving = false;
    private int dragOffsetX;
    private int dragOffsetY;

    private static final Stroke DASHED_STROKE = new BasicStroke(
            1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f, 5.0f}, 0.0f
    );

    @Override
    public void onMouseDown(int x, int y, DrawingModel model, boolean isRightClick) {
        Rectangle bounds = model.getSelectionBounds();

        // Si on clique à l'intérieur d'une sélection déjà existante, on la déplace
        if (bounds != null && bounds.contains(x, y)) {
            isMoving = true;
            dragOffsetX = x - bounds.x;
            dragOffsetY = y - bounds.y;
            if (!model.isSelectionFloating()) {
                model.liftSelection();
            }
        } else {
            // Sinon, valide la sélection précédente et commence une nouvelle
            model.commitSelection();
            isMoving = false;
            super.onMouseDown(x, y, model, isRightClick);
        }
    }

    @Override
    public void onMouseDrag(int x, int y, DrawingModel model, boolean isRightClick) {
        if (isMoving) {
            Rectangle bounds = model.getSelectionBounds();
            if (bounds != null) {
                int newX = x - dragOffsetX;
                int newY = y - dragOffsetY;
                bounds.setLocation(newX, newY);
                model.fireCanvasChanged();
            }
        } else if (isDrawing) {
            super.onMouseDrag(x, y, model, isRightClick);
            model.fireCanvasChanged();
        }
    }

    @Override
    public void onMouseUp(int x, int y, DrawingModel model, boolean isRightClick) {
        if (isMoving) {
            isMoving = false;
        } else if (isDrawing) {
            super.onMouseUp(x, y, model, isRightClick);
            int rx = Math.min(startX, currentX);
            int ry = Math.min(startY, currentY);
            int rw = Math.abs(startX - currentX);
            int rh = Math.abs(startY - currentY);

            if (rw > 3 && rh > 3) {
                model.startSelection(new Rectangle(rx, ry, rw, rh));
            } else {
                model.commitSelection();
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, DrawingModel model) {
        Rectangle bounds = model.getSelectionBounds();

        // Si une sélection existe et est déplacée
        if (bounds != null) {
            if (model.isSelectionFloating() && model.getSelectionImage() != null) {
                g2.drawImage(model.getSelectionImage(), bounds.x, bounds.y, null);
            }
            // Dessine la bordure pointillée
            g2.setColor(Color.BLUE);
            g2.setStroke(DASHED_STROKE);
            g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        } else if (isDrawing) {
            // Dessin du rectangle de sélection en cours
            int rx = Math.min(startX, currentX);
            int ry = Math.min(startY, currentY);
            int rw = Math.abs(startX - currentX);
            int rh = Math.abs(startY - currentY);

            g2.setColor(new Color(0, 102, 204, 180));
            g2.setStroke(DASHED_STROKE);
            g2.drawRect(rx, ry, rw, rh);
        }
    }

    @Override
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }
}
