package com.toufic.myshows.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

public class Utils {

    /**
     * A helper method to run animation programmatically ona recycler view
     *
     * @param recyclerView recyclerView to run the animation on
     * @param animation    animation resource id to be used
     */
    public static void runLayoutAnimation(final RecyclerView recyclerView, int animation) {
        if (recyclerView == null || recyclerView.getAdapter() == null) {
            return;
        }

        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, animation);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        if (!recyclerView.isAnimating()) {
            recyclerView.scheduleLayoutAnimation();
        }
    }

    /**
     * A helper method that changes http to https sequence in a string
     *
     * @param url String url
     * @return a new string that starts with https instead of http
     */
    public static String changeHttpToHttps(String url) {
        if (url == null) {
            return "";
        }
        if (url.startsWith("http://")) {
            return "https://".concat(url.substring(7));
        }
        return url;
    }

    /**
     * Utility method to show a toast msg with long duration
     *
     * @param context Context
     * @param msg     message title
     */
    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Utility method to allow a textview to expand and show mpore details by relying
     * on animation. Once clicked the TextView will expand and collapse on the next click.
     *
     * @param tv TextView t animate
     */
    public static void cycleTextViewExpansion(TextView tv) {
        int collapsedMaxLines = 1;
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                tv.getLineCount() == collapsedMaxLines ? 15 : 1);
        animation.setDuration(200).start();
    }
}
