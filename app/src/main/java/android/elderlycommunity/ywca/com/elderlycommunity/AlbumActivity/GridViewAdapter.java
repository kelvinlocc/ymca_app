package android.elderlycommunity.ywca.com.elderlycommunity.AlbumActivity;


import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.PhotoSlideActivity.PhotoSlideActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private ArrayList<String> images;

    private AlbumActivity albumActivity;
    private LayoutInflater layoutInflater;
    private FrameLayout.LayoutParams params;

    private StorageReference mStorageRef;

    public GridViewAdapter(AlbumActivity albumActivity, ArrayList<String> images) { // gridview width = height
        this.albumActivity = albumActivity;
        this.images = images;
        this.layoutInflater = LayoutInflater.from(albumActivity);
        //this.params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.adapter_album_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            //viewHolder.container.setLayoutParams(params);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(albumActivity, PhotoSlideActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("images", images);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                albumActivity.startActivity(intent);
            }
        });

        final String url = images.get(position);

        // for firebase storage
        Utils.loadImage(albumActivity,
                viewHolder.imageView,
                mStorageRef,
                url);

        /* for web crawling
        Glide.with(albumActivity)
                .load(url)
                .placeholder(R.drawable.rotating_spinner)
                .error(R.drawable.ic_error)
                .dontAnimate()
                .into(viewHolder.imageView);
*/
        return convertView;
    }

}