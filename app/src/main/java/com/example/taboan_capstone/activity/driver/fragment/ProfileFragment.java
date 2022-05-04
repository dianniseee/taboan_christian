package com.example.taboan_capstone.activity.driver.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.customer.CustomerProfileActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CustomerModel;
import com.example.taboan_capstone.models.DriverModel;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    private TextInputLayout ti_FirstName, ti_LastName, ti_Address, ti_Mobile, ti_Email, ti_Password, ti_GPS_Address;
    private Button btnSave;
    private RoomDatabase roomDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        ti_FirstName = view.findViewById(R.id.ti_d_FirstName);
        ti_LastName = view.findViewById(R.id.ti_d_LastName);
        ti_Address = view.findViewById(R.id.ti_d_Address);
        ti_Mobile = view.findViewById(R.id.ti_d_Mobile);
        ti_Email = view.findViewById(R.id.ti_d_Email);
        ti_Password = view.findViewById(R.id.ti_d_Password);
        btnSave = view.findViewById(R.id.btn_d_Save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        getInformation();

        return view;
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
                .addOnSuccessListener(unused -> Toast.makeText(ProfileFragment.this.getContext(), "Profile Update successfully",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileFragment.this.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show());

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

                    DriverModel driverModel = ds.getValue(DriverModel.class);

                    assert driverModel != null;
                    Objects.requireNonNull(ti_FirstName.getEditText()).setText(driverModel.getFirst_name());
                    Objects.requireNonNull(ti_LastName.getEditText()).setText(driverModel.getLast_name());
                    Objects.requireNonNull(ti_Address.getEditText()).setText(driverModel.getAddress());
                    Objects.requireNonNull(ti_Mobile.getEditText()).setText(driverModel.getPhoneNum());
                    Objects.requireNonNull(ti_Email.getEditText()).setText(driverModel.getEmail());
                    Objects.requireNonNull(ti_Password.getEditText()).setText(driverModel.getPassword());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileFragment.this.getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}