package android.elderlycommunity.ywca.com.elderlycommunity.ClassListActivity;

import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.App;
import android.elderlycommunity.ywca.com.elderlycommunity.LoginActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.MainActivity.MainActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity.ProfileActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.SupportActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Chat;
import android.elderlycommunity.ywca.com.elderlycommunity.models.ChatUser;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Course;
import android.elderlycommunity.ywca.com.elderlycommunity.models.User;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClassListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar progressBar;
    private TextView title;
    private boolean showMyCourses = false;

    private DatabaseReference mDatabase;
    String userId;

    private ArrayList<CourseChatInfo> courseChatInfos;
    private ArrayList<String> userCourseKeys;

    public class CourseChatInfo {
        public String courseId;
        public Course course;
        public ArrayList<ChatUser> last3Chats;

        public CourseChatInfo(String courseId, Course course){
            this.courseId = courseId;
            this.course = course;
            last3Chats = new ArrayList<>();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        userCourseKeys = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        title = (TextView) findViewById(R.id.title);

        courseChatInfos = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // enable this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        mAdapter = new ClassAdapter(this, courseChatInfos);
        mRecyclerView.setAdapter(mAdapter);

        userId = Utils.getUserId(FirebaseAuth.getInstance());
        Log.d(App.TAG, "get userId: " + userId);

        mDatabase.child("Users")
                .child(userId)
                .child("Courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            userCourseKeys.add(data.getKey());
                        }
                        // at beginning, must be invoked after fetching user course keys
                        fetchAllCourses();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(App.TAG, "Failed to read value.", databaseError.toException());
                        progressBar.setVisibility(View.GONE);
                        App.getInstance().toastServerError();
                    }
                });
    }

    public void showMyCourses(View v){
        if(userCourseKeys.size() == 0){
            Toast.makeText(this, "You don't have course yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        showMyCourses = !showMyCourses;
        if(showMyCourses){
            title.setText("我的課程");
            fetchUserCourses();
        } else {
            title.setText("所有課程");
            fetchAllCourses();
        }
    }

    public void fetchUserCourses(){
        // must clear previous data first
        courseChatInfos.clear();

        for(String courseKey : userCourseKeys){
            mDatabase.child("Courses")
                    .child(courseKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            fetchCourse(dataSnapshot);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(App.TAG, "Failed to read value.", databaseError.toException());
                            progressBar.setVisibility(View.GONE);
                            App.getInstance().toastServerError();
                        }
                    });
        }
    }

    public void fetchAllCourses(){
        // must clear previous data first
        courseChatInfos.clear();

        mDatabase.child("Courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot data) {
                        // for each course DataSnapShot
                        for(DataSnapshot dataSnapshot : data.getChildren()){
                            fetchCourse(dataSnapshot);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(App.TAG, "Failed to read value.", databaseError.toException());
                        progressBar.setVisibility(View.GONE);
                        App.getInstance().toastServerError();
                    }
                });
    }

    private void fetchCourse(DataSnapshot dataSnapshot){ // should only pass the course dataSnapShot
        Log.d(App.TAG, dataSnapshot.toString());
        String courseId = dataSnapshot.getKey();
        Course course = dataSnapshot.getValue(Course.class);
        course.courseId = courseId;
        Log.d(App.TAG, "get course: " + course.toString() + ", key: " + courseId);

        final CourseChatInfo adapterItem = new CourseChatInfo(courseId, course);
        courseChatInfos.add(adapterItem);

        mDatabase.child("Chats")
                .child(dataSnapshot.getKey())
                .orderByChild("timestamp")
                .limitToLast(3)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Chat chat = dataSnapshot.getValue(Chat.class);
                        Log.d(App.TAG, "get chat: " + chat.toString() + ", key: " + dataSnapshot.getKey());

                        mDatabase.child("Users")
                                .child(chat.userId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        Log.d(App.TAG, "get user: " + user.toString() + ", key: " + dataSnapshot.getKey());
                                        ChatUser chatUser = new ChatUser(chat, user);

                                        adapterItem.last3Chats.add(chatUser);
                                        // make sure chats are in order
                                        // prevent asynchronous response and update
                                        Collections.sort(adapterItem.last3Chats, new Comparator<ChatUser>() {
                                            @Override
                                            public int compare(ChatUser o1, ChatUser o2)
                                            {
                                                if(o1.chat.timestamp < o2.chat.timestamp)
                                                    return -1;
                                                else if(o1.chat.timestamp > o2.chat.timestamp)
                                                    return 1;
                                                else return 0;
                                            }
                                        });

                                        mAdapter.notifyDataSetChanged();
                                        if(adapterItem.last3Chats.size() >= 4){
                                            adapterItem.last3Chats.remove(0);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(App.TAG, "Failed to read value.", databaseError.toException());
                                        progressBar.setVisibility(View.GONE);
                                        App.getInstance().toastServerError();
                                    }
                                });
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
                        Log.w(App.TAG, "Failed to read value.", databaseError.toException());
                        progressBar.setVisibility(View.GONE);
                        App.getInstance().toastServerError();
                    }
                });
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

    public void toSupportActivity(View v){
        startActivity(new Intent(this, SupportActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
