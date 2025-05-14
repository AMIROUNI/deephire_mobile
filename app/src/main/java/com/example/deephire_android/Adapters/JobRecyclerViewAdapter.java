package com.example.deephire_android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deephire_android.Models.Job;
import com.example.deephire_android.R;

import com.google.android.material.button.MaterialButton;
import java.util.List;

public class JobRecyclerViewAdapter extends RecyclerView.Adapter<JobRecyclerViewAdapter.JobViewHolder> {
    private final List<Job> jobList;
    private final Context context;

    public JobRecyclerViewAdapter(List<Job> jobList, Context context) {
        this.jobList = jobList;
        this.context = context;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        // Bind job data to views
        holder.tvJobTitle.setText(job.getTitle() != null ? job.getTitle() : "N/A");
        holder.tvLocality.setText(job.getLocality() != null ? job.getLocality() : "N/A");
        holder.tvLocation.setText(job.getLocation() != null ? job.getLocation() : "N/A");
        holder.tvFormattedRelativeTime.setText(job.getFormattedRelativeTime() != null ? job.getFormattedRelativeTime() : "N/A");

        // Accessibility descriptions
        holder.btnOpenGps.setContentDescription("Open job location in GPS");
        holder.btnViewDetails.setContentDescription("View job details in browser");

        // GPS Button: Open location in GPS app
        holder.btnOpenGps.setOnClickListener(v -> {
            String location = (job.getLocality() != null ? job.getLocality() : "") + ", " + (job.getLocation() != null ? job.getLocation() : "");
            if (!location.trim().isEmpty()) {
                Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(location.trim()));
                Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
                intent.setPackage("com.google.android.apps.maps");
                try {
                    context.startActivity(intent);
                } catch (Exception e) {

                    context.startActivity(new Intent(Intent.ACTION_VIEW, geoUri));
                }
            }
        });

        // Browser Button: Open job link
        holder.btnViewDetails.setOnClickListener(v -> {
            String link = job.getLink();
            if (link != null && !link.trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvLocality, tvLocation, tvFormattedRelativeTime;
        MaterialButton btnOpenGps, btnViewDetails;
        JobRecyclerViewAdapter adapter;

        public JobViewHolder(@NonNull View itemView, JobRecyclerViewAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvLocality = itemView.findViewById(R.id.tvLocality);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvFormattedRelativeTime = itemView.findViewById(R.id.tvFormattedRelativeTime);
            btnOpenGps = itemView.findViewById(R.id.btnOpenGps);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
