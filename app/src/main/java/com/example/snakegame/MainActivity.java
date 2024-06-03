package com.example.snakegame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.parse.Parse;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

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

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());

        shareText();
    }

    public void onClickMenu(View view) {
        Intent intent;
        if (view.getId() == R.id.textView3) {
            intent = new Intent(this, Game.class);
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

    private void shareText() {
        ImageButton shareButton = findViewById(R.id.imageButtonShare);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Olá! Olha esse novo jogo da cobrinha que descobri:\n\n https://play.google.com/store/apps/details?id=com.amelosinteractive.snake&hl=en&pli=1");
                try {
                    startActivity(Intent.createChooser(shareIntent, "Compartilhar via"));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Nenhum aplicativo de compartilhamento encontrado.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}