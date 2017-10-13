package android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity;

import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.AttendClassActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.ChatActivity.ChatAdapter;
import android.elderlycommunity.ywca.com.elderlycommunity.ChatActivity.ClassChatActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.ChatUser;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Course;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileClassAdapter extends RecyclerView.Adapter<ProfileClassAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView courseName;
        public ImageView courseIcon;
        public ViewHolder(View rootView) {
            super(rootView);
            courseIcon = (ImageView) rootView.findViewById(R.id.courseIcon);
            courseName = (TextView) rootView.findViewById(R.id.courseName);
        }
    }

    private ArrayList<Course> courses;
    private ProfileActivity activity;
    private StorageReference mStorageRef;

    public ProfileClassAdapter(ProfileActivity activity, ArrayList<Course> courses, StorageReference mStorageRef) {
        this.activity = activity;
        this.courses = courses;
        this.mStorageRef = mStorageRef;
    }

    @Override
    public ProfileClassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_viewholder_profile_class, parent, false);

        return new ProfileClassAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ProfileClassAdapter.ViewHolder holder, int position) {
        final Course item = courses.get(position);

        Utils.loadCircleImage(activity, holder.courseIcon, mStorageRef, item.storageRefChild);

        holder.courseName.setText(item.name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, AttendClassActivity.class);
                intent.putExtra("className", item.name);
                intent.putExtra("classIcon", item.storageRefChild);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
