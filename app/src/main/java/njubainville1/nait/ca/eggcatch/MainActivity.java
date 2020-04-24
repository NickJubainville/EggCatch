package njubainville1.nait.ca.eggcatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //Frame
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;

    //Image
    private ImageView box, black, orange, pink;
    private Drawable imageBoxRight, imageBoxLeft, imageBoxLeft2, imageBoxRight2;
    private ToggleButton toggle;

    //Size
    private int boxWidth;
    private int screenWidth;
    private int screenHeight;

    //Position
    private float boxX, boxY;
    private float blackX, blackY;
    private float orangeX, orangeY;
    private float pinkX, pinkY;

    //Speed
    private int boxSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;

    //Score
    private TextView scoreLabel, highScoreLabel;
    private int score, highScore, timeCount;
    private SharedPreferences settings;

    //Class
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;

    //Status
    private boolean start_flag = false;
    private boolean action_flag = false;
    private boolean pink_flag = false;
    private boolean chungus_flag = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        gameFrame = (FrameLayout)findViewById(R.id.gameFrame);
        startLayout = (LinearLayout)findViewById(R.id.startLayout);
        box = (ImageView)findViewById(R.id.box);
        black = (ImageView)findViewById(R.id.black);
        orange = (ImageView)findViewById(R.id.orange);
        pink = (ImageView)findViewById(R.id.pink);
        scoreLabel = (TextView)findViewById(R.id.scoreLabel);
        highScoreLabel = (TextView)findViewById(R.id.highScoreLabel);
        toggle = (ToggleButton) findViewById(R.id.unlockChungus);


        imageBoxLeft = getResources().getDrawable(R.drawable.bunny_left);
        imageBoxRight = getResources().getDrawable(R.drawable.bunny_right);
        imageBoxLeft2 = getResources().getDrawable(R.drawable.chungus_left);
        imageBoxRight2 = getResources().getDrawable(R.drawable.chungus_right);

        //High Score
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score : " + highScore);

        checkChungus();

        //Get screen size
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;
    }

    public void changePos()
    {
        //Add timeCount
        timeCount += 20;

        //Pink
        //Since we want pink to appear every 10 seconds, we do a check on timeCount to see when
        //10 seconds has passed (10 seconds = 10000 milliseconds)
        if(!pink_flag && timeCount % 10000 == 0)
        {
            pink_flag = true;
            pinkY = -20;
            pinkX = (float) Math.floor(Math.random() * (frameWidth - pink.getWidth()));
        }
        if(pink_flag)
        {
            pinkY += pinkSpeed;
            float pinkCenterX = pinkX + pink.getWidth() / 2;
            float pinkCenterY = pinkY + pink.getHeight() / 2;

            if(hitCheck(pinkCenterX, pinkCenterY))
            {
                pinkY = frameHeight + 30;
                score += 30;

                //change FrameWidth
                if(initialFrameWidth > frameWidth * 110 / 100)
                {
                    frameWidth = frameWidth * 110 / 100;
                    changeFrameWidth(frameWidth);
                }
                soundPlayer.playHitPinkSound();

            }

            if(pinkY > frameHeight) pink_flag = false;
            pink.setX(pinkX);
            pink.setY(pinkY);
        }

        //Orange
        orangeY += orangeSpeed;

        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        if(hitCheck(orangeCenterX, orangeCenterY))
        {
            orangeY = frameHeight + 100;
            score += 10;
            soundPlayer.playHitOrangeSound();
        }

        if(orangeY > frameHeight)
        {
            orangeY = -100f;
            orangeX = (float) Math.floor(Math.random() * (frameWidth - orange.getWidth()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //Black
        blackY += blackSpeed;

        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;

        if(hitCheck(blackCenterX, blackCenterY))
        {
            blackY = frameHeight + 100;
            //change FrameWidth
            frameWidth = frameWidth * 80 / 100;
            changeFrameWidth(frameWidth);
            soundPlayer.playHitBlackSound();

            if(frameWidth <= boxWidth)
            {
                gameOver();
            }
        }
        if(blackY > frameHeight)
        {
            blackY = -100f;
            blackX = (float) Math.floor(Math.random() * (frameWidth - black.getWidth()));
        }

        black.setX(blackX);
        black.setY(blackY);


        //Move Box
        if(action_flag)
        {
            //Touching screen
            boxX += boxSpeed;
            if(chungus_flag)
            {
                box.setImageDrawable(imageBoxRight2);
            }
            else
            {
                box.setImageDrawable(imageBoxRight);
            }
        }
        else
        {
            //Releasing
            boxX -= boxSpeed;
            if(chungus_flag)
            {
                box.setImageDrawable(imageBoxLeft2);
            }
            else
            {
                box.setImageDrawable(imageBoxLeft);
            }
        }

        //Check box position
        if(boxX < 0)
        {
            boxX = 0;
            if(chungus_flag)
            {
                box.setImageDrawable(imageBoxRight2);
            }
            else
            {
                box.setImageDrawable(imageBoxRight);
            }
        }
        if(frameWidth - boxWidth < boxX)
        {
            boxX = frameWidth - boxWidth;
            if(chungus_flag)
            {
                box.setImageDrawable(imageBoxLeft2);
            }
            else
            {
                box.setImageDrawable(imageBoxLeft);
            }
        }

        box.setX(boxX);
        scoreLabel.setText("Score : " + score);
    }

    public boolean hitCheck(float x, float y)
    {
        if(boxX <= x && x <= boxX + boxWidth &&
                boxY <= y && y <= frameHeight)
        {
            return true;
        }
        return false;
    }

    public void changeFrameWidth(int frameWidth)
    {
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);
    }

    public void checkChungus()
    {
        if(highScore >= 1000)
        {
            toggle.setVisibility(View.VISIBLE);
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isChecked)
                    {
                        chungus_flag = true;
                    } else
                    {
                        chungus_flag = false;
                    }
                }
            });
        }
    }

    public void gameOver()
    {
        //Stop the timer
        timer.cancel();
        timer = null;
        start_flag = false;

        //Before switching to the results screen, sleep for one second
        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        //Update High Score
        if(score > highScore)
        {
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.commit();
        }

        Intent intent = new Intent(getApplicationContext(), ResultScreen.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("HIGH_SCORE", highScore);
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(start_flag)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                action_flag = true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)
            {
                action_flag = false;
            }
        }
        return true;
    }

    public void startGame(View view)
    {
        //Setting speed relational to the size of the screen
        //This ensures it is consistent across all devices
        //For example: Nexus 4 width: 768 height: 1184
        //We place this inside the startGame method due to the check we make on the chungus_flag
        //This ensures the speed of the game is correct based on the player's character

        if(chungus_flag)
        {
            boxSpeed = Math.round(screenWidth / 35F);     // 768 / 35 = 21.942 => 22
            orangeSpeed = Math.round(screenHeight / 50F); // 1184 / 50 = 23.68 => 24
            pinkSpeed = Math.round(screenHeight / 40F);   // 1184 / 40 = 29.6 => 30
            blackSpeed = Math.round(screenHeight / 45F);  // 1184 / 45 = 26.31... => 26
        }
        else
        {
            boxSpeed = Math.round(screenWidth / 55F);     // 768 / 55 = 13.963 => 14
            orangeSpeed = Math.round(screenHeight / 99F); // 1184 / 99 = 11.959 => 12
            pinkSpeed = Math.round(screenHeight / 59F);   // 1184 / 59 = 20.067 => 20
            blackSpeed = Math.round(screenHeight / 66F);  // 1184 / 66 = 17.939 => 18
        }
//        Checking the speed across different devices within the log
//        Log.v("SPEED_BOX", boxSpeed+"");
//        Log.v("SPEED_ORANGE", orangeSpeed+"");
//        Log.v("SPEED_PINK", pinkSpeed+"");
//        Log.v("SPEED_BLACK", blackSpeed+"");

        start_flag = true;
        startLayout.setVisibility(View.INVISIBLE);

        if(frameHeight == 0)
        {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            boxWidth = box.getWidth();
            boxX = box.getX();
            boxY = box.getY();
        }

        frameWidth = initialFrameWidth;

        box.setX(0.0f);
        black.setY(3000.0f);
        orange.setY(3000.0f);
        pink.setY(3000.0f);

        blackY = black.getY();
        orangeY = orange.getY();
        pinkY = pink.getY();

        box.setVisibility(View.VISIBLE);
        black.setVisibility(View.VISIBLE);
        orange.setVisibility(View.VISIBLE);
        pink.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        scoreLabel.setText("Score : 0");

        //The timer is created to execute changePos every 20 milliseconds.
        //TimerTask(){}, delay, period);
        //delay is in milliseconds before the task is to be executed
        //period is the rate at which the task will execute in succession
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(start_flag)
                {
                    handler.post(new Runnable()
                    {
                    @Override
                    public void run()
                    {
                        changePos();
                    }
                    });
                }
            }
        }, 0, 20);



    }
    public void quitGame(View view)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

        //Newer alternative for APIs above Lollipop
        //finishAndRemoveTask();
    }

    //Disable returning to the previous screen
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch(event.getKeyCode())
            {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
