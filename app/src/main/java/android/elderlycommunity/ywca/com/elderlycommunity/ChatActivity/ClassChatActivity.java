package android.elderlycommunity.ywca.com.elderlycommunity.ChatActivity;

import android.elderlycommunity.ywca.com.elderlycommunity.App;
import android.elderlycommunity.ywca.com.elderlycommunity.ClassListActivity.ClassListActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Attendance;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClassChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar progressBar;

    private String courseId;
    private String couresName;
    private String courseIconRef;

    private TextView courseNameView;
    private ImageView courseIcon;
    private EditText input;

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private ArrayList<ChatUser> chatUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_chat);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        chatUsers = new ArrayList<>();
        courseId = getIntent().getStringExtra("courseId");
        couresName = getIntent().getStringExtra("courseName");

        input = (EditText) findViewById(R.id.input);

        courseIconRef = getIntent().getStringExtra("courseIconRef");
        courseIcon = (ImageView) findViewById(R.id.courseIcon);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Utils.loadCircleImage(this, courseIcon, mStorageRef, courseIconRef);
        /*
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(mStorageRef.child(courseIconRef))
                .fitCenter()
                .placeholder(R.drawable.rotating_spinner)
                .error(R.drawable.ic_error)
                .into(courseIcon);
                */

        courseNameView = (TextView) findViewById(R.id.courseName);
        courseNameView.setText(couresName);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // enable this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        mAdapter = new ChatAdapter(this, chatUsers, mStorageRef);
        mRecyclerView.setAdapter(mAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Chats")
                .child(courseId)
                .orderByChild("timestamp")
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
                                        chatUsers.add(chatUser);

                                        // make sure chats are in order
                                        // prevent asynchronous response and update
                                        Collections.sort(chatUsers, new Comparator<ChatUser>() {
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
                                        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

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

    public void sendMsg(View v){
        String msg = input.getText().toString();
        if(msg.isEmpty())
            return;

        String key = mDatabase.child("Chats").push().getKey();

        HashMap<String, Object> postValues = new HashMap<>();
        postValues.put("userId", Utils.getUserId(FirebaseAuth.getInstance()));
        postValues.put("msg", msg);
        postValues.put("timestamp", new Date().getTime());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Chats/" + courseId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        input.setText("");
    }
}
