package com.example.taboan_capstone.activity.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.DriverModel;
import com.example.taboan_capstone.models.DriverOrderModel;
import com.example.taboan_capstone.models.DriverProductModel;
import com.example.taboan_capstone.views.AdapterDriverItems;
import com.google.android.gms.location.LocationRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DriverDeliveryActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 100;

    private LocationManager locationManager;
    private LocationRequest locationRequest;

    private final int MIN_TIME = 1000;
    private final int MIN_DISTANCE = 1;

    private GoogleMap dMap;
    private FirebaseAuth firebaseAuth;
    private Marker dMarker;

    private Button dItems,dRoute;
    private FrameLayout driverFrameLayout;
    private View deliveryFragment;

    private  ArrayList<DriverProductModel> driverProductModelArrayList;

    private double driverLat,driverLong,customerLat,customerLong;

    private String customerFullName;

    private AdapterDriverItems adapterDriverItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_delivery);

        dItems = findViewById(R.id.driver_delivery_orders);
        dRoute = findViewById(R.id.driver_delivery_route);

        firebaseAuth = FirebaseAuth.getInstance();

        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.driver_delivery_frame_layout,supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);


        dItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              showDialog(DriverDeliveryActivity.this);
            }
        });

        dRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRoute();
            }
        });

        setUpDriverInfo();
        locRequest();
        getLocationUpdates();
     //   readUpdates();
    }

    private void setUpDriverInfo(){

        driverProductModelArrayList = new ArrayList<>();

        DatabaseReference orderRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        orderRef.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("Delivery")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        driverProductModelArrayList.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){

                            DriverOrderModel  driverOrderModel = ds.getValue(DriverOrderModel.class);

                            DatabaseReference refItems = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            refItems.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Orders").child(driverOrderModel.getOrderID()).child("Items")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for(DataSnapshot ds : snapshot.getChildren()){

                                                DriverProductModel driverProductModel = ds.getValue(DriverProductModel.class);
                                                driverProductModelArrayList.add(driverProductModel);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(DriverDeliveryActivity.this, "1"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverDeliveryActivity.this, "2"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_dialog_driver_items,null);

        DriverOrderModel driverOrderModel = new DriverOrderModel();

        TextView orderId = view.findViewById(R.id.custom_dialog_driver_order_id);
        TextView orderName = view.findViewById(R.id.custom_dialog_driver_order_name);
        TextView totalValue = view.findViewById(R.id.custom_dialog_driver_total_value);
        Button delivered = view.findViewById(R.id.custom_dialog_driver_delivered);
        RecyclerView orderItems = view.findViewById(R.id.custom_dialog_driver_order_items);

        orderId.setText(""+driverOrderModel.getOrderID());
        orderName.setText(""+customerFullName);
        totalValue.setText(""+driverOrderModel.getOrderTotal());

        adapterDriverItems = new AdapterDriverItems(context,driverProductModelArrayList);
        orderItems.setAdapter(adapterDriverItems);

        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showRoute(){
        String address = "https://maps.google.com/maps?saddr=" + driverLat +"," + driverLong + "&daddr=" + customerLat + "," +customerLong;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void locRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void readUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    DriverModel driverModel = ds.getValue(DriverModel.class);

                    double updateLat = Double.parseDouble(driverModel.getLatitude());
                    double updateLong = Double.parseDouble(driverModel.getLongitude());

                    dMarker.setPosition(new LatLng(updateLat, updateLong));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverDeliveryActivity.this, "4"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0 &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getLocationUpdates();

        } else {
            Toast.makeText(DriverDeliveryActivity.this, "Rider requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocationUpdates() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(DriverDeliveryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(DriverDeliveryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, DriverDeliveryActivity.this);
                } else {
                    showCustomDialogue();
                }
            } else {
                ActivityCompat.requestPermissions(DriverDeliveryActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateLocation(location);
    }

    private void updateLocation(Location location) {
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("latitude", "" + latitude);
        hashMap.put("longitude", "" + longitude);

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap);

    }

    private void showCustomDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverDeliveryActivity.this);
        builder.setMessage("Would you like to turn on your location for location?")
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color='#000000'>Connect</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                    }
                }).setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(DriverDeliveryActivity.this, "You need to turn on your location", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
        getLocationUpdates();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
        showCustomDialogue();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        dMap = googleMap;
        dMap.clear();
        if (ActivityCompat.checkSelfPermission(DriverDeliveryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DriverDeliveryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        dMap.setMyLocationEnabled(true);
        dMap.getUiSettings().setMyLocationButtonEnabled(false);

        DatabaseReference dref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        dref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){

                            try {
                                driverLat  = Double.parseDouble("" + ds.child("latitude").getValue());
                                driverLong  = Double.parseDouble("" + ds.child("longitude").getValue());

                                if(dMarker != null){
                                    dMarker.remove();
                                    dMarker = null;
                                }

                                LatLng driverLoc = new LatLng(driverLat,driverLong);
                                dMarker = dMap.addMarker(new MarkerOptions().position(driverLoc).title("You").icon(bitmapDescriptorFromVector(DriverDeliveryActivity.this,R.drawable.triderpin)));
                                dMap.moveCamera(CameraUpdateFactory.newLatLng(driverLoc));
                                dMap.getUiSettings().setZoomControlsEnabled(true);
                                dMap.getUiSettings().setAllGesturesEnabled(true);

                            }
                            catch (Exception e){
                                Toast.makeText(DriverDeliveryActivity.this, "5"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverDeliveryActivity.this, "6"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("Delivery")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String orderTo = ""+ds.child("orderTo").getValue();
                            String orderBy = ""+ds.child("orderBy").getValue();
                            String orderID = ""+ds.child("orderID").getValue();

                            DatabaseReference custRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            custRef.orderByChild("uid").equalTo(orderBy)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds : snapshot.getChildren()){

                                                try {

                                                    String fName = ""+ds.child("first_name").getValue();
                                                    String lName = ""+ds.child("last_name").getValue();

                                                    customerLat = Double.parseDouble(""+ds.child("latitude").getValue());
                                                    customerLong = Double.parseDouble(""+ds.child("longitude").getValue());

                                                    customerFullName = fName+" "+lName;

                                                    LatLng custLoc = new LatLng(customerLat, customerLong);

                                                    dMap.addMarker(new MarkerOptions().position(custLoc).title("Customer").icon(bitmapDescriptorFromVector(DriverDeliveryActivity.this, R.drawable.ic_person_pin)));
                                                }
                                                catch (Exception e){
                                                   Toast.makeText(DriverDeliveryActivity.this, "7"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(DriverDeliveryActivity.this, "8"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverDeliveryActivity.this, "11"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        }
        Bitmap bitmap = Bitmap.createBitmap(Objects.requireNonNull(vectorDrawable).getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}