package ensa.paint.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Gestionnaire d'historique (Annuler / Rétablir) basé sur des instantanés
 * de l'image (Memento Pattern).
 */
public class HistoryManager {

    private static final int MAX_HISTORY_STEPS = 30;

    private final Deque<BufferedImage> undoStack = new ArrayDeque<>();
    private final Deque<BufferedImage> redoStack = new ArrayDeque<>();

    /**
     * Enregistre l'état actuel avant une nouvelle opération.
     */
    public void pushState(BufferedImage image) {
        if (image == null) return;
        if (undoStack.size() >= MAX_HISTORY_STEPS) {
            undoStack.removeLast(); // Décharge le plus ancien
        }
        undoStack.push(cloneImage(image));
        redoStack.clear(); // Une nouvelle action invalide l'historique de rétablissement
    }

    /**
     * Annule la dernière action.
     * @param currentImage L'image courante à pousser sur la pile redo.
     * @return L'image précédente, ou null si l'historique est vide.
     */
    public BufferedImage undo(BufferedImage currentImage) {
        if (!canUndo() || currentImage == null) return null;
        redoStack.push(cloneImage(currentImage));
        return undoStack.pop();
    }

    /**
     * Rétablit l'action annulée.
     * @param currentImage L'image courante à pousser sur la pile undo.
     * @return L'image suivante, ou null si redo est vide.
     */
    public BufferedImage redo(BufferedImage currentImage) {
        if (!canRedo() || currentImage == null) return null;
        undoStack.push(cloneImage(currentImage));
        return redoStack.pop();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    public static BufferedImage cloneImage(BufferedImage src) {
        if (src == null) return null;
        int type = (src.getType() == BufferedImage.TYPE_CUSTOM || src.getType() == 0)
                ? BufferedImage.TYPE_INT_ARGB : src.getType();
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), type);
        Graphics2D g2 = copy.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return copy;
    }
}
