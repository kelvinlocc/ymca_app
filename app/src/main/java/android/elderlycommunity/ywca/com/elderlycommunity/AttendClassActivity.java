package android.elderlycommunity.ywca.com.elderlycommunity;

import android.elderlycommunity.ywca.com.elderlycommunity.MainActivity.NewsAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AttendClassActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView className;
    private ImageView classIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_class);

        className = (TextView) findViewById(R.id.className);
        classIcon = (ImageView) findViewById(R.id.classIcon);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        className.setText(getIntent().getStringExtra("className"));
        Utils.loadImage(this, classIcon, mStorageRef, getIntent().getStringExtra("classIcon"));
    }
}
