package ensa.paint.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import ensa.paint.model.DrawingModel;
import ensa.paint.tools.Tool;
import ensa.paint.tools.ToolRegistry;

/**
 * Panneau principal affichant la zone de dessin et gérant les interactions souris.
 */
public class CanvasPanel extends JPanel {

    private static final int RESIZE_HANDLE_SIZE = 7;
    private static final int CANVAS_MARGIN = 20;

    private final DrawingModel model;
    private final ToolRegistry toolRegistry;

    private RulerPanel horizontalRuler;
    private RulerPanel verticalRuler;
    private StatusBarPanel statusBar;

    // État de redimensionnement manuel de la zone
    private boolean isResizingCanvas = false;
    private int resizeStartX = 0;
    private int resizeStartY = 0;
    private int originalWidth = 0;
    private int originalHeight = 0;

    public CanvasPanel(DrawingModel model, ToolRegistry toolRegistry) {
        this.model = model;
        this.toolRegistry = toolRegistry;

        setBackground(new Color(230, 233, 238));
        setDoubleBuffered(true);

        MouseHandler handler = new MouseHandler();
        addMouseListener(handler);
        addMouseMotionListener(handler);
        addMouseWheelListener(handler);

        updatePanelSize();
    }

    public void setRulers(RulerPanel hRuler, RulerPanel vRuler) {
        this.horizontalRuler = hRuler;
        this.verticalRuler = vRuler;
    }

    public void setStatusBar(StatusBarPanel statusBar) {
        this.statusBar = statusBar;
    }

    public void updatePanelSize() {
        double zoom = model.getZoomFactor();
        int w = (int) (model.getCanvasWidth() * zoom) + CANVAS_MARGIN * 2;
        int h = (int) (model.getCanvasHeight() * zoom) + CANVAS_MARGIN * 2;
        setPreferredSize(new Dimension(w, h));
        revalidate();
        repaint();

        if (horizontalRuler != null) horizontalRuler.updateDimensions();
        if (verticalRuler != null) verticalRuler.updateDimensions();
    }

    private Point toCanvasCoords(int screenX, int screenY) {
        double zoom = model.getZoomFactor();
        int cx = (int) ((screenX - CANVAS_MARGIN) / zoom);
        int cy = (int) ((screenY - CANVAS_MARGIN) / zoom);
        return new Point(cx, cy);
    }

