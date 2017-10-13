package android.elderlycommunity.ywca.com.elderlycommunity.ChatActivity;

import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.ChatUser;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView userName, userReplyTime, userMsg;
        public ImageView userIcon;
        public ViewHolder(View rootView) {
            super(rootView);
            userIcon = (ImageView) rootView.findViewById(R.id.userIcon);

            userName = (TextView) rootView.findViewById(R.id.userName);
            userReplyTime = (TextView) rootView.findViewById(R.id.userReplyTime);
            userMsg = (TextView) rootView.findViewById(R.id.userMsg);
            //userMsg.setMovementMethod(LinkMovementMethod.getInstance());
            userMsg.setAutoLinkMask(Linkify.WEB_URLS);
        }
    }

    private ArrayList<ChatUser> chatUsers;
    private ClassChatActivity chatActivity;
    private StorageReference mStorageRef;

    public ChatAdapter(ClassChatActivity chatActivity, ArrayList<ChatUser> chatUsers, StorageReference mStorageRef) {
        this.chatActivity = chatActivity;
        this.chatUsers = chatUsers;
        this.mStorageRef = mStorageRef;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_viewholder_chat, parent, false);

        return new ChatAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ChatAdapter.ViewHolder holder, int position) {
        ChatUser item = chatUsers.get(position);

        Utils.loadCircleImage(chatActivity, holder.userIcon, mStorageRef, item.user.storageRefChild);

        holder.userName.setText(item.user.name);
        holder.userMsg.setText(item.chat.msg);
        /*
        if (Build.VERSION.SDK_INT >= 24) {
            holder.userMsg.setText(Html.fromHtml(item.chat.msg, FROM_HTML_MODE_COMPACT));
        } else {
            holder.userMsg.setText(Html.fromHtml(item.chat.msg));
        }
        */
        holder.userReplyTime.setText(Utils.getLastReplyTime(item.chat.timestamp));
    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }
}
