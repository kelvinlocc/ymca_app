package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    public final static String TAG = "YWCA Community";
    private static App instance;
    public static final String PREFERENCE_NAME = "MyPreferenceFileName";
    public static final String PREFERENCE_NAME_NEWS_RECORD = "NewsPreferenceFileName";
    public static final String TOKEN_KEY = "userTokenKey";

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        // setPersistenceEnabled() must be made before any other usage of FirebaseDatabase instance.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isOnline =  (netInfo != null && netInfo.isConnectedOrConnecting());

        if(!isOnline) {
            Toast.makeText(this, "No Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }
        return isOnline;
    }

    public void toastServerError(){
        Toast.makeText(
                this,
                "Incorrect server response, please wait for fixing current error.",
                Toast.LENGTH_SHORT).show();
    }

    public String getStoredToken(){
        SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(TOKEN_KEY, null);
    }
}
