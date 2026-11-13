package ensa.paint.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import ensa.paint.i18n.I18n;
import ensa.paint.i18n.LocaleChangeListener;
import ensa.paint.model.DrawingModel;
import ensa.paint.model.FillMode;
import ensa.paint.model.StrokeStyle;
import ensa.paint.model.ToolType;
import ensa.paint.utils.IconFactory;

/**
 * Barre d'outils supérieure moderne organisée en sections :
 * Outils, Formes, Propriétés du tracé, Couleurs et Actions rapides.
 */
public class ToolBarPanel extends JPanel implements LocaleChangeListener, DrawingModel.ModelListener {

    private final DrawingModel model;
    private final Map<ToolType, JToggleButton> toolButtons = new EnumMap<>(ToolType.class);
    private final ButtonGroup toolGroup = new ButtonGroup();

    // Panneaux avec bordures titrées pour mise à jour i18n
    private final JPanel pnlTools = new JPanel();
    private final JPanel pnlShapes = new JPanel();
    private final JPanel pnlStroke = new JPanel();
    private final JPanel pnlColors;

    // Contrôles
    private final JSpinner spnStrokeWidth;
    private final JComboBox<StrokeStyle> cbStrokeStyle;
    private final JComboBox<FillMode> cbFillMode;
    private final JButton btnUndo;
    private final JButton btnRedo;

    private final JLabel lblStrokeLabel = new JLabel();
    private final JLabel lblStyleLabel = new JLabel();
    private final JLabel lblFillLabel = new JLabel();

