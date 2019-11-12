package com.ma.kissairaproject;

import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class Slogan extends AppCompatActivity {
    ImageView iv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slogan);
        iv=findViewById(R.id.image);
        Drawable d= iv.getDrawable();
        if (d instanceof AnimatedVectorDrawable){
            AnimatedVectorDrawable avd =(AnimatedVectorDrawable) d;
            avd.start();
            avd.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            });
        }else if (d instanceof AnimatedVectorDrawableCompat){
            AnimatedVectorDrawableCompat avd =(AnimatedVectorDrawableCompat) d;
            avd.start();
            avd.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            });

        }



    }

    public void animate(View view) {

    }
}