package android.elderlycommunity.ywca.com.elderlycommunity.MainActivity;

import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.App;
import android.elderlycommunity.ywca.com.elderlycommunity.ClassListActivity.ClassListActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity.ProfileActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.SupportActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Chat;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Lesson;
import android.elderlycommunity.ywca.com.elderlycommunity.models.News;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    private ArrayList<Lesson> lessons;
    private ArrayList<Chat> chats;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private StorageReference mStorageRef;
    private ArrayList<News> newses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // enable this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        newses = new ArrayList<>();
        mAdapter = new NewsAdapter(this, mStorageRef, newses);
        mRecyclerView.setAdapter(mAdapter);

        mDatabase.child("News").orderByChild("timestamp").limitToLast(200).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                Log.d(TAG, "onChildAdded dataSnapshot: " + dataSnapshot.toString());

                News news = dataSnapshot.getValue(News.class);
                news.key = dataSnapshot.getKey();

                Log.d(TAG, "Create Class News: " + news);

                newses.add(0, news);
                mAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                progressBar.setVisibility(View.GONE);
                App.getInstance().toastServerError();
            }
        });

        boolean isNotified = getIntent().getBooleanExtra("isNotified", false);
        String key = getIntent().getStringExtra("key");
        key = key == null ? "" : key;
        if(isNotified){
            Log.d(App.TAG, "To NoticeHistoryActivity");
            Intent intent = new Intent(this, NoticeHistoryActivity.class);
            intent.putExtra("noticeContent", key);
            startActivity(intent);
        }
    }

    public void showNotifications(View v){
        Log.d(App.TAG, "To NoticeHistoryActivity");
        startActivity(new Intent(this, NoticeHistoryActivity.class));
    }

    public void toSharingActivity(View v){
        startActivity(new Intent(this, ClassListActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void toProfileActivity(View v){
        startActivity(new Intent(this, ProfileActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void toSupportActivity(View v){
        startActivity(new Intent(this, SupportActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
