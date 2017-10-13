package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.Activity;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Attendance;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CheckBox checkBox;
        public ViewHolder(View rootView) {
            super(rootView);
            checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        }
    }

    private AttendanceActivity activity;
    private ArrayList<AttendanceActivity.StudentIdName> studentIdNames;

    public AttendanceAdapter(AttendanceActivity activity, ArrayList<AttendanceActivity.StudentIdName> studentIdNames) {
        this.studentIdNames = studentIdNames;
        this.activity = activity;
    }

    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_viewholder_attendance, parent, false);

        return new AttendanceAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AttendanceAdapter.ViewHolder holder, int position) {
        final AttendanceActivity.StudentIdName item =  studentIdNames.get(position);
        holder.checkBox.setText(item.id + " - " + item.name);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.attend = holder.checkBox.isChecked();
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentIdNames.size();
    }
}
