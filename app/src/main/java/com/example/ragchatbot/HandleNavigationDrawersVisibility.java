package com.example.ragchatbot;

import android.widget.ImageView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HandleNavigationDrawersVisibility {
    private ImageView rightNavigationDrawerIcon;
    private NavigationView rightNavigationView;
    private DrawerLayout drawerLayout;

    public HandleNavigationDrawersVisibility(ImageView rightNavigationDrawerIcon, NavigationView rightNavigationView, DrawerLayout drawerLayout) {
        this.rightNavigationDrawerIcon = rightNavigationDrawerIcon;
        this.rightNavigationView = rightNavigationView;
        this.drawerLayout = drawerLayout;
    }

    public void setNavigationDrawerListeners()
    {
        this.rightNavigationDrawerIcon.setOnClickListener(v -> {
            if (this.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                this.drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                this.drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }
}