    private Rectangle getResizeHandleRect() {
        double zoom = model.getZoomFactor();
        int x = CANVAS_MARGIN + (int) (model.getCanvasWidth() * zoom);
        int y = CANVAS_MARGIN + (int) (model.getCanvasHeight() * zoom);
        return new Rectangle(x, y, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        double zoom = model.getZoomFactor();
        int imgW = (int) (model.getCanvasWidth() * zoom);
        int imgH = (int) (model.getCanvasHeight() * zoom);

        // Ombre portée sous le canvas
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRect(CANVAS_MARGIN + 3, CANVAS_MARGIN + 3, imgW, imgH);
        g2.setColor(new Color(0, 0, 0, 15));
        g2.fillRect(CANVAS_MARGIN + 5, CANVAS_MARGIN + 5, imgW, imgH);

        // Fond blanc du canvas
        g2.setColor(Color.WHITE);
        g2.fillRect(CANVAS_MARGIN, CANVAS_MARGIN, imgW, imgH);

        // Dessin de l'image
        BufferedImage img = model.getCanvasImage();
        if (img != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    zoom < 1.0 ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(img, CANVAS_MARGIN, CANVAS_MARGIN, imgW, imgH, null);
        }

        // Prévisualisation de l'outil courant (formes, sélections...)
        Tool activeTool = toolRegistry.getTool(model.getCurrentTool());
        if (activeTool != null) {
            Graphics2D toolG2 = (Graphics2D) g2.create();
            toolG2.translate(CANVAS_MARGIN, CANVAS_MARGIN);
            toolG2.scale(zoom, zoom);
            activeTool.drawPreview(toolG2, model);
            toolG2.dispose();
        }

        // Grille de repères si activée
        if (model.isGridVisible() && zoom >= 1.0) {
            drawGrid(g2, zoom, imgW, imgH);
        }

        // Bordure du canvas
        g2.setColor(new Color(180, 180, 185));
        g2.drawRect(CANVAS_MARGIN, CANVAS_MARGIN, imgW, imgH);

        // Poignée de redimensionnement en bas à droite
        Rectangle handle = getResizeHandleRect();
        g2.setColor(Color.WHITE);
        g2.fillRect(handle.x, handle.y, handle.width, handle.height);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(handle.x, handle.y, handle.width, handle.height);

        g2.dispose();
    }

    private void drawGrid(Graphics2D g2, double zoom, int imgW, int imgH) {
        g2.setColor(new Color(200, 200, 200, 100));
        g2.setStroke(new BasicStroke(0.8f));

        int gridSize = (int) (20 * zoom);
        if (gridSize <= 5) return;

        for (int x = CANVAS_MARGIN; x <= CANVAS_MARGIN + imgW; x += gridSize) {
            g2.drawLine(x, CANVAS_MARGIN, x, CANVAS_MARGIN + imgH);
        }
        for (int y = CANVAS_MARGIN; y <= CANVAS_MARGIN + imgH; y += gridSize) {
            g2.drawLine(CANVAS_MARGIN, y, CANVAS_MARGIN + imgW, y);
        }
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Rectangle handle = getResizeHandleRect();
            if (handle.contains(e.getPoint())) {
                isResizingCanvas = true;
                resizeStartX = e.getX();
                resizeStartY = e.getY();
                originalWidth = model.getCanvasWidth();
                originalHeight = model.getCanvasHeight();
                return;
            }

            Point p = toCanvasCoords(e.getX(), e.getY());
            boolean isRight = SwingUtilities.isRightMouseButton(e);
            Tool tool = toolRegistry.getTool(model.getCurrentTool());
            if (tool != null) {
                tool.onMouseDown(p.x, p.y, model, isRight);
                setCursor(tool.getCursor());
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (isResizingCanvas) {
                double zoom = model.getZoomFactor();
                int dx = (int) ((e.getX() - resizeStartX) / zoom);
                int dy = (int) ((e.getY() - resizeStartY) / zoom);
                int newW = Math.max(50, originalWidth + dx);
                int newH = Math.max(50, originalHeight + dy);
                model.resizeCanvas(newW, newH, false);
                updatePanelSize();
                if (statusBar != null) {
                    statusBar.updateCanvasSize(newW, newH);
                }
                return;
            }

            Point p = toCanvasCoords(e.getX(), e.getY());
            boolean isRight = SwingUtilities.isRightMouseButton(e);
            Tool tool = toolRegistry.getTool(model.getCurrentTool());
            if (tool != null) {
                tool.onMouseDrag(p.x, p.y, model, isRight);
                repaint();
            }

            updateMouseInfo(p.x, p.y);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (isResizingCanvas) {
                isResizingCanvas = false;
                setCursor(Cursor.getDefaultCursor());
                return;
            }

            Point p = toCanvasCoords(e.getX(), e.getY());
            boolean isRight = SwingUtilities.isRightMouseButton(e);
            Tool tool = toolRegistry.getTool(model.getCurrentTool());
            if (tool != null) {
                tool.onMouseUp(p.x, p.y, model, isRight);
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Rectangle handle = getResizeHandleRect();
            if (handle.contains(e.getPoint())) {
                setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            } else {
                Tool tool = toolRegistry.getTool(model.getCurrentTool());
                setCursor(tool != null ? tool.getCursor() : Cursor.getDefaultCursor());
            }

            Point p = toCanvasCoords(e.getX(), e.getY());
            updateMouseInfo(p.x, p.y);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (horizontalRuler != null) horizontalRuler.setMousePosition(-1);
            if (verticalRuler != null) verticalRuler.setMousePosition(-1);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.isControlDown()) {
                double current = model.getZoomFactor();
                if (e.getWheelRotation() < 0) {
                    model.setZoomFactor(Math.min(8.0, current * 1.25));
                } else {
                    model.setZoomFactor(Math.max(0.25, current / 1.25));
                }
                updatePanelSize();
            } else {
                // Laisser le défilement standard du JScrollPane
                getParent().dispatchEvent(e);
            }
        }

        private void updateMouseInfo(int cx, int cy) {
            if (horizontalRuler != null) horizontalRuler.setMousePosition(cx);
            if (verticalRuler != null) verticalRuler.setMousePosition(cy);
            if (statusBar != null) statusBar.updateMousePosition(cx, cy);
        }
    }
}
