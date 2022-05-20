package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.Constants;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CustomerCartModel;
import com.example.taboan_capstone.views.AdapterCustomerCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerCartActivity extends AppCompatActivity {

    private ImageView back,deleteCart;
    private TextView marketName,personName,personAddress,setTotal;
    private Button placeOrder;
    private RecyclerView customerCartList;
    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;
    private RelativeLayout hasCartCover,noCartCover;

    private AdapterCustomerCart adapterCustomerCart;
    private AdapterCustomerCart.OnAdapterClick mListener;
    private String getMarketName;
    private List<CustomerCartModel> currentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        firebaseAuth = FirebaseAuth.getInstance();
        roomDatabase = Room.databaseBuilder(getApplicationContext(),RoomDatabase.class,"maindb").allowMainThreadQueries().build();

        back = findViewById(R.id.cart_user_back);
        marketName = findViewById(R.id.cart_user_market_name);
        personAddress = findViewById(R.id.cart_user_address);
        personName = findViewById(R.id.cart_user_name);
        setTotal = findViewById(R.id.cart_user_cart_total);
        placeOrder = findViewById(R.id.cart_user_place_order);
        customerCartList = findViewById(R.id.cart_user_list);
        hasCartCover = findViewById(R.id.cart_user_cover_order);
        noCartCover = findViewById(R.id.cart_user_cover_empty);
        deleteCart = findViewById(R.id.cart_user_delete);

        currentCart = new ArrayList<>();

        if(getIntent() != null){
            getMarketName = getIntent().getStringExtra(Constants.Companion.getMARKET_NAME());
            marketName.setText(getMarketName);
        }

        setupUser();
        setupCarts();
        checkCart();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        deleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomDatabase.dbDao().clearCustomerCart();
                onBackPressed();
            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitOrder();
            }
        });
    }

    private void setupUser(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            String firstName = ""+ds.child("first_name").getValue();
                            String last_name = ""+ds.child("last_name").getValue();
                            String address = ""+ds.child("address").getValue();

                            personName.setText(firstName + " " +last_name);
                            personAddress.setText(address);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerCartActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private double getTotal = 0;
    private double getSubtotal = 0;
    private double devFee = 0;
    private double total = 0;
    private void setupCarts(){
        setOnAdapterClick();

        currentCart.clear();
        setTotal.setText("");
        getTotal = 0;
        getSubtotal = 0;
        devFee = 0;
        total = 0;

        if(roomDatabase.dbDao().getAllCart() != null){
            currentCart = roomDatabase.dbDao().getAllCart();
            adapterCustomerCart = new AdapterCustomerCart(CustomerCartActivity.this,currentCart,mListener);

            customerCartList.setAdapter(adapterCustomerCart);
            adapterCustomerCart.notifyDataSetChanged();
        }

        for(int i = 0; i < currentCart.size(); i++){
            getSubtotal = currentCart.get(i).getSubtotal();
            devFee = 39.00;
            getTotal += getSubtotal;
            total = getTotal + devFee;
            setTotal.setText("₱ " + total);
        }
    }

    private final String timestamp = "" + System.currentTimeMillis();
    private void submitOrder(){

        List<String> distinctSeller = roomDatabase.dbDao().getDistinctSeller();

        for(String id : distinctSeller){

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("orderID", "" + timestamp);
            hashMap.put("userID", "" + firebaseAuth.getUid());
            hashMap.put("orderBy", "" + firebaseAuth.getUid());
            hashMap.put("orderTo", "" + id);
            hashMap.put("orderDevFee", "" + devFee);
            hashMap.put("orderMarket", "" + getMarketName);
            hashMap.put("orderDateTime", "" + timestamp);
            hashMap.put("orderDriverID", ""+ "null");
            hashMap.put("orderStatus", "" +"Waiting");
            hashMap.put("orderSubTotal","" +getTotal);
            hashMap.put("orderTotal", "" + total);
            hashMap.put("orderDelivered",""+"null");

            DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(id).child("Orders");
            ref.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            List<CustomerCartModel> cartItems = roomDatabase.dbDao().getCartSellerData(id);
                            for(int x = 0; x < cartItems.size(); x++){

                                int id = cartItems.get(x).getId();
                                String productID = cartItems.get(x).getProductID();
                                String productName = cartItems.get(x).getProductName();
                                String product_Desc = cartItems.get(x).getProduct_Desc();
                                String product_category = cartItems.get(x).getProduct_category();
                                double price_each = cartItems.get(x).getPrice_each();
                                double quantity = cartItems.get(x).getQuantity();
                                double subtotal = cartItems.get(x).getSubtotal();

                                HashMap<String, String> hashMapCart = new HashMap<>();
                                hashMapCart.put("id", "" + id);
                                hashMapCart.put("productID", "" + productID);
                                hashMapCart.put("productName", "" + productName);
                                hashMapCart.put("product_Desc", "" + product_Desc);
                                hashMapCart.put("product_category", "" + product_category);
                                hashMapCart.put("price_each", "" + price_each);
                                hashMapCart.put("quantity", "" + quantity);
                                hashMapCart.put("subtotal", "" + subtotal);

                                ref.child(timestamp).child("Items").child(productID).setValue(hashMapCart);
                                //roomDatabase.dbDao().deleteCartById(id);
                                //delete cart by id will now only execute after the order of customer is finish importing
                            }
                            Toast.makeText(CustomerCartActivity.this, "Order Submitted", Toast.LENGTH_SHORT).show();
                            adapterCustomerCart.notifyDataSetChanged();
                            if(roomDatabase.dbDao().checkIfCustomerCartExist() == 0){
                               noCartCover.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(e -> Toast.makeText(CustomerCartActivity.this, "CustomerCart"+e.getMessage(), Toast.LENGTH_SHORT).show());


            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                DatabaseReference custRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(firebaseAuth.getUid()).child("Orders");
                custRef.child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                List<CustomerCartModel> cartItems = roomDatabase.dbDao().getCartSellerData(id);
                                for(int x = 0; x < cartItems.size(); x++){

                                    int id1 = cartItems.get(x).getId();
                                    String productID = cartItems.get(x).getProductID();
                                    String productName = cartItems.get(x).getProductName();
                                    String product_Desc = cartItems.get(x).getProduct_Desc();
                                    String product_category = cartItems.get(x).getProduct_category();
                                    double price_each = cartItems.get(x).getPrice_each();
                                    double quantity = cartItems.get(x).getQuantity();
                                    double subtotal = cartItems.get(x).getSubtotal();

                                    HashMap<String, String> hashMapCart = new HashMap<>();
                                    hashMapCart.put("id", "" + id1);
                                    hashMapCart.put("productID", "" + productID);
                                    hashMapCart.put("productName", "" + productName);
                                    hashMapCart.put("product_Desc", "" + product_Desc);
                                    hashMapCart.put("product_category", "" + product_category);
                                    hashMapCart.put("price_each", "" + price_each);
                                    hashMapCart.put("quantity", "" + quantity);
                                    hashMapCart.put("subtotal", "" + subtotal);

                                    custRef.child(timestamp).child("Items").child(productID).setValue(hashMapCart);
                                    roomDatabase.dbDao().deleteCartById(id1);

                                }
                                Toast.makeText(CustomerCartActivity.this, "Order Submitted", Toast.LENGTH_SHORT).show();
                                adapterCustomerCart.notifyDataSetChanged();
                                if(roomDatabase.dbDao().checkIfCustomerCartExist() == 0){
                                    noCartCover.setVisibility(View.VISIBLE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CustomerCartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            },1000);

        }
    }

    private void checkCart(){
        if(roomDatabase.dbDao().checkIfCustomerCartExist() == 0){
            noCartCover.setVisibility(View.VISIBLE);
            hasCartCover.setVisibility(View.GONE);
        }else{
            noCartCover.setVisibility(View.GONE);
            hasCartCover.setVisibility(View.VISIBLE);
        }
    }

    private double setQuantity;
    private double setSubTotal;
    private int bundlePieces;
    private double getKilo;
    private void setOnAdapterClick(){

        mListener = new AdapterCustomerCart.OnAdapterClick() {
            @Override
            public void onAdapterClick(View v, int position) {

                int id = currentCart.get(position).getId();
                String prodId = currentCart.get(position).getProductID();
                double qty = currentCart.get(position).getQuantity();
                double subtotal = currentCart.get(position).getSubtotal();
                double prodPrice = currentCart.get(position).getPrice_each();
                String prodCategory = currentCart.get(position).getProduct_category();

                setQuantity = qty;

                PopupMenu popupMenu = new PopupMenu(CustomerCartActivity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_cart_quantity, popupMenu.getMenu());
                popupMenu.show();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    popupMenu.setForceShowIcon(true);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.cart_subtract:

                                if(prodCategory.equals("Pieces") || prodCategory.equals("Bundle")){
                                    if(setQuantity > 0.25){
                                        setQuantity = setQuantity - 1;
                                        bundlePieces = (int) setQuantity;
                                        setSubTotal = bundlePieces * prodPrice;

                                        updateLocalCart(prodId,bundlePieces,setSubTotal);
                                    }
                                }else{

                                    if(setQuantity > 1){
                                        setQuantity = setQuantity - 0.25;
                                        getKilo = setQuantity;
                                        setSubTotal = getKilo * prodPrice;

                                        updateLocalCart(prodId,bundlePieces,setSubTotal);
                                    }
                                }

                                return true;

                            case R.id.cart_add:

                                if(prodCategory.equals("Pieces") || prodCategory.equals("Bundle")){
                                    setQuantity = setQuantity + 1;

                                    bundlePieces = (int) setQuantity;
                                    setSubTotal = bundlePieces * prodPrice;

                                    updateLocalCart(prodId,bundlePieces,setSubTotal);
                                }else{
                                    setQuantity = setQuantity + 0.25;
                                    getKilo = setQuantity;
                                    setSubTotal = getKilo * prodPrice;

                                    updateLocalCart(prodId,bundlePieces,setSubTotal);
                                }

                                return true;
                            case R.id.cart_delete:
                                remoceCartRow(id);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
            }
        };

    }


    private void updateLocalCart(String productId, double qty, double subtotal){

        if(roomDatabase.dbDao().checkExistingCart(productId)){
            roomDatabase.dbDao().updateCustomerCartExist(productId, qty, subtotal);
            adapterCustomerCart.notifyDataSetChanged();
        }
        setupCarts();
    }


    private void remoceCartRow(int id){
        roomDatabase.dbDao().deleteCartById(id);
        setupCarts();
        adapterCustomerCart.notifyDataSetChanged();
        checkCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUser();
        checkCart();
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