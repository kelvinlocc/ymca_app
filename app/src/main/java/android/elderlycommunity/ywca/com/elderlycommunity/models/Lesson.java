package android.elderlycommunity.ywca.com.elderlycommunity.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Lesson {
    public String attendanceId;
    public String classId;
    public String className;
    public String endDate;
    public String haveAttend;
    public String joinedDate;
    public String lessonId;
    public int lessonNumber;
    public String participantId;
    public String startDate;
    public int totalLessons;
    public String tutorId;
    public String tutorName;
    public String userId;
    public String userName;

    public Lesson() {
        // Default constructor required for calls to DataSnapshot.getValue(Lesson.class)
    }

    public Lesson(String attendanceId, String classId, String className, String endDate, String haveAttend, String joinedDate, String lessonId, int lessonNumber, String participantId, String startDate, int totalLessons, String tutorId, String tutorName, String userId, String userName) {
        this.attendanceId = attendanceId;
        this.classId = classId;
        this.className = className;
        this.endDate = endDate;
        this.haveAttend = haveAttend;
        this.joinedDate = joinedDate;
        this.lessonId = lessonId;
        this.lessonNumber = lessonNumber;
        this.participantId = participantId;
        this.startDate = startDate;
        this.totalLessons = totalLessons;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.userId = userId;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "attendanceId='" + attendanceId + '\'' +
                ", classId='" + classId + '\'' +
                ", className='" + className + '\'' +
                ", endDate='" + endDate + '\'' +
                ", haveAttend='" + haveAttend + '\'' +
                ", joinedDate='" + joinedDate + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", lessonNumber=" + lessonNumber +
                ", participantId='" + participantId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", totalLessons=" + totalLessons +
                ", tutorId='" + tutorId + '\'' +
                ", tutorName='" + tutorName + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getHaveAttend() {
        return haveAttend;
    }

    public void setHaveAttend(String haveAttend) {
        this.haveAttend = haveAttend;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
