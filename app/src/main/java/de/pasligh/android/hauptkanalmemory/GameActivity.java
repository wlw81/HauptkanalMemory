package de.pasligh.android.hauptkanalmemory;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener {
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

    public String[] getImageNames() {
        if (null == imageNames) {
            try {
                imageNames = getAssets().list("hauptkanalLinks");
            } catch (IOException e) {
                Log.e("BLA", e.getMessage());
            }
        }

        return imageNames;
    }

    private int currentNumber = 0;
    private int score = 0;
    private ImageButton imageButtonCorrectChoice;
    private String[] imageNames = null;
    private View mContentView;
    private View mControlsView;

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
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
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

        mVisible = true;
        mControlsView = findViewById(R.id.GameContentLayout);

        ((ImageButton) findViewById(R.id.imageButtonOption1)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.imageButtonOption2)).setOnClickListener(this);

        displayHousenumber(currentNumber);
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

            Bitmap bitmapWrongHouse = scaleBitmap(generateHousenumber());
            Bitmap bitmapHouseAfter = scaleBitmap(currentNumber + 1);

            if (new Random().nextBoolean()) {
                ((ImageButton) findViewById(R.id.imageButtonOption1)).setImageDrawable(new BitmapDrawable(getApplicationContext().getResources()
                        , bitmapWrongHouse));
                imageButtonCorrectChoice = ((ImageButton) findViewById(R.id.imageButtonOption2));
                imageButtonCorrectChoice.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources()
                        , bitmapHouseAfter));
            } else {
                ((ImageButton) findViewById(R.id.imageButtonOption2)).setImageDrawable(new BitmapDrawable(getApplicationContext().getResources()
                        , bitmapWrongHouse));
                imageButtonCorrectChoice = ((ImageButton) findViewById(R.id.imageButtonOption1));
                imageButtonCorrectChoice.setImageDrawable(new BitmapDrawable(getApplicationContext().getResources()
                        , bitmapHouseAfter));
            }

        } catch (Exception e) {
            Log.e("BLA", e.getMessage());
        }
    }

    private Bitmap scaleBitmap(int p_number) {
        BitmapDrawable bd = (BitmapDrawable) getImage(p_number);
        return Bitmap.createScaledBitmap(bd.getBitmap(),
                (int) (bd.getIntrinsicHeight() * 0.8),
                (int) (bd.getIntrinsicWidth() * 0.8),
                false);
    }

    public Drawable getImage(int p_number) {
        Drawable imageReturn = null;
        InputStream inputstream = null;
        try {
            inputstream = getApplicationContext().getAssets().open("hauptkanalLinks/" + getImageNames()[p_number]);
            imageReturn = Drawable.createFromStream(inputstream, null);
        } catch (IOException e) {
            Log.e("BLA", e.getMessage());
        } finally {
            if (null != inputstream) {
                try {
                    inputstream.close();
                    inputstream = null;
                } catch (IOException e) {
                    Log.e("BLA", e.getMessage());
                }
            }
        }

        return imageReturn;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
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

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

    }

    @Override
    public void onClick(View v) {
        currentNumber++;

        if(v.getId() == imageButtonCorrectChoice.getId()){
            Toast.makeText(getApplicationContext(), "Stimmt!", Toast.LENGTH_SHORT).show();
            score++;
        }else{
            Toast.makeText(getApplicationContext(), "NOPE.", Toast.LENGTH_SHORT).show();
        }

        if(currentNumber >= (imageNames.length-1)){
            Toast.makeText(getApplicationContext(), "Dein Score "+score, Toast.LENGTH_LONG).show();
            finish();
        }else{
            displayHousenumber(currentNumber);
        }

    }
}