    public ToolBarPanel(DrawingModel model) {
        this.model = model;
        model.addListener(this);
        I18n.addListener(this);

        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        setBackground(new Color(245, 246, 248));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(215, 215, 220)));

        // --- 1. Section Outils libres ---
        pnlTools.setLayout(new GridLayout(2, 4, 3, 3));
        pnlTools.setOpaque(false);
        addToolButton(ToolType.PENCIL, pnlTools);
        addToolButton(ToolType.BRUSH, pnlTools);
        addToolButton(ToolType.ERASER, pnlTools);
        addToolButton(ToolType.BUCKET, pnlTools);
        addToolButton(ToolType.PICKER, pnlTools);
        addToolButton(ToolType.TEXT, pnlTools);
        addToolButton(ToolType.SELECTION, pnlTools);

        // --- 2. Section Formes géométriques ---
        pnlShapes.setLayout(new GridLayout(2, 4, 3, 3));
        pnlShapes.setOpaque(false);
        addToolButton(ToolType.LINE, pnlShapes);
        addToolButton(ToolType.RECTANGLE, pnlShapes);
        addToolButton(ToolType.ROUNDED_RECTANGLE, pnlShapes);
        addToolButton(ToolType.ELLIPSE, pnlShapes);
        addToolButton(ToolType.TRIANGLE, pnlShapes);
        addToolButton(ToolType.STAR, pnlShapes);
        addToolButton(ToolType.ARROW, pnlShapes);

        // --- 3. Section Propriétés du tracé ---
        pnlStroke.setLayout(new BoxLayout(pnlStroke, BoxLayout.Y_AXIS));
        pnlStroke.setOpaque(false);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row1.setOpaque(false);
        spnStrokeWidth = new JSpinner(new SpinnerNumberModel((int) model.getStrokeWidth(), 1, 50, 1));
        spnStrokeWidth.setPreferredSize(new Dimension(50, 22));
        spnStrokeWidth.addChangeListener(e -> model.setStrokeWidth(((Number) spnStrokeWidth.getValue()).floatValue()));
        row1.add(lblStrokeLabel);
        row1.add(spnStrokeWidth);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row2.setOpaque(false);
        cbStrokeStyle = new JComboBox<>(StrokeStyle.values());
        cbStrokeStyle.setPreferredSize(new Dimension(85, 22));
        cbStrokeStyle.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof StrokeStyle) {
                    setText(I18n.get(((StrokeStyle) value).getI18nKey()));
                }
                return this;
            }
        });
        cbStrokeStyle.addActionListener(e -> model.setStrokeStyle((StrokeStyle) cbStrokeStyle.getSelectedItem()));
        row2.add(lblStyleLabel);
        row2.add(cbStrokeStyle);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row3.setOpaque(false);
        cbFillMode = new JComboBox<>(FillMode.values());
        cbFillMode.setPreferredSize(new Dimension(105, 22));
        cbFillMode.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof FillMode) {
                    setText(I18n.get(((FillMode) value).getI18nKey()));
                }
                return this;
            }
        });
        cbFillMode.addActionListener(e -> model.setFillMode((FillMode) cbFillMode.getSelectedItem()));
        row3.add(lblFillLabel);
        row3.add(cbFillMode);

        pnlStroke.add(row1);
        pnlStroke.add(row2);
        pnlStroke.add(row3);

        // --- 4. Section Palette de couleurs ---
        pnlColors = new ColorPalettePanel(model);

        // --- 5. Actions Rapides (Annuler / Rétablir) ---
        JPanel pnlQuickActions = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlQuickActions.setOpaque(false);

        btnUndo = new JButton(IconFactory.createActionIcon("undo", 16));
        btnUndo.setFocusable(false);
        btnUndo.setEnabled(false);
        btnUndo.addActionListener(e -> model.undo());

        btnRedo = new JButton(IconFactory.createActionIcon("redo", 16));
        btnRedo.setFocusable(false);
        btnRedo.setEnabled(false);
        btnRedo.addActionListener(e -> model.redo());

        pnlQuickActions.add(btnUndo);
        pnlQuickActions.add(btnRedo);

        // Assemblage des sections
        add(pnlTools);
        add(createVerticalSeparator());
        add(pnlShapes);
        add(createVerticalSeparator());
        add(pnlStroke);
        add(createVerticalSeparator());
        add(pnlColors);
        add(createVerticalSeparator());
        add(pnlQuickActions);

        // Sélection initiale du crayon
        toolButtons.get(ToolType.PENCIL).setSelected(true);

        updateBordersAndTexts();
    }

    private JSeparator createVerticalSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 60));
        return sep;
    }

    private void addToolButton(ToolType type, JPanel parent) {
        JToggleButton btn = new JToggleButton(IconFactory.getToolIcon(type, 20));
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setFocusable(false);
        btn.setToolTipText(I18n.get(type.getI18nKey()));
        btn.addActionListener(e -> model.setCurrentTool(type));
        toolGroup.add(btn);
        toolButtons.put(type, btn);
        parent.add(btn);
    }

    private void updateBordersAndTexts() {
        Font titleFont = new Font("SansSerif", Font.BOLD, 10);
        Color titleColor = new Color(90, 95, 105);

        pnlTools.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 225)),
                I18n.get("toolbar.tools"), TitledBorder.CENTER, TitledBorder.BOTTOM, titleFont, titleColor));

        pnlShapes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 225)),
                I18n.get("toolbar.shapes"), TitledBorder.CENTER, TitledBorder.BOTTOM, titleFont, titleColor));

        pnlStroke.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 225)),
                I18n.get("toolbar.stroke"), TitledBorder.CENTER, TitledBorder.BOTTOM, titleFont, titleColor));

        lblStrokeLabel.setText(I18n.get("toolbar.stroke") + " : ");
        lblStyleLabel.setText(I18n.get("toolbar.style") + " : ");
        lblFillLabel.setText(I18n.get("toolbar.fill") + " : ");

        btnUndo.setToolTipText(I18n.get("menu.edit.undo"));
        btnRedo.setToolTipText(I18n.get("menu.edit.redo"));

        for (Map.Entry<ToolType, JToggleButton> entry : toolButtons.entrySet()) {
            entry.getValue().setToolTipText(I18n.get(entry.getKey().getI18nKey()));
        }

        cbStrokeStyle.repaint();
        cbFillMode.repaint();
        revalidate();
        repaint();
    }

    @Override
    public void onLocaleChange() {
        updateBordersAndTexts();
    }

    @Override
    public void onToolChanged(ToolType tool) {
        JToggleButton btn = toolButtons.get(tool);
        if (btn != null && !btn.isSelected()) {
            btn.setSelected(true);
        }
    }

    @Override
    public void onHistoryChanged(boolean canUndo, boolean canRedo) {
        btnUndo.setEnabled(canUndo);
        btnRedo.setEnabled(canRedo);
    }

    @Override
    public void onCanvasChanged() {}
    @Override
    public void onColorsChanged(Color primary, Color secondary) {}
    @Override
    public void onZoomChanged(double zoom) {}
    @Override
    public void onFileStateChanged(File file, boolean modified) {}
}
