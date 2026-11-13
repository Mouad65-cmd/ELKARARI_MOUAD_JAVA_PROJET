package ensa.paint.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Gestionnaire d'internationalisation (i18n) pour l'application Paint ENSA.
 * Implémente le pattern Singleton / Observer pour notifier tous les composants
 * de l'interface lors d'un changement de langue dynamique.
 */
public class I18n {

    public static final Locale LOCALE_FR = Locale.FRENCH;
    public static final Locale LOCALE_EN = Locale.ENGLISH;
    public static final Locale LOCALE_AR = Locale.forLanguageTag("ar");
    public static final Locale LOCALE_ES = Locale.forLanguageTag("es");

    private static Locale currentLocale = LOCALE_FR;
    private static ResourceBundle bundle;
    private static final List<LocaleChangeListener> listeners = new ArrayList<>();

    static {
        loadBundle();
    }

    private static void loadBundle() {
        try {
            bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
        } catch (MissingResourceException e) {
            System.err.println("Bundle i18n introuvable pour la locale : " + currentLocale + ", repli sur FR.");
            try {
                bundle = ResourceBundle.getBundle("i18n.messages", LOCALE_FR);
            } catch (Exception ex) {
                bundle = null;
            }
        }
    }

    /**
     * Définit la nouvelle locale et avertit tous les écouteurs enregistrés.
     */
    public static void setLocale(Locale newLocale) {
        if (newLocale != null && !newLocale.equals(currentLocale)) {
            currentLocale = newLocale;
            loadBundle();
            notifyListeners();
        }
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static boolean isRTL() {
        return "ar".equalsIgnoreCase(currentLocale.getLanguage());
    }

    /**
     * Récupère la chaîne traduite associée à la clé donnée.
     */
    public static String get(String key) {
        if (bundle == null) return key;
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    /**
     * Récupère une chaîne traduite avec arguments formatés.
     */
    public static String get(String key, Object... args) {
        String pattern = get(key);
        try {
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return pattern;
        }
    }

    public static void addListener(LocaleChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (LocaleChangeListener l : new ArrayList<>(listeners)) {
            try {
                l.onLocaleChange();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
