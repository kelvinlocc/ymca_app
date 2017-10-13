package android.elderlycommunity.ywca.com.elderlycommunity.models;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Attendance  {
    public long startTime;
    public long endTime;

    public String courseName;
    public Date startDate;
    public Date endDate;
    public boolean display = false;

    public Attendance(){
        courseName = "";
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", courseName='" + courseName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", display=" + display +
                '}';
    }
}
