package com.lava.portscan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lava.portscan.R;
import com.shersoft.portscan.NoticeDialogFragment;


import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    //    private String[] localDataSet;
    private ArrayList localDataSet;
    private NoticeDialogFragment.NoticeDialogListener listener;
    private NoticeDialogFragment noticeDialogFragment;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (Button) view.findViewById(R.id.textview);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet              String[] containing the data to populate views to be used
     *                             by RecyclerView.
     * @param listener
     * @param noticeDialogFragment
     */
    public CustomAdapter(ArrayList<?> dataSet, NoticeDialogFragment.NoticeDialogListener listener, NoticeDialogFragment noticeDialogFragment) {
        localDataSet = dataSet;
        this.listener = listener;
        this.noticeDialogFragment = noticeDialogFragment;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        TextView textView = viewHolder.getTextView();

        textView.setText(localDataSet.get(position).toString());
        textView.setOnClickListener(v -> {
            listener.onIpClicked(localDataSet.get(position).toString(), noticeDialogFragment);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
