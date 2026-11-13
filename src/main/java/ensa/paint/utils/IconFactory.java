package ensa.paint.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import ensa.paint.model.ToolType;

/**
 * Générateur d'icônes vectorielles dynamiques haute résolution.
 * Permet un rendu parfait et indépendant de fichiers d'images externes.
 */
public class IconFactory {

    private static final int DEFAULT_SIZE = 22;

    public static Icon getToolIcon(ToolType type) {
        return getToolIcon(type, DEFAULT_SIZE);
    }

    public static Icon getToolIcon(ToolType type, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        setupG2(g2);

        Color mainColor = new Color(45, 52, 58);
        Color accentColor = new Color(0, 120, 215);

        switch (type) {
            case PENCIL:
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(4, size - 4, size - 6, 6);
                g2.setColor(accentColor);
                g2.fillOval(size - 7, 3, 5, 5);
                g2.setColor(Color.DARK_GRAY);
                g2.drawLine(4, size - 4, 3, size - 3);
                break;

            case BRUSH:
                g2.setColor(new Color(139, 69, 19));
                g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(size - 4, 4, size / 2, size / 2);
                g2.setColor(accentColor);
                g2.fillOval(3, size / 2 - 2, size / 2, size / 2);
                break;

            case ERASER:
                g2.setColor(new Color(255, 128, 128));
                g2.fillRoundRect(4, 5, size - 8, size - 10, 4, 4);
                g2.setColor(Color.WHITE);
                g2.fillRect(4, size / 2 - 1, size - 8, 3);
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(4, 5, size - 8, size - 10, 4, 4);
                break;

            case BUCKET:
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.5f));
                // Corps du seau incliné
                Polygon p = new Polygon();
                p.addPoint(5, 7);
                p.addPoint(size - 7, 4);
                p.addPoint(size - 9, size - 6);
                p.addPoint(7, size - 6);
                g2.setColor(new Color(70, 130, 180));
                g2.fill(p);
                g2.setColor(mainColor);
                g2.draw(p);
                // Goutte de peinture
                g2.setColor(accentColor);
                g2.fillOval(size - 5, size - 6, 4, 5);
                break;

            case PICKER:
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawLine(size - 5, 5, 8, size - 8);
                g2.setColor(accentColor);
                g2.fillOval(size - 8, 3, 6, 6);
                g2.setColor(Color.BLACK);
                g2.drawLine(8, size - 8, 4, size - 4);
                break;

            case TEXT:
                g2.setColor(mainColor);
                g2.setFont(new Font("SansSerif", Font.BOLD, size - 4));
                g2.drawString("A", 4, size - 4);
                break;

