package android.elderlycommunity.ywca.com.elderlycommunity.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.App;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.News;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Notice;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.Date;

import static android.elderlycommunity.ywca.com.elderlycommunity.App.PREFERENCE_NAME;
import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class NoticeHistoryActivity extends AppCompatActivity {

    ArrayList<Notice> notices;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_history);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // enable this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        notices = new ArrayList<>();
        mAdapter = new NoticeAdapter();
        mRecyclerView.setAdapter(mAdapter);

        String noticeContent = getIntent().getStringExtra("noticeContent");
        if(noticeContent != null && !noticeContent.isEmpty()){
            Notice notice = new Notice();
            notice.content = getIntent().getStringExtra("noticeContent");
            notice.timestamp = new Date().getTime();
            notice.title = "YWCA 女青";
            notices.add(notice);
        }

        mDatabase.child("Notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                boolean addToFirst = notices.size() != 1;

                for(DataSnapshot dataSnapshot : data.getChildren()){
                    Notice notice = dataSnapshot.getValue(Notice.class);
                    notice.key = dataSnapshot.getKey();

                    if(dataSnapshot.hasChild("Users")){
                        DataSnapshot users = dataSnapshot.child("Users");
                        String userId = Utils.getUserId(FirebaseAuth.getInstance());
                        if(userId != null && users.hasChild(userId)){
                            notice.isRead = (boolean) users.child(userId).getValue();
                        }
                    }

                    if(addToFirst){
                        notices.add(0, notice);
                    } else {
                        notices.add(1, notice);
                    }
                }
                mAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(App.TAG, "loadPost:onCancelled", databaseError.toException());
                progressBar.setVisibility(View.GONE);
                App.getInstance().toastServerError();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title, content, postedTime, read;
        public ViewHolder(View rootView) {
            super(rootView);
            title = (TextView) rootView.findViewById(R.id.title);
            content = (TextView) rootView.findViewById(R.id.content);
            postedTime = (TextView) rootView.findViewById(R.id.postedTime);
            read = (TextView) rootView.findViewById(R.id.read);
        }
    }

    public class NoticeAdapter extends RecyclerView.Adapter<ViewHolder>  {

        Drawable drawableRead, drawableUnread;

        public NoticeAdapter() {
            this.drawableRead = ContextCompat.getDrawable(NoticeHistoryActivity.this, R.drawable.ic_read);
            this.drawableUnread = ContextCompat.getDrawable(NoticeHistoryActivity.this, R.drawable.ic_unread);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_viewholder_notice, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Notice notice = notices.get(position);

            holder.title.setText(notice.title);
            holder.content.setText(notice.content);

            String postedTime = Utils.getLastReplyTime(notice.timestamp);
            holder.postedTime.setText(postedTime);

            if(notice.isRead){
                holder.read.setText("已讀");
                holder.read.setCompoundDrawablesWithIntrinsicBounds(drawableRead, null, null, null);
                holder.itemView.setOnClickListener(null);
            } else {
                holder.read.setText("未讀, 請按此確認");
                holder.read.setCompoundDrawablesWithIntrinsicBounds(drawableUnread, null, null, null);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(notice.isRead)
                            return;

                        String userId = Utils.getUserId(FirebaseAuth.getInstance());
                        if(userId == null){
                            Log.e(App.TAG, "itemView OnClick: userId == null");
                            return;
                        }

                        if(notice.key != null && !notice.key.isEmpty()){
                            mDatabase.child("Notifications")
                                    .child(notice.key)
                                    .child("Users")
                                    .child(userId)
                                    .setValue(true);
                        }

                        notice.isRead = true;
                        holder.read.setText("已讀");
                        holder.read.setCompoundDrawablesWithIntrinsicBounds(drawableRead, null, null, null);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return notices.size();
        }
    }

    public void finishClick(View v){
        finish();
    }
}
