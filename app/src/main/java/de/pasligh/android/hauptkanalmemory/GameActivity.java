package de.pasligh.android.hauptkanalmemory;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Drawable nextHouse;
    private Bitmap bitmapHouseAfter;

    private Map<ImageButton, CardView> mapCardViews = new HashMap<>();
    private List<Bitmap> listRandomImages = new ArrayList<Bitmap>();

    ImageButton imgButton1;
    ImageButton imgButton2;
    ImageButton imgButton3;

    CardView crdView1;
    CardView crdView2;
    CardView crdView3;

    Handler myHandler;

    Animation slideUp1;
    Animation slideUp2;
    Animation slideUp3;

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

    private int lastRandomHouseNumber = -1;
    private int currentNumber = 0;
    private String currentStreet;

    private class SwooshListener implements Animation.AnimationListener {


        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            soundPool.play(spSwoosh, 1, 1, 0, 0, 1);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

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
                    String countdown = String.format("%02d:%02d",
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
        Animation.AnimationListener mySwooshListner = new SwooshListener();
        currentStreet = getIntent().getStringExtra(Flags.STREET);
        mControlsView = findViewById(R.id.GameContentLayout);

        imgButton1 = (ImageButton) findViewById(R.id.imageButtonOption1);
        imgButton2 = (ImageButton) findViewById(R.id.imageButtonOption2);
        imgButton3 = (ImageButton) findViewById(R.id.imageButtonOption3);

        crdView1 = (CardView) findViewById(R.id.cardViewOption1);
        crdView2 = (CardView) findViewById(R.id.cardViewOption2);
        crdView3 = (CardView) findViewById(R.id.cardViewOption3);

        mapCardViews.put(imgButton1, crdView1);
        mapCardViews.put(imgButton2, crdView2);
        mapCardViews.put(imgButton3, crdView3);

        imgButton1.setOnClickListener(this);
        imgButton2.setOnClickListener(this);
        imgButton3.setOnClickListener(this);

        slideUp1 = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_up);
        slideUp2 = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_up);
        slideUp3 = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_up);

        slideUp2.setStartOffset(Flags.DELAY);
        slideUp3.setStartOffset(Flags.DELAY * 2);

        slideUp1.setAnimationListener(mySwooshListner);
        slideUp2.setAnimationListener(mySwooshListner);
        slideUp3.setAnimationListener(mySwooshListner);

        refillImages();

        displayHousenumber(currentNumber);
        displayScore();
        soundPool = new SoundPool.Builder().build();

        spBad = soundPool.load(this, R.raw.bad, 1);
        spFlick = soundPool.load(this, R.raw.flick, 1);
        spSuck = soundPool.load(this, R.raw.suck, 1);
        spSwoosh = soundPool.load(this, R.raw.swoosh, 1);
        getTimer().start();
    }

    private void refillImages() {
        Log.i(Flags.LOG, "Refilling images, currently " + listRandomImages.size() + " in stock.");
        for (int i = 0; i < Flags.RANDOM_IMAGES; i++) {
            listRandomImages.add(scaleBitmap(generateHousenumber()));
        }
        nextHouse = getImage(currentNumber + 1);
        bitmapHouseAfter = scaleBitmap(currentNumber + 2);
    }

    public int generateHousenumber() {
        Random myGenerator = new Random();
        int houseNumberGenerated = currentNumber;
        while (houseNumberGenerated == currentNumber || houseNumberGenerated == (currentNumber + 1) || houseNumberGenerated == lastRandomHouseNumber) {
            houseNumberGenerated = myGenerator.nextInt(getImageNames().length);
        }
        lastRandomHouseNumber = houseNumberGenerated; // not the same number next time
        return houseNumberGenerated;
    }

    public void displayHousenumber(int p_number) {
        try {
            ((ImageView) findViewById(R.id.imageView)).setImageDrawable(nextHouse);
            int random = new Random().nextInt(3);

            ImageButton[] imageButtons = new ImageButton[]{imgButton1, imgButton2, imgButton3};
            for (ImageButton imgbtn : imageButtons) {
                imgbtn.setImageDrawable(null);
            }

            imageButtonCorrectChoice = imageButtons[random];
            imageButtonCorrectChoice.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources()
                    , bitmapHouseAfter));


            for (ImageButton imgbtn : imageButtons) {
                if (null == imgbtn.getDrawable()) {
                    Bitmap nextRandomImage = listRandomImages.iterator().next();
                    imgbtn.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources(), nextRandomImage));
                    listRandomImages.remove(nextRandomImage);
                }
                mapCardViews.get(imgbtn).setVisibility(View.VISIBLE);
            }

            crdView1.startAnimation(slideUp1);
            crdView2.startAnimation(slideUp2);
            crdView3.startAnimation(slideUp3);


        } catch (Exception e) {
            Log.e(LOG, e.getMessage());
        } finally {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    refillImages();
                }
            });
        }
    }

    private Bitmap scaleBitmap(int p_number) {
        BitmapDrawable bd = (BitmapDrawable) getImage(p_number);
        float ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bd.getIntrinsicHeight(), getResources().getDisplayMetrics());
        float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bd.getIntrinsicWidth(), getResources().getDisplayMetrics());
        return Bitmap.createScaledBitmap(bd.getBitmap(),
                (int) wt_px / 3,
                (int) ht_px / 3,
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

        displayScore();

        if (imageButtonCorrectChoice != imgButton1) {
            crdView1.startAnimation(slideDown);
            crdView1.setVisibility(View.GONE);
            soundPool.play(spSuck, 1, 1, 0, 0, 1);
        }
        if (imageButtonCorrectChoice != imgButton2) {
            crdView2.startAnimation(slideDown);
            crdView2.setVisibility(View.GONE);
            soundPool.play(spSuck, 1, 1, 0, 0, 1);
        }
        if (imageButtonCorrectChoice != imgButton3) {
            crdView3.startAnimation(slideDown);
            crdView3.setVisibility(View.GONE);
            soundPool.play(spSuck, 1, 1, 0, 0, 1);
        }

        mapCardViews.get(imageButtonCorrectChoice).startAnimation(scaleAnim);
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
                    mapCardViews.get(imageButtonCorrectChoice).startAnimation(slideDownFast);
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

    private void displayScore() {
        final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        ((TextView) findViewById(R.id.textViewScore)).setText(String.valueOf(score) + " " + getString(R.string.score));
        findViewById(R.id.cardViewScore).startAnimation(bounce);
    }

    @Override
    public void finish() {
        Toast.makeText(getApplicationContext(), "Dein Score " + score, Toast.LENGTH_LONG).show();
        super.finish();
    }
}
