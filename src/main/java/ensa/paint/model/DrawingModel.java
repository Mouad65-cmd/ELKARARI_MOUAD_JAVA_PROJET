package ensa.paint.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de données central de l'application de dessin (MVC Pattern).
 * Gère l'image matricielle, l'état des outils, les sélections et l'historique.
 */
public class DrawingModel {

    public static final int DEFAULT_WIDTH = 960;
    public static final int DEFAULT_HEIGHT = 600;

    private BufferedImage canvasImage;
    private Color primaryColor = Color.BLACK;
    private Color secondaryColor = Color.WHITE;
    private float strokeWidth = 3.0f;
    private StrokeStyle strokeStyle = StrokeStyle.SOLID;
    private FillMode fillMode = FillMode.STROKE_ONLY;
    private ToolType currentTool = ToolType.PENCIL;

    private double zoomFactor = 1.0;
    private boolean gridVisible = false;
    private boolean rulersVisible = true;

    // Gestion de la sélection
    private Rectangle selectionBounds = null;
    private BufferedImage selectionImage = null;
    private boolean isSelectionFloating = false;
    private static BufferedImage clipboardImage = null;

    // Gestion du texte
    private String fontFamily = "SansSerif";
    private int fontSize = 24;
    private boolean fontBold = false;
    private boolean fontItalic = false;

    // Fichier & Historique
    private File currentFile = null;
    private boolean isModified = false;
    private final HistoryManager historyManager = new HistoryManager();

    // Écouteurs d'événements du modèle
    public interface ModelListener {
        void onCanvasChanged();
        void onToolChanged(ToolType tool);
        void onColorsChanged(Color primary, Color secondary);
        void onZoomChanged(double zoom);
        void onHistoryChanged(boolean canUndo, boolean canRedo);
        void onFileStateChanged(File file, boolean modified);
    }

    private final List<ModelListener> listeners = new ArrayList<>();

    public DrawingModel() {
        createNewCanvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void addListener(ModelListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ModelListener listener) {
        listeners.remove(listener);
    }

    /**
     * Initialise une nouvelle zone de dessin vierge (fond blanc).
     */
    public void createNewCanvas(int width, int height) {
        commitSelection();
        BufferedImage newImg = new BufferedImage(Math.max(10, width), Math.max(10, height), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = newImg.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, newImg.getWidth(), newImg.getHeight());
        g2.dispose();

        this.canvasImage = newImg;
        this.currentFile = null;
        this.isModified = false;
        historyManager.clear();

        fireCanvasChanged();
        fireFileStateChanged();
        fireHistoryChanged();
    }

    /**
     * Définit une nouvelle image (lors de l'ouverture d'un fichier).
     */
    public void setImage(BufferedImage image, File file) {
        commitSelection();
        if (image == null) return;
        // Conversion vers format RGB standard compatible
        BufferedImage converted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = converted.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        this.canvasImage = converted;
        this.currentFile = file;
        this.isModified = false;
        historyManager.clear();

        fireCanvasChanged();
        fireFileStateChanged();
        fireHistoryChanged();
    }

    /**
     * Enregistre un instantané avant modification.
     */
    public void saveSnapshot() {
        commitSelection();
        historyManager.pushState(canvasImage);
        setModified(true);
        fireHistoryChanged();
    }

    public void undo() {
        if (!historyManager.canUndo()) return;
        commitSelection();
        BufferedImage prev = historyManager.undo(canvasImage);
        if (prev != null) {
            this.canvasImage = prev;
            setModified(true);
            fireCanvasChanged();
            fireHistoryChanged();
        }
    }

    public void redo() {
        if (!historyManager.canRedo()) return;
        commitSelection();
        BufferedImage next = historyManager.redo(canvasImage);
        if (next != null) {
            this.canvasImage = next;
            setModified(true);
            fireCanvasChanged();
            fireHistoryChanged();
        }
    }

    /**
     * Redimensionne la zone de dessin (avec ou sans mise à l'échelle du contenu).
     */
    public void resizeCanvas(int newW, int newH, boolean scaleContent) {
        if (newW <= 0 || newH <= 0) return;
        saveSnapshot();
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setColor(secondaryColor);
        g2.fillRect(0, 0, newW, newH);

        if (scaleContent) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(canvasImage, 0, 0, newW, newH, null);
        } else {
            g2.drawImage(canvasImage, 0, 0, null);
        }
        g2.dispose();

        this.canvasImage = resized;
        fireCanvasChanged();
    }

