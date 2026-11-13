package ensa.paint.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import ensa.paint.model.DrawingModel;

/**
 * Règle graduée en pixels (horizontale ou verticale) entourant la zone de dessin.
 */
public class RulerPanel extends JPanel {

    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    public static final int RULER_SIZE = 24;

    private final int orientation;
    private final DrawingModel model;
    private int currentMousePos = -1;

    public RulerPanel(int orientation, DrawingModel model) {
        this.orientation = orientation;
        this.model = model;
        setBackground(new Color(245, 245, 247));
        setFont(new Font("SansSerif", Font.PLAIN, 9));

        if (orientation == ORIENTATION_HORIZONTAL) {
            setPreferredSize(new Dimension(model.getCanvasWidth(), RULER_SIZE));
        } else {
            setPreferredSize(new Dimension(RULER_SIZE, model.getCanvasHeight()));
        }
    }

    public void setMousePosition(int pos) {
        if (this.currentMousePos != pos) {
            this.currentMousePos = pos;
            repaint();
        }
    }

    public void updateDimensions() {
        double zoom = model.getZoomFactor();
        if (orientation == ORIENTATION_HORIZONTAL) {
            int w = (int) (model.getCanvasWidth() * zoom);
            setPreferredSize(new Dimension(w, RULER_SIZE));
        } else {
            int h = (int) (model.getCanvasHeight() * zoom);
            setPreferredSize(new Dimension(RULER_SIZE, h));
        }
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!model.isRulersVisible()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(240, 240, 242));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(200, 200, 205));
        if (orientation == ORIENTATION_HORIZONTAL) {
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        } else {
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        }

        double zoom = model.getZoomFactor();
        int step = 50;
        if (zoom < 0.5) step = 100;
        if (zoom >= 2.0) step = 25;

        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(100, 100, 105));

        int maxPixel = (orientation == ORIENTATION_HORIZONTAL) ? model.getCanvasWidth() : model.getCanvasHeight();

        for (int p = 0; p <= maxPixel; p += 10) {
            int screenCoord = (int) (p * zoom);
            boolean isMajor = (p % step == 0);

            if (orientation == ORIENTATION_HORIZONTAL) {
                int tickHeight = isMajor ? 10 : 5;
                g2.drawLine(screenCoord, getHeight() - tickHeight, screenCoord, getHeight() - 1);
                if (isMajor && p > 0) {
                    String label = String.valueOf(p);
                    g2.drawString(label, screenCoord + 2, 12);
                }
            } else {
                int tickWidth = isMajor ? 10 : 5;
                g2.drawLine(getWidth() - tickWidth, screenCoord, getWidth() - 1, screenCoord);
                if (isMajor && p > 0) {
                    String label = String.valueOf(p);
                    g2.drawString(label, 2, screenCoord - 2);
                }
            }
        }

        // Indicateur de la position courante de la souris sur la règle
        if (currentMousePos >= 0) {
            int screenCoord = (int) (currentMousePos * zoom);
            g2.setColor(new Color(0, 120, 215, 200));
            if (orientation == ORIENTATION_HORIZONTAL) {
                g2.drawLine(screenCoord, 0, screenCoord, getHeight() - 1);
            } else {
                g2.drawLine(0, screenCoord, getWidth() - 1, screenCoord);
            }
        }
    }
}
