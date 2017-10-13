package android.elderlycommunity.ywca.com.elderlycommunity.models;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notice {
    public String title;
    public String content;
    public long timestamp;
    public String key;

    public boolean isRead;

    public Notice(){
        key = "";
    }

    @Override
    public String toString() {
        return "Notice{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
