package android.elderlycommunity.ywca.com.elderlycommunity.MainActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.elderlycommunity.ywca.com.elderlycommunity.App;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.News;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.elderlycommunity.ywca.com.elderlycommunity.App.PREFERENCE_NAME;
import static android.elderlycommunity.ywca.com.elderlycommunity.App.PREFERENCE_NAME_NEWS_RECORD;
import static android.elderlycommunity.ywca.com.elderlycommunity.App.TOKEN_KEY;
import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>  {
    MainActivity mainActivity;
    StorageReference mStorageRef;
    ArrayList<News> newses;
    SharedPreferences pref;
    Drawable drawableRead, drawableUnread;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView newsTitle, newsContent, postedTime, readRecord;
        public ImageView newsImage, readImageView;
        public ViewHolder(View rootView) {
            super(rootView);
            newsTitle = (TextView) rootView.findViewById(R.id.newsTitle);
            newsImage = (ImageView) rootView.findViewById(R.id.newsImage);
            newsContent = (TextView) rootView.findViewById(R.id.newsContent);
            postedTime = (TextView) rootView.findViewById(R.id.postedTime);

            readRecord = (TextView) rootView.findViewById(R.id.readRecord);
            readImageView = (ImageView) rootView.findViewById(R.id.readImageView);
        }
    }

    public NewsAdapter(MainActivity mainActivity, StorageReference mStorageRef, ArrayList<News> newses) {
        this.mainActivity = mainActivity;
        this.mStorageRef = mStorageRef;
        this.newses = newses;
        this.pref = App.getInstance().getSharedPreferences(PREFERENCE_NAME_NEWS_RECORD, Activity.MODE_PRIVATE);
        this.drawableRead = ContextCompat.getDrawable(mainActivity, R.drawable.ic_read);
        this.drawableUnread = ContextCompat.getDrawable(mainActivity, R.drawable.ic_unread);
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_viewholder_news, parent, false);

        return new NewsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NewsAdapter.ViewHolder holder, final int position) {
        final News news = newses.get(position);

        holder.newsTitle.setText(news.title);

        if (Build.VERSION.SDK_INT >= 24) {
            holder.newsContent.setText(Html.fromHtml(news.content, FROM_HTML_MODE_COMPACT));
        } else {
            holder.newsContent.setText(Html.fromHtml(news.content));
        }

        String postedTime = Utils.getLastReplyTime(news.timestamp);
        holder.postedTime.setText(postedTime);

        Utils.loadCircleImage(mainActivity,
                holder.newsImage,
                mStorageRef,
                news.storageRefChild);

        final boolean clicked = pref.getBoolean(news.key, false);
        if(clicked){
            holder.readImageView.setImageDrawable(drawableRead);
            holder.readRecord.setText("已讀");
        } else {
            holder.readImageView.setImageDrawable(drawableUnread);
            holder.readRecord.setText("未讀");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clicked){
                    holder.readImageView.setImageDrawable(drawableRead);
                    holder.readRecord.setText("已讀");
                }
                pref.edit().putBoolean(news.key, true).apply();

                Intent intent = new Intent(mainActivity, NewsDetailActivity.class);
                intent.putExtra("newsTitle", news.title);
                intent.putExtra("newsContent", news.content);
                intent.putExtra("storageRefChild", news.storageRefChild);
                mainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newses.size();
    }
}
