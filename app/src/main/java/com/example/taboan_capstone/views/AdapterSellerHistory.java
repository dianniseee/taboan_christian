package com.example.taboan_capstone.views;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        if(orderStatus.equals("Successful")){
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }else{
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
        holder.orderStatus.setText(""+orderStatus);
        holder.orderDate.setText(""+formattedDate);

        setUpName(sellerOrderModel,holder);
    }

    private void setUpName(SellerOrderModel sellerOrderModel,AdapterSellerHistoryHolder holder){
        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.orderByChild("uid").equalTo(sellerOrderModel.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String fName = ""+ds.child("first_name").getValue();
                            String lName = ""+ds.child("last_name").getValue();
                            holder.orderBy.setText(fName + " " + lName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
