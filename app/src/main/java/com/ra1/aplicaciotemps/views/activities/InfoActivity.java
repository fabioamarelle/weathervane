package com.ra1.aplicaciotemps.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ra1.aplicaciotemps.R;

import java.util.Locale;
import java.util.Random;

public class InfoActivity extends BaseActivity {

    private ImageButton languageButton, backButton;
    private TextView tvC, tvF;
    private ConstraintLayout rootLayout;
    private String currentLang, currentUnit;
    private final Handler rainHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_information);

        languageButton = findViewById(R.id.language_button);
        backButton = findViewById(R.id.back_button);
        tvC = findViewById(R.id.tv_celsius);
        tvF = findViewById(R.id.tv_fahrenheit);
        rootLayout = findViewById(R.id.info_root_layout);
        LinearLayout toggleContainer = findViewById(R.id.unit_toggle_container);

        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        currentLang = prefs.getString("language", Locale.getDefault().getLanguage());
        currentUnit = prefs.getString("temp_unit", "metric");

        toggleContainer.setBackground(getRoundedDrawable(Color.parseColor("#33FFFFFF"), 100, Color.parseColor("#80FFFFFF"), 3));

        updateFlagIcon();
        updateToggleUI();

        rootLayout.post(() -> {
            preWarmRain();
            startRainEffect();
        });

        backButton.setOnClickListener(v -> finish());

        languageButton.setOnClickListener(v -> {
            if (currentLang.equals("ca")) currentLang = "es";
            else if (currentLang.equals("es")) currentLang = "en";
            else currentLang = "ca";
            prefs.edit().putString("language", currentLang).apply();
            recreate();
        });

        toggleContainer.setOnClickListener(v -> {
            currentUnit = currentUnit.equals("metric") ? "imperial" : "metric";
            prefs.edit().putString("temp_unit", currentUnit).apply();
            updateToggleUI();
        });
    }

    private void preWarmRain() {
        for (int i = 0; i < 20; i++) {
            createVerticalRainDrop(true);
        }
    }

    private void startRainEffect() {
        rainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createVerticalRainDrop(false);
                rainHandler.postDelayed(this, 40);
            }
        }, 40);
    }

    private void createVerticalRainDrop(boolean isWarmUp) {
        if (rootLayout.getWidth() <= 0) return;

        final View drop = new View(this);
        int width = 5;
        int height = random.nextInt(60) + 60;

        drop.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        drop.setBackgroundColor(Color.parseColor("#8CFFFFFF"));

        drop.setX(random.nextInt(rootLayout.getWidth()));

        if (isWarmUp) {
            drop.setY(random.nextInt(rootLayout.getHeight()));
            drop.setAlpha(random.nextFloat());
        } else {
            drop.setY(-height);
            drop.setAlpha(0f);
        }

        rootLayout.addView(drop);

        int duration = random.nextInt(400) + 700;

        drop.animate()
                .alpha(1f)
                .translationY(rootLayout.getHeight() + height)
                .setDuration(duration)
                .withEndAction(() -> rootLayout.removeView(drop))
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rainHandler.removeCallbacksAndMessages(null);
    }

    private void updateFlagIcon() {
        switch (currentLang) {
            case "es": languageButton.setImageResource(R.drawable.icon_flag_es); break;
            case "en": languageButton.setImageResource(R.drawable.icon_flag_en); break;
            default: languageButton.setImageResource(R.drawable.icon_flag_ca); break;
        }
    }

    private void updateToggleUI() {
        if (currentUnit.equals("metric")) {
            tvC.setBackground(getRoundedDrawable(Color.WHITE, 100, 0, 0));
            tvC.setTextColor(Color.parseColor("#696995"));
            tvF.setBackground(null);
            tvF.setTextColor(Color.WHITE);
        } else {
            tvF.setBackground(getRoundedDrawable(Color.WHITE, 100, 0, 0));
            tvF.setTextColor(Color.parseColor("#696995"));
            tvC.setBackground(null);
            tvC.setTextColor(Color.WHITE);
        }
    }

    private GradientDrawable getRoundedDrawable(int color, float radius, int strokeColor, int strokeWidth) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(radius);
        shape.setColor(color);
        if (strokeWidth > 0) shape.setStroke(strokeWidth, strokeColor);
        return shape;
    }
}