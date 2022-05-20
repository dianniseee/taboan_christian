package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.taboan_capstone.DrawerBaseActivity;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.databinding.ActivityCustomerHomeBinding;
import com.example.taboan_capstone.databinding.ActivityCustomerOrdersBinding;
import com.example.taboan_capstone.databinding.ActivityCustomerProfileBinding;
import com.example.taboan_capstone.models.CustomerOrderDetailsModel;
import com.example.taboan_capstone.views.AdapterCustomerOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class CustomerOrdersActivity extends DrawerBaseActivity {

    private RecyclerView customer_order_rv;
    private ArrayList<CustomerOrderDetailsModel> customerOrderDetailsModelArrayList;
    private AdapterCustomerOrder adapterCustomerOrder;
    private AdapterCustomerOrder.OnAdapterClick mAdapterClick;
    private FirebaseAuth firebaseAuth;

    ActivityCustomerOrdersBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerOrdersBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        allocateActivtyTitle("Orders");
        firebaseAuth = FirebaseAuth.getInstance();

        customer_order_rv = binding.getRoot().findViewById(R.id.customer_order_rv);

        loadOrders();
        setOnclickListenerAdapter();
    }

    private void loadOrders(){

        customerOrderDetailsModelArrayList = new ArrayList<>();

        DatabaseReference ref1 = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(firebaseAuth.getUid()).child("Orders");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                customerOrderDetailsModelArrayList.clear();

                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        CustomerOrderDetailsModel modelOrderUser = ds.getValue(CustomerOrderDetailsModel.class);

                        customerOrderDetailsModelArrayList.add(modelOrderUser);
                    }

                    if(!customerOrderDetailsModelArrayList.isEmpty()){
                        Collections.sort(customerOrderDetailsModelArrayList,CustomerOrderDetailsModel.CustomerOrderDetailsModelDate);
                        adapterCustomerOrder = new AdapterCustomerOrder(CustomerOrdersActivity.this,customerOrderDetailsModelArrayList,mAdapterClick,firebaseAuth.getUid());
                        customer_order_rv.setAdapter(adapterCustomerOrder);
                        customer_order_rv.scrollToPosition(0);
                        adapterCustomerOrder.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerOrdersActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnclickListenerAdapter(){
        mAdapterClick = (v, position) -> {

            String orderId = customerOrderDetailsModelArrayList.get(position).getOrderID();
            String orderTo = customerOrderDetailsModelArrayList.get(position).getOrderTo();
            String orderStatus = customerOrderDetailsModelArrayList.get(position).getOrderStatus();

            if(orderStatus.equals("Delivery") || orderStatus.equals("Waiting") || orderStatus.equals("In Progress")){

                Intent intent = new Intent(this,CustomerOrderDetailsActivity.class);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderId",orderId);
                startActivity(intent);

            }else if(orderStatus.equals("Completed")){
                Intent intent = new Intent(this,CustomerHistoryDetailsActivity.class);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderId",orderId);
                startActivity(intent);
            }
        };
    }
}