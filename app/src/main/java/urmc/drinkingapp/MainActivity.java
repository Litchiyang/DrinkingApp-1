package urmc.drinkingapp;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;
import ng.max.slideview.SlideView;
import urmc.drinkingapp.control.DrunkAlgorithm;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.IntentParam;
import urmc.drinkingapp.control.SMSListener;
import urmc.drinkingapp.control.Utils;
import urmc.drinkingapp.model.Friend;
import urmc.drinkingapp.pages.DrunkMode.DrunkModeDefaultActivity;
import urmc.drinkingapp.pages.Friends.FriendsViewPagerActivity;
import urmc.drinkingapp.pages.GoingOutSettings.GoingOutSettingsActivity;
import urmc.drinkingapp.pages.MapsActivity;
import urmc.drinkingapp.pages.Profile.ProfileActivity;

/**
 * Main Activity that is displayed after a successful login into the app. From this activity it is possible
 * to activate drunk mode, go to my profile and go to friends.
 * This activity also performs the drunk text analysis and displays the drunk texting behavior in a graph
 */
public class MainActivity extends AppCompatActivity {

    private FancyButton mProfile;
    private FancyButton mFriends;
    private FancyButton mSettings;
    private FancyButton mText;
    private FancyButton mMap;
    int READ_SMS_REQUEST_CODE = 77;
    private static final String TAG = "MainActivity";
    private static final Uri SMS_STATUS_URI = Uri.parse("content://sms");
    private int analyzeText;

    private FirebaseAnalytics mFirebaseAnalytics;

    Cursor cursor;

    private GraphView mGraph;
    private FirebaseDAO dao;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //http://devdeeds.com/android-location-tracking-in-background-service/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
        //following came from sample
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        SMSListener smsSentObserver = new SMSListener(new Handler(), this);
        this.getContentResolver().registerContentObserver(SMS_STATUS_URI, true, smsSentObserver);

        //Obtain the FirebaseAnalytics instance - Initial tests with the Firabase framework
        //More useful information can be placed here to analyze how the user is using the app
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        dao = new FirebaseDAO();

        Query q = dao.getFriendsDatabase().child(Utils.getUid());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Friend f = ds.getValue(Friend.class);
                    Log.d(TAG,"GETTING USER");
                    Log.d(TAG,"uid is" + f.getfriendID());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(TAG,"test query:"+q.toString());

        analyzeText = getIntent().getIntExtra(IntentParam.ANALYZE, 0);
        //Start analyzing texts
        mText = (FancyButton) findViewById(R.id.button_analyzing_text_main_activity);
        mGraph = (GraphView) findViewById(R.id.main_activity_graph);
            mText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGraph.getGridLabelRenderer().setNumVerticalLabels(3);
                    mGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    mGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    mGraph.setTitleColor(Color.WHITE);
                    mGraph.setTitle("Drunk Texting Behavior");

