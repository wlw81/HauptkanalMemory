package de.pasligh.android.hauptkanalmemory;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.pasligh.android.tools.Flags;

import static de.pasligh.android.tools.Flags.LOG;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    int spBad;
    int spFlick;
    int spSwoosh;
    int spSuck;
    SoundPool soundPool;
    ImageButton imgButton1;
    ImageButton imgButton2;
    ImageButton imgButton3;
    Handler myHandler;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private int currentNumber = 0;
    private String currentStreet;

    public String[] getImageNames() {
        if (null == imageNames) {
            try {
                imageNames = getAssets().list(currentStreet);
            } catch (IOException e) {
                Log.e(LOG, e.getMessage());
            }
        }

        return imageNames;
    }

    private int score = 0;
    private ImageButton imageButtonCorrectChoice;
    private String[] imageNames = null;
    private View mContentView;
    private View mControlsView;

    public CountDownTimer getTimer() {
        if (timer == null) {
            timer = new CountDownTimer(Flags.COUNTDOWN, 1000) {


                @Override
                public void onTick(long millisUntilFinished) {
                    String countdown = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                    ((TextView) findViewById(R.id.textViewTimer)).setText(countdown);
                }

                @Override
                public void onFinish() {
                    finish();
                }
            };
        }

        return timer;
    }

    private CountDownTimer timer;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        myHandler = new Handler();
        currentStreet = getIntent().getStringExtra(Flags.STREET);
        mControlsView = findViewById(R.id.GameContentLayout);

        imgButton1 = (ImageButton) findViewById(R.id.imageButtonOption1);
        imgButton2 = (ImageButton) findViewById(R.id.imageButtonOption2);
        imgButton3 = (ImageButton) findViewById(R.id.imageButtonOption3);

        imgButton1.setOnClickListener(this);
        imgButton2.setOnClickListener(this);
        imgButton3.setOnClickListener(this);


        displayHousenumber(currentNumber);
        soundPool = new SoundPool.Builder().build();

        spBad = soundPool.load(this, R.raw.bad, 1);
        spFlick = soundPool.load(this, R.raw.flick, 1);
        spSuck = soundPool.load(this, R.raw.suck, 1);
        spSwoosh = soundPool.load(this, R.raw.swoosh, 1);
        getTimer().start();
    }

    public int generateHousenumber() {
        Random myGenerator = new Random();
        int houseNumberGenerated = currentNumber;
        while (houseNumberGenerated == currentNumber || houseNumberGenerated == (currentNumber + 1)) {
            houseNumberGenerated = myGenerator.nextInt(getImageNames().length);
        }
        return houseNumberGenerated;
    }

    public void displayHousenumber(int p_number) {
        try {
            ((ImageView) findViewById(R.id.imageView)).setImageDrawable(getImage(p_number));

            Bitmap bitmapHouseAfter = scaleBitmap(currentNumber + 1);

            int random = new Random().nextInt(3);

            imgButton1.setImageDrawable(null);
            imgButton2.setImageDrawable(null);
            imgButton3.setImageDrawable(null);

            switch (random) {
                case 0:
                    imageButtonCorrectChoice = ((ImageButton) findViewById(R.id.imageButtonOption1));
                    imageButtonCorrectChoice.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources()
                            , bitmapHouseAfter));
                    break;
                case 1:
                    imageButtonCorrectChoice = ((ImageButton) findViewById(R.id.imageButtonOption2));
                    imageButtonCorrectChoice.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources()
                            , bitmapHouseAfter));
                    break;
                case 2:
                    imageButtonCorrectChoice = ((ImageButton) findViewById(R.id.imageButtonOption3));
                    imageButtonCorrectChoice.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources()
                            , bitmapHouseAfter));
                    break;
            }

            if (null == imgButton1.getDrawable()) {
                imgButton1.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources(), scaleBitmap(generateHousenumber())));
            }

            if (null == imgButton2.getDrawable()) {
                imgButton2.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources(), scaleBitmap(generateHousenumber())));
            }

            if (null == imgButton3.getDrawable()) {
                imgButton3.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources(), scaleBitmap(generateHousenumber())));
            }

            final Animation slideUp = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_up);
            imgButton1.setVisibility(View.VISIBLE);
            imgButton1.startAnimation(slideUp);
            soundPool.play(spSwoosh, 1, 1, 0, 0, 1);
            slideUp.setStartOffset(200);
            imgButton2.startAnimation(slideUp);
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imgButton2.setVisibility(View.VISIBLE);
                    soundPool.play(spSwoosh, 1, 1, 0, 0, 1);

                }
            }, 150);

            slideUp.setStartOffset(400);
            imgButton3.startAnimation(slideUp);
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imgButton3.setVisibility(View.VISIBLE);
                    soundPool.play(spSwoosh, 1, 1, 0, 0, 1);
                }
            }, 350);

        } catch (Exception e) {
            Log.e(LOG, e.getMessage());
        }
    }

    private Bitmap scaleBitmap(int p_number) {
        BitmapDrawable bd = (BitmapDrawable) getImage(p_number);
        return Bitmap.createScaledBitmap(bd.getBitmap(),
                (int) (bd.getIntrinsicWidth() * 0.9),
                (int) (bd.getIntrinsicHeight() * 0.9),
                false);
    }

    public Drawable getImage(int p_number) {
        Drawable imageReturn = null;
        InputStream inputstream = null;
        try {
            inputstream = getApplicationContext().getAssets().open(currentStreet + "/" + getImageNames()[p_number]);
            imageReturn = Drawable.createFromStream(inputstream, null);
        } catch (IOException e) {
            Log.e(Flags.LOG, e.getMessage());
        } finally {
            if (null != inputstream) {
                try {
                    inputstream.close();
                    inputstream = null;
                } catch (IOException e) {
                    Log.e(Flags.LOG, e.getMessage());
                }
            }
        }

        return imageReturn;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        currentNumber++;

        if (v.getId() == imageButtonCorrectChoice.getId()) {
            soundPool.play(spFlick, 1, 1, 1, 0, 1);
            score++;
        } else {
            score--;
            soundPool.play(spBad, 1, 1, 1, 0, 1);
        }

        final Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.animate);
        final Animation slideDown = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_down);
        final Animation slideDownFast = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_down);
        if(imageButtonCorrectChoice != imgButton1){
            imgButton1.startAnimation(slideDown);
            soundPool.play(spSuck, 1, 1, 0, 0, 1);
            imgButton1.setVisibility(View.GONE);
        }
        if(imageButtonCorrectChoice != imgButton2){
            imgButton2.startAnimation(slideDown);
            soundPool.play(spSuck, 1, 1, 0, 0, 1);
            imgButton2.setVisibility(View.GONE);
        }
        if(imageButtonCorrectChoice != imgButton3){
            imgButton3.startAnimation(slideDown);
            soundPool.play(spSuck, 1, 1, 0, 0, 1);
            imgButton3.setVisibility(View.GONE);

        }
        imageButtonCorrectChoice.startAnimation(scaleAnim);
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (currentNumber >= (imageNames.length - 1)) {
                    finish();
                } else {
                    soundPool.play(spSuck, 1, 1, 0, 0, 1);
                    imageButtonCorrectChoice.startAnimation(slideDownFast);
                    slideDownFast.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            displayHousenumber(currentNumber);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void finish() {
        Toast.makeText(getApplicationContext(), "Dein Score " + score, Toast.LENGTH_LONG).show();
        super.finish();
    }
}
