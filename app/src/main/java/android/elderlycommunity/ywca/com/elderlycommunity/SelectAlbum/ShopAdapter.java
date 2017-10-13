package android.elderlycommunity.ywca.com.elderlycommunity.SelectAlbum;


import android.app.Activity;
import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.AlbumActivity.AlbumActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity.ProfileActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.Utils;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Album;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private ArrayList<Album> data;
    Activity activity;
    StorageReference mStorageRef;

    public ShopAdapter(Activity activity, ArrayList<Album> data) {
        this.data = data;
        this.activity = activity;
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_album_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Album item = data.get(position);

        /*Glide.with(holder.itemView.getContext())
                .load(data.get(position).getImage())
                .into(holder.image);
                */
        Utils.loadImage(activity,
                holder.image,
                mStorageRef,
                item.coverStorageRefChild);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> storageRefChilds = new ArrayList<String>();
                for (int i = 1; i <= item.storageRefChilds.size(); i++){
                    String ref = item.storageRefChilds.get("p" + i);
                    if(ref != null && !ref.isEmpty()){
                        storageRefChilds.add(ref);
                    }
                }
                Intent intent = new Intent(activity, AlbumActivity.class);
                intent.putStringArrayListExtra("storageRefChilds", storageRefChilds);
                intent.putExtra("title", item.name);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
