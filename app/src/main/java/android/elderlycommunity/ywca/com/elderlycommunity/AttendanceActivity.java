package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.ProgressDialog;
import android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity.ProfileActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity.ProfileClassAdapter;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Course;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<StudentIdName> studentIdNames;
    public String courseId;
    public String courseName;
    public String lessonId;
    private TextView title;

    public class StudentIdName {
        public String id;
        public String name;
        public boolean attend;

        public StudentIdName(String id, String name){
            this.id = id;
            this.name = name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        courseId = getIntent().getStringExtra("courseId");
        courseName = getIntent().getStringExtra("courseName");
        lessonId = getIntent().getStringExtra("lessonId");

        title = (TextView) findViewById(R.id.title);
        title.setText(courseName + "\nSection" + lessonId.substring(lessonId.length()-3));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        studentIdNames = new ArrayList<>();

        // set horizontal scroll
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // enable this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        mAdapter = new AttendanceAdapter(this, studentIdNames);
        mRecyclerView.setAdapter(mAdapter);

        mDatabase.child("Attendances")
                .child(lessonId)
                .child("Students")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        studentIdNames.clear();

                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            final String studentId = child.getKey();
                            mDatabase.child("Users")
                                    .child(studentId)
                                    .child("name")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String name = dataSnapshot.getValue().toString();
                                            Log.d(App.TAG, "Get student name: " + name);

                                            StudentIdName studentIdName = new StudentIdName(studentId, name);
                                            studentIdNames.add(studentIdName);

                                            Log.d(App.TAG, "Update studentIdNames: " + studentIdNames.toString());

                                            mAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void updateAttendance(View v){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Checking course's information");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            for(AttendanceActivity.StudentIdName item : studentIdNames){
                mDatabase.child("Attendances")
                        .child(lessonId)
                        .child("Students")
                        .child(item.id)
                        .setValue(item.attend);
            }

            Toast.makeText(AttendanceActivity.this, "Attendance Updated", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e){
            Toast.makeText(AttendanceActivity.this, "Incorrect server response, please wait for fixing.", Toast.LENGTH_SHORT).show();
        } finally {
            progressDialog.dismiss();
        }

    }
}
