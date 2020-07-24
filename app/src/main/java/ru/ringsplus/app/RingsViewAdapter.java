package ru.ringsplus.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.RingItem;

public class RingsViewAdapter extends RecyclerView.Adapter<RingsViewAdapter.ViewHolder> {

    private List<RingItem> mRingItems;
    private LayoutInflater mInflater;
    private DeleteClickListener mDeleteClickListener;

    public RingsViewAdapter(Context context, List<RingItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mRingItems = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rings_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RingItem nextRingItem = mRingItems.get(position);

        holder.myTextView.setText(nextRingItem.getName());
    }

    @Override
    public int getItemCount() {
        return mRingItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton mDeleteButton;
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);

            mDeleteButton = itemView.findViewById(R.id.delete_item);
            myTextView = itemView.findViewById(R.id.stockName);

            mDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mDeleteClickListener != null) mDeleteClickListener.onDeleteButtonClick(view, getAdapterPosition());
        }
    }

    public RingItem getItem(int id) {
        return mRingItems.get(id);
    }

    public void setDeleteClickListener(DeleteClickListener itemClickListener) {
        this.mDeleteClickListener = itemClickListener;
    }

    public interface DeleteClickListener {
        void onDeleteButtonClick(View view, int position);
    }
}
