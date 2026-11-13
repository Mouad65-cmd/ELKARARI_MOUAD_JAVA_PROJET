package ensa.paint.tools;

import java.awt.Cursor;
import java.awt.Graphics2D;
import ensa.paint.model.DrawingModel;

/**
 * Interface Strategy pour chaque outil de dessin.
 */
public interface Tool {
    /**
     * Clic de la souris enfoncé.
     */
    void onMouseDown(int x, int y, DrawingModel model, boolean isRightClick);

    /**
     * Déplacement de la souris avec bouton enfoncé.
     */
    void onMouseDrag(int x, int y, DrawingModel model, boolean isRightClick);

    /**
     * Relâchement du bouton de la souris.
     */
    void onMouseUp(int x, int y, DrawingModel model, boolean isRightClick);

    /**
     * Dessine la prévisualisation en direct (ex: pendant le tracé d'un rectangle).
     */
    void drawPreview(Graphics2D g2, DrawingModel model);

    /**
     * Curseur personnalisé pour l'outil.
     */
    Cursor getCursor();
}
