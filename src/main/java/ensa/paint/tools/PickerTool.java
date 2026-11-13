package ensa.paint.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import ensa.paint.model.DrawingModel;

/**
 * Outil Pipette (Color Picker) : capture la couleur d'un pixel du canvas.
 */
public class PickerTool extends AbstractTool {

    @Override
    public void onMouseDown(int x, int y, DrawingModel model, boolean isRightClick) {
        BufferedImage img = model.getCanvasImage();
        if (img == null) return;
        if (x < 0 || x >= img.getWidth() || y < 0 || y >= img.getHeight()) return;

        int rgb = img.getRGB(x, y);
        Color picked = new Color(rgb, true);

        if (isRightClick) {
            model.setSecondaryColor(picked);
        } else {
            model.setPrimaryColor(picked);
        }
    }

    @Override
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }
}
