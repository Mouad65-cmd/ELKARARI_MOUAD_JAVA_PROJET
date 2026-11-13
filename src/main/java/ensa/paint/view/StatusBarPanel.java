package ensa.paint.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import ensa.paint.i18n.I18n;
import ensa.paint.i18n.LocaleChangeListener;
import ensa.paint.model.DrawingModel;
import ensa.paint.model.ToolType;

/**
 * Barre d'état en bas de la fenêtre affichant les coordonnées, la taille,
 * le zoom et l'état du document.
 */
public class StatusBarPanel extends JPanel implements LocaleChangeListener, DrawingModel.ModelListener {

    private final DrawingModel model;

    private final JLabel lblPosition = new JLabel();
    private final JLabel lblDimensions = new JLabel();
    private final JLabel lblZoom = new JLabel();
    private final JLabel lblTool = new JLabel();
    private final JLabel lblStatus = new JLabel();

    private int lastMouseX = 0;
    private int lastMouseY = 0;

    public StatusBarPanel(DrawingModel model) {
        this.model = model;
        model.addListener(this);
        I18n.addListener(this);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 215)),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        setBackground(new Color(245, 245, 247));
        setPreferredSize(new Dimension(100, 26));

        Font font = new Font("SansSerif", Font.PLAIN, 12);
        lblPosition.setFont(font);
        lblDimensions.setFont(font);
        lblZoom.setFont(font);
        lblTool.setFont(font);
        lblStatus.setFont(font);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(lblStatus);
        leftPanel.add(createSep());
        leftPanel.add(lblTool);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(lblPosition);
        rightPanel.add(createSep());
        rightPanel.add(lblDimensions);
        rightPanel.add(createSep());
        rightPanel.add(lblZoom);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        refreshTexts();
    }

    private JSeparator createSep() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 14));
        return sep;
    }

    public void updateMousePosition(int x, int y) {
        this.lastMouseX = x;
        this.lastMouseY = y;
        lblPosition.setText(I18n.get("status.position", x, y));
    }

    public void updateCanvasSize(int w, int h) {
        lblDimensions.setText(I18n.get("status.size", w, h));
    }

    public void refreshTexts() {
        lblPosition.setText(I18n.get("status.position", lastMouseX, lastMouseY));
        lblDimensions.setText(I18n.get("status.size", model.getCanvasWidth(), model.getCanvasHeight()));
        lblZoom.setText(I18n.get("status.zoom", (int) (model.getZoomFactor() * 100)));
        lblTool.setText(I18n.get("status.tool", I18n.get(model.getCurrentTool().getI18nKey())));
        lblStatus.setText(model.isModified() ? I18n.get("status.modified") : I18n.get("status.ready"));
    }

    @Override
    public void onLocaleChange() {
        refreshTexts();
    }

    @Override
    public void onCanvasChanged() {
        updateCanvasSize(model.getCanvasWidth(), model.getCanvasHeight());
    }

    @Override
    public void onToolChanged(ToolType tool) {
        lblTool.setText(I18n.get("status.tool", I18n.get(tool.getI18nKey())));
    }

    @Override
    public void onColorsChanged(Color primary, Color secondary) {}

    @Override
    public void onZoomChanged(double zoom) {
        lblZoom.setText(I18n.get("status.zoom", (int) (zoom * 100)));
    }

    @Override
    public void onHistoryChanged(boolean canUndo, boolean canRedo) {}

    @Override
    public void onFileStateChanged(File file, boolean modified) {
        lblStatus.setText(modified ? I18n.get("status.modified") : I18n.get("status.ready"));
    }
}
