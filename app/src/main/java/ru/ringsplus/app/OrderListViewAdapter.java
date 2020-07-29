package ru.ringsplus.app;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.RingOrderItem;

public class OrderListViewAdapter extends RecyclerView.Adapter<OrderListViewAdapter.ViewHolder> {

    private List<OrderItem> mOrderItems;
    private LayoutInflater mInflater;
    private OrderClickListener mOrderClickListener;
    private OrderCheckStatusClickListener mOrderCheckStatusClickListener;

    public OrderListViewAdapter(Context context, List<OrderItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mOrderItems = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.order_list_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderItem nextOrderItem = mOrderItems.get(position);

        holder.mTitleText.setText(nextOrderItem.getTitle());

        if (!nextOrderItem.getDetails().isEmpty()) {
            holder.mDescriptionText.setText(nextOrderItem.getDetails());
            holder.mDescriptionText.setVisibility(View.VISIBLE);
        } else {
            holder.mDescriptionText.setVisibility(View.GONE);
        }

        if ((nextOrderItem.getRingOrderItemList() != null) && (nextOrderItem.getRingOrderItemList().size() > 0)) {
            StringBuilder mRingOrderItems = new StringBuilder();
            for (int i = 0; i < nextOrderItem.getRingOrderItemList().size(); i++ ) {
                RingOrderItem nextRingOrderItem = nextOrderItem.getRingOrderItemList().get(i);

                mRingOrderItems.append("&#8226; ");
                mRingOrderItems.append(nextRingOrderItem.getRingName());
                mRingOrderItems.append(" - <b>(");
                mRingOrderItems.append(nextRingOrderItem.getCount());
                mRingOrderItems.append(")</b>");

                if (i + 1 < nextOrderItem.getRingOrderItemList().size()) {
                    mRingOrderItems.append("<br/>");
                }
            }
            holder.mOrderRingsText.setText(Html.fromHtml(mRingOrderItems.toString()));
            holder.mOrderRingsText.setVisibility(View.VISIBLE);
        } else {
            holder.mOrderRingsText.setVisibility(View.GONE);
        }

        if (!nextOrderItem.getAuthor().isEmpty()) {
            holder.mAuthor.setText(nextOrderItem.getAuthor());
            holder.mAuthor.setVisibility(View.VISIBLE);
        } else {
            holder.mAuthor.setVisibility(View.GONE);
        }

        switch (nextOrderItem.getOrderStatus()) {
            case NewOrder: {
                holder.mOrderIcon.setImageResource(R.drawable.order_icon_new);
                holder.mCheckStatusButton.setImageResource(R.drawable.execute_order);
                holder.mCheckStatusButton.setVisibility(View.VISIBLE);
                break;
            }
            case ExecuteOrder: {
                holder.mOrderIcon.setImageResource(R.drawable.order_icon_execute);
                holder.mCheckStatusButton.setImageResource(R.drawable.archive_order);
                holder.mCheckStatusButton.setVisibility(View.VISIBLE);
                break;
            }
            case ArchiveOrder: {
                holder.mOrderIcon.setImageResource(R.drawable.order_icon_archive);
                holder.mCheckStatusButton.setVisibility(View.GONE);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mOrderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton mCheckStatusButton;
        ImageView mOrderIcon;
        TextView mTitleText;
        TextView mDescriptionText;
        TextView mOrderRingsText;
        TextView mAuthor;

        ViewHolder(View itemView) {
            super(itemView);

            mOrderIcon = itemView.findViewById(R.id.order_icon);
            mTitleText = itemView.findViewById(R.id.titleName);
            mDescriptionText = itemView.findViewById(R.id.description_text);
            mOrderRingsText = itemView.findViewById(R.id.rings_list_text);
            mAuthor = itemView.findViewById(R.id.author);

            mCheckStatusButton = itemView.findViewById(R.id.check_order_status);
            mCheckStatusButton.setOnClickListener((view -> {
                if (mOrderCheckStatusClickListener != null) {
                    mOrderCheckStatusClickListener.onCheckStatusButtonClick(itemView, getAdapterPosition());
                }
            }));

            itemView.setOnClickListener((v) -> {
                if (mOrderClickListener != null) {
                    mOrderClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }
    }

    public OrderItem getItem(int id) {
        return mOrderItems.get(id);
    }

    public void setOrderClickListener(OrderClickListener itemClickListener) {
        this.mOrderClickListener = itemClickListener;
    }

    public void setOrderCheckStatusClickListener(OrderCheckStatusClickListener checkStatusClickListener) {
        this.mOrderCheckStatusClickListener = checkStatusClickListener;
    }

    public interface OrderClickListener {
        void onItemClick(View view, int position);
    }

    public interface OrderCheckStatusClickListener {
        void onCheckStatusButtonClick(View view, int position);
    }
}
