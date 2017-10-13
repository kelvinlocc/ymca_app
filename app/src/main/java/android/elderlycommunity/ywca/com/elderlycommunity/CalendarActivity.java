package android.elderlycommunity.ywca.com.elderlycommunity;

import android.elderlycommunity.ywca.com.elderlycommunity.models.Attendance;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Course;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference mDatabase;

    // default calendar view has not event label feature
    // private CalendarView calendarView;
    private CaldroidFragment caldroidFragment;
    private ColorDrawable colorDrawable;
    private ColorDrawable colorDrawableSelect;

    private ArrayList<Attendance> attends;
    private ArrayList<Attendance> dispplayAttends;
    private Date previousSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        attends = new ArrayList<>();
        dispplayAttends = new ArrayList<>();

        colorDrawable = new ColorDrawable(ContextCompat.getColor(this, R.color.mainBlue));
        colorDrawableSelect = new ColorDrawable(ContextCompat.getColor(this, R.color.selectedBlue));
        setupCalendarFragment(savedInstanceState);

        /*
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                try {
                    Log.d(App.TAG, "year, month, dayOfMonth: " + year + ", " + month + ", " + dayOfMonth);
                    Calendar calendar = Calendar.getInstance();
                    for(Attendance a : attends){
                        calendar.setTime(a.startDate);

                        a.display = (calendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth
                                && calendar.get(Calendar.MONTH) == month
                                && calendar.get(Calendar.YEAR) == year);

                        Log.d(App.TAG, "event: year, month, dayOfMonth: " + calendar.get(Calendar.YEAR) + ", " + calendar.get(Calendar.MONTH) + ", " + calendar.get(Calendar.DAY_OF_MONTH));
                    }
                    updateDisplayAttends();
                    mAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        */

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // enable this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        mAdapter = new CalendarEventAdapter(this, dispplayAttends);
        mRecyclerView.setAdapter(mAdapter);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = Utils.getUserId(FirebaseAuth.getInstance());
        mDatabase.child("Users")
                .child(userId)
                .child("Courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            String courseId = child.getKey();
                            mDatabase.child("Courses")
                                    .child(courseId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final Course course = dataSnapshot.getValue(Course.class);
                                            course.courseId = dataSnapshot.getKey();
                                            Log.d(App.TAG, "get course object: " + course.toString());

                                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                                if(child.getKey().equals("Attendances")){
                                                    Log.d(App.TAG, "getting Attendances ref: " + course.toString());

                                                    for(DataSnapshot attendObject : child.getChildren()){
                                                        mDatabase.child("Attendances")
                                                                .child(attendObject.getKey())
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        Attendance attend = dataSnapshot.getValue(Attendance.class);
                                                                        Log.d(App.TAG, "get attendance object: " + attend.toString());

                                                                        attend.courseName = course.name;
                                                                        attend.startDate = new Date(attend.startTime);
                                                                        attend.endDate = new Date(attend.endTime);
                                                                        attends.add(attend);
                                                                        Collections.sort(attends, new Comparator<Attendance>() {
                                                                            @Override
                                                                            public int compare(Attendance attend1, Attendance attend2)
                                                                            {
                                                                                if(attend1.startTime < attend2.startTime)
                                                                                    return -1;
                                                                                else if(attend1.startTime > attend2.startTime)
                                                                                    return 1;
                                                                                else return 0;
                                                                            }
                                                                        });
                                                                        Calendar today = Calendar.getInstance();
                                                                        today.setTime(new Date());

                                                                        Calendar eventDate = Calendar.getInstance();
                                                                        eventDate.setTime(attend.startDate);

                                                                        attend.display = (eventDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                                                                                && eventDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                                                                                && eventDate.get(Calendar.YEAR) == today.get(Calendar.YEAR));

                                                                        caldroidFragment.setBackgroundDrawableForDate(
                                                                                colorDrawable,
                                                                                attend.startDate);
                                                                        caldroidFragment.setTextColorForDate(R.color.white, attend.startDate);

                                                                        caldroidFragment.clearTextColorForDate(today.getTime());
                                                                        caldroidFragment.clearBackgroundDrawableForDate(today.getTime());

                                                                        caldroidFragment.refreshView();

                                                                        Log.d(App.TAG, "event: year, month, dayOfMonth: " + eventDate.get(Calendar.YEAR) + ", " + eventDate.get(Calendar.MONTH) + ", " + eventDate.get(Calendar.DAY_OF_MONTH));
                                                                        updateDisplayAttends();
                                                                        mAdapter.notifyDataSetChanged();
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                        Log.w(App.TAG, "loadPost:onCancelled", databaseError.toException());
                                                                    }
                                                                });
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(App.TAG, "loadPost:onCancelled", databaseError.toException());
                                        }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(App.TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });

    }

    private void updateDisplayAttends(){
        dispplayAttends.clear();
        for(Attendance attend : attends){
            if(attend.display){
                dispplayAttends.add(attend);
            }
        }
    }

    private void setupCalendarFragment(Bundle savedInstanceState){
        caldroidFragment = new CaldroidFragment();

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
            // args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
            // CaldroidFragment.TUESDAY); // Tuesday

            // Uncomment this line to use Caldroid in compact mode
            // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            // Uncomment this line to use dark theme
            // args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

            caldroidFragment.setArguments(args);

            // Attach to the activity
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.calendarContainer, caldroidFragment);
            t.commit();

            caldroidFragment.setCaldroidListener(new CaldroidListener() {
                @Override
                public void onSelectDate(Date date, View view) {
                    try {
                        caldroidFragment.setBackgroundDrawableForDate(
                                colorDrawableSelect,
                                date);
                        caldroidFragment.setTextColorForDate(R.color.white, date);
                        if(previousSelectedDate != null){
                            caldroidFragment.clearBackgroundDrawableForDate(previousSelectedDate);
                            caldroidFragment.clearTextColorForDate(previousSelectedDate);
                        }
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.setTime(date);
                        Log.d(App.TAG, "onSelectDate: year, month, dayOfMonth: " +
                                selectedDate.get(Calendar.YEAR) + ", " +
                                selectedDate.get(Calendar.MONTH) + ", " +
                                selectedDate.get(Calendar.DAY_OF_MONTH));

                        Calendar previous = Calendar.getInstance();
                        if(previousSelectedDate != null) previous.setTime(previousSelectedDate);
                        boolean hasResetEvent = false;

                        Calendar eventDate = Calendar.getInstance();
                        for(Attendance a : attends){
                            eventDate.setTime(a.startDate);

                            a.display = (eventDate.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
                                    && eventDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH)
                                    && eventDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR));

                            if(!hasResetEvent && previousSelectedDate != null
                                    && previous.get(Calendar.DAY_OF_MONTH) == eventDate.get(Calendar.DAY_OF_MONTH)
                                    && previous.get(Calendar.MONTH) == eventDate.get(Calendar.MONTH)
                                    && previous.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR)){
                                hasResetEvent = true;
                                caldroidFragment.setBackgroundDrawableForDate(
                                        colorDrawable,
                                        a.startDate);
                                caldroidFragment.setTextColorForDate(R.color.white, previousSelectedDate);
                            }

                            Log.d(App.TAG, "event: year, month, dayOfMonth: " +
                                    eventDate.get(Calendar.YEAR) + ", " +
                                    eventDate.get(Calendar.MONTH) + ", " +
                                    eventDate.get(Calendar.DAY_OF_MONTH));
                        }

                        previousSelectedDate = date;
                        Date today = new Date();
                        caldroidFragment.clearTextColorForDate(today);
                        caldroidFragment.clearBackgroundDrawableForDate(today);
                        caldroidFragment.refreshView();

                        updateDisplayAttends();
                        mAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onChangeMonth(int month, int year) {

                }
                @Override
                public void onLongClickDate(Date date, View view) {

                }
                @Override
                public void onCaldroidViewCreated() {
                    if (caldroidFragment.getLeftArrowButton() != null) {
                        Log.d(App.TAG, "Caldroid view is created");
                    }
                }
            });
        }
    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }
}
