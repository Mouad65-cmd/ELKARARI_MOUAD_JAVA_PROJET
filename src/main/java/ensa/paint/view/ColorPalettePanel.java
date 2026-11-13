package ensa.paint.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import ensa.paint.i18n.I18n;
import ensa.paint.i18n.LocaleChangeListener;
import ensa.paint.model.DrawingModel;

/**
 * Palette de couleurs avec prévisualisation Couleur 1 / Couleur 2,
 * grille de 24 nuances prédéfinies et sélecteur JColorChooser complet.
 */
public class ColorPalettePanel extends JPanel implements LocaleChangeListener, DrawingModel.ModelListener {

    private final DrawingModel model;

    private final ColorSwatch primarySwatch;
    private final ColorSwatch secondarySwatch;
    private final JButton btnMoreColors;
    private final JLabel lblPrimary;
    private final JLabel lblSecondary;

    private static final Color[] PRESET_COLORS = {
            // Ligne 1
            Color.BLACK,
            new Color(128, 128, 128),
            new Color(136, 0, 21),
            new Color(237, 28, 36),
            new Color(255, 127, 39),
            new Color(255, 242, 0),
            new Color(34, 177, 76),
            new Color(0, 162, 232),
            new Color(63, 72, 204),
            new Color(163, 73, 164),
            new Color(153, 51, 102),
            new Color(185, 122, 87),
            // Ligne 2
            Color.WHITE,
            new Color(195, 195, 195),
            new Color(180, 20, 60),
            new Color(255, 174, 201),
            new Color(255, 201, 14),
            new Color(239, 228, 176),
            new Color(181, 230, 29),
            new Color(153, 217, 234),
            new Color(112, 146, 190),
            new Color(200, 191, 231),
            new Color(255, 218, 185),
            new Color(64, 128, 128)
    };

    public ColorPalettePanel(DrawingModel model) {
        this.model = model;
        model.addListener(this);
        I18n.addListener(this);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);

        // Panneau des 2 couleurs actives
        JPanel activeColorsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        activeColorsPanel.setOpaque(false);

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setOpaque(false);
        primarySwatch = new ColorSwatch(model.getPrimaryColor(), 28);
        primarySwatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color c = JColorChooser.showDialog(ColorPalettePanel.this, I18n.get("toolbar.color_primary"), model.getPrimaryColor());
                if (c != null) model.setPrimaryColor(c);
            }
        });
        lblPrimary = new JLabel(I18n.get("toolbar.color_primary"), SwingConstants.CENTER);
        lblPrimary.setFont(lblPrimary.getFont().deriveFont(9.0f));
        lblPrimary.setAlignmentX(CENTER_ALIGNMENT);
        primarySwatch.setAlignmentX(CENTER_ALIGNMENT);
        p1.add(primarySwatch);
        p1.add(Box.createVerticalStrut(2));
        p1.add(lblPrimary);

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setOpaque(false);
        secondarySwatch = new ColorSwatch(model.getSecondaryColor(), 28);
        secondarySwatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color c = JColorChooser.showDialog(ColorPalettePanel.this, I18n.get("toolbar.color_secondary"), model.getSecondaryColor());
                if (c != null) model.setSecondaryColor(c);
            }
        });
        lblSecondary = new JLabel(I18n.get("toolbar.color_secondary"), SwingConstants.CENTER);
        lblSecondary.setFont(lblSecondary.getFont().deriveFont(9.0f));
        lblSecondary.setAlignmentX(CENTER_ALIGNMENT);
        secondarySwatch.setAlignmentX(CENTER_ALIGNMENT);
        p2.add(secondarySwatch);
        p2.add(Box.createVerticalStrut(2));
        p2.add(lblSecondary);

        activeColorsPanel.add(p1);
        activeColorsPanel.add(p2);

        // Grille des 24 couleurs rapides
        JPanel gridPanel = new JPanel(new GridLayout(2, 12, 3, 3));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        for (Color color : PRESET_COLORS) {
            ColorSwatch swatch = new ColorSwatch(color, 16);
            swatch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            swatch.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        model.setSecondaryColor(color);
                    } else {
                        model.setPrimaryColor(color);
                    }
                }
            });
            gridPanel.add(swatch);
        }

        // Bouton Plus de couleurs
        btnMoreColors = new JButton("<html><center>🎨<br>" + I18n.get("toolbar.more_colors") + "</center></html>");
        btnMoreColors.setFont(btnMoreColors.getFont().deriveFont(9.5f));
        btnMoreColors.setFocusable(false);
        btnMoreColors.setToolTipText(I18n.get("toolbar.more_colors"));
        btnMoreColors.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, I18n.get("toolbar.more_colors"), model.getPrimaryColor());
            if (chosen != null) {
                model.setPrimaryColor(chosen);
            }
        });

        add(activeColorsPanel);
        add(gridPanel);
        add(Box.createHorizontalStrut(4));
        add(btnMoreColors);
    }

    @Override
    public void onLocaleChange() {
        lblPrimary.setText(I18n.get("toolbar.color_primary"));
        lblSecondary.setText(I18n.get("toolbar.color_secondary"));
        btnMoreColors.setText("<html><center>🎨<br>" + I18n.get("toolbar.more_colors") + "</center></html>");
        btnMoreColors.setToolTipText(I18n.get("toolbar.more_colors"));
    }

    @Override
    public void onColorsChanged(Color primary, Color secondary) {
        primarySwatch.setColor(primary);
        secondarySwatch.setColor(secondary);
        repaint();
    }

    @Override
    public void onCanvasChanged() {}
    @Override
    public void onToolChanged(ensa.paint.model.ToolType tool) {}
    @Override
    public void onZoomChanged(double zoom) {}
    @Override
    public void onHistoryChanged(boolean canUndo, boolean canRedo) {}
    @Override
    public void onFileStateChanged(java.io.File file, boolean modified) {}

    /**
     * Composant visuel représentant une vignette de couleur avec bordure nette.
     */
    private static class ColorSwatch extends JPanel {
        private Color color;
        private final int size;

        public ColorSwatch(Color color, int size) {
            this.color = color;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        public void setColor(Color color) {
            this.color = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color != null ? color : Color.BLACK);
            g2.fillRoundRect(1, 1, size - 2, size - 2, 4, 4);

            g2.setColor(new Color(160, 160, 160));
            g2.drawRoundRect(0, 0, size - 1, size - 1, 4, 4);
        }
    }
}
