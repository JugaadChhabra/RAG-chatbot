package com.example.ragchatbot;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class HandleSwipeAndDrawers extends GestureDetector.SimpleOnGestureListener {

    private final DrawerLayout drawerLayout;

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private boolean isDrawerFullyClosed = true;


    public HandleSwipeAndDrawers(DrawerLayout drawerLayout)
    {
        this.drawerLayout = drawerLayout;

        this.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                isDrawerFullyClosed = slideOffset == 0;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerFullyClosed = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerFullyClosed = true;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        });
    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY))
        {
            boolean isSwipeThresholdReached = Math.abs(diffX) > SWIPE_THRESHOLD;
            boolean isSwipeVelocityThresholdReached = Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD;

            if (isSwipeThresholdReached && isSwipeVelocityThresholdReached && isDrawerFullyClosed)
            {
                if (diffX > 0) {
//                    drawerLayout.openDrawer(GravityCompat.START);
//                    openLeftDrawer();
                    Log.d("test","no left drawer");
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
//                    openRightDrawer();
                }
            }
            return true;
        }
        return false;
    }
}
