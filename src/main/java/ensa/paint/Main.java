package ensa.paint;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ensa.paint.view.MainWindow;

/**
 * Point d'entrée principal de l'application Paint ENSA Safi.
 * Projet de Dessin Internationalisé - ENSA Safi / Université Cadi Ayyad.
 */
public class Main {

    public static void main(String[] args) {
        // Gestionnaire d'erreurs global pour afficher toute exception éventuelle
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Erreur : " + e.getMessage() + "\n" + e.getClass().getName(),
                    "Erreur d'exécution", javax.swing.JOptionPane.ERROR_MESSAGE);
        });

        // Activation du lissage des polices système
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Utilise le Look & Feel natif de la plateforme
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Repli sur le look & feel Swing standard
            }

            MainWindow window = new MainWindow();
            window.setVisible(true);
            window.toFront();
            window.requestFocus();
            window.setAlwaysOnTop(true);
            window.setAlwaysOnTop(false);
        });
    }
}
