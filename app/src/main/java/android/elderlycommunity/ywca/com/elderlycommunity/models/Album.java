package android.elderlycommunity.ywca.com.elderlycommunity.models;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class Album {
    public String name;
    public String dateString;
    public String coverStorageRefChild;
    public HashMap<String, String> storageRefChilds;

    public Album() {
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", dateString='" + dateString + '\'' +
                ", coverStorageRefChild='" + coverStorageRefChild + '\'' +
                ", storageRefChilds=" + storageRefChilds +
                '}';
    }
}
