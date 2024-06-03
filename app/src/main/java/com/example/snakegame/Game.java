package com.example.snakegame;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.parse.ParseObject;

public class Game extends AppCompatActivity implements SurfaceHolder.Callback {

    // Lista os pontos da cobrinha / tamanho
    private final List<SnakePoints> snakePointsList = new ArrayList<>();
    private SurfaceView surfaceView;
    private TextView scoreTV;

    // objeto que permite manipular a superfície do SurfaceView
    private SurfaceHolder surfaceHolder;

    // posição da movimentação da cobra, valores podem ser "up", "down", "left" e "right"
    // por padrão a cobra começa indo para a direita
    private String snakePosition = "right";

    // tamanho do ponto da cobrinha
    public static final int pointSize = 28;
    public static final int defaultTablePoints = 3;

    public static final int snakeColor = Color.rgb(0, 119, 5); //verde escuro
    public static final int snakeHeadColor = Color.GREEN;
    public static final int foodColor = Color.rgb(252, 194, 47); //amarelo

    // velocidade de movimentação da cobrinha, valores podem ser setados entre 1 - 1000
    public static final int snakeMovingSpeed = 800;

    // pontuação aleatória com as coordenadas na surfaceView
    private int positionX, positionY;

    // tempo para mover a cobrinha (mudar a posição da cobrinha depois de um tempo específico (snakeMovingSpeed)
    private Timer timer;

    // canvas para desenhar a cobrinha
    private Canvas canvas = null;

    private Paint pointColor = null;
    private Paint headPaintColor = null;
    private Paint foodPaintColor = null;

