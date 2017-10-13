package android.elderlycommunity.ywca.com.elderlycommunity.SelectAlbum;

import android.content.DialogInterface;
import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.App;
import android.elderlycommunity.ywca.com.elderlycommunity.AttendanceActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.ProfileActivity.ProfileActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Album;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener {

    private ArrayList<Album> data;
    private DatabaseReference databaseReference;

    private TextView currentItemName;
    private TextView currentItemDate;
    private DiscreteScrollView itemPicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_album);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentItemName = (TextView) findViewById(R.id.item_name);
        currentItemDate = (TextView) findViewById(R.id.item_date);

        data = new ArrayList<>();
        databaseReference.child("Album")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                Album album = child.getValue(Album.class);
                                Log.d(App.TAG, album.toString());

                                data.add(album);
                            }
                            if(data.size() > 0){
                                itemPicker = (DiscreteScrollView) findViewById(R.id.item_picker);
                                itemPicker.setOrientation(Orientation.HORIZONTAL);
                                itemPicker.setOnItemChangedListener(ShopActivity.this);
                                itemPicker.setAdapter(new ShopAdapter(ShopActivity.this, data));
                                itemPicker.setItemTransitionTimeMillis(150);
                                itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                                        .setMinScale(0.8f)
                                        .build());

                                onItemChanged(data.get(0));
                            }
                            else {
                                App.getInstance().toastServerError();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            App.getInstance().toastServerError();
                        } finally {
                            ShopActivity.this.findViewById(R.id.progressBar).setVisibility(View.GONE);
                            ShopActivity.this.findViewById(R.id.carouselContainer).setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        App.getInstance().toastServerError();
                    }
                });
    }

    private void onItemChanged(Album item) {
        currentItemName.setText(item.name);
        currentItemDate.setText(item.dateString);
    }

    @Override
    public void onCurrentItemChanged(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        onItemChanged(data.get(position));
    }

    public void leftClick(View v){
        int movePos = itemPicker.getCurrentItem() - 1;
        if(movePos >= 0 && movePos < data.size()){
            itemPicker.smoothScrollToPosition(movePos);
        }
    }

    public void rightClick(View v){
        int movePos = itemPicker.getCurrentItem() + 1;
        if(movePos >= 1 && movePos < data.size()){
            itemPicker.smoothScrollToPosition(movePos);
        }
    }

    public void selectAndScrollAblum(View v){
        CharSequence albums[] = new CharSequence[data.size()];
        for (int i = 0; i < albums.length; i++){
            Album item = data.get(i);
            albums[i] = item.dateString + " " + item.name;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick an album");
        builder.setItems(albums, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemPicker.smoothScrollToPosition(i);
            }
        });
        builder.show();
    }

    public void finishClick(View v){
        finish();
    }
}
