package com.example.bim.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bim.R;
import com.example.bim.ui.fragments.DashboardFragment;
import com.example.bim.ui.fragments.InventoryFragment;
import com.example.bim.ui.fragments.ReportsFragment;
import com.example.bim.ui.fragments.SalesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        loadFragment(new DashboardFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_inventory) {
                fragment = new InventoryFragment();
            } else if (id == R.id.nav_sales) {
                fragment = new SalesFragment();
            } else if (id == R.id.nav_reports) {
                fragment = new ReportsFragment();
            } else if (id == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
            }

            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}