    private int score = 0;
    private boolean isPaused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.scoreTv);

        final AppCompatImageButton upButton = findViewById(R.id.upButton);
        final AppCompatImageButton downButton = findViewById(R.id.downButton);
        final AppCompatImageButton leftButton = findViewById(R.id.leftButton);
        final AppCompatImageButton rightButton = findViewById(R.id.rightButton);
        final AppCompatImageButton pauseButton = findViewById(R.id.pauseButton); // Botão de pausa

        surfaceView.getHolder().addCallback(this);



        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!snakePosition.equals("down")) {
                    snakePosition = "up";
                }
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!snakePosition.equals("up")) {
                    snakePosition = "down";
                }
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!snakePosition.equals("right")) {
                    snakePosition = "left";
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!snakePosition.equals("left")) {
                    snakePosition = "right";
                }
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inverte o estado de pausa
                isPaused = !isPaused;
                if (isPaused) {
                    //muda img do botao de pause pra play
                    pauseButton.setImageResource(R.drawable.play_botao);
                    pauseGame();
                } else {
                    //volta img do botao de pause
                    pauseButton.setImageResource(R.drawable.pause_botao);
                    resumeGame();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    private void pauseGame() {
        timer.cancel();
        // Exibe o Snackbar informando que o jogo foi pausado
        Snackbar.make(findViewById(R.id.main), "Jogo pausado", Snackbar.LENGTH_SHORT).show();
    }

    private void resumeGame() {
        // Reinicia o timer para retomar a movimentação da cobra
        moveSnake();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;

        // inicia os dados para a cobrinha / surfaceView
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void init() {

        // Limpa os pontos da cobrinha / tamanho
        snakePointsList.clear();
        scoreTV.setText("0");
        score = 0;
        snakePosition = "right";

        // inicialização da posição da cobrinha na tela
        int startPositioX = (pointSize) * defaultTablePoints;

        for(int i = 0; i < defaultTablePoints; i++) {

            // adiciona os pontos da cobrinha / tamanho
            SnakePoints snakePoints = new SnakePoints(startPositioX, pointSize);
            snakePointsList.add(snakePoints);


            startPositioX = startPositioX - (pointSize * 2);
        }

        // adiciona ponto aleatório na tela para ser comido pela cobra
        addPoints();

        // inicia a movimentação da cobrinha
        moveSnake();
    }

    private void addPoints() {

        // obtendo o tamanho da surfaceView para adicionar ponto na tela para cobrinha comer
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        if((randomXPosition % 2) != 0) {
            randomXPosition = randomXPosition + 1;
        }
        if((randomYPosition % 2) != 0) {
            randomYPosition = randomYPosition + 1;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;
    }

    private void moveSnake() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                // checa se a cobrinha comeu o ponto
                if (headPositionX == positionX && positionY == headPositionY) {
                    growSnake();
                    addPoints();
                }

                // checa qual direção a cobrinha está indo
                switch (snakePosition) {
                    case "right":
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "left":
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "up":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;
                    case "down":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }

                // checa se game over
                if (checkGameOver(headPositionX, headPositionY)) {
                    timer.purge();
                    timer.cancel();

                    AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
                    builder.setTitle("Game Over");
                    builder.setMessage("Você perdeu! Sua pontuação foi: " + score);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", (dialogInterface, i) -> init());

                    runOnUiThread(builder::show);
                } else {
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                    // Desenha a cabeça da cobra com a cor da cabeça
                    canvas.drawCircle(snakePointsList.get(0).getPositionX(), snakePointsList.get(0).getPositionY(), pointSize, createHeadPaintColor());

                    // Desenha o ponto a ser comido com a cor da comida
                    canvas.drawCircle(positionX, positionY, pointSize, createFoodPaintColor());

                    for (int i = 1; i < snakePointsList.size(); i++) {
                        int getTempPositionX = snakePointsList.get(i).getPositionX();
                        int getTempPositionY = snakePointsList.get(i).getPositionY();

                        snakePointsList.get(i).setPositionX(headPositionX);
                        snakePointsList.get(i).setPositionY(headPositionY);

                        // Desenha o corpo da cobra com a cor atual
                        canvas.drawCircle(snakePointsList.get(i).getPositionX(), snakePointsList.get(i).getPositionY(), pointSize, createPaintColor());

                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }
        }, 1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }


    private void growSnake() {
        SnakePoints snakePoints = new SnakePoints(0, 0);
        snakePointsList.add(snakePoints);

        score++;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    private boolean checkGameOver(int headerPositionX, int headerPositionY) {
        boolean gameOver = false;

        if(snakePointsList.get(0).getPositionX() < 0 ||
                snakePointsList.get(0).getPositionY() < 0 ||
                snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionY() >= surfaceView.getHeight()) {

            gameOver = true;
        } else {
            for(int i = 1; i < snakePointsList.size(); i++) {
                if(headerPositionX == snakePointsList.get(i).getPositionX() &&
                        headerPositionY == snakePointsList.get(i).getPositionY()) {
                    gameOver = true;
                    break;
                }
            }
        }

        if (gameOver) {
            saveScoreToDatabase(score);
        }

        return gameOver;
    }

    private Paint createPaintColor() {
        if(pointColor == null) {
            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);
        }
        return  pointColor;
    }

    private Paint createHeadPaintColor() {
        if (headPaintColor == null) {
            headPaintColor = new Paint();
            headPaintColor.setColor(snakeHeadColor);
            headPaintColor.setStyle(Paint.Style.FILL);
            headPaintColor.setAntiAlias(true);
        }
        return headPaintColor;
    }

    private Paint createFoodPaintColor() {
        if (foodPaintColor == null) {
            foodPaintColor = new Paint();
            foodPaintColor.setColor(foodColor);
            foodPaintColor.setStyle(Paint.Style.FILL);
            foodPaintColor.setAntiAlias(true);
        }
        return foodPaintColor;
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

    private void saveScoreToDatabase(int score) {
        // Cria um novo objeto Parse
        ParseObject scoreObject = new ParseObject("Score");

        // Define o campo 'score' com o valor do score atual
        scoreObject.put("score", score);

        // Salva o objeto no Parse de forma assíncrona
        scoreObject.saveInBackground(e -> {
            if (e == null) {
                // Score salvo com sucesso
                Log.d("Game", "Score salvo com sucesso");
            } else {
                // Ocorreu um erro ao salvar o score
                Log.e("Game", "Erro ao salvar o score: " + e.getMessage());
            }
        });
    }
}