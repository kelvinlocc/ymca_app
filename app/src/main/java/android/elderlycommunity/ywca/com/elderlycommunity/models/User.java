package android.elderlycommunity.ywca.com.elderlycommunity.models;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String storageRefChild;

    public User(){
        name = "";
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", storageRefChild='" + storageRefChild + '\'' +
                '}';
    }
}
