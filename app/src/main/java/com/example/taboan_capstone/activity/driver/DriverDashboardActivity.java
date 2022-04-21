package com.example.taboan_capstone.activity.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.databinding.ActivityDriverDashboardBinding;
import com.example.taboan_capstone.models.DriverModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverDashboardActivity extends AppCompatActivity {

    private ActivityDriverDashboardBinding binding;
    private ImageView driverPortal,driverStatus,driverProfile,driverHistory;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDriverDashboardBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        driverPortal = findViewById(R.id.iv_driver_portal);
        driverStatus = findViewById(R.id.iv_driver_status);
        driverProfile = findViewById(R.id.iv_driver_profile);
        driverHistory = findViewById(R.id.iv_driver_history);

        firebaseAuth = FirebaseAuth.getInstance();
        loadDriver();

        driverPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverDashboardActivity.this, DriverPortalActivity.class);
                startActivity(intent);
            }
        });

        driverStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverDashboardActivity.this, DriverStatusActivity.class);
                startActivity(intent);
            }
        });

        driverProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverDashboardActivity.this, DriverProfileActivity.class);
                startActivity(intent);
            }
        });

        driverHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverDashboardActivity.this, DriverHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDriver(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Globals.currentDriver = ds.getValue(DriverModel.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverDashboardActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}