package tv.animetake.app;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by mauricio on 03/08/17.
 */

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/Lato-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build()
        );
    }
}
