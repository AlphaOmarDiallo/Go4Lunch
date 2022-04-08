package com.alphaomardiallo.go4lunch.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupBackgroundImage();

    }

    private void setupBackgroundImage(){
        Glide.with(this)
                .load("https://images.pexels.com/photos/3184188/pexels-photo-3184188.jpeg?cs=srgb&dl=pexels-fauxels-3184188.jpg&fm=jpg")
                .into(binding.ivBackgroundLogin);
    }
}