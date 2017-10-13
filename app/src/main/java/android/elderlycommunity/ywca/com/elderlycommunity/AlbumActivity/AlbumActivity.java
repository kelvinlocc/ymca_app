package android.elderlycommunity.ywca.com.elderlycommunity.AlbumActivity;

import android.elderlycommunity.ywca.com.elderlycommunity.App;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    public final static String IMAGES_PAGE = "http://esd.ywca.org.hk/image.aspx";
    public final static String IMAGES_SRC_HEADER = "http://esd.ywca.org.hk/";

    private ArrayList<String> images;

    private GridView gridView;
    private GridViewAdapter gridViewAdapter;

    private ProgressBar progressBar;
    private Handler handler;
    private TextView banner, titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        handler = new Handler();
        titleView = (TextView) findViewById(R.id.title);
        banner = (TextView) findViewById(R.id.banner);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        gridView = (GridView) findViewById(R.id.gridView);

        String title = getIntent().getStringExtra("title");
        if(title != null)
            titleView.setText(title);

        images = getIntent().getStringArrayListExtra("storageRefChilds");
        if(images == null){
            images = new ArrayList<>();
        }

        gridViewAdapter = new GridViewAdapter(AlbumActivity.this, images);
        gridView.setAdapter(gridViewAdapter);

        banner.setText("(" + images.size() + " 張)");
        progressBar.setVisibility(View.GONE);

        //getAblumImages();
        //crawlImageLink(images, progressBar);
    }

    private void getAblumImages(){
        for(int i = 1; i < 14; i++){
            images.add("images/album/" + "p" + i + ".jpg");
        }
        banner.setText("(" + images.size() + " Photos)");
        gridViewAdapter.notifyDataSetChanged();

        progressBar.setVisibility(View.GONE);
    }

    private void crawlImageLink(final ArrayList<String> images, final ProgressBar progressBar){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(IMAGES_PAGE).get();

                    Log.d(App.TAG, "Jsoup.connect, doc.title: " + doc.title());

                    Elements items = doc.getElementsByClass("rrItem");

                    for (Element item : items){
                        Element div = item
                                .getElementsByTag("div").first()
                                .getElementsByTag("div").first(); // img in between <div><div> </div></div>
                        Element img = div.getElementsByTag("img").first();

                        if(img.hasClass("albumImage")){
                            String imageLink = img.attr("src");
                            Log.d(App.TAG, "found imageLink: " + imageLink);
                            imageLink = IMAGES_SRC_HEADER + imageLink.replace("w=160&h=107", "w=600&h=600");
                            Log.d(App.TAG, "change imageLink: " + imageLink);
                            images.add(imageLink);
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            banner.setText("(" + images.size() + " Photos)");
                            gridViewAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(App.TAG, e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    App.getInstance(),
                                    "Incorrect server response, please wait for fixing current error.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
    }

}
