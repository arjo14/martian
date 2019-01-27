package com.example.root.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.root.dto.Image;
import com.example.root.dto.Sound;
import com.example.root.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements SensorEventListener2 {

    private Sound coinSound;
    private Sound gameOverSound;
    private Sound clickSound;
    private Sound dogBarkSound;
    private Sound gameSound;
    private boolean menuStep = true;
    private boolean paused;
    private boolean gameOver;
    private boolean needToCalibrate;
    private boolean notValidated = true;
    private BallView ballView;
    private float xPos, xAccel, xVel = 0.0f;
    private float yPos, yAccel, yVel = 0.0f;
    private Image ballHappy;
    private Image ballSad;
    private Image ballNow;
    private Image over;
    private Image circle;
    private Image restart;
    private Image exit;
    private Image coin;
    private Image pause;
    private Image playInMenu;
    private Image playInPause;
    private Image shop;
    private Image settings;
    private Image reset;
    private Image done;
    private List<Image> dangerList = new ArrayList<>();
    private SensorManager sensorManager;
    private int maxWidth;
    private int maxHeight;
    private int score = 0;
    private int highscore = 0;
    private boolean circleGoingXIsPositive = true;
    private boolean circleGoingYIsPositive = true;
    private int circleGoingY;
    private SharedPreferences.Editor editor;

    private Random random = new Random();
    private int randXForCircle = 5;
    private int randYForCircle = (int) (Math.random() * 5);

    private int coinCount;
    private int coinCountPerGame = 0;

    private int dangerCountInGame = 0;
    private float calibratedX;
    private float calibratedY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // region Highscore and money
        SharedPreferences sharedPref = this.getSharedPreferences("ball", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (sharedPref.getInt("highscore", -1) == -1) {
            editor.putInt("highscore", highscore);
            editor.putInt("money", coinCount);
            editor.apply();
        } else {
            highscore = sharedPref.getInt("highscore", highscore);
            coinCount = sharedPref.getInt("money", coinCount);
        }
        //endregion

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ballView = new BallView(this);
        setContentView(ballView);

        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (needToCalibrate) {
                calibratedX = sensorEvent.values[0];
                calibratedY = sensorEvent.values[1];
            } else if (!menuStep && !paused && !gameOver && !notValidated) {
                xAccel = sensorEvent.values[0];
                yAccel = -sensorEvent.values[1];
                updateBall();
            }
        }
    }

    private void updateBall() {
        float speedDecreaser = 0.1f;
        xVel += xAccel;
        yVel += yAccel;

        float xS = xVel * speedDecreaser;
        float yS = yVel * speedDecreaser;

        xPos -= xS;
        yPos -= yS;
        if (score > 99) {
            if (dangerCountInGame == 0) {
                dangerCountInGame++;
                dangerList.get(0).setX((int) (Math.random() * maxWidth));
                dangerList.get(0).setY(0);
            } else {
                dangerList.get(0).setY(dangerList.get(0).getY() + 10);
                if (dangerList.get(0).getY() > maxHeight) {
                    dangerList.get(0).setX((int) (Math.random() * maxWidth));
                    dangerList.get(0).setY(0);
                }
            }
        }

        if (score > 500) {
            if (dangerCountInGame == 1) {
                dangerCountInGame++;
                dangerList.get(1).setX((int) (Math.random() * maxWidth));
                dangerList.get(1).setY(0);
            } else {
                dangerList.get(1).setY(dangerList.get(1).getY() + 10);
                if (dangerList.get(1).getY() > maxHeight) {
                    dangerList.get(1).setX((int) (Math.random() * maxWidth));
                    dangerList.get(1).setY(0);
                }
            }
        }

        if (score > 999) {
            if (dangerCountInGame == 2) {
                dangerCountInGame++;
                dangerList.get(2).setX((int) (Math.random() * maxWidth));
                dangerList.get(2).setY(0);
            } else {
                dangerList.get(2).setY(dangerList.get(2).getY() + 10);
                if (dangerList.get(2).getY() > maxHeight) {
                    dangerList.get(2).setX((int) (Math.random() * maxWidth));
                    dangerList.get(2).setY(0);
                }
            }
        }

        int circleGoingX = 5;

        if (score > 200) {
            circleGoingX = 8;
        } else if (score > 500) {
            circleGoingX = 12;
        } else if (score > 999) {
            circleGoingX = 16;
        } else if (score > 1500) {
            circleGoingX = 19;
        } else if (score > 2000) {
            circleGoingX = 25;
        } else if (score > 2500) {
            circleGoingX = 30;
        }

        if (circle.getCentreX() >= maxWidth || circle.getCentreX() <= 0) {
            circleGoingXIsPositive = !circleGoingXIsPositive;
        }
        if (circle.getCentreY() >= maxHeight || circle.getCentreY() <= 0) {
            circleGoingYIsPositive = !circleGoingYIsPositive;
            circleGoingY = random.nextInt(5);
        }
        randXForCircle = circleGoingX * (circleGoingXIsPositive ? -1 : 1);
        randYForCircle = circleGoingY * (circleGoingYIsPositive ? -1 : 1);

        circle.setX(circle.getX() + randXForCircle);
        circle.setY(circle.getY() + randYForCircle);

        ballNow.setCentreX((int) (xPos + ballNow.getWidth() / 2));
        ballNow.setCentreY((int) (yPos + ballNow.getHeight() / 2));

        circle.setCentreX(circle.getX() + circle.getWidth() / 2);
        circle.setCentreY(circle.getY() + circle.getHeight() / 2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!gameOver || !menuStep) {
            paused = true;
        }
        if (dogBarkSound.getMediaPlayer().isPlaying()) {
            dogBarkSound.setCurrentPosition(dogBarkSound.getMediaPlayer().getCurrentPosition());
            dogBarkSound.getMediaPlayer().pause();
        } else if (gameSound.getMediaPlayer().isPlaying()) {
            gameSound.setCurrentPosition(gameSound.getMediaPlayer().getCurrentPosition());
            gameSound.getMediaPlayer().pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuStep || gameOver || notValidated) {
            dogBarkSound.getMediaPlayer().seekTo(dogBarkSound.getCurrentPosition());
            dogBarkSound.getMediaPlayer().start();
            dogBarkSound.getMediaPlayer().setLooping(true);
        } else {
            gameSound.getMediaPlayer().seekTo(gameSound.getCurrentPosition());
            gameSound.getMediaPlayer().start();
            gameSound.getMediaPlayer().setLooping(true);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.abs((x2 - x1) * (x2 - x1)) + Math.abs((y2 - y1) * (y2 - y1)));
    }

    private void drawCoinPerRound(Canvas canvas, Paint highScorePaint) {
        float temp = 2f;
        if (coinCountPerGame > 9999) {
            temp = 3.2f;
        } else if (coinCountPerGame > 999) {
            temp = 2.9f;
        } else if (coinCountPerGame > 99) {
            temp = 2.6f;
        } else if (coinCountPerGame > 9) {
            temp = 2.3f;
        }
        canvas.drawText("\uD83D\uDCB0 ", 64, 64, highScorePaint);
        canvas.drawText(coinCountPerGame + "", temp * 64, 64, highScorePaint);
    }

    private void drawScore(Canvas canvas, Paint highScorePaint) {
        float temp = 2f;
        if (score > 9999) {
            temp = 3.2f;
        } else if (score > 999) {
            temp = 2.9f;
        } else if (score > 99) {
            temp = 2.6f;
        } else if (score > 9) {
            temp = 2.3f;
        }
        canvas.drawText("⭐", 54, 150, highScorePaint);
        canvas.drawText(score + "", temp * 64, 150, highScorePaint);
    }

    private void drawHighScoreAndCoins(Canvas canvas, Paint highScorePaint) {
        float temp = 2f;
        if (coinCount > 9999) {
            temp = 3.2f;
        } else if (coinCount > 999) {
            temp = 2.9f;
        } else if (coinCount > 99) {
            temp = 2.6f;
        } else if (coinCount > 9) {
            temp = 2.3f;
        }
        canvas.drawText("\uD83D\uDCB0 ", 64, 64, highScorePaint);
        canvas.drawText(coinCount + "", temp * 64, 64, highScorePaint);

        canvas.drawText("Highscore", maxWidth / 2, 64, highScorePaint);
        canvas.drawText(String.valueOf(highscore), maxWidth / 2, 128, highScorePaint);
    }

    private void initImages() {
        exit = new Image(getResources(), R.drawable.exit, 200, 200, (maxWidth + 200) / 2, maxHeight / 2 + 200);
        playInMenu = new Image(getResources(), R.drawable.play, 200, 200, maxWidth / 2 - 300, (maxHeight - 400) / 2);
        playInPause = new Image(getResources(), R.drawable.play, 200, 200, maxWidth / 2 - 100, maxHeight / 2 - 100);
        shop = new Image(getResources(), R.drawable.shop, 200, 200, (maxWidth + 200) / 2, (maxHeight - 400) / 2);
        settings = new Image(getResources(), R.drawable.settings, 200, 200, maxWidth / 2 - 300, maxHeight / 2 + 200);
        reset = new Image(getResources(), R.drawable.reset, 200, 200, (maxWidth + 200) / 2, maxHeight - 400);
        done = new Image(getResources(), R.drawable.done, 200, 200, (int) (maxWidth / 2 - 300), maxHeight - 400);
        circle = new Image(getResources(), R.drawable.mars3, 458, 458, 0, 0);
        coin = new Image(getResources(), R.drawable.coin, 48, 48, 0, 0);
        restart = new Image(getResources(), R.drawable.restart, 200, 200, (maxWidth - 456) / 2, maxHeight / 2 + 200);
        dangerList.add(new Image(getResources(), R.drawable.asteroid, 72, 72, 0, 0));
        dangerList.add(new Image(getResources(), R.drawable.asteroid, 72, 72, 0, 0));
        dangerList.add(new Image(getResources(), R.drawable.asteroid, 72, 72, 0, 0));
        pause = new Image(getResources(), R.drawable.pause, 100, 100, maxWidth - 120, 20);
        ballHappy = new Image(getResources(), R.drawable.ball_happy, 130, 130, 0, 0);
        ballSad = new Image(getResources(), R.drawable.ball_sad, 130, 130, 0, 0);
        ballNow = ballSad;
        over = new Image(getResources(), R.drawable.over, 512, 180, maxWidth / 2 - 256, maxHeight / 2 - 164);
    }

    private class BallView extends View {
        private final Paint highScorePaint;
        private final Paint scorePaint;

        public BallView(Context context) {
            super(context);

            //region init
            setBackgroundResource(R.drawable.background);
            if (getActionBar() != null) {
                getActionBar().hide();
            }
            highScorePaint = new Paint();
            highScorePaint.setColor(Color.WHITE);
            highScorePaint.setTextSize(64);
            highScorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            highScorePaint.setTextAlign(Paint.Align.CENTER);

            scorePaint = new Paint();
            scorePaint.setColor(Color.WHITE);
            scorePaint.setTextSize(120);
            scorePaint.setTextAlign(Paint.Align.CENTER);
            scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            coinSound = new Sound(context, R.raw.coin_sound);
            gameOverSound = new Sound(context, R.raw.game_over);
            clickSound = new Sound(context, R.raw.click);
            dogBarkSound = new Sound(context, R.raw.dog);
            gameSound = new Sound(context, R.raw.game);
            //endregion
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (menuStep) {
                if (!dogBarkSound.getMediaPlayer().isLooping()) {
                    dogBarkSound.getMediaPlayer().start();
                    dogBarkSound.getMediaPlayer().setLooping(true);
                }
                drawHighScoreAndCoins(canvas, highScorePaint);
                canvas.drawBitmap(playInMenu.getImage(), playInMenu.getX(), playInMenu.getY(), null);
                canvas.drawBitmap(shop.getImage(), shop.getX(), shop.getY(), null);
                canvas.drawBitmap(settings.getImage(), settings.getX(), settings.getY(), null);
                canvas.drawBitmap(exit.getImage(), exit.getX(), exit.getY(), null);
            } else if (needToCalibrate) {
                //region calibration text
                String calibrationText1 = "place your device straight up";
                String calibrationText2 = "and    press    the   ✅   button";
                String calibrationText3 = "or   you   can   reset   settings";
                String calibrationText4 = "by  clicking  the  RESET  button";
                Paint calibrationPaint = new Paint();
                calibrationPaint.setTextSize(64);
                calibrationPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                calibrationPaint.setColor(Color.WHITE);
                canvas.drawText(calibrationText1, (maxWidth - calibrationPaint.measureText(calibrationText1)) / 2, maxHeight / 2 - 128, calibrationPaint);
                canvas.drawText(calibrationText2, (maxWidth - calibrationPaint.measureText(calibrationText2)) / 2, maxHeight / 2, calibrationPaint);
                canvas.drawText(calibrationText3, (maxWidth - calibrationPaint.measureText(calibrationText3)) / 2, maxHeight / 2 + 128, calibrationPaint);
                canvas.drawText(calibrationText4, (maxWidth - calibrationPaint.measureText(calibrationText3)) / 2, maxHeight / 2 + 256, calibrationPaint);
                //endregion
                canvas.drawBitmap(reset.getImage(), reset.getX(), reset.getY(), null);
                canvas.drawBitmap(done.getImage(), done.getX(), done.getY(), null);
            } else if (notValidated) {
                notValidated = false;
                xPos = maxWidth / 2;
                xVel = 0.0f;
                yVel = 0.0f;
                yPos = maxHeight / 2;
                score = 0;
                coinCountPerGame = 0;
                circle.setX((int) (Math.random() * (maxWidth - circle.getWidth() / 2)));
                circle.setY((int) (Math.random() * (maxHeight - circle.getHeight() / 2)));
                coin.setX((int) (Math.random() * (maxWidth - 296) + 148));
                coin.setY((int) (Math.random() * (maxHeight - 296) + 148));
                coin.setCentreX(coin.getX() + coin.getWidth() / 2);
                coin.setCentreY(coin.getY() + coin.getHeight() / 2);
                invalidate();
            } else if (gameOver) {
                //region reinit fields
                if (score > highscore) {
                    highscore = score;
                    editor.putInt("highscore", highscore);
                    editor.apply();
                }
                if (!gameOverSound.getMediaPlayer().isPlaying() && !dogBarkSound.getMediaPlayer().isPlaying()) {
                    dogBarkSound.getMediaPlayer().start();
                    dogBarkSound.getMediaPlayer().setLooping(true);
                }
                dangerCountInGame = 0;

                coinCount += coinCountPerGame;
                coinCountPerGame = 0;
                editor.putInt("money", coinCount);
                editor.apply();

                //endregion

                drawHighScoreAndCoins(canvas, highScorePaint);

                Paint centrePaint = new Paint();
                centrePaint.setTextAlign(Paint.Align.CENTER);

                canvas.drawText("Your Score", maxWidth / 2, maxHeight / 2 - 464, scorePaint);
                canvas.drawText(String.valueOf(score), maxWidth / 2, maxHeight / 2 - 300, scorePaint);

                //region draw images
                canvas.drawBitmap(over.getImage(), over.getX(), over.getY(), centrePaint);
                canvas.drawBitmap(restart.getImage(), restart.getX(), restart.getY(), null);
                canvas.drawBitmap(exit.getImage(), exit.getX(), exit.getY(), null);
                invalidate();
                //endregion
            } else if (paused) {
                drawCoinPerRound(canvas, highScorePaint);
                drawScore(canvas, highScorePaint);
                canvas.drawBitmap(playInPause.getImage(), playInPause.getX(), playInPause.getY(), null);
            } else {
                //region score,coin and danger detection
                if (distance(ballNow.getCentreX(), ballNow.getCentreY(), circle.getCentreX(), circle.getCentreY()) < circle.getHeight() / 2 + ballNow.getHeight() / 2) {
                    score++;
                    ballNow = ballHappy;
                } else {
                    ballNow = ballSad;
                }
                if (distance(ballNow.getCentreX(), ballNow.getCentreY(), coin.getCentreX(), coin.getCentreY()) < coin.getHeight() / 2 + ballNow.getHeight() / 2) {
                    coinSound.getMediaPlayer().start();
                    coinCountPerGame++;
                    coin.setX((int) (Math.random() * (maxWidth - 296) + 148));
                    coin.setY((int) (Math.random() * (maxHeight - 296) + 148));
                    coin.setCentreX(coin.getX() + coin.getWidth() / 2);
                    coin.setCentreY(coin.getY() + coin.getHeight() / 2);
                }
                if (dangerCountInGame > 0) {
                    if (distance(ballNow.getCentreX(), ballNow.getCentreY(), circle.getCentreX(), circle.getCentreY()) > ballNow.getHeight() / 2 + circle.getHeight() / 2) {
                        for (int i = 0; i < dangerCountInGame; i++) {
                            if (distance(ballNow.getCentreX(), ballNow.getCentreY(),
                                    dangerList.get(i).getX() + dangerList.get(i).getHeight() / 2,
                                    dangerList.get(i).getY() + dangerList.get(i).getHeight() / 2) < dangerList.get(i).getHeight() / 2 + ballNow.getHeight() / 2) {
                                gameOverSound.getMediaPlayer().start();
                                gameSound.getMediaPlayer().pause();
                                gameSound.getMediaPlayer().seekTo(0);
                                gameOver = true;
                                invalidate();
                                return;
                            }
                        }
                    }
                }
                //endregion

                //region draw images

                canvas.drawBitmap(circle.getImage(), circle.getX(), circle.getY(), null);
                canvas.drawBitmap(coin.getImage(), coin.getX(), coin.getY(), null);
                canvas.drawBitmap(pause.getImage(), pause.getX(), pause.getY(), null);
                drawCoinPerRound(canvas, highScorePaint);
                drawScore(canvas, highScorePaint);

                for (int i = 0; i < dangerCountInGame; i++) {
                    canvas.drawBitmap(dangerList.get(i).getImage(), dangerList.get(i).getX(), dangerList.get(i).getY(), null);
                }
                canvas.drawBitmap(ballNow.getImage(), xPos, yPos, null);

                //endregion

                if (xPos > maxWidth) {
                    xPos -= maxWidth;
                }
                if (xPos + 120 < 0) {
                    xPos += maxWidth;
                }
                if (yPos > maxHeight) {
                    yPos -= maxHeight;
                }
                if (yPos + 120 < 0) {
                    yPos += maxHeight;
                }
                invalidate();
            }

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            maxWidth = w;
            maxHeight = h;
            initImages();
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (menuStep) {
                    if (x > playInMenu.getX() && x < playInMenu.getX() + playInMenu.getWidth() && y > playInMenu.getY() && y < playInMenu.getY() + playInMenu.getHeight()) {
                        clickSound.getMediaPlayer().start();
                        Log.d("Start", "User wants to start the game");
                        menuStep = false;
                        notValidated = true;
                        paused = false;
                        dogBarkSound.getMediaPlayer().pause();
                        dogBarkSound.getMediaPlayer().seekTo(0);
                        gameSound.getMediaPlayer().start();
                        gameSound.getMediaPlayer().setLooping(true);
                        invalidate();
                    } else if (x > exit.getX() && x < exit.getX() + exit.getWidth() && y > exit.getY() && y < exit.getY() + exit.getHeight()) {
                        Log.d("Exit", "User wants to exit");
                        finishAndRemoveTask();
                        System.exit(0);
                    } else if (x > settings.getX() &&
                            x < settings.getX() + settings.getWidth() &&
                            y > settings.getY() &&
                            y < settings.getY() + settings.getHeight()) {
                        clickSound.getMediaPlayer().start();
                        Log.d("Calibrate", "User wants to calibrate the screen");
                        menuStep = false;
                        needToCalibrate = true;
                        invalidate();
                    } else if (x > shop.getX() && x < shop.getX() + shop.getWidth() && y > shop.getY() && y < shop.getY() + shop.getHeight()) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        startActivity(intent);
                    }
                } else if (needToCalibrate) {
                    if (x > done.getX() &&
                            x < done.getX() + done.getWidth() &&
                            y > done.getY() &&
                            y < done.getY() + done.getHeight()) {
                        clickSound.getMediaPlayer().start();
                        needToCalibrate = false;
                        menuStep = true;
                        invalidate();
                    } else if (x > reset.getX() &&
                            x < reset.getX() + reset.getWidth() &&
                            y > reset.getY() &&
                            y < reset.getY() + reset.getHeight()) {
                        clickSound.getMediaPlayer().start();
                        calibratedX = 0.0f;
                        calibratedY = 0.0f;
                        needToCalibrate = false;
                        menuStep = true;
                        invalidate();
                    }
                } else if (gameOver) {
                    if (x > restart.getX() && x < restart.getX() + restart.getWidth() && y > restart.getY() && y < restart.getY() + restart.getHeight()) {
                        clickSound.getMediaPlayer().start();
                        dogBarkSound.getMediaPlayer().pause();
                        dogBarkSound.getMediaPlayer().seekTo(0);
                        gameSound.getMediaPlayer().start();
                        gameSound.getMediaPlayer().setLooping(true);
                        gameOver = false;
                        notValidated = true;
                        invalidate();
                    } else if (x > exit.getX() && x < exit.getX() + exit.getWidth() && y > exit.getY() && y < exit.getY() + exit.getHeight()) {
                        clickSound.getMediaPlayer().start();
                        menuStep = true;
                        gameOver = false;
                        invalidate();
                    }
                } else if (paused) {
                    if (x > playInPause.getX() && x < playInPause.getX() + playInPause.getWidth() && y > playInPause.getY() && y < playInPause.getY() + playInPause.getHeight()) {
                        clickSound.getMediaPlayer().start();
                        paused = false;
                        invalidate();
                    }
                } else {
                    if (x > pause.getX() && x < pause.getX() + pause.getWidth() && y > pause.getY() && y < pause.getY() + pause.getHeight()) {
                        clickSound.getMediaPlayer().start();
                        paused = true;
                    }
                }
            }
            return true;
        }
    }
}
