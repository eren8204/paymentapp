package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar); // Ensure your toolbar ID matches
        setSupportActionBar(toolbar);

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up the ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);  // Disable default indicator
        toggle.setHomeAsUpIndicator(R.drawable.baseline_person_24);  // Set your custom icon
        toggle.syncState();

        // Handle custom icon click
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        // Set up Navigation Drawer item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId(); // Get the item ID

            if (id == R.id.drawer_item1) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.drawer_item2) {
                selectedFragment = new BusinessFragment();
            } else if (id == R.id.drawer_item3) {
                selectedFragment = new EducationFragment();
            } else if (id == R.id.drawer_item4) {
                selectedFragment = new MoreFragment();
            }

            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, selectedFragment);
                transaction.commit();
                drawerLayout.closeDrawers(); // Close the drawer after selection
            }

            return true; // Indicate that the event was handled
        });

        // Initialize Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.my_business) {
                selectedFragment = new BusinessFragment();
            } else if (item.getItemId() == R.id.education) {
                selectedFragment = new EducationFragment();
            } else if (item.getItemId() == R.id.more) {
                selectedFragment = new MoreFragment();
            } else {
                selectedFragment = new HomeFragment();
            }
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, selectedFragment);
                transaction.commit();
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Optionally load a default fragment when the app starts
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.drawer_item1); // Set a default selected item
            Fragment defaultFragment = new HomeFragment(); // Default fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, defaultFragment);
            transaction.commit();
        }

        // Access and set up the header view
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            ImageView headerImageView = headerView.findViewById(R.id.imageView);
            TextView headerUserName = headerView.findViewById(R.id.userName);
            TextView headerUserEmail = headerView.findViewById(R.id.userEmail);

            // Set the user details (example)
            headerImageView.setImageResource(R.drawable.baseline_person_24);
            headerUserName.setText("User Name");
            headerUserEmail.setText("user@example.com");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
