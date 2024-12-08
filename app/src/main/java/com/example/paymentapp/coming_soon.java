package com.example.paymentapp;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class coming_soon extends AppCompatActivity {



    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
//        View view = inflater.inflate(R.layout.activity_coming_soon, container, false);
//        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
//
//
//
//
//        ArrayList<SlideModel> slideModels = new ArrayList<>();
//
//        slideModels.add(new SlideModel(R.drawable.a, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.b, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.c, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.d, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.e, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.f, ScaleTypes.FIT));
//
//        imageSlider.setImageList(slideModels, ScaleTypes.FIT);


        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimation);

        lottieAnimationView.playAnimation();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}