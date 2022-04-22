package com.example.taboan_capstone.activity.driver.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.driver.DriverDeliveryActivity;
import com.example.taboan_capstone.activity.driver.DriverPortalActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.databinding.ActivityDriverPortalBinding;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.example.taboan_capstone.models.SellerStoreModel;
import com.example.taboan_capstone.views.AdapterDriverOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ActivityDriverPortalBinding binding;
    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;
    private TextView driverStats;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;
    private RecyclerView orderList;
    private RelativeLayout driverHasOrder, driverNoOrder,driverDelivery;
    private AdapterDriverOrder adapterDriverOrder;
    private TextView deliverBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        firebaseAuth = FirebaseAuth.getInstance();


        driverStats = view.findViewById(R.id.driver_portal_status_value);
        driverHasOrder = view.findViewById(R.id.driver_portal_order);
        driverNoOrder = view.findViewById(R.id.driver_portal_no_order);
        driverDelivery = view.findViewById(R.id.driver_portal_delivery);
        orderList = view.findViewById(R.id.driver_coming_orders);
        deliverBtn = view.findViewById(R.id.driver_portal_delivery_progress);


        deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DriverDeliveryActivity.class);
                startActivity(intent);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //added background thread to load first driver details before getting market order
                getDriverStatus();
            }
        },400);

        return view;
    }

    private void getDriverStatus(){
        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String status = "" +ds.child("availStat").getValue();
                            String isOnline = ""+ ds.child("online").getValue();

                            if(status.equals("Available") && isOnline.equals("true") ){
                                loadOrders();
                                driverHasOrder.setVisibility(View.VISIBLE);
                                driverNoOrder.setVisibility(View.GONE);
                                driverDelivery.setVisibility(View.GONE);

                            }else if(status.equals("Delivery") && isOnline.equals("true")){
                                driverHasOrder.setVisibility(View.GONE);
                                driverNoOrder.setVisibility(View.GONE);
                                driverDelivery.setVisibility(View.VISIBLE);

                            } else if(status.equals("Offline") && isOnline.equals("true")){
                                driverHasOrder.setVisibility(View.GONE);
                                driverNoOrder.setVisibility(View.VISIBLE);
                                driverDelivery.setVisibility(View.GONE);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrders(){

        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){
                            SellerStoreModel modelShop = ds.getValue(SellerStoreModel.class);

                            assert modelShop != null;
                            String shopUID = modelShop.getUid();

                            sellerOrderModelArrayList = new ArrayList<>();
                            DatabaseReference refOrders = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            refOrders.child(shopUID).child("Orders").orderByChild("orderStatus").equalTo("In Progress")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for(DataSnapshot ds : snapshot.getChildren()){

                                                String timestamp = "" + System.currentTimeMillis();
                                                SellerOrderModel modelOrderShop = ds.getValue(SellerOrderModel.class);
                                                String orderStats = modelOrderShop.getOrderStatus();
                                                String orderDAccepted = ""+ds.child("orderDAccepted").getValue();

                                                if(orderStats.equals("In Progress") &&  orderDAccepted.equals("null")){
                                                    sellerOrderModelArrayList.add(modelOrderShop);
                                                }

                                                adapterDriverOrder = new AdapterDriverOrder(getContext(), sellerOrderModelArrayList,firebaseAuth,timestamp);
                                                orderList.setAdapter(adapterDriverOrder);

                                                if(adapterDriverOrder.getItemCount() == 0){
                                                    driverHasOrder.setVisibility(View.GONE);
                                                    driverNoOrder.setVisibility(View.VISIBLE);

                                                }else{
                                                    driverHasOrder.setVisibility(View.VISIBLE);
                                                    driverNoOrder.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                        String timestamp = "" + System.currentTimeMillis();
                        adapterDriverOrder = new AdapterDriverOrder(getContext(), sellerOrderModelArrayList,firebaseAuth,timestamp);
                        orderList.setAdapter(adapterDriverOrder);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}