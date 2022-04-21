package com.example.taboan_capstone.views;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.SellerOrderModel;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterSellerHistory extends  RecyclerView.Adapter<AdapterSellerHistory.AdapterSellerHistoryHolder> {

    private Context context;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;

    public AdapterSellerHistory(Context context, ArrayList<SellerOrderModel> sellerOrderModelArrayList) {
        this.context = context;
        this.sellerOrderModelArrayList = sellerOrderModelArrayList;
    }

    @NonNull
    @Override
    public AdapterSellerHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_history_seller,null);
        return new AdapterSellerHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSellerHistoryHolder holder, int position) {
        SellerOrderModel sellerOrderModel = sellerOrderModelArrayList.get(position);

        String orderID = sellerOrderModel.getOrderID();
        String orderBy = sellerOrderModel.getOrderBy();
        String orderStatus = sellerOrderModel.getOrderStatus();
        String orderDate = sellerOrderModel.getOrderDateTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderDate));
        String formattedDate = DateFormat.format("MM/dd/yyyy",calendar).toString();

        holder.orderId.setText("#"+orderID);
        holder.orderBy.setText("#"+orderBy);

        if(orderStatus.equals("Successful")){
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }else{
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
        holder.orderStatus.setText(""+orderStatus);
        holder.orderDate.setText(""+formattedDate);

    }

    @Override
    public int getItemCount() {
        return sellerOrderModelArrayList.size();
    }

    class AdapterSellerHistoryHolder extends RecyclerView.ViewHolder{

        private TextView orderId,orderDate,orderStatus,orderBy;
        public AdapterSellerHistoryHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.row_history_seller_id);
            orderDate = itemView.findViewById(R.id.row_history_seller_date);
            orderStatus = itemView.findViewById(R.id.row_history_seller_status);
            orderBy = itemView.findViewById(R.id.row_history_seller_order_by_value);
        }
    }
}
