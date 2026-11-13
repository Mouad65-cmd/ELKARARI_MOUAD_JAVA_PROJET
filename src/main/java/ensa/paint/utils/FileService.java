package ensa.paint.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import ensa.paint.i18n.I18n;
import ensa.paint.model.DrawingModel;

/**
 * Service pour la gestion des fichiers (Ouvrir, Enregistrer, Exporter, Imprimer).
 */
public class FileService {

    /**
     * Ouvre une boîte de dialogue pour ouvrir un fichier image.
     */
    public static void openImage(Component parent, DrawingModel model) {
        if (!checkUnsavedChanges(parent, model)) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(I18n.get("menu.file.open"));
        chooser.setFileFilter(new FileNameExtensionFilter("Images (*.png, *.jpg, *.jpeg, *.bmp, *.gif)",
                "png", "jpg", "jpeg", "bmp", "gif"));

        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedFile);
                if (img != null) {
                    model.setImage(img, selectedFile);
                } else {
                    JOptionPane.showMessageDialog(parent, I18n.get("dialog.open_error"),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, I18n.get("dialog.open_error") + "\n" + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Enregistre l'image dans le fichier courant ou ouvre "Enregistrer sous" si nouveau fichier.
     */
    public static boolean saveImage(Component parent, DrawingModel model) {
        if (model.getCurrentFile() == null) {
            return saveImageAs(parent, model);
        }
        return doSave(parent, model, model.getCurrentFile());
    }

    /**
     * Ouvre une boîte de dialogue "Enregistrer sous".
     */
    public static boolean saveImageAs(Component parent, DrawingModel model) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(I18n.get("menu.file.save_as"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG (*.jpg)", "jpg"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP (*.bmp)", "bmp"));
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String name = file.getName().toLowerCase();
            String format = "png";

            if (chooser.getFileFilter() instanceof FileNameExtensionFilter) {
                FileNameExtensionFilter filter = (FileNameExtensionFilter) chooser.getFileFilter();
                format = filter.getExtensions()[0];
            }

            if (!name.endsWith("." + format)) {
                file = new File(file.getParentFile(), file.getName() + "." + format);
            }

            boolean ok = doSave(parent, model, file);
            if (ok) {
                model.setCurrentFile(file);
            }
            return ok;
        }
        return false;
    }

    private static boolean doSave(Component parent, DrawingModel model, File file) {
        try {
            model.commitSelection();
            String name = file.getName();
            int dot = name.lastIndexOf('.');
            String ext = (dot > 0) ? name.substring(dot + 1).toLowerCase() : "png";

            BufferedImage toWrite = model.getCanvasImage();
            // Pour JPG ou BMP, s'assurer qu'il n'y a pas d'alpha
            if ("jpg".equals(ext) || "jpeg".equals(ext) || "bmp".equals(ext)) {
                BufferedImage rgbImg = new BufferedImage(toWrite.getWidth(), toWrite.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = rgbImg.createGraphics();
                g2.drawImage(toWrite, 0, 0, null);
                g2.dispose();
                toWrite = rgbImg;
            }

            ImageIO.write(toWrite, ext, file);
            model.setModified(false);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, I18n.get("dialog.save_error") + "\n" + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Vérifie si le document a été modifié et propose à l'utilisateur d'enregistrer.
     * @return true si l'action peut continuer, false si l'utilisateur a annulé.
     */
    public static boolean checkUnsavedChanges(Component parent, DrawingModel model) {
        if (!model.isModified()) return true;

        int choice = JOptionPane.showConfirmDialog(
                parent,
                I18n.get("dialog.save_prompt"),
                I18n.get("dialog.save_title"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            return saveImage(parent, model);
        } else if (choice == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Imprime le canvas sur l'imprimante système.
     */
    public static void printCanvas(Component parent, DrawingModel model) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Paint ENSA Document");

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                BufferedImage img = model.getCanvasImage();
                double pw = pageFormat.getImageableWidth();
                double ph = pageFormat.getImageableHeight();
                double iw = img.getWidth();
                double ih = img.getHeight();

                double scale = Math.min(pw / iw, ph / ih);
                g2d.scale(scale, scale);
                g2d.drawImage(img, 0, 0, null);

                return PAGE_EXISTS;
            }
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(parent, "Erreur d'impression : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
