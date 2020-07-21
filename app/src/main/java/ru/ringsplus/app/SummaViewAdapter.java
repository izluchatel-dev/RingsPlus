package ru.ringsplus.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.RingItem;

public class SummaViewAdapter extends RecyclerView.Adapter<SummaViewAdapter.ViewHolder> {

    private List<RingItem> mRingItems;
    private LayoutInflater mInflater;;

    SummaViewAdapter(Context context, List<RingItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mRingItems = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.summa_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RingItem nextRingItem = mRingItems.get(position);

        holder.mRingName.setText(nextRingItem.getName());

        String ringsOrderCount = String.format(mInflater.getContext().getString(R.string.summa_rings_order_postfix), nextRingItem.getCount());
        holder.mRingCount.setText(ringsOrderCount);
    }

    @Override
    public int getItemCount() {
        return mRingItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mRingName;
        TextView mRingCount;

        ViewHolder(View itemView) {
            super(itemView);

            mRingName = itemView.findViewById(R.id.summaRingName);
            mRingCount = itemView.findViewById(R.id.summaRingCount);
        }
    }

    public RingItem getItem(int id) {
        return mRingItems.get(id);
    }
}
