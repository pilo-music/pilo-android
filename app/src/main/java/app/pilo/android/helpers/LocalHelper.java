package app.pilo.android.helpers;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocalHelper {

    public static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        UserSharedPrefManager userSharedPrefManager = new UserSharedPrefManager(context);
        userSharedPrefManager.setLocal(language);

        return context.createConfigurationContext(configuration);
    }
}