                    //If permission to read text messages has been granted then proceed to do so and analyze the drunk texting behavior
                    //otherwise show rationale and request permission
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Check texts
                        readTexts();

                    } else {
                        // Show rationale and request permission.
                        Toast.makeText(MainActivity.this,
                                "This App requires your permission to access your texts and evaluate your drunk texting behavior",
                                Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.READ_SMS},
                                READ_SMS_REQUEST_CODE);
                    }
                }
            });


        //start profile activity
        mProfile = (FancyButton) findViewById(R.id.button_profile_main_activity);
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        //start friends activity
        mFriends = (FancyButton) findViewById(R.id.button_friends_main_activity);
        mFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(MainActivity.this, FriendsActivity.class);
                //Intent i = new Intent(MainActivity.this, FriendsFullScreenSearchActivity.class);
                Intent i = new Intent(MainActivity.this, FriendsViewPagerActivity.class);
                startActivity(i);
            }
        });


        //start going out settings
        mSettings = (FancyButton) findViewById(R.id.button_out_setting_main_activity);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GoingOutSettingsActivity.class);
                startActivity(i);
            }
        });

        mMap = (FancyButton) findViewById(R.id.button_maps_main_activity);
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        Log.d(TAG,"data access created");

        //start drunk mode when slide is completed
        ((SlideView) findViewById(R.id.switch_main_activity)).setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                dao.getUserDatabase().child(Utils.getUid()).child("drunk").setValue(true);
                Intent i = new Intent(MainActivity.this, DrunkModeDefaultActivity.class);
                startActivity(i);
            }
        });

    }

    //disable back button
    @Override
    public void onBackPressed() {
    }

    /**
     * This method is capable of reading the SMS that the user has sent and perform the drunk texting analysis using Nabil's algorithm
     * The function stores the number of drunk texts that were sent on a given day and stores this information in a hashMap that is passed
     * displayGraph() function to show this information in a nice graph.
     */
    public void readTexts() {
        DrunkAlgorithm drunkTest = new DrunkAlgorithm(this);
        Date initialDate = null;
        Date finalDate = null;

        cursor = getContentResolver().query(Uri.parse("content://sms/sent"), null, null, null, null);
        HashMap<Date,Integer> drunkDays = new HashMap<Date,Integer>();
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                Date messageDate = millisToDate(Long.parseLong(cursor.getString(cursor.getColumnIndex("date"))));
                Log.d("DATE-TO-STRING", dateToString(messageDate));
                Log.d("STRING-TO-DATE", stringToDate(dateToString(messageDate)).toString());
                messageDate = stringToDate(dateToString(messageDate));
                if (finalDate == null){
                    finalDate = messageDate;
                }
                initialDate = messageDate;

                //using drunk text analysis to determine whether a given text is considered drunk texting or not
                //depending on the results add the date to the drunkDays hashMap and increase the counter for that day
                Log.d(TAG,"Drunk test:"+cursor.getString(12));
                if (drunkTest.isDrunk(cursor.getString(12))) {
                    dao.getUserDatabase().child(Utils.getUid()).child("isDrunk").setValue(true);
                    if (drunkDays.containsKey(messageDate)) {
                        Log.e("ADDING DATE", messageDate.toString());
                        drunkDays.put(messageDate, drunkDays.get(messageDate) + 1);
                        Log.e("TOTAL DRUNKDAYS", drunkDays.get(messageDate).toString());
                        //Toast.makeText(getBaseContext(), drunkDays.get(messageDate), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ADDING DATE", messageDate.toString());
                        drunkDays.put(messageDate, 1);
                    }
                }
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
            Toast.makeText(MainActivity.this,
                    "No texts available",
                    Toast.LENGTH_SHORT).show();
        }
        //Display the drunk texting behavior on a graph
        displayGraph(drunkDays, initialDate, finalDate);
    }

    //uses graph repository: http://www.android-graphview.org/
    //to be able to display the different dates and the number of drunk texts on that day on a nice graph
    public void displayGraph(HashMap<Date,Integer> drunkDays, Date initialDate, Date finalDate){
        if (drunkDays == null || drunkDays.isEmpty()){
            Toast.makeText(MainActivity.this,
                    "Congrats! You have no drunk texts",
                    Toast.LENGTH_LONG).show();
        }else{
            DataPoint[] dataPoints = new DataPoint[drunkDays.size()];
            ArrayList<Date> dateArray = new ArrayList<>();
            int index = 0;
            for(Date date:drunkDays.keySet()){
                //Log.e("DATE",date.toString());
                if (date != null){
                    //dataPoints[index] = new DataPoint(date,drunkDays.get(date));
                    //index++;
                    dateArray.add(date);
                }
            }
            Collections.sort(dateArray);

            for (Date date:dateArray){
                dataPoints[index] = new DataPoint(date,drunkDays.get(date));
                index++;
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            series.setTitle("Drunk Texts");
            series.setDrawDataPoints(true);
            mGraph.addSeries(series);

            // set date label formatter
            mGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MainActivity.this));
            mGraph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space

            // set manual x bounds to have nice steps

            mGraph.getViewport().setMinX(initialDate.getTime());
            mGraph.getViewport().setMaxX(finalDate.getTime());
            mGraph.getViewport().setXAxisBoundsManual(true);

            mGraph.getViewport().setScalable(true);
            mGraph.getViewport().setScrollable(true);
            //mGraph.getViewport().setMaxY(5);
            mGraph.getViewport().setMinY(0);
            mGraph.getViewport().setYAxisBoundsManual(true);
            mGraph.getGridLabelRenderer().setNumVerticalLabels(3);
            mGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
            mGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
            mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
            mGraph.setTitleColor(Color.WHITE);
            mGraph.setTitle("Drunk Texting Behavior");
            mGraph.getLegendRenderer().setVisible(true);
            mGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            mGraph.getLegendRenderer().setBackgroundColor(16777215);
            mGraph.getLegendRenderer().setTextColor(Color.WHITE);
            // as we use dates as labels, the human rounding to nice readable numbers is not necessary
            mGraph.getGridLabelRenderer().setHumanRounding(false);
        }
    }

    //If the user gave permission then proceed to read SMS and compute the drunk texting behavior
    //if the user didn't give permission then show rationale why we need permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == READ_SMS_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equals( Manifest.permission.READ_SMS )&&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //mMap.setMyLocationEnabled(true);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    //read messages
                    readTexts();
                }
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(MainActivity.this,
                        "Can't Analyze your drunk texting behavior without your permission. Go to phone settings to update your permissions",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //convert the millisecond format from the SMS attributes to a date
    public Date millisToDate(long currentTime) {
//        String finalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        //Date date = calendar.getTime();
        return calendar.getTime();
    }

    //Get a string of a simple date from a regular long date Wed Oct 16 00:00:00 CEST 2013
    //Conversion back and forth being made so we can get rid of the seconds and milliseconds and compare dates by day
    public String dateToString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        String dateInString = "Wed Oct 16 00:00:00 CEST 2013";
        try {
            SimpleDateFormat parse = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            Date datef = parse.parse(date.toString());
//            Log.e("FORMATTEDDATE",datef.toString());
//            Log.e("FORMATTEDDATE",formatter.format(datef));
            String date_to_format = formatter.format(datef);
            return formatter.format(datef);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    //Convert simple string date back to long date
    //Conversion back and forth being made so we can get rid of the seconds and milliseconds and compare dates by day
    public Date stringToDate(String sdate){
        //Date format_date = formatter.format(datef);
        SimpleDateFormat parse2 = new SimpleDateFormat("MM-dd-yyyy",Locale.ENGLISH);
        try{
            Date format_date = parse2.parse(sdate);
//            Log.e("FINALFORMAT",format_date.toString());
            return format_date;
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
        //Date final_date = formatter.parse(datef.toString());
        //Date format_date = new SimpleDateFormat("MM-dd").parse(formatter.format(datef));
    }
}
