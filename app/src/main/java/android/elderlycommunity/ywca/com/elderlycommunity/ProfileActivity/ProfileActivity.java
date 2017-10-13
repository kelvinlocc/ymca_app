package android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.elderlycommunity.ywca.com.elderlycommunity.AlbumActivity.AlbumActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.App;
import android.elderlycommunity.ywca.com.elderlycommunity.AttendanceActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.CalendarActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.ChatActivity.ChatAdapter;
import android.elderlycommunity.ywca.com.elderlycommunity.ClassListActivity.ClassListActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.LoginActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.MainActivity.MainActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.SelectAlbum.ShopActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.SupportActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Attendance;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Chat;
import android.elderlycommunity.ywca.com.elderlycommunity.models.ChatUser;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Course;
import android.elderlycommunity.ywca.com.elderlycommunity.models.User;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.elderlycommunity.ywca.com.elderlycommunity.App.PREFERENCE_NAME_NEWS_RECORD;
import static android.elderlycommunity.ywca.com.elderlycommunity.LoginActivity.EMAIL;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private ImageView userIcon;
    private TextView userId, userName;
    private View attendanceBtnContainer;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private ArrayList<Course> courses;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        courses = new ArrayList<>();
        attendanceBtnContainer = findViewById(R.id.attendanceBtnContainer);
        userId = (TextView) findViewById(R.id.userId);
        userName = (TextView) findViewById(R.id.userName);
        userIcon = (ImageView) findViewById(R.id.userIcon);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // set horizontal scroll
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // enable this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        mAdapter = new ProfileClassAdapter(this, courses, mStorageRef);
        mRecyclerView.setAdapter(mAdapter);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(App.TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else {
                    // User is signed out
                    Log.d(App.TAG, "onAuthStateChanged:signed_out");

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("Users").child(currentUserId).child("token").setValue("");

                    SharedPreferences pref = App.getInstance().getSharedPreferences(PREFERENCE_NAME_NEWS_RECORD, Activity.MODE_PRIVATE);
                    pref.edit().clear().apply();

                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        currentUserId = Utils.getUserId(FirebaseAuth.getInstance());
        Log.d(App.TAG, "get userId: " + userId);
        setUserInfo(currentUserId);
        setEnrolledClasses(currentUserId);
        enableTutorFunction(currentUserId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void setUserInfo(final String id){
        mDatabase.child("Users")
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Log.d(App.TAG, "get user: " + user.toString() + ", key: " + dataSnapshot.getKey());

                        userId.setText("編號: " + id);
                        userName.setText("姓名: " + user.name);
                        Utils.loadCircleImage(ProfileActivity.this,
                                ProfileActivity.this.userIcon,
                                ProfileActivity.this.mStorageRef,
                                user.storageRefChild);

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

    private void setEnrolledClasses(String userId){
        mDatabase.child("Users")
                .child(userId)
                .child("Courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            Log.d(App.TAG, child.toString());

                            mDatabase.child("Courses")
                                    .child(child.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.d(App.TAG, dataSnapshot.toString());
                                            String courseId = dataSnapshot.getKey();
                                            Course course = dataSnapshot.getValue(Course.class);
                                            course.courseId = courseId;
                                            Log.d(App.TAG, "get course: " + course.toString() + ", key: " + courseId);

                                            courses.add(course);
                                            mAdapter.notifyDataSetChanged();

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
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(App.TAG, "Failed to read value.", databaseError.toException());
                        progressBar.setVisibility(View.GONE);
                        App.getInstance().toastServerError();
                    }
                });
    }

    private void enableTutorFunction(String userId){
        mDatabase.child("Users")
                .child(userId)
                .child("isTutor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(App.TAG, "find whether current user is tutor: " + dataSnapshot.getValue()
                                + ", dataSnapshot: " + dataSnapshot);

                        if(dataSnapshot.getValue() != null && (Boolean) dataSnapshot.getValue()){
                            attendanceBtnContainer.setVisibility(View.VISIBLE);
                        }

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        App.getInstance().toastServerError();
                    }
                });
    }

    public void attendanceSheetClicked(View v){
        CharSequence courseNames[] = new CharSequence[courses.size()];
        for(int i = 0; i < courseNames.length; i++){
            courseNames[i] = courses.get(i).name;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇課程");
        builder.setItems(courseNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                final Course selected = courses.get(i);

                final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setTitle("Checking course's information");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                mDatabase.child("Courses")
                        .child(selected.courseId)
                        .child("Attendances")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<String> lessonIds = new ArrayList<String>();
                                for (DataSnapshot child : dataSnapshot.getChildren()){
                                    lessonIds.add(child.getKey());
                                }

                                CharSequence lessons[] = new CharSequence[lessonIds.size()];
                                for (int i = 0; i < lessons.length; i++){
                                    lessons[i] = "Section " + (i+1) + " (" + lessonIds.get(i) + ")";
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                builder.setTitle(selected.name + "\n選擇堂數");
                                builder.setItems(lessons, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String selectedSectionId = lessonIds.get(i);
                                        Intent intent = new Intent(ProfileActivity.this, AttendanceActivity.class);
                                        intent.putExtra("lessonId", selectedSectionId);
                                        intent.putExtra("courseId", selected.courseId);
                                        intent.putExtra("courseName", selected.name);
                                        startActivity(intent);
                                    }
                                });

                                progressDialog.dismiss();

                                builder.show();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();
                            }
                        });
            }
        });
        builder.show();
    }

    public void toAlbumActivity(View v){
        startActivity(new Intent(ProfileActivity.this, ShopActivity.class));
    }

    public void toCalendarActivity(View v){
        startActivity(new Intent(ProfileActivity.this, CalendarActivity.class));
    }

    public void ResetPassword(View v){

    }

    public void Logout(View v){
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
            return;

        FirebaseAuth.getInstance().signOut();
    }

    public void toMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void toSharingActivity(View v){
        startActivity(new Intent(this, ClassListActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void toSupportActivity(View v){
        startActivity(new Intent(this, SupportActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
