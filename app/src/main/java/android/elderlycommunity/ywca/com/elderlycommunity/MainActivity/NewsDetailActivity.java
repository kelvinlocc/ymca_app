package android.elderlycommunity.ywca.com.elderlycommunity.MainActivity;

import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class NewsDetailActivity extends AppCompatActivity {

    StorageReference mStorageRef;

    public TextView newsTitle, newsContent;
    public ImageView newsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        newsTitle = (TextView) findViewById(R.id.newsTitle);
        newsImage = (ImageView) findViewById(R.id.newsImage);
        newsContent = (TextView) findViewById(R.id.newsContent);
        newsContent.setMovementMethod(LinkMovementMethod.getInstance());

        Intent intent = getIntent();
        String title = intent.getStringExtra("newsTitle");
        String content = intent.getStringExtra("newsContent");
        String storageRefChild = intent.getStringExtra("storageRefChild");

        newsTitle.setText(title);

        if (Build.VERSION.SDK_INT >= 24) {
            newsContent.setText(Html.fromHtml(content, FROM_HTML_MODE_COMPACT));
        } else {
            newsContent.setText(Html.fromHtml(content));
        }

        Utils.loadImage(this,
                newsImage,
                mStorageRef,
                storageRefChild);
    }

    public void finishClick(View v){
        finish();
    }
}
