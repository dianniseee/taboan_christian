package com.example.taboan_capstone.activity.driver.fragment;

import android.Manifest;
import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.driver.permission.FragmentPermissionHelper;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryFragment extends Fragment implements OnMapReadyCallback,LocationListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DeliveryFragment() {

    }

    public static DeliveryFragment newInstance(String param1, String param2) {
        DeliveryFragment fragment = new DeliveryFragment();
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

    private String customerFullName,phoneNo;

    private AdapterDriverItems adapterDriverItems;
    DriverOrderModel driverOrderModel;
    private ActivityResultLauncher<String[]> mPermissionResultLauncher;

    private static final String STATUS_DELIVERY = "Completed";
    private static final String STATUS_AVAIL = "Available";

    private String deliveryCity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delivery, container, false);

        dItems = view.findViewById(R.id.driver_delivery_orders);
        dRoute = view.findViewById(R.id.driver_delivery_route);


        firebaseAuth = FirebaseAuth.getInstance();
        driverOrderModel = new DriverOrderModel();

        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().add(R.id.driver_delivery_frame_layout,supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);


        dItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DeliveryFragment.this.getContext());
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


        //OnRequestPermissionResult Depreciated -> ActivityResultLauncher
        String[] permissionRequests = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION };
        new FragmentPermissionHelper().permissionRequest(DeliveryFragment.this, isGranted -> {
            if(isGranted){
                getLocationUpdates();
            }else{
                showCustomDialogue();
            }
        },permissionRequests);

        return  view;
    }


    private void getLocationUpdates() {

        List<String> permissionsRequest = new ArrayList<>();
        locationManager =(LocationManager)DeliveryFragment.this.getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(DeliveryFragment.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(DeliveryFragment.this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE, this);

                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE,  this);
                } else {
                    showCustomDialogue();
                }

                if(!permissionsRequest.isEmpty()){
                    mPermissionResultLauncher.launch(permissionsRequest.toArray(new String[0]));
                }

            } else {
                ActivityCompat.requestPermissions((Activity) DeliveryFragment.this.getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                permissionsRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissionsRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);

                mPermissionResultLauncher.launch(permissionsRequest.toArray(new String[0]));
            }
        }
    }

    private void locRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.custom_dialog_driver_items,null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        TextView orderId = view.findViewById(R.id.custom_dialog_driver_order_id);
        TextView storeName = view.findViewById(R.id.custom_dialog_driver_store_name);
        TextView orderName = view.findViewById(R.id.custom_dialog_driver_order_name);
        TextView totalValue = view.findViewById(R.id.custom_dialog_driver_total_value);
        TextView totalDevFee = view.findViewById(R.id.custom_dialog_driver_dev_fee_value);
        ImageView callValue = view.findViewById(R.id.custom_dialog_driver_call_value);
        Button delivered = view.findViewById(R.id.custom_dialog_driver_delivered);
        RecyclerView orderItems = view.findViewById(R.id.custom_dialog_driver_order_items);

        DatabaseReference ref2 = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref2.orderByChild("uid").equalTo(driverOrderModel.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            String fName = ""+ds.child("first_name").getValue();
                            String lName = ""+ds.child("last_name").getValue();
                            String pNum = "" +ds.child("phoneNum").getValue();

                            String custFullName = fName + " " + lName;
                            orderName.setText(""+custFullName);

                            callValue.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    callUser(pNum);
                                    dialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        ref2.orderByChild("uid").equalTo(driverOrderModel.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String getStoreName = ""+ds.child("store_name").getValue();

                            storeName.setText(getStoreName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        orderId.setText("#"+driverOrderModel.getOrderID());
        totalDevFee.setText("₱ "+driverOrderModel.getOrderDevFee());
        double devFee = Double.parseDouble(driverOrderModel.getOrderDevFee());
        double ordertots = Double.parseDouble(driverOrderModel.getOrderTotal());
        double setTotalValue = devFee + ordertots;
        totalValue.setText("₱ "+setTotalValue);

        adapterDriverItems = new AdapterDriverItems(context,driverProductModelArrayList);
        orderItems.setAdapter(adapterDriverItems);
        adapterDriverItems.notifyDataSetChanged();

        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deliverOrder();
                dialog.dismiss();
            }
        });



        dialog.show();
    }

    private void deliverOrder(){
        String timestamp = "" + System.currentTimeMillis();

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("Delivery")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){
                            DriverOrderModel driverOrderModel = ds.getValue(DriverOrderModel.class);

                            HashMap<String,Object> hashMapPayment = new HashMap<>();
                            hashMapPayment.put("orderID" ,""+ driverOrderModel.getOrderID());
                            hashMapPayment.put("userID",""+driverOrderModel.getUserID());
                            hashMapPayment.put("orderBy",""+driverOrderModel.getOrderBy());
                            hashMapPayment.put("orderTo",""+driverOrderModel.getOrderTo());
                            hashMapPayment.put("orderDevFee",""+driverOrderModel.getOrderDevFee());
                            hashMapPayment.put("orderMarket",""+driverOrderModel.getOrderMarket());
                            hashMapPayment.put("orderDateTime",""+driverOrderModel.getOrderDateTime());
                            hashMapPayment.put("orderDriverID",""+driverOrderModel.getOrderDriverID());
                            hashMapPayment.put("orderStatus",""+STATUS_DELIVERY);
                            hashMapPayment.put("orderSubTotal",""+driverOrderModel.getOrderSubTotal());
                            hashMapPayment.put("orderTotal",""+driverOrderModel.getOrderTotal());
                            hashMapPayment.put("orderDelivered",""+timestamp);

                            DatabaseReference custStore = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            custStore.child(driverOrderModel.getOrderBy()).child("Orders").child(driverOrderModel.getOrderID()).updateChildren(hashMapPayment)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Customer received their order", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("orderStatus","" + STATUS_DELIVERY);

                            DatabaseReference driverRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            driverRef.child(firebaseAuth.getUid()).child("Orders").child(driverOrderModel.getOrderID()).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(DeliveryFragment.this.getContext(), "Drop Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            DatabaseReference storeRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            storeRef.child(driverOrderModel.getOrderTo()).child("Orders").child(driverOrderModel.getOrderID()).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(DeliveryFragment.this.getContext(), "Drop Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DeliveryFragment.this.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DeliveryFragment.this.getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("availStat",""+STATUS_AVAIL);

        DatabaseReference driverRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        driverRef.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DeliveryFragment.this.getContext(), "You are now Available", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DeliveryFragment.this.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCustomDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryFragment.this.getContext());
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

                Toast.makeText(DeliveryFragment.this.getContext(), "You need to turn on your location", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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

                             driverOrderModel = ds.getValue(DriverOrderModel.class);

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
                                            Toast.makeText(DeliveryFragment.this.getContext(), "1"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DeliveryFragment.this.getContext(), "2"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRoute(){
        String address = "https://maps.google.com/maps?saddr=" + driverLat +"," + driverLong + "&daddr=" + customerLat + "," +customerLong;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void callUser(String number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0"+number));
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        dMap = googleMap;
        dMap.clear();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                                dMarker = dMap.addMarker(new MarkerOptions().position(driverLoc).title("You").icon(bitmapDescriptorFromVector(DeliveryFragment.this.getContext(),R.drawable.triderpin)));
                                dMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLoc,15));
                                dMap.getUiSettings().setZoomControlsEnabled(true);
                                dMap.getUiSettings().setAllGesturesEnabled(true);

                            }
                            catch (Exception e){
                                Toast.makeText(getContext(), "5"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "6"+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                            custRef.orderByChild("uid").equalTo(driverOrderModel.getOrderBy())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds : snapshot.getChildren()){

                                                deliveryCity = ""+ds.child("userCity").getValue();
                                                customerLat = Double.parseDouble(""+ds.child("latitude").getValue());
                                                customerLong = Double.parseDouble(""+ds.child("longitude").getValue());

                                                LatLng custLoc = new LatLng(customerLat, customerLong);

                                                dMap.addMarker(new MarkerOptions().position(custLoc).title("Customer").icon(bitmapDescriptorFromVector(DeliveryFragment.this.getContext(), R.drawable.ic_person_pin_red)));

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                     //       Toast.makeText(getContext(), "8"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            DatabaseReference storeRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            storeRef.orderByChild("uid").equalTo(driverOrderModel.getOrderTo()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot ds : snapshot.getChildren()){

                                        try {

                                            String fName = ""+ds.child("first_name").getValue();
                                            String lName = ""+ds.child("last_name").getValue();

                                            double storeLat = Double.parseDouble(""+ds.child("latitude").getValue());
                                            double storeLong = Double.parseDouble(""+ds.child("longitude").getValue());

                                            customerFullName = fName+" "+lName;

                                            LatLng custLoc = new LatLng(storeLat, storeLong);

                                            dMap.addMarker(new MarkerOptions().position(custLoc).title("Customer").icon(bitmapDescriptorFromVector(DeliveryFragment.this.getContext(), R.drawable.tstorepin)));
                                        }
                                        catch (Exception e){

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "11"+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
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



        if(dMarker != null){
            dMarker.remove();
            dMarker = null;
        }

        LatLng driverLoc = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        dMarker = dMap.addMarker(new MarkerOptions().position(driverLoc).title("You").icon(bitmapDescriptorFromVector(DeliveryFragment.this.getContext(),R.drawable.triderpin)));
        dMap.moveCamera(CameraUpdateFactory.newLatLng(driverLoc));
        dMap.getUiSettings().setZoomControlsEnabled(true);
        dMap.getUiSettings().setAllGesturesEnabled(true);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
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

}