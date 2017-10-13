package android.elderlycommunity.ywca.com.elderlycommunity.PhotoSlideActivity;

import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class PhotoSlideActivity extends AppCompatActivity {

    private ArrayList<String> images;
    private PhotoViewPager viewPager;
    private ImagePagerAdapter pagerAdapter;
    private TextView banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_slide);

        banner = (TextView) findViewById(R.id.banner);

        Bundle bundle = getIntent().getExtras();
        images = bundle.getStringArrayList("images");
        if(images == null) images = new ArrayList<>();

        viewPager = (PhotoViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), images);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(bundle.getInt("position"));
        banner.setText("第 " + (bundle.getInt("position") + 1) + " /" + images.size() + " 張");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                banner.setText("第 " + (position + 1) + " /" + images.size() + " 張");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class ImagePagerAdapter extends FragmentPagerAdapter {
        private ArrayList<String> images;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> images) {
            super(fm);
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(images.get(position));
        }
    }
}
