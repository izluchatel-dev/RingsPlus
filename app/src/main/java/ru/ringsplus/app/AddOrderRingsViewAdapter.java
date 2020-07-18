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

public class AddOrderRingsViewAdapter extends RecyclerView.Adapter<AddOrderRingsViewAdapter.ViewHolder> {

    private List<RingItem> mRingItems;
    private LayoutInflater mInflater;
    private PlusClickListener mPlusClickListener;
    private MinusClickListener mMinusClickListener;

    AddOrderRingsViewAdapter(Context context, List<RingItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mRingItems = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.order_rings_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RingItem nextRingItem = mRingItems.get(position);

        holder.mRingName.setText(nextRingItem.getName());

        holder.mRingCount.setText(nextRingItem.getCount().toString());
    }

    @Override
    public int getItemCount() {
        return mRingItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton mPlusButton;
        ImageButton mMinusButton;
        TextView mRingName;
        TextView mRingCount;

        ViewHolder(View itemView) {
            super(itemView);

            mRingName = itemView.findViewById(R.id.ringName);
            mRingCount = itemView.findViewById(R.id.ringOrderCount);

            mPlusButton = itemView.findViewById(R.id.plus_item);
            mPlusButton.setOnClickListener((v) -> {
                if (mPlusClickListener != null) mPlusClickListener.onPlusClick(itemView, getAdapterPosition());
            });

            mMinusButton = itemView.findViewById(R.id.minus_item);
            mMinusButton.setOnClickListener((v) -> {
                if (mMinusClickListener != null) mMinusClickListener.onMinusClick(itemView, getAdapterPosition());
            });
        }
    }

    public RingItem getItem(int id) {
        return mRingItems.get(id);
    }

    void setPlusClickListener(PlusClickListener plusClickListener) {
        this.mPlusClickListener = plusClickListener;
    }

    void setMinusClickListener(MinusClickListener minusClickListener) {
        this.mMinusClickListener = minusClickListener;
    }

    public interface MinusClickListener {
        void onMinusClick(View view, int position);
    }

    public interface PlusClickListener {
        void onPlusClick(View view, int position);
    }
}
