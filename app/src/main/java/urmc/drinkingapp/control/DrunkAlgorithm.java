package urmc.drinkingapp.control;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import urmc.drinkingapp.MainActivity;

/**
 * Created by Shengqi Suizhu on 2017/8/2.
 * Algorithm for calculating level of drunkness
 */

public class DrunkAlgorithm {

    private final static String TAG = "DrunkAlgorithm";
    private Context mContext;
    private HashMap<String, Float> params;
    private ArrayList<String> bagOfWords;

    public DrunkAlgorithm(Context c){
        mContext = c;
        params = readParameters();
        bagOfWords = getDrunkWords();
    }

    //Read drunk words (bag of words) from .txt file in the assets folder
    private ArrayList<String> getDrunkWords(){
        ArrayList<String> BOW = new ArrayList<String>();
        try{
            InputStreamReader is = new InputStreamReader(mContext.getAssets().open("bank_of_words.txt"));
            try (BufferedReader br = new BufferedReader(is)) {
                String line;
                while ((line = br.readLine()) != null) {
                    Log.e(TAG,"READFILE BOW"+line);
                    BOW.add(line);
                }
            }
        }catch (Exception e){
            Log.e("FILEREADER","Exception Drunk Words");
        }
        return BOW;
    }

    //Nabil's algorithm to determine if a given text is considered as drunk texting or not
    public boolean isDrunk(String text) {
        text = text.toLowerCase();
        boolean first_test  = false;
        for(String test: bagOfWords){
            if(text.contains(test)){
                first_test = true;
                break;
            }
        }
        if(first_test) {
            float threshold = 0;
            for(String key: params.keySet()){
                if(text.contains(key)){
                    threshold += params.get(key);
                }
            }
            if(threshold > 0){
                Log.e(TAG,"DRUNKTEST true"+" "+threshold);
                return true;
            }
            else{
                Log.e(TAG,"DRUNKTEST false"+" "+threshold);
                return false;
            }
        }
        else {
            Log.e(TAG, "DRUNKTEST false no threshold");
            return false;
        }
    }

    //Read parameters from .txt file in the assets folder
    private HashMap<String, Float> readParameters(){
        HashMap<String, Float> params = new HashMap<String, Float>();
        //try (BufferedReader br = new BufferedReader(new FileReader("par1.txt"))) {
        try{
            InputStreamReader is = new InputStreamReader(mContext.getAssets().open("par1.txt"));
            try (BufferedReader br = new BufferedReader(is)) {
                String line;
                while ((line = br.readLine()) != null) {
                    Log.e("READFILE",line);
                    String[] parpr = line.split("	");
                    Float reg = Float.parseFloat(parpr[0]);
                    params.put(parpr[1], reg);
                }
            }
        }catch (Exception e){
            Log.e("FILEREADER","Exception Params");
        }
        return params;
    }
}
