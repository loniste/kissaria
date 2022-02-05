package com.hakik.record_view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;


public class ScaleAnim {
    private View view;
    public ScaleAnim(View view) {
        this.view = view;
    }


    void start() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 2.0f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 2.0f);
        set.setDuration(150);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(scaleY, scaleX);
        set.start();
    }
    float mAmplitude=0.2f;
    float mFrequency=10f;
    void stop() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f);
//        scaleY.setDuration(250);
//        scaleY.setInterpolator(new DecelerateInterpolator());


        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f);
//        scaleX.setDuration(250);
//        scaleX.setInterpolator(new DecelerateInterpolator());


        set.setDuration(150);
        set.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float x) {
                return (float) ((-1*Math.exp(-x/mAmplitude)*Math.cos(mFrequency*x)+1)/(-1*Math.exp(-1/mAmplitude)*Math.cos(mFrequency*1)+1));

            }
        });
        set.playTogether(scaleY, scaleX);
        set.start();
    }
}