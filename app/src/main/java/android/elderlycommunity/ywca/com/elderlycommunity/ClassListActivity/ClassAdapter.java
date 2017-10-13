package android.elderlycommunity.ywca.com.elderlycommunity.ClassListActivity;


import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.ChatActivity.ClassChatActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.ChatUser;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView courseIcon;
        public ImageView[] userIcons;
        public TextView courseName;
        public TextView[] names, msgs, times;
        public RelativeLayout[] chatContainers;
        public View outerContainer;

        public ViewHolder(View rootView) {
            super(rootView);
            outerContainer = rootView.findViewById(R.id.outerContainer);
            courseIcon = (ImageView) rootView.findViewById(R.id.courseIcon);
            courseName = (TextView) rootView.findViewById(R.id.courseName);

            userIcons = new ImageView[3];
            names = new TextView[3];
            msgs = new TextView[3];
            times = new TextView[3];
            chatContainers = new RelativeLayout[3];

            userIcons[0] = (ImageView) rootView.findViewById(R.id.topUserIcon);
            userIcons[1] = (ImageView) rootView.findViewById(R.id.midUserIcon);
            userIcons[2] = (ImageView) rootView.findViewById(R.id.bottomUserIcon);

            names[0] = (TextView) rootView.findViewById(R.id.topUserName);
            names[1] = (TextView) rootView.findViewById(R.id.midUserName);
            names[2] = (TextView) rootView.findViewById(R.id.bottomUserName);

            msgs[0] = (TextView) rootView.findViewById(R.id.topUserMsg);
            msgs[1] = (TextView) rootView.findViewById(R.id.midUserMsg);
            msgs[2] = (TextView) rootView.findViewById(R.id.bottomUserMsg);
            //userMsg.setMovementMethod(LinkMovementMethod.getInstance());
            msgs[0].setAutoLinkMask(Linkify.WEB_URLS);
            msgs[1].setAutoLinkMask(Linkify.WEB_URLS);
            msgs[2].setAutoLinkMask(Linkify.WEB_URLS);

            times[0] = (TextView) rootView.findViewById(R.id.topUserReplyTime);
            times[1] = (TextView) rootView.findViewById(R.id.midUserReplyTime);
            times[2] = (TextView) rootView.findViewById(R.id.bottomUserReplyTime);

            chatContainers[0] = (RelativeLayout) rootView.findViewById(R.id.chatContainer1);
            chatContainers[1] = (RelativeLayout) rootView.findViewById(R.id.chatContainer2);
            chatContainers[2] = (RelativeLayout) rootView.findViewById(R.id.chatContainer3);
        }
    }

    private StorageReference mStorageRef;
    private ClassListActivity classListActivity;
    private ArrayList<ClassListActivity.CourseChatInfo> courseChatInfos;

    public ClassAdapter(ClassListActivity classListActivity,
                        ArrayList<ClassListActivity.CourseChatInfo> courseChatInfos) {
        this.classListActivity = classListActivity;
        this.courseChatInfos = courseChatInfos;
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_viewholder_class, parent, false);

        return new ClassAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ClassAdapter.ViewHolder holder, int position) {
        final ClassListActivity.CourseChatInfo item = courseChatInfos.get(position);

        Utils.loadCircleImage(classListActivity, holder.courseIcon, mStorageRef, item.course.storageRefChild);

        holder.courseName.setText(item.course.name);

        for(int i = 0; i < item.last3Chats.size(); i++){
            if(i == 3)
                break;

            ChatUser chatUser = item.last3Chats.get(i);

            final ImageView userIcon = holder.userIcons[i];
            Utils.loadCircleImage(classListActivity, userIcon, mStorageRef, chatUser.user.storageRefChild);

            holder.names[i].setText(chatUser.user.name);
            holder.msgs[i].setText(chatUser.chat.msg);
            /*
            if (Build.VERSION.SDK_INT >= 24) {
                holder.msgs[i].setText(Html.fromHtml(chatUser.chat.msg, FROM_HTML_MODE_COMPACT));
            } else {
                holder.msgs[i].setText(Html.fromHtml(chatUser.chat.msg));
            }
            */
            holder.times[i].setText(Utils.getLastReplyTime(chatUser.chat.timestamp));

            holder.chatContainers[i].setVisibility(View.VISIBLE);
        }

        for(int i = item.last3Chats.size(); i < 3; i++){
            holder.chatContainers[i].setVisibility(View.GONE);
        }

        holder.outerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toChatActivity(item.courseId, item.course.name, item.course.storageRefChild);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseChatInfos.size();
    }

    public void toChatActivity(String courseId, String courseName, String courseIconRef){
        Intent intent = new Intent(classListActivity, ClassChatActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("courseName", courseName);
        intent.putExtra("courseIconRef", courseIconRef);
        classListActivity.startActivity(intent);
    }
}
