package com.ma.kissairaproject;

import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ma.kissairaproject.utilities.Constants;
import com.ma.kissairaproject.utilities.Preferences;

public class SplashScreen extends AppCompatActivity {
    ImageView iv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        iv=findViewById(R.id.image);
        Drawable d= iv.getDrawable();
        if (d instanceof AnimatedVectorDrawable){
            AnimatedVectorDrawable avd =(AnimatedVectorDrawable) d;
            avd.start();
            avd.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    Intent intent;

                    if(!Preferences.getValue(SplashScreen.this, Constants.EMAIL, "").equals("") &&!Preferences.getValue(SplashScreen.this, Constants.PASSWORD, "").equals("") ){
                        intent = new Intent(getApplicationContext(),MainActivity.class);
                    }else{
                        intent = new Intent(getApplicationContext(),LoginActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }else if (d instanceof AnimatedVectorDrawableCompat){
            AnimatedVectorDrawableCompat avd =(AnimatedVectorDrawableCompat) d;
            avd.start();
            avd.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    Intent intent;
                    if(!Preferences.getValue(SplashScreen.this, Constants.EMAIL, "").equals("") &&!Preferences.getValue(SplashScreen.this, Constants.PASSWORD, "").equals("") ){
                        intent = new Intent(getApplicationContext(),MainActivity.class);
                    }else{
                        intent = new Intent(getApplicationContext(),LoginActivity.class);
                    }
                    startActivity(intent);
                }
            });

        }





    }

    public void animate(View view) {

    }
}