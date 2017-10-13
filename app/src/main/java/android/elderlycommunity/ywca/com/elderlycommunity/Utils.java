package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getUserId(FirebaseAuth firebaseAuthInstance){
        try{
            return firebaseAuthInstance.getCurrentUser().getEmail().replace(LoginActivity.EMAIL, "").toUpperCase();
        } catch (Exception e){
            Log.e(App.TAG, e.toString());
            return null;
        }
    }

    public static void loadCircleImage(final Activity activity,
                                       final ImageView imageView,
                                       final StorageReference mStorageRef,
                                       final String storageRefChild){
        Glide.with(activity)
                .using(new FirebaseImageLoader())
                .load(mStorageRef.child(storageRefChild))
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_loading_static)
                .error(R.drawable.ic_error)
                .into(new BitmapImageViewTarget(imageView){ // circle the image
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void loadImage(final Activity activity,
                                 final ImageView imageView,
                                 final StorageReference mStorageRef,
                                 final String storageRefChild){
        Glide.with(activity)
                .using(new FirebaseImageLoader())
                .load(mStorageRef.child(storageRefChild))
                //.fitCenter()
                .dontAnimate()
                .placeholder(R.drawable.ic_loading_static)
                .error(R.drawable.ic_error)
                .into(imageView);
    }


    public final static SimpleDateFormat sdf_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    /*public final static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, EEE, dd MMM yyyy");
    public static String getDateString(Date date){
        return sdf.format(date);
    }*/

    public final static int ONE_MINUTE = 60000;
    public final static int ONE_HOUR = 3600000;
    public final static int ONE_DAY = 86400000;

    public static String getLastReplyTime(Date lastReplyDate){
        long diff = new Date().getTime() - lastReplyDate.getTime();

        //less than one minute, 1000*60
        if (diff < ONE_MINUTE)
            return "1m";
            //less than 1 hour, 1000*60*60
        else if (diff < ONE_HOUR){
            int intTime = (int) (diff / ONE_MINUTE);
            return (intTime + "m");
        }
        //less than one day, 1000*60*60*24
        else if (diff < ONE_DAY){
            int intTime = (int) (diff / ONE_HOUR);
            return (intTime + "h");
        }
        else{
            int intTime = (int) (diff / ONE_DAY);
            return (intTime + "d");
        }
    }

    public static String getLastReplyTime(long timestamp){
        long diff = new Date().getTime() - timestamp;

        //less than one minute, 1000*60
        if (diff < ONE_MINUTE)
            return "1分鐘";
            //less than 1 hour, 1000*60*60
        else if (diff < ONE_HOUR){
            int intTime = (int) (diff / ONE_MINUTE);
            return (intTime + "分鐘");
        }
        //less than one day, 1000*60*60*24
        else if (diff < ONE_DAY){
            int intTime = (int) (diff / ONE_HOUR);
            return (intTime + "小時");
        }
        else{
            int intTime = (int) (diff / ONE_DAY);
            return (intTime + "日");
        }
    }
}
