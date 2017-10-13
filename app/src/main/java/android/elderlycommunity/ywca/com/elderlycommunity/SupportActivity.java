package android.elderlycommunity.ywca.com.elderlycommunity;

import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.ClassListActivity.ClassListActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.MainActivity.MainActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity.ProfileActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SupportActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    private EditText feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        feedback = (EditText) findViewById(R.id.feedback);
    }

    public void sendFeedback(View v){
        String msg = feedback.getText().toString();
        if(msg.isEmpty())
            return;
        // generate new key for new data
        String key = mDatabase.child("Feedbacks").push().getKey();

        HashMap<String, Object> postValues = new HashMap<>();
        postValues.put("userId", Utils.getUserId(FirebaseAuth.getInstance()));
        postValues.put("msg", msg);
        postValues.put("timestamp", new Date().getTime());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Feedbacks/" + key, postValues);

        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(App.getInstance(), "Message Received", Toast.LENGTH_SHORT).show();
                feedback.setText("");
            }
        });
    }

    public void toCenterInfoActivity(View v){
        startActivity(new Intent(this, CenterInfoActivity.class));
    }

    public void toMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void toProfileActivity(View v){
        startActivity(new Intent(this, ProfileActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void toSharingActivity(View v){
        startActivity(new Intent(this, ClassListActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
