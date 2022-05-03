package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taboan_capstone.Constants;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CustomerCartModel;
import com.example.taboan_capstone.models.SellerStoreModel;
import com.example.taboan_capstone.views.AdapterStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private AdapterStore adapterStore;

    private ImageView backDash, cartDash,filterCategory;
    private EditText searchStore;
    private TextView cartCount;
    private String market;
    private RecyclerView storeList;

    private RoomDatabase roomDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();

        backDash = findViewById(R.id.iv_back_dash);
        cartDash = findViewById(R.id.iv_cart_dash);
        searchStore = findViewById(R.id.et_search_store_dash);
        cartCount = findViewById(R.id.tv_cart_count_dash);
        storeList = findViewById(R.id.rv_store_list);
        filterCategory = findViewById(R.id.ibUserDashUserFilterBtn);

        roomDatabase = Room.databaseBuilder(getApplicationContext(),RoomDatabase.class,"maindb").allowMainThreadQueries().build();

        if(getIntent() != null){
            market = getIntent().getStringExtra("market");
            loadStore(market);
        }

        backDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        filterCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDashboardActivity.this);
                builder.setTitle("Filter Products")
                        .setItems(Constants.Companion.getMARKET_CATEGORY(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constants.Companion.getMARKET_CATEGORY()[which];
                                searchStore.setText(selected);
                                if(selected.equals("All")){
                                    loadStore(market);
                                }
                                else{
                                    adapterStore.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        cartDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDashboardActivity.this,CustomerCartActivity.class);
                intent.putExtra(Constants.Companion.getMARKET_NAME(),market);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        countCart();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadStore(market);
        countCart();
    }

    private void loadStore(String market){
        ArrayList<SellerStoreModel> shopList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){

                            SellerStoreModel sellerStoreModel = ds.getValue(SellerStoreModel.class);

                            String getMarket = sellerStoreModel != null ? sellerStoreModel.getStore_market() : null;

                            if(market.equalsIgnoreCase(getMarket)) {
                                shopList.add(sellerStoreModel);
                            }
                            
                        }
                        adapterStore = new AdapterStore(CustomerDashboardActivity.this,shopList,market);
                        storeList.setAdapter(adapterStore);
                        storeList.setLayoutManager(new LinearLayoutManager(CustomerDashboardActivity.this));
                        adapterStore.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("loadStore",""+ error.getMessage());
                    }
                });
    }

    private void countCart(){
        if(roomDatabase.dbDao().checkIfCustomerCartExist() > 0){
            int count =  roomDatabase.dbDao().checkIfCustomerCartExist();
            cartCount.setVisibility(View.VISIBLE);
            cartCount.setText(String.valueOf(count));
        }else{
            cartCount.setVisibility(View.GONE);
        }
    }
}