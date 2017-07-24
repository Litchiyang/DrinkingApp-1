package urmc.drinkingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import urmc.drinkingapp.model.PhoneNumbers;

/**
 * Created by litchiyang on 6/27/17.
 */

public class PhoneNumberViewHolder extends RecyclerView.ViewHolder{

    private TextView mNumberView;

    protected static PhoneNumbers mPhoneNumber;
    private RelativeLayout mRecyclerView;
    private int click;

    //view holder
    public PhoneNumberViewHolder(View v) {
        super(v);
        mNumberView = (TextView)v.findViewById(R.id.view_phone_numbers);
        mRecyclerView = (RelativeLayout) v.findViewById(R.id.relative_layout_recyler_view);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                click++;

                if(click%2 == 1) {
                    args.putString("PHONENUMBER", mPhoneNumber.getNumber());
                    mRecyclerView.setSelected(true);
                } else {
                    args.putString("PHONENUMBER", "");
                    mRecyclerView.setSelected(false);
                }
            }
        });
    }

    //Bind phone numbers
    public void bindPhoneNumbers(PhoneNumbers numbers) {
        mPhoneNumber = numbers;
        mNumberView.setText(numbers.getNumber());

    }

}
