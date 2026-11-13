package ensa.paint.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import ensa.paint.i18n.I18n;
import ensa.paint.utils.IconFactory;

/**
 * Boîte de dialogue "À propos" présentant le projet universitaire ENSA Safi.
 */
public class AboutDialog extends JDialog {

    public AboutDialog(Frame owner) {
        super(owner, I18n.get("about.title"), true);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        content.setBackground(Color.WHITE);

        // En-tête avec logo et titre
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        headerPanel.setOpaque(false);

        JLabel lblLogo = new JLabel(new ImageIcon(IconFactory.createAppLogo(64)));
        headerPanel.add(lblLogo);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Paint ENSA Safi");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(24, 76, 120));

        JLabel lblUni = new JLabel("École Nationale des Sciences Appliquées - Safi");
        lblUni.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblUni.setForeground(new Color(90, 95, 105));

        JLabel lblSub = new JLabel("Université Cadi Ayyad Marrakech");
        lblSub.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblSub.setForeground(new Color(120, 125, 135));

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(lblUni);
        titlePanel.add(lblSub);
        headerPanel.add(titlePanel);

        content.add(headerPanel);
        content.add(Box.createVerticalStrut(15));
        content.add(new JSeparator());
        content.add(Box.createVerticalStrut(15));

        // Description du projet
        JLabel lblDescTitle = new JLabel(I18n.get("about.project"));
        lblDescTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        content.add(lblDescTitle);
        content.add(Box.createVerticalStrut(6));

        JTextArea txtDesc = new JTextArea(I18n.get("about.description"));
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDesc.setEditable(false);
        txtDesc.setOpaque(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        content.add(txtDesc);

        content.add(Box.createVerticalStrut(12));

        // Membres du groupe (3 à 4 personnes)
        JLabel lblAuthors = new JLabel(I18n.get("about.authors"));
        lblAuthors.setFont(new Font("SansSerif", Font.BOLD, 12));
        content.add(lblAuthors);
        content.add(Box.createVerticalStrut(4));

        JTextArea txtMembers = new JTextArea(I18n.get("about.group_members"));
        txtMembers.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtMembers.setEditable(false);
        txtMembers.setOpaque(false);
        content.add(txtMembers);

        content.add(Box.createVerticalStrut(10));

        JLabel lblVersion = new JLabel(I18n.get("about.version"));
        lblVersion.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblVersion.setForeground(Color.GRAY);
        content.add(lblVersion);

        // Bouton Fermer
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton(I18n.get("dialog.btn_ok"));
        btnClose.setPreferredSize(new Dimension(90, 28));
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);

        add(content, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }
}
