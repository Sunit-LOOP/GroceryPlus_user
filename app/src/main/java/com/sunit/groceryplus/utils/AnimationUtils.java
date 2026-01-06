package com.sunit.groceryplus.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;

/** Utility class providing a centralized collection of UI animations and transitions. */
public class AnimationUtils {

    /** Animates a view to fade in from invisible to visible. */
    public static void fadeIn(View view, long duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /** Animates a view to fade out from visible to gone. */
    public static void fadeOut(View view, long duration) {
        view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    /** Scales up a view with a pop-in effect. */
    public static void scaleUp(View view, long duration) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(duration)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    /** Scales down a view with a pop-out effect and hides it. */
    public static void scaleDown(View view, long duration) {
        view.animate()
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    /** Slides a view up into position from the bottom. */
    public static void slideUp(View view, long duration) {
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationY(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /** Slides a view down into position from the top. */
    public static void slideDown(View view, long duration) {
        view.setTranslationY(-view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationY(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /** Applies a scaling pulse animation to a view. */
    public static void pulse(View view, long duration) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    /** Starts a repeated shimmer alpha animation on a view. */
    public static void startShimmerAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0.3f, 1f, 0.3f);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    /** Stops any shimmer animation and resets alpha. */
    public static void stopShimmerAnimation(View view) {
        view.clearAnimation();
        view.setAlpha(1f);
    }

    /** Sets a slide-in layout animation for RecyclerView items. */
    public static void setRecyclerViewAnimation(RecyclerView recyclerView) {
        android.view.animation.Animation animation = android.view.animation.AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.slide_in_from_bottom);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setDelay(0.1f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        recyclerView.setLayoutAnimation(controller);
    }

    /** Simulates a button press effect by scaling the view down and then back up. */
    public static void buttonPressAnimation(View view) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();
    }

    /** Applies a success-themed animation with scaling and alpha fade-in. */
    public static void successAnimation(View view) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        
        AnimatorSet animatorSet = new AnimatorSet();
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.2f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(600);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    /** Applies a horizontal shake animation usually indicating an error. */
    public static void shakeAnimation(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0, -25, 25, -25, 25, -15, 15, -5, 5, 0);
        shake.setDuration(500);
        shake.start();
    }

    /** Makes a ProgressBar visible and sets it to indeterminate mode. */
    public static void startLoadingAnimation(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
    }

    /** Hides a ProgressBar and stops its indeterminate state. */
    public static void stopLoadingAnimation(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminate(false);
    }

    /** Applies a character-by-character typewriter effect to a TextView. */
    public static void typewriterEffect(TextView textView, String text, long delay) {
        textView.setText("");
        textView.setVisibility(View.VISIBLE);
        
        ValueAnimator animator = ValueAnimator.ofInt(0, text.length());
        animator.setDuration(text.length() * delay);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        animator.addUpdateListener(animation -> {
            int currentIndex = (int) animation.getAnimatedValue();
            textView.setText(text.substring(0, currentIndex));
        });
        
        animator.start();
    }

    /** Executes a 3D card flip animation between two views. */
    public static void flipCard(View frontView, View backView, long duration) {
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(frontView, "rotationY", 0f, 90f);
        flipOut.setDuration(duration / 2);
        
        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setVisibility(View.GONE);
                backView.setVisibility(View.VISIBLE);
                
                ObjectAnimator flipIn = ObjectAnimator.ofFloat(backView, "rotationY", -90f, 0f);
                flipIn.setDuration(duration / 2);
                flipIn.start();
            }
        });
        
        flipOut.start();
    }

    /** Applies a Material Design circular reveal animation to a view. */
    public static void circularReveal(View view, int centerX, int centerY, float startRadius, float endRadius, long duration) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius)
                    .setDuration(duration)
                    .start();
        } else {
            fadeIn(view, duration);
        }
    }

    /** Applies a staggered slide-up and fade-in animation to an array of views. */
    public static void staggeredAnimation(View[] views, long delayBetweenViews) {
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setAlpha(0f);
            view.setTranslationY(50f);
            
            long delay = i * delayBetweenViews;
            
            view.postDelayed(() -> {
                view.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            }, delay);
        }
    }

    /** Applies a vertical bounce animation to a view. */
    public static void bounce(View view, long duration) {
        ObjectAnimator bounce = ObjectAnimator.ofFloat(view, "translationY", 0, -30, 0);
        bounce.setDuration(duration);
        bounce.setInterpolator(new AccelerateDecelerateInterpolator());
        bounce.start();
    }

    /** Rotates a view between specified degrees over a duration. */
    public static void rotate(View view, long duration, float fromDegrees, float toDegrees) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", fromDegrees, toDegrees);
        rotate.setDuration(duration);
        rotate.start();
    }

    /** Starts an infinite continuous rotation animation on a view. */
    public static void startContinuousRotation(View view, long duration) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        rotate.setDuration(duration);
        rotate.setRepeatCount(ValueAnimator.INFINITE);
        rotate.setRepeatMode(ValueAnimator.RESTART);
        rotate.start();
    }

    /** Stops any continuous rotation and resets the view's rotation. */
    public static void stopContinuousRotation(View view) {
        view.clearAnimation();
        view.setRotation(0f);
    }

    /** Applies an elastic scaling animation effect to a view. */
    public static void elasticScale(View view, long duration) {
        AnimatorSet animatorSet = new AnimatorSet();
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f, 0.8f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f, 0.8f, 1.1f, 1f);
        
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    /** Slides a view into position from the left. */
    public static void slideInFromLeft(View view, long duration) {
        view.setTranslationX(-view.getWidth());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationX(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /** Slides a view into position from the right. */
    public static void slideInFromRight(View view, long duration) {
        view.setTranslationX(view.getWidth());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationX(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /** Slides a view into position from the bottom. */
    public static void slideInFromBottom(View view, long duration) {
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationY(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /** Executes a crossfade transition between two views. */
    public static void crossFade(View viewOut, View viewIn, long duration) {
        viewOut.animate()
                .alpha(0f)
                .setDuration(duration / 2)
                .withEndAction(() -> {
                    viewOut.setVisibility(View.GONE);
                    viewIn.setAlpha(0f);
                    viewIn.setVisibility(View.VISIBLE);
                    viewIn.animate()
                            .alpha(1f)
                            .setDuration(duration / 2)
                            .start();
                })
                .start();
    }
}