            case SELECTION:
                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        5.0f, new float[]{3.0f, 3.0f}, 0.0f));
                g2.drawRect(3, 3, size - 7, size - 7);
                break;

            case LINE:
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(4, size - 5, size - 5, 4);
                break;

            case RECTANGLE:
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRect(4, 5, size - 9, size - 11);
                break;

            case ROUNDED_RECTANGLE:
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(4, 5, size - 9, size - 11, 6, 6);
                break;

            case ELLIPSE:
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(4, 4, size - 9, size - 9);
                break;

            case TRIANGLE:
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.8f));
                Polygon tri = new Polygon(
                        new int[]{size / 2, size - 4, 4},
                        new int[]{4, size - 5, size - 5},
                        3
                );
                g2.draw(tri);
                break;

            case STAR:
                g2.setColor(new Color(230, 160, 20));
                g2.fill(createStarShape(size / 2.0, size / 2.0, size / 2.0 - 3, (size / 2.0 - 3) * 0.45));
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(createStarShape(size / 2.0, size / 2.0, size / 2.0 - 3, (size / 2.0 - 3) * 0.45));
                break;

            case ARROW:
                g2.setColor(accentColor);
                Path2D.Double arrow = new Path2D.Double();
                arrow.moveTo(3, size * 0.35);
                arrow.lineTo(size * 0.55, size * 0.35);
                arrow.lineTo(size * 0.55, size * 0.15);
                arrow.lineTo(size - 3, size * 0.5);
                arrow.lineTo(size * 0.55, size * 0.85);
                arrow.lineTo(size * 0.55, size * 0.65);
                arrow.lineTo(3, size * 0.65);
                arrow.closePath();
                g2.fill(arrow);
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(arrow);
                break;
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    public static Icon createActionIcon(String action, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        setupG2(g2);

        Color c = new Color(50, 60, 70);

        switch (action) {
            case "new":
                g2.setColor(Color.WHITE);
                g2.fillRect(4, 3, size - 9, size - 6);
                g2.setColor(c);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRect(4, 3, size - 9, size - 6);
                g2.drawLine(size - 9, 3, size - 5, 7);
                break;

            case "open":
                g2.setColor(new Color(245, 185, 60));
                Polygon folder = new Polygon(
                        new int[]{3, 8, 11, size - 3, size - 3, 3},
                        new int[]{7, 7, 10, 10, size - 4, size - 4},
                        6
                );
                g2.fill(folder);
                g2.setColor(c);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(folder);
                break;

            case "save":
                g2.setColor(new Color(30, 110, 200));
                g2.fillRect(4, 4, size - 8, size - 8);
                g2.setColor(Color.WHITE);
                g2.fillRect(7, 4, size - 14, 5);
                g2.fillRect(6, size - 9, size - 12, 5);
                g2.setColor(c);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRect(4, 4, size - 8, size - 8);
                break;

            case "undo":
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(6, 6, size - 10, size - 10, 45, 200);
                g2.fillPolygon(new int[]{4, 10, 10}, new int[]{8, 5, 11}, 3);
                break;

            case "redo":
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(4, 6, size - 10, size - 10, -25, 200);
                g2.fillPolygon(new int[]{size - 4, size - 10, size - 10}, new int[]{8, 5, 11}, 3);
                break;

            case "zoom_in":
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawOval(4, 4, size - 10, size - 10);
                g2.drawLine(size - 8, size - 8, size - 3, size - 3);
                g2.drawLine(7, size / 2 - 1, size - 11, size / 2 - 1);
                g2.drawLine(size / 2 - 2, 6, size / 2 - 2, size - 10);
                break;

            case "zoom_out":
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawOval(4, 4, size - 10, size - 10);
                g2.drawLine(size - 8, size - 8, size - 3, size - 3);
                g2.drawLine(7, size / 2 - 1, size - 11, size / 2 - 1);
                break;

            case "rotate_cw":
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawArc(4, 4, size - 8, size - 8, 45, 250);
                g2.fillPolygon(new int[]{size - 4, size - 9, size - 5}, new int[]{10, 9, 14}, 3);
                break;

            case "clear":
                g2.setColor(new Color(220, 60, 60));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(5, 5, size - 5, size - 5);
                g2.drawLine(size - 5, 5, 5, size - 5);
                break;

            default:
                g2.setColor(c);
                g2.drawRect(4, 4, size - 8, size - 8);
                break;
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    /**
     * Crée le logo de l'application (palette de peintre avec pinceau).
     */
    public static BufferedImage createAppLogo(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        setupG2(g2);

        // Palette bois
        g2.setColor(new Color(230, 180, 120));
        g2.fillOval(2, 2, size - 4, size - 6);
        g2.setColor(new Color(180, 120, 60));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawOval(2, 2, size - 4, size - 6);

        // Trou pour le pouce
        g2.setColor(Color.WHITE);
        g2.fillOval(size / 2 + 2, size / 2, size / 5, size / 5);
        g2.setColor(new Color(180, 120, 60));
        g2.drawOval(size / 2 + 2, size / 2, size / 5, size / 5);

        // Taches de couleur sur la palette
        Color[] spots = { Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, new Color(255, 140, 0) };
        int r = size / 6;
        g2.setColor(spots[0]); g2.fillOval(size / 5, size / 5, r, r);
        g2.setColor(spots[1]); g2.fillOval(size / 2 - r / 2, size / 8, r, r);
        g2.setColor(spots[2]); g2.fillOval(size * 3 / 4 - r, size / 4, r, r);
        g2.setColor(spots[3]); g2.fillOval(size / 6, size / 2, r, r);
        g2.setColor(spots[4]); g2.fillOval(size / 3, size * 2 / 3, r, r);

        g2.dispose();
        return img;
    }

    private static Path2D.Double createStarShape(double cx, double cy, double rOuter, double rInner) {
        Path2D.Double path = new Path2D.Double();
        int points = 10;
        double angleStep = Math.PI / 5.0;
        double startAngle = -Math.PI / 2.0;

        for (int i = 0; i < points; i++) {
            double angle = startAngle + i * angleStep;
            double r = (i % 2 == 0) ? rOuter : rInner;
            double px = cx + r * Math.cos(angle);
            double py = cy + r * Math.sin(angle);
            if (i == 0) path.moveTo(px, py);
            else path.lineTo(px, py);
        }
        path.closePath();
        return path;
    }

    private static void setupG2(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
