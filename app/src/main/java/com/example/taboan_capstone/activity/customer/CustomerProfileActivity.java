package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.taboan_capstone.DrawerBaseActivity;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.databinding.ActivityCustomerHomeBinding;
import com.example.taboan_capstone.databinding.ActivityCustomerProfileBinding;
import com.example.taboan_capstone.models.CustomerModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;

public class CustomerProfileActivity extends DrawerBaseActivity {

    private ActivityCustomerProfileBinding binding;
    private TextInputLayout ti_FirstName, ti_LastName, ti_Address, ti_Mobile, ti_Email, ti_Password, ti_GPS_Address;
    private Button btnSave;
    private RoomDatabase roomDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        allocateActivtyTitle("Profile");

        firebaseAuth = FirebaseAuth.getInstance();

        ti_FirstName = binding.getRoot().findViewById(R.id.ti_p_FirstName);
        ti_LastName = binding.getRoot().findViewById(R.id.ti_p_LastName);
        ti_Address = binding.getRoot().findViewById(R.id.ti_p_Address);
        ti_Mobile = binding.getRoot().findViewById(R.id.ti_p_Mobile);
        ti_GPS_Address = binding.getRoot().findViewById(R.id.ti_p_GPS_Address);
        ti_Email = binding.getRoot().findViewById(R.id.ti_p_Email);
        ti_Password = binding.getRoot().findViewById(R.id.ti_p_Password);
        btnSave = binding.getRoot().findViewById(R.id.btn_p_Save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            runOnUiThread(this::getInformation);
        });

    }

    private void saveData(){
        String fName,lName,address,mobile,email,password;
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");

        HashMap<String, Object> hashMap = new HashMap<>();

        fName = ti_FirstName.getEditText().getText().toString().trim();
        lName = ti_LastName.getEditText().getText().toString().trim();
        address = ti_Address.getEditText().getText().toString().trim();
        mobile = ti_Mobile.getEditText().getText().toString().trim();
        email = ti_Email.getEditText().getText().toString().trim();
        password = ti_Password.getEditText().getText().toString().trim();

        hashMap.put("email", "" + email);
        hashMap.put("first_name", "" + fName);
        hashMap.put("last_name", "" + lName);
        hashMap.put("phoneNum", "" + mobile);
        hashMap.put("address", "" + address);
        hashMap.put("password", "" + password);

        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(unused -> Toast.makeText(CustomerProfileActivity.this, "Profile Update successfully",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(CustomerProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                getInformation();
            }
        },500);

    }

    private void getInformation(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){

                    CustomerModel customerModel = ds.getValue(CustomerModel.class);

                    assert customerModel != null;
                    Objects.requireNonNull(ti_FirstName.getEditText()).setText(customerModel.getFirst_name());
                    Objects.requireNonNull(ti_LastName.getEditText()).setText(customerModel.getLast_name());
                    Objects.requireNonNull(ti_Address.getEditText()).setText(customerModel.getAddress());
                    Objects.requireNonNull(ti_Mobile.getEditText()).setText(customerModel.getPhoneNum());
                    Objects.requireNonNull(ti_Email.getEditText()).setText(customerModel.getEmail());
                    Objects.requireNonNull(ti_GPS_Address.getEditText()).setText(customerModel.getGps_address());
                    Objects.requireNonNull(ti_Password.getEditText()).setText(customerModel.getPassword());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}