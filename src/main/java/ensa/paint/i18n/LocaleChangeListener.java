package ensa.paint.i18n;

/**
 * Interface pour les composants écoutant les changements de langue.
 */
public interface LocaleChangeListener {
    /**
     * Appelé lorsque la langue courante de l'application est modifiée.
     */
    void onLocaleChange();
}
