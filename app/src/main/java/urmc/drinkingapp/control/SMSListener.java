package urmc.drinkingapp.control;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SMSListener extends ContentObserver {

    private static final String TAG = "SMSListener";
    private Context mContext;
    private String contactId = "", contactName = "";
    private String smsBodyStr = "", phoneNoStr = "";
    private long previousTime = 0;
    private boolean isDrunk;
    private final String key = "drunk";

    private static DrunkAlgorithm mDrunkAlgorithm;
    private static final Uri SMS_STATUS_URI = Uri.parse("content://sms/sent");

    public SMSListener(Handler handler, Context ctx) {
        super(handler);
        mContext = ctx;
    }

    public void onChange(boolean selfChange) {
        try{
            Log.d(TAG,"Notification on SMS observer");
            Cursor sms_sent_cursor = mContext.getContentResolver().query(SMS_STATUS_URI, null, null, null, null);
            if (sms_sent_cursor != null) {
                if (sms_sent_cursor.moveToFirst()) {
                    String protocol = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("protocol"));
                    Log.e(TAG,"protocol : " + protocol);
                    //for sender  protocol is null
                    if(protocol == null){
                        int type = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("type"));
                        Log.d("Info","SMS Type : " + type);
                        // for actual state type=2
                        if(type == 2){
                            long smsDatTime;
                            smsBodyStr = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body")).trim();
                            phoneNoStr = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address")).trim();
                            smsDatTime = sms_sent_cursor.getLong(sms_sent_cursor.getColumnIndex("date"));
                            if(previousTime == 0) {
                                previousTime = smsDatTime;
                            }
                            else if(previousTime == smsDatTime){
                                return;
                            }
                            else{
                                previousTime = smsDatTime;
                            }
                            //handle text here
                            //smsbody_str test drunk algorithm
                            isDrunk = mDrunkAlgorithm.isDrunk(smsBodyStr);
                            //result putextra to
                            Intent intent = new Intent(this, );
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(key, isDrunk);
                            intent.putExtras(bundle);
                            
                            Log.d(TAG,"SMS Content : "+smsBodyStr);
                            Log.d(TAG,"SMS Phone No : "+phoneNoStr);
                            Log.d(TAG,"SMS Time : "+smsDatTime);
                        }
                    }
                }
            }
            else
                Log.e(TAG,"Send Cursor is Empty");
        }
        catch(Exception sggh){
            Log.e(TAG, "Error on onChange : "+sggh.toString());
        }
        super.onChange(selfChange);
    }//fn onChange

}//End of class SmsObserver