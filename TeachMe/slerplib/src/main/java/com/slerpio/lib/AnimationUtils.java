package com.slerpio.lib;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationUtils {

    public static Animation slideUp(final View view, int duration) {
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                view.getHeight(),
                0);
        animate.setDuration(duration);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        return animate;
    }

    public static Animation slideDown(final View view, int duration) {
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                view.getHeight());
        animate.setDuration(duration);
        animate.setFillAfter(false);
        view.startAnimation(animate);
        return animate;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
