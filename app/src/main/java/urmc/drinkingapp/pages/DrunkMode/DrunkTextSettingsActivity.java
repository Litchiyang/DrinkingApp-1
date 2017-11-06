package urmc.drinkingapp.pages.DrunkMode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.MainActivity;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.IntentParam;

public class DrunkTextSettingsActivity extends AppCompatActivity {

    private FancyButton mYes;
    private FancyButton mNo;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drunk_text_settings);


        mYes = (FancyButton) findViewById(R.id.button_text_settings_yes);
        mNo = (FancyButton) findViewById(R.id.button_text_settings_no);


        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DrunkTextSettingsActivity.this, MainActivity.class);
                i.putExtra(IntentParam.ANALYZE, 1);
                startActivity(i);
                finish();
            }
        });

        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DrunkTextSettingsActivity.this, MainActivity.class);
                i.putExtra(IntentParam.ANALYZE, 0);
                startActivity(i);
                finish();
            }
        });
    }
}
