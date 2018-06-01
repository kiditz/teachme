package com.slerpio.teachme.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by kiditz on 03/03/18.
 */

public class ViewUtils {
    public static void setTextTo(TextView textView, CharSequence text){
        CharSequence tmp = text == null ? "" : text;
        textView.setText(tmp);
    }
    public static int dpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    public static int spToPx(int dp, Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, r.getDisplayMetrics()));
    }
    public static void setViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public static void setViewDrawableColor(Menu menu, int color) {
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }


    @NonNull
    public static Snackbar makeSnackbar(@NonNull View layout, @NonNull CharSequence text, int duration) {
        Snackbar snackBarView = Snackbar.make(layout, text, duration);
        View view = snackBarView.getView();
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.BOTTOM;
        } else {
            ((FrameLayout.LayoutParams) params).gravity = Gravity.BOTTOM;
        }
        view.setLayoutParams(params);
        TextView tv = snackBarView.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        return snackBarView;
    }


    public static AlertDialog.Builder showMessage(Context context, CharSequence title, CharSequence message, MessageButton button) {
        return showMessage(context, title, message, false, button);
    }

    public static AlertDialog.Builder showMessage(Context context, CharSequence title, CharSequence message, boolean showCancel, MessageButton button) {
        return showMessage(context, null, title, message, showCancel, button);
    }

    public static AlertDialog.Builder showMessage(Context context, View view, CharSequence title, CharSequence message, boolean showCancel, MessageButton button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(!StringUtils.isEmpty(title)){
            builder.setTitle(title);
        }
        if (!StringUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (view != null) {
            builder.setView(view);
        }
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (button != null)
                button.ok(dialog, which);
        });
        if (showCancel) {
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                if (button != null)
                    button.cancel(dialog, which);
            });
        }
        builder.show();
        return builder;
    }

    public static abstract class MessageButton {
        public abstract void ok(DialogInterface dialog, int which);

        public abstract void cancel(DialogInterface dialog, int which);
    }

    public static class MessageButtonHandle extends MessageButton {
        @Override
        public void ok(DialogInterface dialog, int which) {

        }

        @Override
        public void cancel(DialogInterface dialog, int which) {

        }
    }
}
