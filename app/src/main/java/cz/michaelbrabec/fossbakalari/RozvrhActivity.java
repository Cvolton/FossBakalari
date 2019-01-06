package cz.michaelbrabec.fossbakalari;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.widget.FrameLayout;

public class RozvrhActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_rozvrh, contentFrameLayout);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(1).setChecked(true);
    }
}
