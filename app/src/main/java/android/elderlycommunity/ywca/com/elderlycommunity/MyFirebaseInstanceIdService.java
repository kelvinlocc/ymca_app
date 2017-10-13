package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.elderlycommunity.ywca.com.elderlycommunity.App.PREFERENCE_NAME;
import static android.elderlycommunity.ywca.com.elderlycommunity.App.TOKEN_KEY;
import static android.elderlycommunity.ywca.com.elderlycommunity.LoginActivity.EMAIL;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(App.TAG, "Refreshed token: " + refreshedToken);

        SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        pref.edit().putString(TOKEN_KEY, refreshedToken).apply();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        updateToken(refreshedToken);
    }

    private void updateToken(String token){
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
            return;

        String userId = user.getEmail().replace(EMAIL, "").toUpperCase();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Users").child(userId).child("token").setValue(token);
    }
}
