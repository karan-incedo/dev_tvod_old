package com.viewlift.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleUtils {

    /*@Retention(RetentionPolicy.SOURCE)
    @StringDef({ENGLISH, FRENCH, SPANISH})
    public @interface LocaleDef {
        public String[] SUPPORTED_LOCALES = {ENGLISH, FRENCH, SPANISH};
    }

    public static final String ENGLISH = "en";
    public static final String FRENCH = "fr";
    public static final String SPANISH = "es";

    public static void initialize(Context context) {
        setLocale(context, ENGLISH);
    }

    public static void initialize(Context context,  String defaultLanguage) {
        setLocale(context, defaultLanguage);
    }*/

    public static boolean setLocale(Context context,  String language) {
        return updateResources(context, language);
    }

    /*public static boolean setLocale(Context context, int languageIndex) {
        if (languageIndex >= LocaleDef.SUPPORTED_LOCALES.length) {
            return false;
        }

        return updateResources(context, LocaleDef.SUPPORTED_LOCALES[languageIndex]);
    }*/

    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        //Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }
}