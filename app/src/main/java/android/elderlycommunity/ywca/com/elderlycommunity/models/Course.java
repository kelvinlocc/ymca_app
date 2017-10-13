package android.elderlycommunity.ywca.com.elderlycommunity.models;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Course {
    public String name;
    public String storageRefChild;
    public long startDate;
    public long endDate;
    public int totalLessons;
    public int totalStudents;

    public String courseId;

    public Course() {
        courseId = "";
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", storageRefChild='" + storageRefChild + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalLessons=" + totalLessons +
                ", totalStudents=" + totalStudents +
                '}';
    }
}
