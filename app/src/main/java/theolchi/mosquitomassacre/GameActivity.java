package theolchi.mosquitomassacre;

import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    long MAXAGE_MS = 1000; //public static final long
    public int timeLine = 60;
    public boolean gameRuns;
    private int round;
    private int points;
    private int msqtos;
    private int catchedMsqtos;
    private int time;
    private float scale;
    private Random randomSeedGenerator = new Random();
    private ViewGroup gameArea;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedStateInstance){

        super.onCreate(savedStateInstance);
        setContentView(R.layout.activity_game);
        scale = getResources().getDisplayMetrics().density;
        gameArea = findViewById(R.id.gamearea);
        startGame();

    }

    public void startGame(){

        gameRuns = true;
        round = 0;
        points = 0;
        startRound();

    }

    public void startRound(){

        round += 1;
        msqtos = round * 15;
        time = timeLine;
        checkHighRound();
        updateScreen();
        handler.postDelayed(this, 1000);

    }

    public void checkHighRound(){

        if(round >= 2){

            msqtos = round * 20;
            MAXAGE_MS = 500;
            timeLine = 30;

        }

        if(round >= 5){

            msqtos = round * 25;
            MAXAGE_MS = 300;
            timeLine = 15;

        }
    }

    //deletes a warning
    @SuppressLint("SetTextI18n")
    public void updateScreen(){

        TextView tvPoints = findViewById(R.id.points);
        tvPoints.setText(Integer.toString(points));

        TextView tvRounds = findViewById(R.id.rounds);
        tvRounds.setText(Integer.toString(round));

        TextView tvTime = findViewById(R.id.time);
        tvTime.setText(Integer.toString(time));

        TextView tvHits = findViewById(R.id.hits);
        tvHits.setText(Integer.toString(catchedMsqtos));

        FrameLayout flTime = findViewById(R.id.bar_time);
        FrameLayout flHits = findViewById(R.id.bar_hits);

        LayoutParams lpTime = flTime.getLayoutParams();
        lpTime.width = Math.round(scale * time * 300 / timeLine);

        LayoutParams lpHits = flHits.getLayoutParams();
        lpHits.width = Math.round(scale * 300 * Math.min(catchedMsqtos, msqtos) / msqtos);

    }

    private void countDownTime(){

        time -= 1;
        float randomNumber = randomSeedGenerator.nextFloat();
        double probability = msqtos * 1.5 / timeLine;

        if(probability > 1) {

            showOneMsqto();
            if (randomNumber < probability - 1) {
                showOneMsqto();
            }

        }else{
            if(randomNumber < probability){
                showOneMsqto();
            }
        }

        msqtosDissapear();
        updateScreen();
        if(!checkGameEnd()){
            if(!checkRoundEnd()){
                handler.postDelayed(this, 1000);
            }
        }
    }

    private void msqtosDissapear(){

        int number = 0;

        while(number < gameArea.getChildCount()){

            ImageView msqto = (ImageView) gameArea.getChildAt(number);
            Date birthday = (Date) msqto.getTag(R.id.birthday);
            long age = (new Date()).getTime()  - birthday.getTime();

            if (age > MAXAGE_MS){
                gameArea.removeView(msqto);
            } else{
                number++;
            }
        }
    }

    private boolean checkGameEnd(){

        if(time == 0 && catchedMsqtos < msqtos){

            gameOver();
            return true;

        }
        return false;
    }

    private void gameOver(){

        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.activity_game_over);
        dialog.show();
        gameRuns = false;

    }

    private boolean checkRoundEnd(){

        if(catchedMsqtos >= msqtos){

            startRound();
            return true;

        }
        return false;
    }

    public void showOneMsqto(){

        int width = gameArea.getWidth();
        int height = gameArea.getHeight();
        int msqto_with = Math.round(scale * 50);
        int msqto_height = Math.round(scale * 38);
        int left = randomSeedGenerator.nextInt(width - msqto_with);
        int top = randomSeedGenerator.nextInt(height - msqto_height);

        ImageView msqto = new ImageView(this);
        msqto.setImageResource(R.drawable.msqto);
        msqto.setOnClickListener(this);
        msqto.setTag(R.id.birthday, new Date());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(msqto_with, msqto_height);
        params.leftMargin = left;
        params.topMargin = top;
        params.gravity = Gravity.TOP + Gravity.START;

        gameArea.addView(msqto, params);

    }

    @Override
    public void onClick(View msqto){

        catchedMsqtos++;
        points += 100;

        updateScreen();
        gameArea.removeView(msqto);

    }

    @Override
    public void run(){
        countDownTime();
    }
}