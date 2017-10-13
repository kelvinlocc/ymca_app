package android.elderlycommunity.ywca.com.elderlycommunity;

import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.MainActivity.MainActivity;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                try {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgressStatus += 30;
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                    Thread.sleep(400);

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgressStatus += 100;
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                    Thread.sleep(200);

                    final String key = getIntent().getStringExtra("key");
                    if (key != null){
                        Log.d("FCM", "key: "+key);
                    }

                    if (user != null) { // user logged before
                        // The user's ID, unique to the Firebase project. Do NOT use this value to
                        // authenticate with your backend server, if you have one. Use
                        // FirebaseUser.getToken() instead.
                        mHandler.post(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                if (key != null){
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("isNotified", true);
                                    intent.putExtra("key", key);
                                    ActivityCompat.finishAffinity(WelcomeActivity.this);
                                }
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        });
                    }
                    else {
                        mHandler.post(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                if (key != null){
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ActivityCompat.finishAffinity(WelcomeActivity.this);
                                }
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
