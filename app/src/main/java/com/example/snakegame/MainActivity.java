package com.example.snakegame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickMenu(View view) {
        Intent intent;
        if (view.getId() == R.id.textView3) {
            intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.textView2) {
            intent = new Intent(this, RankingActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.textView4) {
            intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.textView5) {
            finishAffinity();
        }
    }

}