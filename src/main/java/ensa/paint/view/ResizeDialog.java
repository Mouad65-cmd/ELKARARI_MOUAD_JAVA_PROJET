package ensa.paint.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import ensa.paint.i18n.I18n;
import ensa.paint.model.DrawingModel;

/**
 * Boîte de dialogue permettant de redimensionner la zone de dessin.
 */
public class ResizeDialog extends JDialog {

    private final JSpinner spnWidth;
    private final JSpinner spnHeight;
    private final JCheckBox chkMaintainRatio;
    private final JCheckBox chkScaleContent;

    private boolean confirmed = false;
    private double originalRatio;

    public ResizeDialog(Frame owner, DrawingModel model) {
        super(owner, I18n.get("dialog.resize_title"), true);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        int currentW = model.getCanvasWidth();
        int currentH = model.getCanvasHeight();
        originalRatio = (double) currentW / (double) currentH;

        JPanel contentPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        spnWidth = new JSpinner(new SpinnerNumberModel(currentW, 50, 4000, 10));
        spnHeight = new JSpinner(new SpinnerNumberModel(currentH, 50, 4000, 10));

        chkMaintainRatio = new JCheckBox("Conserver les proportions", false);
        chkScaleContent = new JCheckBox("Mettre le dessin à l'échelle", false);

        spnWidth.addChangeListener(e -> {
            if (chkMaintainRatio.isSelected()) {
                int w = (int) spnWidth.getValue();
                spnHeight.setValue((int) Math.round(w / originalRatio));
            }
        });

        spnHeight.addChangeListener(e -> {
            if (chkMaintainRatio.isSelected()) {
                int h = (int) spnHeight.getValue();
                spnWidth.setValue((int) Math.round(h * originalRatio));
            }
        });

        contentPanel.add(new JLabel(I18n.get("dialog.width")));
        contentPanel.add(spnWidth);
        contentPanel.add(new JLabel(I18n.get("dialog.height")));
        contentPanel.add(spnHeight);
        contentPanel.add(chkMaintainRatio);
        contentPanel.add(chkScaleContent);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnOk = new JButton(I18n.get("dialog.btn_ok"));
        JButton btnCancel = new JButton(I18n.get("dialog.btn_cancel"));

        btnOk.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getNewWidth() {
        return (int) spnWidth.getValue();
    }

    public int getNewHeight() {
        return (int) spnHeight.getValue();
    }

    public boolean isScaleContent() {
        return chkScaleContent.isSelected();
    }
}
