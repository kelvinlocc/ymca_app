package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginGuideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_guide);
    }

    public void close(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
