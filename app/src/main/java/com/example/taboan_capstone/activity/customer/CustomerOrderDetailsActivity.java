package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CustomerOrderDetailsProductModel;
import com.example.taboan_capstone.models.ProductModel;
import com.example.taboan_capstone.views.AdapterOrderDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomerOrderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final int LOCATION_REQUEST_CODE = 100;
    private boolean isPermissionGranted;

    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;
    private FrameLayout frameLayout;
    private ImageView back;
    private TextView marketName, tvOrderId, tvStoreName, subtotal,total,inProgress,inCooking,inDelivered;
    private RecyclerView orderList;
    private GoogleMap orderMap;
    private String getOrderId,getOrderTo;

    private ArrayList<CustomerOrderDetailsProductModel> customerOrderDetailsProductModelArrayList;
    private AdapterOrderDetails adapterOrderDetails;
    private Marker dMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_details);

        firebaseAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.customer_order_d_close);
        marketName = findViewById(R.id.customer_order_d_market_name);
        tvOrderId = findViewById(R.id.customer_order_d_id_value);
        tvStoreName = findViewById(R.id.customer_order_d_store_name);
        subtotal = findViewById(R.id.customer_order_d_store_subtotal_value);
        total = findViewById(R.id.customer_order_d_store_total_value);
        inProgress = findViewById(R.id.customer_order_d_store_progress);
        inCooking = findViewById(R.id.customer_order_d_store_cooking);
        inDelivered = findViewById(R.id.customer_order_d_store_delivered);

        orderList = findViewById(R.id.customer_order_d_order_list);
        frameLayout = findViewById(R.id.customer_order_d_frame);

        if(getIntent() != null){
            getOrderId = getIntent().getStringExtra("orderId");
            getOrderTo = getIntent().getStringExtra("orderTo");
        }

        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.customer_order_d_frame, supportMapFragment).commit();

        if(isPermissionGranted){
            supportMapFragment.getMapAsync(this);
        }else{

        }

        back.setOnClickListener(v -> {
          onBackPressed();
        });

        checkPermission();
        loadOrderDetails();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadStoreName();
            loadOrderedItems();
        },700);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;
                } else {
                    Toast.makeText(CustomerOrderDetailsActivity.this, "Please turn on your location", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(CustomerOrderDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(CustomerOrderDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(CustomerOrderDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    44);
        }
    }


    private void loadOrderDetails(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(getOrderTo).child("Orders");
        ref.child(getOrderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String orderId = ""+snapshot.child("orderID").getValue();
                        String orderMarket = ""+snapshot.child("orderMarket").getValue();
                        String orderDateTime = ""+snapshot.child("orderDateTime").getValue();
                        String orderStatus = ""+snapshot.child("orderStatus").getValue();
                        String orderTotal = ""+snapshot.child("orderTotal").getValue();
                        String orderSub = ""+snapshot.child("orderSubtotal").getValue();
                        String orderFee = ""+snapshot.child("orderDevFee").getValue();

                        marketName.setText(orderMarket);
                        tvOrderId.setText(orderId);
                        subtotal.setText(orderSub);
                        total.setText(orderTotal);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderDateTime));
                        String formattedDate = DateFormat.format("MM/dd/yyyy hh:mm a", calendar).toString();

                        if(orderStatus.equals("Waiting")){
                            inProgress.setVisibility(View.VISIBLE);
                            frameLayout.setVisibility(View.GONE);
                            inCooking.setVisibility(View.GONE);
                            inDelivered.setVisibility(View.GONE);
                        } else if(orderStatus.equals("In Progress")){
                            inProgress.setVisibility(View.VISIBLE);
                            frameLayout.setVisibility(View.GONE);
                            inCooking.setVisibility(View.GONE);
                            inDelivered.setVisibility(View.GONE);
                        }   else if(orderStatus.equals("Ready")){
                            inProgress.setVisibility(View.VISIBLE);
                            frameLayout.setVisibility(View.GONE);
                            inCooking.setVisibility(View.GONE);
                            inDelivered.setVisibility(View.GONE);
                        } else if(orderStatus.equals("Delivery")){
                            frameLayout.setVisibility(View.VISIBLE);
                            inProgress.setVisibility(View.GONE);
                            inCooking.setVisibility(View.GONE);
                            inDelivered.setVisibility(View.GONE);
                        } else if(orderStatus.equals("Completed")){
                            frameLayout.setVisibility(View.VISIBLE);
                            inProgress.setVisibility(View.GONE);
                            inCooking.setVisibility(View.GONE);
                            inDelivered.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerOrderDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadStoreName(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(getOrderTo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String storeName = ""+ds.child("store_name").getValue();

                    tvStoreName.setText(storeName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerOrderDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrderedItems(){
        customerOrderDetailsProductModelArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(getOrderTo).child("Orders").child(getOrderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        customerOrderDetailsProductModelArrayList.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){
                            CustomerOrderDetailsProductModel productModel = ds.getValue(CustomerOrderDetailsProductModel.class);
                            customerOrderDetailsProductModelArrayList.add(productModel);
                        }
                        adapterOrderDetails = new AdapterOrderDetails(CustomerOrderDetailsActivity.this, customerOrderDetailsProductModelArrayList);
                        orderList.setAdapter(adapterOrderDetails);
                        adapterOrderDetails.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerOrderDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
        isPermissionGranted = true;
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Toast.makeText(this, "ACTION_LOCATION_SOURCE_SETTINGS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        orderMap = googleMap;
        if (ActivityCompat.checkSelfPermission(CustomerOrderDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CustomerOrderDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CustomerOrderDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    44);
        }else{
            orderMap.setMyLocationEnabled(false);
            orderMap.getUiSettings().setMyLocationButtonEnabled(false);
            orderMap.getUiSettings().setZoomControlsEnabled(false);
            orderMap.getUiSettings().setMapToolbarEnabled(false);

            DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
            ref.orderByChild("uid").equalTo(getOrderTo)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds: snapshot.getChildren()){
                                String uid = ""+ds.child("uid").getValue();
                                String storeLat = ""+ds.child("latitude").getValue();
                                String storeLong = ""+ds.child("longitude").getValue();


                                LatLng storeLocs = new LatLng(Double.parseDouble(storeLat) ,Double.parseDouble(storeLong));

                                orderMap.addMarker(new MarkerOptions().position(storeLocs).title("Store").icon(bitmapDescriptorFromVector(CustomerOrderDetailsActivity.this, R.drawable.tstorepin)));

                                DatabaseReference storeRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                                storeRef.child(uid).child("Orders").orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot ds : snapshot.getChildren()){
                                                    String orderDriverID = ""+ds.child("orderDriverID").getValue();
                                                    String orderBy = ""+ds.child("orderBy").getValue();
                                                    String orderStatus = ""+ds.child("orderStatus").getValue();

                                                    if(orderStatus.equals("Delivery")){
                                                        DatabaseReference dRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                                                        dRef.orderByChild("uid").equalTo(orderDriverID)
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                        for(DataSnapshot ds : snapshot.getChildren()){
                                                                            double driverCLat = Double.parseDouble(""+ds.child("latitude").getValue());
                                                                            double driverCLong = Double.parseDouble(""+ds.child("longitude").getValue());

                                                                            LatLng driverLoc = new LatLng(driverCLat,driverCLong);

                                                                            if(dMarker != null){
                                                                                dMarker.remove();
                                                                                dMarker = null;
                                                                            }
                                                                            dMarker = orderMap.addMarker(new MarkerOptions().position(driverLoc).title("You").icon(bitmapDescriptorFromVector(CustomerOrderDetailsActivity.this,R.drawable.triderpin)));
                                                                        }
                                                                    }
                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                        Toast.makeText(CustomerOrderDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                    DatabaseReference cust = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                                                    cust.orderByChild("uid").equalTo(orderBy)
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for(DataSnapshot ds : snapshot.getChildren()){
                                                                        double userCLat = Double.parseDouble(""+ds.child("latitude").getValue());
                                                                        double userCLong = Double.parseDouble(""+ds.child("longitude").getValue());

                                                                        LatLng userLoc = new LatLng(userCLat, userCLong);

                                                                        orderMap.addMarker(new MarkerOptions().position(userLoc).title("Customer").icon(bitmapDescriptorFromVector(CustomerOrderDetailsActivity.this, R.drawable.ic_person_pin)));
                                                                        orderMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc,15));

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Toast.makeText(CustomerOrderDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(CustomerOrderDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(CustomerOrderDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            orderMap.getUiSettings().setZoomControlsEnabled(true);
            orderMap.getUiSettings().setAllGesturesEnabled(true);
        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}