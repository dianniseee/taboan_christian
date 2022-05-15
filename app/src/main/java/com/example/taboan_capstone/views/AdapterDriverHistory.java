package com.example.taboan_capstone.views;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.DriverOrderModel;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterDriverHistory extends  RecyclerView.Adapter<AdapterDriverHistory.AdapterDriverHistoryHolder> {

    private Context context;
    private ArrayList<DriverOrderModel> driverOrderModelArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterDriverHistory(Context context, ArrayList<DriverOrderModel> driverOrderModelArrayList, FirebaseAuth firebaseAuth) {
        this.context = context;
        this.driverOrderModelArrayList = driverOrderModelArrayList;
        this.firebaseAuth = firebaseAuth;
    }

    @NonNull
    @Override
    public AdapterDriverHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_history_seller,null);
        return new AdapterDriverHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDriverHistoryHolder holder, int position) {
        DriverOrderModel driverOrderModel = driverOrderModelArrayList.get(position);

        String orderID = driverOrderModel.getOrderID();
        String orderBy = driverOrderModel.getOrderBy();
        String orderStatus = driverOrderModel.getOrderStatus();
        String orderDate = driverOrderModel.getOrderDateTime();

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

        loadCustomer(driverOrderModel,holder);

    }

    private void loadCustomer(DriverOrderModel driverOrderModel,AdapterDriverHistoryHolder holder){
        firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.orderByChild("uid").equalTo(driverOrderModel.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String fName = ""+ds.child("first_name").getValue();
                            String lName = ""+ds.child("last_name").getValue();
                            holder.orderBy.setText(fName + " " + lName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return driverOrderModelArrayList.size();
    }

    class AdapterDriverHistoryHolder extends RecyclerView.ViewHolder{

        private TextView orderId,orderDate,orderStatus,orderBy;

        public AdapterDriverHistoryHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.row_history_seller_id);
            orderDate = itemView.findViewById(R.id.row_history_seller_date);
            orderStatus = itemView.findViewById(R.id.row_history_seller_status);
            orderBy = itemView.findViewById(R.id.row_history_seller_order_by_value);
        }
    }
}
