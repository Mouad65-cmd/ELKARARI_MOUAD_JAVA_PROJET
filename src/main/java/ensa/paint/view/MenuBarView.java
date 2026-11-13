package ensa.paint.view;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import ensa.paint.i18n.I18n;
import ensa.paint.i18n.LocaleChangeListener;
import ensa.paint.model.DrawingModel;
import ensa.paint.utils.FileService;
import ensa.paint.utils.IconFactory;

/**
 * Barre de menus complète de l'application avec raccourcis clavier et support
 * du basculement dynamique de langue.
 */
public class MenuBarView extends JMenuBar implements LocaleChangeListener, DrawingModel.ModelListener {

    private final JFrame parentFrame;
    private final DrawingModel model;
    private final CanvasPanel canvasPanel;

    // Menus
    private final JMenu menuFile = new JMenu();
    private final JMenu menuEdit = new JMenu();
    private final JMenu menuImage = new JMenu();
    private final JMenu menuView = new JMenu();
    private final JMenu menuLanguage = new JMenu();
    private final JMenu menuHelp = new JMenu();

    // File items
    private final JMenuItem itemNew = new JMenuItem();
    private final JMenuItem itemOpen = new JMenuItem();
    private final JMenuItem itemSave = new JMenuItem();
    private final JMenuItem itemSaveAs = new JMenuItem();
    private final JMenuItem itemPrint = new JMenuItem();
    private final JMenuItem itemExit = new JMenuItem();

    // Edit items
    private final JMenuItem itemUndo = new JMenuItem();
    private final JMenuItem itemRedo = new JMenuItem();
    private final JMenuItem itemCut = new JMenuItem();
    private final JMenuItem itemCopy = new JMenuItem();
    private final JMenuItem itemPaste = new JMenuItem();
    private final JMenuItem itemClear = new JMenuItem();
    private final JMenuItem itemSelectAll = new JMenuItem();

    // Image items
    private final JMenuItem itemResize = new JMenuItem();
    private final JMenuItem itemRotateCW = new JMenuItem();
    private final JMenuItem itemRotateCCW = new JMenuItem();
    private final JMenuItem itemRotate180 = new JMenuItem();
    private final JMenuItem itemFlipH = new JMenuItem();
    private final JMenuItem itemFlipV = new JMenuItem();
    private final JMenuItem itemInvert = new JMenuItem();
    private final JMenuItem itemGrayscale = new JMenuItem();

    // View items
    private final JMenuItem itemZoomIn = new JMenuItem();
    private final JMenuItem itemZoomOut = new JMenuItem();
    private final JMenuItem itemZoomReset = new JMenuItem();
    private final JCheckBoxMenuItem itemGrid = new JCheckBoxMenuItem();
    private final JCheckBoxMenuItem itemRulers = new JCheckBoxMenuItem();

    // Language radio items
    private final JRadioButtonMenuItem itemLangFr = new JRadioButtonMenuItem("Français");
    private final JRadioButtonMenuItem itemLangEn = new JRadioButtonMenuItem("English");
    private final JRadioButtonMenuItem itemLangAr = new JRadioButtonMenuItem("العربية");
    private final JRadioButtonMenuItem itemLangEs = new JRadioButtonMenuItem("Español");

    // Help items
    private final JMenuItem itemShortcuts = new JMenuItem();
    private final JMenuItem itemAbout = new JMenuItem();

    public MenuBarView(JFrame parentFrame, DrawingModel model, CanvasPanel canvasPanel) {
        this.parentFrame = parentFrame;
        this.model = model;
        this.canvasPanel = canvasPanel;

        model.addListener(this);
        I18n.addListener(this);

        initShortcuts();
        initActions();
        buildMenus();
        updateMenuTexts();
    }

