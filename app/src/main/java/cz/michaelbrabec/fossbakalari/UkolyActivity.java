package cz.michaelbrabec.fossbakalari;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.widget.FrameLayout;

public class UkolyActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_ukoly, contentFrameLayout);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(0).setChecked(true);

        setTitle(R.string.nav_item_ukoly); //workaround so we can have the name hardcoded to Fossaláři, so our app doesn't get renamed to Úkoly
    }
}
