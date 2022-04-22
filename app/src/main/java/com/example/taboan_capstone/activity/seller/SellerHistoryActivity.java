package com.example.taboan_capstone.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.ProductModel;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.example.taboan_capstone.views.AdapterSellerHistory;
import com.example.taboan_capstone.views.AdapterSellerMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SellerHistoryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView sellerHistoryList;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;
    private AdapterSellerHistory adapterSellerHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_history);
        firebaseAuth = FirebaseAuth.getInstance();

        sellerHistoryList = findViewById(R.id.seller_history_list);

        loadHistory();
    }

    private void loadHistory(){
        sellerOrderModelArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Orders").orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sellerOrderModelArrayList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            SellerOrderModel modelProduct = ds.getValue(SellerOrderModel.class);
                            sellerOrderModelArrayList.add(modelProduct);

                        }
                        adapterSellerHistory = new AdapterSellerHistory(SellerHistoryActivity.this, sellerOrderModelArrayList);
                        sellerHistoryList.setAdapter(adapterSellerHistory);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SellerHistoryActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}