    private void initShortcuts() {
        int cmd = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, cmd));
        itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, cmd));
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, cmd));
        itemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, cmd | InputEvent.SHIFT_DOWN_MASK));
        itemPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, cmd));

        itemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, cmd));
        itemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, cmd));
        itemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, cmd));
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, cmd));
        itemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, cmd));
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, cmd));

        itemResize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, cmd));
        itemInvert.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, cmd));

        itemZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, cmd));
        itemZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, cmd));
        itemZoomReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, cmd));
    }

    private void initActions() {
        // Fichier
        itemNew.addActionListener(e -> {
            if (FileService.checkUnsavedChanges(parentFrame, model)) {
                model.createNewCanvas(DrawingModel.DEFAULT_WIDTH, DrawingModel.DEFAULT_HEIGHT);
                canvasPanel.updatePanelSize();
            }
        });
        itemOpen.addActionListener(e -> {
            FileService.openImage(parentFrame, model);
            canvasPanel.updatePanelSize();
        });
        itemSave.addActionListener(e -> FileService.saveImage(parentFrame, model));
        itemSaveAs.addActionListener(e -> FileService.saveImageAs(parentFrame, model));
        itemPrint.addActionListener(e -> FileService.printCanvas(parentFrame, model));
        itemExit.addActionListener(e -> {
            if (FileService.checkUnsavedChanges(parentFrame, model)) {
                System.exit(0);
            }
        });

        // Édition
        itemUndo.addActionListener(e -> model.undo());
        itemRedo.addActionListener(e -> model.redo());
        itemCut.addActionListener(e -> model.cutSelection());
        itemCopy.addActionListener(e -> model.copySelection());
        itemPaste.addActionListener(e -> model.pasteSelection());
        itemClear.addActionListener(e -> model.clearCanvas());
        itemSelectAll.addActionListener(e -> model.selectAll());

        // Image
        itemResize.addActionListener(e -> {
            ResizeDialog dlg = new ResizeDialog(parentFrame, model);
            dlg.setVisible(true);
            if (dlg.isConfirmed()) {
                model.resizeCanvas(dlg.getNewWidth(), dlg.getNewHeight(), dlg.isScaleContent());
                canvasPanel.updatePanelSize();
            }
        });
        itemRotateCW.addActionListener(e -> {
            model.rotate(90);
            canvasPanel.updatePanelSize();
        });
        itemRotateCCW.addActionListener(e -> {
            model.rotate(270);
            canvasPanel.updatePanelSize();
        });
        itemRotate180.addActionListener(e -> {
            model.rotate(180);
            canvasPanel.updatePanelSize();
        });
        itemFlipH.addActionListener(e -> model.flipHorizontal());
        itemFlipV.addActionListener(e -> model.flipVertical());
        itemInvert.addActionListener(e -> model.invertColors());
        itemGrayscale.addActionListener(e -> model.toGrayscale());

        // Affichage
        itemZoomIn.addActionListener(e -> {
            model.setZoomFactor(Math.min(8.0, model.getZoomFactor() * 1.25));
            canvasPanel.updatePanelSize();
        });
        itemZoomOut.addActionListener(e -> {
            model.setZoomFactor(Math.max(0.25, model.getZoomFactor() / 1.25));
            canvasPanel.updatePanelSize();
        });
        itemZoomReset.addActionListener(e -> {
            model.setZoomFactor(1.0);
            canvasPanel.updatePanelSize();
        });
        itemGrid.addActionListener(e -> model.setGridVisible(itemGrid.isSelected()));
        itemRulers.setSelected(model.isRulersVisible());
        itemRulers.addActionListener(e -> model.setRulersVisible(itemRulers.isSelected()));

        // Langues
        ButtonGroup bgLang = new ButtonGroup();
        bgLang.add(itemLangFr);
        bgLang.add(itemLangEn);
        bgLang.add(itemLangAr);
        bgLang.add(itemLangEs);
        itemLangFr.setSelected(true);

        itemLangFr.addActionListener(e -> I18n.setLocale(I18n.LOCALE_FR));
        itemLangEn.addActionListener(e -> I18n.setLocale(I18n.LOCALE_EN));
        itemLangAr.addActionListener(e -> I18n.setLocale(I18n.LOCALE_AR));
        itemLangEs.addActionListener(e -> I18n.setLocale(I18n.LOCALE_ES));

        // Aide
        itemShortcuts.addActionListener(e -> showShortcutsDialog());
        itemAbout.addActionListener(e -> new AboutDialog(parentFrame).setVisible(true));
    }

    private void buildMenus() {
        // Fichier
        menuFile.add(itemNew);
        menuFile.add(itemOpen);
        menuFile.addSeparator();
        menuFile.add(itemSave);
        menuFile.add(itemSaveAs);
        menuFile.addSeparator();
        menuFile.add(itemPrint);
        menuFile.addSeparator();
        menuFile.add(itemExit);

        // Édition
        menuEdit.add(itemUndo);
        menuEdit.add(itemRedo);
        menuEdit.addSeparator();
        menuEdit.add(itemCut);
        menuEdit.add(itemCopy);
        menuEdit.add(itemPaste);
        menuEdit.addSeparator();
        menuEdit.add(itemClear);
        menuEdit.add(itemSelectAll);

        // Image
        menuImage.add(itemResize);
        menuImage.addSeparator();
        menuImage.add(itemRotateCW);
        menuImage.add(itemRotateCCW);
        menuImage.add(itemRotate180);
        menuImage.addSeparator();
        menuImage.add(itemFlipH);
        menuImage.add(itemFlipV);
        menuImage.addSeparator();
        menuImage.add(itemInvert);
        menuImage.add(itemGrayscale);

        // Affichage
        menuView.add(itemZoomIn);
        menuView.add(itemZoomOut);
        menuView.add(itemZoomReset);
        menuView.addSeparator();
        menuView.add(itemGrid);
        menuView.add(itemRulers);

        // Langues
        menuLanguage.add(itemLangFr);
        menuLanguage.add(itemLangEn);
        menuLanguage.add(itemLangAr);
        menuLanguage.add(itemLangEs);

        // Aide
        menuHelp.add(itemShortcuts);
        menuHelp.addSeparator();
        menuHelp.add(itemAbout);

        add(menuFile);
        add(menuEdit);
        add(menuImage);
        add(menuView);
        add(menuLanguage);
        add(menuHelp);
    }

    public void updateMenuTexts() {
        menuFile.setText(I18n.get("menu.file"));
        itemNew.setText(I18n.get("menu.file.new"));
        itemOpen.setText(I18n.get("menu.file.open"));
        itemSave.setText(I18n.get("menu.file.save"));
        itemSaveAs.setText(I18n.get("menu.file.save_as"));
        itemPrint.setText(I18n.get("menu.file.print"));
        itemExit.setText(I18n.get("menu.file.exit"));

        menuEdit.setText(I18n.get("menu.edit"));
        itemUndo.setText(I18n.get("menu.edit.undo"));
        itemRedo.setText(I18n.get("menu.edit.redo"));
        itemCut.setText(I18n.get("menu.edit.cut"));
        itemCopy.setText(I18n.get("menu.edit.copy"));
        itemPaste.setText(I18n.get("menu.edit.paste"));
        itemClear.setText(I18n.get("menu.edit.clear"));
        itemSelectAll.setText(I18n.get("menu.edit.select_all"));

        menuImage.setText(I18n.get("menu.image"));
        itemResize.setText(I18n.get("menu.image.resize"));
        itemRotateCW.setText(I18n.get("menu.image.rotate_cw"));
        itemRotateCCW.setText(I18n.get("menu.image.rotate_ccw"));
        itemRotate180.setText(I18n.get("menu.image.rotate_180"));
        itemFlipH.setText(I18n.get("menu.image.flip_h"));
        itemFlipV.setText(I18n.get("menu.image.flip_v"));
        itemInvert.setText(I18n.get("menu.image.invert"));
        itemGrayscale.setText(I18n.get("menu.image.grayscale"));

        menuView.setText(I18n.get("menu.view"));
        itemZoomIn.setText(I18n.get("menu.view.zoom_in"));
        itemZoomOut.setText(I18n.get("menu.view.zoom_out"));
        itemZoomReset.setText(I18n.get("menu.view.zoom_reset"));
        itemGrid.setText(I18n.get("menu.view.grid"));
        itemRulers.setText(I18n.get("menu.view.rulers"));

        menuLanguage.setText(I18n.get("menu.language"));
        itemLangFr.setText(I18n.get("menu.language.fr"));
        itemLangEn.setText(I18n.get("menu.language.en"));
        itemLangAr.setText(I18n.get("menu.language.ar"));
        itemLangEs.setText(I18n.get("menu.language.es"));

        menuHelp.setText(I18n.get("menu.help"));
        itemShortcuts.setText(I18n.get("menu.help.shortcuts"));
        itemAbout.setText(I18n.get("menu.help.about"));

        itemUndo.setEnabled(model.canUndo());
        itemRedo.setEnabled(model.canRedo());
    }

    private void showShortcutsDialog() {
        String msg = "• Ctrl + N : " + I18n.get("menu.file.new") + "\n" +
                     "• Ctrl + O : " + I18n.get("menu.file.open") + "\n" +
                     "• Ctrl + S : " + I18n.get("menu.file.save") + "\n" +
                     "• Ctrl + Shift + S : " + I18n.get("menu.file.save_as") + "\n" +
                     "• Ctrl + P : " + I18n.get("menu.file.print") + "\n" +
                     "• Ctrl + Z : " + I18n.get("menu.edit.undo") + "\n" +
                     "• Ctrl + Y : " + I18n.get("menu.edit.redo") + "\n" +
                     "• Ctrl + X / C / V : " + I18n.get("menu.edit.cut") + " / " + I18n.get("menu.edit.copy") + " / " + I18n.get("menu.edit.paste") + "\n" +
                     "• Ctrl + A : " + I18n.get("menu.edit.select_all") + "\n" +
                     "• Ctrl + R : " + I18n.get("menu.image.resize") + "\n" +
                     "• Ctrl + I : " + I18n.get("menu.image.invert") + "\n" +
                     "• Ctrl + + / - / 0 : " + I18n.get("toolbar.zoom") + "\n" +
                     "• Ctrl + Molette : " + I18n.get("toolbar.zoom");

        JOptionPane.showMessageDialog(parentFrame, msg, I18n.get("menu.help.shortcuts"), JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onLocaleChange() {
        updateMenuTexts();
    }

    @Override
    public void onHistoryChanged(boolean canUndo, boolean canRedo) {
        itemUndo.setEnabled(canUndo);
        itemRedo.setEnabled(canRedo);
    }

    @Override
    public void onCanvasChanged() {}
    @Override
    public void onToolChanged(ensa.paint.model.ToolType tool) {}
    @Override
    public void onColorsChanged(java.awt.Color primary, java.awt.Color secondary) {}
    @Override
    public void onZoomChanged(double zoom) {}
    @Override
    public void onFileStateChanged(java.io.File file, boolean modified) {}
}
