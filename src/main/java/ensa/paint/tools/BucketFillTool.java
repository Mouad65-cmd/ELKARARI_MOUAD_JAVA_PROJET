package ensa.paint.tools;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import ensa.paint.model.DrawingModel;

/**
 * Outil Pot de peinture (Remplissage de zone) utilisant un algorithme Flood-Fill BFS itératif rapide.
 */
public class BucketFillTool extends AbstractTool {

    @Override
    public void onMouseDown(int startX, int startY, DrawingModel model, boolean isRightClick) {
        BufferedImage img = model.getCanvasImage();
        if (img == null) return;
        if (startX < 0 || startX >= img.getWidth() || startY < 0 || startY >= img.getHeight()) return;

        int targetColor = img.getRGB(startX, startY);
        int fillColor = getDrawColor(model, isRightClick).getRGB();

        // Si la couleur cible est déjà la couleur de remplissage, rien à faire
        if ((targetColor & 0x00FFFFFF) == (fillColor & 0x00FFFFFF)) {
            return;
        }

        model.saveSnapshot();
        floodFill(img, startX, startY, targetColor, fillColor);
        model.setModified(true);
        model.fireCanvasChanged();
    }

    private void floodFill(BufferedImage img, int x0, int y0, int targetRgb, int fillRgb) {
        int width = img.getWidth();
        int height = img.getHeight();

        int[] queue = new int[width * height];
        int head = 0;
        int tail = 0;

        boolean[] visited = new boolean[width * height];

        int startPos = y0 * width + x0;
        queue[tail++] = startPos;
        visited[startPos] = true;

        int targetNoAlpha = targetRgb & 0x00FFFFFF;

        while (head < tail) {
            int pos = queue[head++];
            int cx = pos % width;
            int cy = pos / width;

            img.setRGB(cx, cy, fillRgb);

            // Gauche
            if (cx > 0) {
                int leftPos = cy * width + (cx - 1);
                if (!visited[leftPos] && ((img.getRGB(cx - 1, cy) & 0x00FFFFFF) == targetNoAlpha)) {
                    visited[leftPos] = true;
                    queue[tail++] = leftPos;
                }
            }
            // Droite
            if (cx < width - 1) {
                int rightPos = cy * width + (cx + 1);
                if (!visited[rightPos] && ((img.getRGB(cx + 1, cy) & 0x00FFFFFF) == targetNoAlpha)) {
                    visited[rightPos] = true;
                    queue[tail++] = rightPos;
                }
            }
            // Haut
            if (cy > 0) {
                int upPos = (cy - 1) * width + cx;
                if (!visited[upPos] && ((img.getRGB(cx, cy - 1) & 0x00FFFFFF) == targetNoAlpha)) {
                    visited[upPos] = true;
                    queue[tail++] = upPos;
                }
            }
            // Bas
            if (cy < height - 1) {
                int downPos = (cy + 1) * width + cx;
                if (!visited[downPos] && ((img.getRGB(cx, cy + 1) & 0x00FFFFFF) == targetNoAlpha)) {
                    visited[downPos] = true;
                    queue[tail++] = downPos;
                }
            }
        }
    }

    @Override
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }
}
