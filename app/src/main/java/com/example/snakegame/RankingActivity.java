package com.example.snakegame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RankingActivity extends AppCompatActivity {

    private ListView rankingListView;
    private ArrayAdapter<String> adapter;
    private List<String> rankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rankingListView = findViewById(R.id.rankingListView);
        rankingList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rankingList);
        rankingListView.setAdapter(adapter);
        fetchTopScores();
        onClickBackButton();
    }

    public void onClickBackButton( ) {
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void fetchTopScores() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
        query.orderByDescending("score");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    rankingList.clear();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    for (ParseObject scoreObject : objects) {
                        int score = scoreObject.getInt("score");
                        Date date = scoreObject.getCreatedAt();
                        String dateString = dateFormat.format(date);

                        rankingList.add("Score: " + score + " - Date: " + dateString);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("RankingActivity", "Error fetching scores: " + e.getMessage());
                }
            }
        });
    }
}