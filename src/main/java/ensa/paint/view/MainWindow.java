package ensa.paint.view;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import ensa.paint.i18n.I18n;
import ensa.paint.i18n.LocaleChangeListener;
import ensa.paint.model.DrawingModel;
import ensa.paint.tools.ToolRegistry;
import ensa.paint.utils.FileService;
import ensa.paint.utils.IconFactory;

/**
 * Fenêtre principale de l'application Paint ENSA Safi.
 * Intègre la barre de menus, la barre d'outils, la zone de dessin à règles et la barre d'état.
 */
public class MainWindow extends JFrame implements LocaleChangeListener, DrawingModel.ModelListener {

    private final DrawingModel model;
    private final ToolRegistry toolRegistry;

    private final MenuBarView menuBarView;
    private final ToolBarPanel toolBarPanel;
    private final CanvasPanel canvasPanel;
    private final StatusBarPanel statusBarPanel;
    private final RulerPanel horizontalRuler;
    private final RulerPanel verticalRuler;
    private final JScrollPane scrollPane;

    public MainWindow() {
        this.model = new DrawingModel();
        this.toolRegistry = new ToolRegistry();

        // Logo de la fenêtre
        setIconImage(IconFactory.createAppLogo(64));

        // Initialisation des composants
        canvasPanel = new CanvasPanel(model, toolRegistry);
        horizontalRuler = new RulerPanel(RulerPanel.ORIENTATION_HORIZONTAL, model);
        verticalRuler = new RulerPanel(RulerPanel.ORIENTATION_VERTICAL, model);
        canvasPanel.setRulers(horizontalRuler, verticalRuler);

        statusBarPanel = new StatusBarPanel(model);
        canvasPanel.setStatusBar(statusBarPanel);

        toolBarPanel = new ToolBarPanel(model);
        menuBarView = new MenuBarView(this, model, canvasPanel);

        setJMenuBar(menuBarView);

        // Configuration de la zone de défilement avec les règles intégrées
        scrollPane = new JScrollPane(canvasPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setColumnHeaderView(horizontalRuler);
        scrollPane.setRowHeaderView(verticalRuler);

        // Coin supérieur gauche entre les deux règles
        JPanel corner = new JPanel();
        corner.setBackground(horizontalRuler.getBackground());
        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, corner);

        // Disposition générale
        setLayout(new BorderLayout());
        add(toolBarPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusBarPanel, BorderLayout.SOUTH);

        // Enregistrement des écouteurs
        model.addListener(this);
        I18n.addListener(this);

        // Gestion de la fermeture
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (FileService.checkUnsavedChanges(MainWindow.this, model)) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        updateTitle();
        setPreferredSize(new Dimension(1150, 780));
        pack();
        setLocationRelativeTo(null);
    }

    private void updateTitle() {
        String title = I18n.get("app.title");
        if (model.getCurrentFile() != null) {
            title += " - " + model.getCurrentFile().getName();
        } else {
            title += " - [" + I18n.get("menu.file.new") + "]";
        }
        if (model.isModified()) {
            title += " *";
        }
        setTitle(title);
    }

    @Override
    public void onLocaleChange() {
        updateTitle();

        // Support complet Right-To-Left pour l'arabe
        ComponentOrientation orientation = I18n.isRTL()
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT;

        applyComponentOrientation(orientation);
        revalidate();
        repaint();
    }

    @Override
    public void onCanvasChanged() {
        updateTitle();
        canvasPanel.repaint();
    }

    @Override
    public void onToolChanged(ensa.paint.model.ToolType tool) {}

    @Override
    public void onColorsChanged(java.awt.Color primary, java.awt.Color secondary) {}

    @Override
    public void onZoomChanged(double zoom) {}

    @Override
    public void onHistoryChanged(boolean canUndo, boolean canRedo) {}

    @Override
    public void onFileStateChanged(File file, boolean modified) {
        updateTitle();
    }
}
