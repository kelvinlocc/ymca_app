package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.Activity;
import android.elderlycommunity.ywca.com.elderlycommunity.models.Attendance;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView eventTitle, startTime, endTime;
        public View container;
        public ViewHolder(View rootView) {
            super(rootView);
            eventTitle = (TextView) rootView.findViewById(R.id.eventTitle);
            startTime = (TextView) rootView.findViewById(R.id.startTime);
            endTime = (TextView) rootView.findViewById(R.id.endTime);
            container = rootView.findViewById(R.id.container);
        }
    }

    private Activity activity;
    private ArrayList<Attendance> dispplayAttends;

    public CalendarEventAdapter(Activity activity, ArrayList<Attendance> dispplayAttends) {
        this.dispplayAttends = dispplayAttends;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_viewholder_event, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Attendance item =  dispplayAttends.get(position);
        holder.eventTitle.setText(item.courseName);
        SimpleDateFormat sdf_yyyyMMdd = new SimpleDateFormat("HH:mm");
        holder.startTime.setText(sdf_yyyyMMdd.format(new Date(item.startTime)));
        holder.endTime.setText(sdf_yyyyMMdd.format(new Date(item.endTime)));
    }

    @Override
    public int getItemCount() {
        return dispplayAttends.size();
    }
}