    /**
     * Rotation de l'image (90°, 180°, 270°).
     */
    public void rotate(int degrees) {
        saveSnapshot();
        int w = canvasImage.getWidth();
        int h = canvasImage.getHeight();
        int newW = (degrees % 180 == 0) ? w : h;
        int newH = (degrees % 180 == 0) ? h : w;

        BufferedImage rotated = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = rotated.createGraphics();
        AffineTransform at = new AffineTransform();

        if (degrees == 90) {
            at.translate(newW, 0);
            at.rotate(Math.PI / 2);
        } else if (degrees == 180) {
            at.translate(newW, newH);
            at.rotate(Math.PI);
        } else if (degrees == 270) {
            at.translate(0, newH);
            at.rotate(3 * Math.PI / 2);
        }
        g2.drawImage(canvasImage, at, null);
        g2.dispose();

        this.canvasImage = rotated;
        fireCanvasChanged();
    }

    /**
     * Miroir horizontal.
     */
    public void flipHorizontal() {
        saveSnapshot();
        int w = canvasImage.getWidth();
        int h = canvasImage.getHeight();
        BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = flipped.createGraphics();
        g2.drawImage(canvasImage, w, 0, -w, h, null);
        g2.dispose();
        this.canvasImage = flipped;
        fireCanvasChanged();
    }

    /**
     * Miroir vertical.
     */
    public void flipVertical() {
        saveSnapshot();
        int w = canvasImage.getWidth();
        int h = canvasImage.getHeight();
        BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = flipped.createGraphics();
        g2.drawImage(canvasImage, 0, h, w, -h, null);
        g2.dispose();
        this.canvasImage = flipped;
        fireCanvasChanged();
    }

