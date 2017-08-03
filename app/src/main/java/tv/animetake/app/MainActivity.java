package tv.animetake.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import tv.animetake.app.fragment.AnimeFavoriteListFragment;
import tv.animetake.app.fragment.AnimeHistoricListFragment;
import tv.animetake.app.fragment.AnimeListFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fm = getSupportFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_historic:
                    fm.beginTransaction()
                        .replace(R.id.content, new AnimeHistoricListFragment())
                        .commit();
                    return true;
                case R.id.navigation_home:
                    fm.beginTransaction()
                            .replace(R.id.content, new AnimeListFragment())
                            .commit();
                    return true;
                case R.id.navigation_favorites:
                    fm.beginTransaction()
                        .replace(R.id.content, new AnimeFavoriteListFragment())
                        .commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fm.beginTransaction()
                .replace(R.id.content, new AnimeListFragment())
                .commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
