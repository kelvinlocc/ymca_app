package android.elderlycommunity.ywca.com.elderlycommunity.models;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Chat {
    public long timestamp;
    public String msg;
    public String userId;
    public String userName;

    public Chat() {
        msg = "";
        userName = "";
    }

    public Chat(String userId, String msg, long timestamp, String userName) {
        this.userId = userId;
        this.msg = msg;
        this.timestamp = timestamp;
        this.userName = userName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("msg", msg);
        result.put("timestamp", timestamp);

        return result;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "timestamp=" + timestamp +
                ", msg='" + msg + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
