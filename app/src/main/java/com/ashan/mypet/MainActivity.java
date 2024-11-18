package com.ashan.mypet;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;  // Import Handler
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        // Make the status bar transparent and extend content behind it
        Window window = getWindow();
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Makes icons and text dark
        );

        // Set light system bar icons (if required)
        WindowInsetsController controller = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            controller = window.getInsetsController();
        }
        if (controller != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        }

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.top_nav_toolbar);
        setSupportActionBar(toolbar);

        // Setup NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found in layout");
        }
        NavController navController = navHostFragment.getNavController();

        // Set up AppBarConfiguration
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_buy_sell, R.id.nav_medical, R.id.nav_profile)
                .build();

        // Link NavController with Toolbar for dynamic title updates
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Handle WindowInsets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            bottomNavigationView.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        // Handle bell icon click
        findViewById(R.id.bell_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom popup layout
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_notification, null);

                // Create the PopupWindow
                PopupWindow popupWindow = new PopupWindow(popupView,
                        WindowManager.LayoutParams.WRAP_CONTENT, // Use wrap_content for width
                        WindowManager.LayoutParams.WRAP_CONTENT);

                // Show the popup at the top-center of the screen
                popupWindow.showAtLocation(v, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);

                // Dismiss the popup when user taps anywhere outside
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);

                // Optional: Dismiss the popup when clicking outside
                popupView.setOnTouchListener((view, event) -> {
                    popupWindow.dismiss();
                    return false;
                });

                // Automatically dismiss the popup after 3 seconds (3000 milliseconds)
                new Handler().postDelayed(popupWindow::dismiss, 2500);
            }
        });
    }
}
