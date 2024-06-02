package com.example.snakegame;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.parse.Parse;

import java.io.File;

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

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
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

    public void onClickShareText(View view) {
        shareText("Jogue este jogo incrível!");
    }

    public void onClickShareImage(View view) {
        shareImage();
    }

    private void shareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(shareIntent, "Compartilhar via"));
        } catch (Exception e) {
            Toast.makeText(this, "Nenhum aplicativo de compartilhamento encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage() {
        Uri imageUri = getImageUri();

        if (imageUri != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            try {
                startActivity(Intent.createChooser(shareIntent, "Compartilhar via"));
            } catch (Exception e) {
                Toast.makeText(this, "Nenhum aplicativo de compartilhamento encontrado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri() {
        File imagePath = new File(getFilesDir(), "images");
        File newFile = new File(imagePath, "image.jpg");
        return FileProvider.getUriForFile(this, "com.example.snakegame.fileprovider", newFile);
    }