    /**
     * Effet négatif / inversion des couleurs.
     */
    public void invertColors() {
        saveSnapshot();
        int w = canvasImage.getWidth();
        int h = canvasImage.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = canvasImage.getRGB(x, y);
                int r = 255 - ((rgb >> 16) & 0xFF);
                int g = 255 - ((rgb >> 8) & 0xFF);
                int b = 255 - (rgb & 0xFF);
                canvasImage.setRGB(x, y, (rgb & 0xFF000000) | (r << 16) | (g << 8) | b);
            }
        }
        fireCanvasChanged();
    }

    /**
     * Conversion en niveaux de gris.
     */
    public void toGrayscale() {
        saveSnapshot();
        int w = canvasImage.getWidth();
        int h = canvasImage.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = canvasImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                canvasImage.setRGB(x, y, (rgb & 0xFF000000) | (gray << 16) | (gray << 8) | gray);
            }
        }
        fireCanvasChanged();
    }

    /**
     * Efface complètement la zone de dessin avec la couleur secondaire.
     */
    public void clearCanvas() {
        saveSnapshot();
        Graphics2D g2 = canvasImage.createGraphics();
        g2.setColor(secondaryColor);
        g2.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        g2.dispose();
        fireCanvasChanged();
    }

    // --- Gestion de la sélection ---

    public void startSelection(Rectangle bounds) {
        commitSelection();
        if (bounds == null || bounds.width <= 0 || bounds.height <= 0) {
            selectionBounds = null;
            selectionImage = null;
            isSelectionFloating = false;
            fireCanvasChanged();
            return;
        }

        // Clamp to image bounds
        int x = Math.max(0, bounds.x);
        int y = Math.max(0, bounds.y);
        int w = Math.min(canvasImage.getWidth() - x, bounds.width);
        int h = Math.min(canvasImage.getHeight() - y, bounds.height);

        if (w <= 0 || h <= 0) {
            selectionBounds = null;
            return;
        }

        this.selectionBounds = new Rectangle(x, y, w, h);
        this.selectionImage = canvasImage.getSubimage(x, y, w, h);
        this.isSelectionFloating = false;
        fireCanvasChanged();
    }

    public void liftSelection() {
        if (selectionBounds == null || isSelectionFloating) return;
        saveSnapshot();
        // Clone the subimage
        selectionImage = HistoryManager.cloneImage(canvasImage.getSubimage(
                selectionBounds.x, selectionBounds.y, selectionBounds.width, selectionBounds.height));

        // Fill background with secondary color
        Graphics2D g2 = canvasImage.createGraphics();
        g2.setColor(secondaryColor);
        g2.fillRect(selectionBounds.x, selectionBounds.y, selectionBounds.width, selectionBounds.height);
        g2.dispose();

        isSelectionFloating = true;
        fireCanvasChanged();
    }

    public void moveSelection(int dx, int dy) {
        if (selectionBounds != null) {
            if (!isSelectionFloating) {
                liftSelection();
            }
            selectionBounds.translate(dx, dy);
            fireCanvasChanged();
        }
    }

    public void commitSelection() {
        if (selectionBounds != null && isSelectionFloating && selectionImage != null) {
            Graphics2D g2 = canvasImage.createGraphics();
            g2.drawImage(selectionImage, selectionBounds.x, selectionBounds.y, null);
            g2.dispose();
            setModified(true);
        }
        selectionBounds = null;
        selectionImage = null;
        isSelectionFloating = false;
        fireCanvasChanged();
    }

    public void cutSelection() {
        if (selectionBounds == null) return;
        copySelection();
        deleteSelection();
    }

    public void copySelection() {
        if (selectionBounds == null) return;
        if (!isSelectionFloating && selectionImage != null) {
            clipboardImage = HistoryManager.cloneImage(selectionImage);
        } else if (isSelectionFloating && selectionImage != null) {
            clipboardImage = HistoryManager.cloneImage(selectionImage);
        }
    }

    public void pasteSelection() {
        if (clipboardImage == null) return;
        commitSelection();
        saveSnapshot();
        selectionImage = HistoryManager.cloneImage(clipboardImage);
        selectionBounds = new Rectangle(10, 10, clipboardImage.getWidth(), clipboardImage.getHeight());
        isSelectionFloating = true;
        setCurrentTool(ToolType.SELECTION);
        fireCanvasChanged();
    }

    public void deleteSelection() {
        if (selectionBounds == null) return;
        saveSnapshot();
        if (!isSelectionFloating) {
            Graphics2D g2 = canvasImage.createGraphics();
            g2.setColor(secondaryColor);
            g2.fillRect(selectionBounds.x, selectionBounds.y, selectionBounds.width, selectionBounds.height);
            g2.dispose();
        }
        selectionBounds = null;
        selectionImage = null;
        isSelectionFloating = false;
        fireCanvasChanged();
    }

    public void selectAll() {
        commitSelection();
        selectionBounds = new Rectangle(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        selectionImage = canvasImage.getSubimage(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        isSelectionFloating = false;
        setCurrentTool(ToolType.SELECTION);
        fireCanvasChanged();
    }

    // --- Getters / Setters ---

    public BufferedImage getCanvasImage() {
        return canvasImage;
    }

    public int getCanvasWidth() {
        return canvasImage != null ? canvasImage.getWidth() : DEFAULT_WIDTH;
    }

    public int getCanvasHeight() {
        return canvasImage != null ? canvasImage.getHeight() : DEFAULT_HEIGHT;
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(Color primaryColor) {
        this.primaryColor = primaryColor;
        fireColorsChanged();
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor;
        fireColorsChanged();
    }

    public void swapColors() {
        Color tmp = primaryColor;
        primaryColor = secondaryColor;
        secondaryColor = tmp;
        fireColorsChanged();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = Math.max(1.0f, Math.min(100.0f, strokeWidth));
    }

    public StrokeStyle getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(StrokeStyle strokeStyle) {
        this.strokeStyle = strokeStyle != null ? strokeStyle : StrokeStyle.SOLID;
    }

    public FillMode getFillMode() {
        return fillMode;
    }

    public void setFillMode(FillMode fillMode) {
        this.fillMode = fillMode != null ? fillMode : FillMode.STROKE_ONLY;
    }

    public ToolType getCurrentTool() {
        return currentTool;
    }

    public void setCurrentTool(ToolType tool) {
        if (this.currentTool != tool) {
            if (this.currentTool == ToolType.SELECTION && tool != ToolType.SELECTION) {
                commitSelection();
            }
            this.currentTool = tool;
            fireToolChanged();
        }
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(double zoom) {
        this.zoomFactor = Math.max(0.25, Math.min(8.0, zoom));
        fireZoomChanged();
    }

    public boolean isGridVisible() {
        return gridVisible;
    }

    public void setGridVisible(boolean gridVisible) {
        this.gridVisible = gridVisible;
        fireCanvasChanged();
    }

    public boolean isRulersVisible() {
        return rulersVisible;
    }

    public void setRulersVisible(boolean rulersVisible) {
        this.rulersVisible = rulersVisible;
        fireCanvasChanged();
    }

    public Rectangle getSelectionBounds() {
        return selectionBounds;
    }

    public BufferedImage getSelectionImage() {
        return selectionImage;
    }

    public boolean isSelectionFloating() {
        return isSelectionFloating;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File file) {
        this.currentFile = file;
        fireFileStateChanged();
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        this.isModified = modified;
        fireFileStateChanged();
    }

    public Font getDrawingFont() {
        int style = Font.PLAIN;
        if (fontBold) style |= Font.BOLD;
        if (fontItalic) style |= Font.ITALIC;
        return new Font(fontFamily, style, fontSize);
    }

    public void setFontFamily(String family) { this.fontFamily = family; }
    public void setFontSize(int size) { this.fontSize = size; }
    public void setFontBold(boolean bold) { this.fontBold = bold; }
    public void setFontItalic(boolean italic) { this.fontItalic = italic; }
    public String getFontFamily() { return fontFamily; }
    public int getFontSize() { return fontSize; }
    public boolean isFontBold() { return fontBold; }
    public boolean isFontItalic() { return fontItalic; }

    public boolean canUndo() { return historyManager.canUndo(); }
    public boolean canRedo() { return historyManager.canRedo(); }

    // --- Notifications ---

    public void fireCanvasChanged() {
        for (ModelListener l : new ArrayList<>(listeners)) l.onCanvasChanged();
    }

    public void fireToolChanged() {
        for (ModelListener l : new ArrayList<>(listeners)) l.onToolChanged(currentTool);
    }

    public void fireColorsChanged() {
        for (ModelListener l : new ArrayList<>(listeners)) l.onColorsChanged(primaryColor, secondaryColor);
    }

    public void fireZoomChanged() {
        for (ModelListener l : new ArrayList<>(listeners)) l.onZoomChanged(zoomFactor);
    }

    public void fireHistoryChanged() {
        for (ModelListener l : new ArrayList<>(listeners)) l.onHistoryChanged(canUndo(), canRedo());
    }

    public void fireFileStateChanged() {
        for (ModelListener l : new ArrayList<>(listeners)) l.onFileStateChanged(currentFile, isModified);
    }
}
