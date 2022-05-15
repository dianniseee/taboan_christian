package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Database;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class CustomerRegisterActivity extends AppCompatActivity implements LocationListener {

    private TextInputLayout ti_FirstName, ti_LastName, ti_Address, ti_Mobile, ti_Email, ti_Password, ti_GPS_Address,ti_Confirm_Password;
    private Button btnSignUp;
    private ImageView back, gps;

    //LOCATION SERVICE
    private static final int LOCATION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String[] locationPermissions;
    private LocationManager locationManager;
    private LocationCallback locationCallback;
    private double latitude, longitude;

    //Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        ti_FirstName = findViewById(R.id.ti_FirstName);
        ti_LastName = findViewById(R.id.ti_LastName);
        ti_Address = findViewById(R.id.ti_Address);
        ti_Mobile = findViewById(R.id.ti_Mobile);
        ti_Email = findViewById(R.id.ti_Email);
        ti_Password = findViewById(R.id.ti_Password);
        ti_GPS_Address = findViewById(R.id.ti_GPS_Address);
        ti_Confirm_Password = findViewById(R.id.ti_Confirm_Password);
        btnSignUp = findViewById(R.id.btn_SingUp);
        back = findViewById(R.id.back);
        gps = findViewById(R.id.gps);

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(CustomerRegisterActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();

        createUI();
    }

    private void createUI() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCustomerCurrentLocation();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private String regFName,regLName,regAddress,regGPSAddress,regMobile,
            regConfirmPassword,userCity,userCountry,userPostal;

    private void registerUser(){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String regEmail,regPassword;

        regFName = Objects.requireNonNull(ti_FirstName.getEditText()).getText().toString().trim();
        regLName = Objects.requireNonNull(ti_LastName.getEditText()).getText().toString().trim();
        regAddress = Objects.requireNonNull(ti_Address.getEditText()).getText().toString().trim();
        regGPSAddress = Objects.requireNonNull(ti_GPS_Address.getEditText()).getText().toString().trim();
        regMobile = Objects.requireNonNull(ti_Mobile.getEditText()).getText().toString().trim();
        regEmail = Objects.requireNonNull(ti_Email.getEditText()).getText().toString().trim();
        regPassword = Objects.requireNonNull(ti_Password.getEditText()).getText().toString().trim();
        regConfirmPassword = Objects.requireNonNull(ti_Confirm_Password.getEditText()).getText().toString().trim();

        if (TextUtils.isEmpty(regFName)) {
            ti_FirstName.setError("Required First Name");
            return;
        }
        else{
            ti_FirstName.setError(null);
        }
        if (TextUtils.isEmpty(regLName)) {
            ti_LastName.setError("Required your Last Name");
            return;
        }else{
            ti_LastName.setError(null);
        }
        if (TextUtils.isEmpty(regAddress)) {
            ti_Address.setError("Required address");
            return;
        }
        else{
            ti_Address.setError(null);
        }
        if (TextUtils.isEmpty(regGPSAddress)) {
            ti_GPS_Address.setError("Click the GPS button above");
            return;
        }
        else{
            ti_GPS_Address.setError(null);
        }
        if (TextUtils.isEmpty(regMobile)) {
            ti_GPS_Address.setError("Required Mobile Number");
            return;
        }
        else{
            ti_GPS_Address.setError(null);
        }
        if (TextUtils.isEmpty(regEmail)) {
            ti_Email.setError("Required email address");
        }else{
            ti_Email.setError(null);
        }
        if (TextUtils.isEmpty(regPassword)) {
            ti_Password.setError("Required your password");
            return;
        }
        else{
            ti_Password.setError(null);
        }
        if (TextUtils.isEmpty(regConfirmPassword)) {
            ti_Confirm_Password.setError("Required your password");
            return;
        }
        else{
            ti_Confirm_Password.setError(null);
        }
        if (regPassword.length() < 6) {
            ti_Password.setError("Password must be 6 characters long");
            return;
        }
        else{
            ti_Password.setError(null);
        }
        if (!regEmail.matches(emailPattern)) {
            ti_Email.setError("Incorrect Email format");
        } else{
            ti_Email.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(regEmail).matches()) {
            ti_Email.setError("Invalid Email");
        }
        else{
            ti_Email.setError(null);
        }
        if (!regPassword.equals(regConfirmPassword)) {
            ti_Confirm_Password.setError("Password Doesn't match");
            return;
        }
        else{
            ti_Confirm_Password.setError(null);
        }
        if(regFName != null && regLName != null && regAddress != null && regGPSAddress != null && regMobile != null && regPassword.equalsIgnoreCase(regConfirmPassword)){

            if(regEmail.equals(null) && regPassword.equals(null) && regEmail.equalsIgnoreCase("") && regPassword.equalsIgnoreCase("")){
                Toast.makeText(CustomerRegisterActivity.this,"Please review your credentials",Toast.LENGTH_SHORT).show();
            }else{
                createCustomer(regEmail,regPassword);
            }
        }
    }

    private void createCustomer(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                saveCustomerFirebase(email,password);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CustomerRegisterActivity.this, "Creation Failed",Toast.LENGTH_SHORT).show();
                Log.e("FirebaseCreation",e.getMessage());
            }
        });
    }

    private void saveCustomerFirebase(String email,String password){
        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("email", "" + email);
        hashMap.put("first_name", "" + regFName);
        hashMap.put("last_name", "" + regLName);
        hashMap.put("phoneNum", "" + regMobile);
        hashMap.put("address", "" + regAddress);
        hashMap.put("gps_address", "" + regGPSAddress);
        hashMap.put("avatar", "" + "null");
        hashMap.put("gender", "" + "null");
        hashMap.put("password", "" + password);
        hashMap.put("latitude", "" + latitude);
        hashMap.put("longitude", "" + longitude);
        hashMap.put("userCity", "" + userCity);
        hashMap.put("userCountry", "" + userCountry);
        hashMap.put("userPostal", "" + userPostal);
        hashMap.put("accountType", "Customer");
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("online", "false");

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid()).setValue(hashMap).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CustomerRegisterActivity.this,"Registeration Complete",Toast.LENGTH_SHORT).show();
                        clearFields();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CustomerRegisterActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                Log.e("FirebaseRegistration",e.getMessage());
            }
        });
    }

    private void clearFields(){
        ti_FirstName.getEditText().setText("");
        ti_LastName.getEditText().setText("");
        ti_Address.getEditText().setText("");
        ti_GPS_Address.getEditText().setText("");
        ti_Mobile.getEditText().setText("");
        ti_Email.getEditText().setText("");
        ti_Password.getEditText().setText("");
        ti_Confirm_Password.getEditText().setText("");
    }
    private void getCustomerCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(CustomerRegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(CustomerRegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(CustomerRegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        getCurrentLocation();
                    } else {
                        Toast.makeText(CustomerRegisterActivity.this,"Taboan Express requires permission to be granted in order to work properly",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(CustomerRegisterActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Objects.requireNonNull(ti_GPS_Address.getEditText()).setTextColor(Color.BLACK);
                            ti_GPS_Address.getEditText().setText(Html.fromHtml("<font color='#6200EE'></b></font>"
                                    + addresses.get(0).getAddressLine(0)));
                            userCity = addresses.get(0).getLocality();
                            userCountry = addresses.get(0).getCountryName();
                            userPostal = addresses.get(0).getPostalCode();

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(5000)
                                .setNumUpdates(1)
                                .setFastestInterval(1000);

                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();

                                Geocoder geocoder1 = new Geocoder(CustomerRegisterActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder1.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1);
                                    Objects.requireNonNull(ti_GPS_Address.getEditText()).setTextColor(Color.BLACK);
                                    ti_GPS_Address.getEditText().setText(Html.fromHtml("<font color='#6200EE'></b></font>"
                                            + addresses.get(0).getAddressLine(0)));
                                    userCity = addresses.get(0).getLocality();
                                    userCountry = addresses.get(0).getCountryName();
                                    userPostal = addresses.get(0).getPostalCode();

                                    latitude = location1.getLatitude();
                                    longitude = location1.getLongitude();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        if (ActivityCompat.checkSelfPermission(CustomerRegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(CustomerRegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(CustomerRegisterActivity.this,"Location enabled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Toast.makeText(CustomerRegisterActivity.this,"Cannot find location",Toast.LENGTH_SHORT).show();
    }

}