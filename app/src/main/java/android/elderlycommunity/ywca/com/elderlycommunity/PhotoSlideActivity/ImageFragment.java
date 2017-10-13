package android.elderlycommunity.ywca.com.elderlycommunity.PhotoSlideActivity;

import android.app.Activity;
import android.elderlycommunity.ywca.com.elderlycommunity.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageFragment extends Fragment {
    private ImageView imageView;
    private ProgressBar progressBar;
    private StorageReference mStorageRef;

    public static ImageFragment newInstance(String url) {
        ImageFragment f = new ImageFragment();

        Bundle args = new Bundle();
        args.putString("url", url);
        f.setArguments(args);

        return f;
    }

    public String getUrl() {
        return getArguments().getString("url");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        getImage(getUrl());
        return view;
    }

    private void getImage(final String url){
        Activity activity = getActivity();
        PhotoSlideActivity photoSlideActivity;
        if(! (activity instanceof PhotoSlideActivity) ) return;
        else photoSlideActivity = (PhotoSlideActivity) activity;

        // for firebase storage
        Glide.with(photoSlideActivity)
                .using(new FirebaseImageLoader())
                .load(mStorageRef.child(url))
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        new PhotoViewAttacher(imageView);
                        return false;
                    }
                })
                .error(R.drawable.ic_error)
                .into(imageView);

        // for web crawling
        /*
        Glide.with(photoSlideActivity)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        new PhotoViewAttacher(imageView);
                        return false;
                    }
                })
                .error(R.drawable.ic_error)
                .into(imageView);*/
    }
}

