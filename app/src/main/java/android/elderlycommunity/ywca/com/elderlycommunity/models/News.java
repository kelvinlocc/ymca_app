package android.elderlycommunity.ywca.com.elderlycommunity.models;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class News {

    public String title;
    public String storageRefChild;
    public String content;
    public long timestamp;
    public String key;

    public News() {
        key = "";
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", storageRefChild='" + storageRefChild + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
