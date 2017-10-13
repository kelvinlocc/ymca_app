package android.elderlycommunity.ywca.com.elderlycommunity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class CenterInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_info);
        ImageView mapImageView = (ImageView) findViewById(R.id.mapImageView);
        new PhotoViewAttacher(mapImageView);
    }

    public void finishClick(View v){
        finish();
    }
}
