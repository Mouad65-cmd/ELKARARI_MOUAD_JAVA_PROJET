package ensa.paint.tools;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.swing.JOptionPane;
import ensa.paint.i18n.I18n;
import ensa.paint.model.DrawingModel;

/**
 * Outil Texte permettant d'insérer du texte sur la zone de dessin.
 */
public class TextTool extends AbstractTool {

    @Override
    public void onMouseDown(int x, int y, DrawingModel model, boolean isRightClick) {
        String input = JOptionPane.showInputDialog(
                null,
                I18n.get("dialog.text_prompt"),
                I18n.get("dialog.enter_text_title"),
                JOptionPane.PLAIN_MESSAGE
        );

        if (input != null && !input.trim().isEmpty()) {
            model.saveSnapshot();
            Graphics2D g2 = model.getCanvasImage().createGraphics();
            setupGraphics(g2);
            g2.setColor(getDrawColor(model, isRightClick));
            Font font = model.getDrawingFont();
            g2.setFont(font);

            FontMetrics fm = g2.getFontMetrics(font);
            // Aligner la ligne de base du texte par rapport au clic
            int baselineY = y + fm.getAscent();

            // Gestion multilignes si le texte contient des sauts de ligne
            String[] lines = input.split("\n");
            for (int i = 0; i < lines.length; i++) {
                g2.drawString(lines[i], x, baselineY + i * fm.getHeight());
            }

            g2.dispose();
            model.setModified(true);
            model.fireCanvasChanged();
        }
    }

    @Override
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    }
}
