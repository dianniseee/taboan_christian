 package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taboan_capstone.Constants;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CurrentUserModel;
import com.example.taboan_capstone.models.CustomerCartModel;
import com.example.taboan_capstone.models.ProductModel;
import com.example.taboan_capstone.models.SellerStoreModel;
import com.example.taboan_capstone.views.AdapterSellerProduct;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomerStoreDetailsActivity extends AppCompatActivity  {

    private GridLayoutManager gridLayoutManager;
    private String getUid,marketName;
    private RecyclerView seller_product_list;
    private ArrayList<ProductModel> productsList;
    private AdapterSellerProduct adapterSellerProduct;
    private ImageView sellerCover,back;
    private TextView sellerName;
    private AdapterSellerProduct.OnAdapterClick mListener;
    private RoomDatabase roomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_store_details);

        seller_product_list = findViewById(R.id.seller_product_list);
        sellerCover = findViewById(R.id.customer_seller_cover);
        back = findViewById(R.id.customer_seller_back);
        sellerName = findViewById(R.id.customer_seller_name);

        productsList = new ArrayList<>();

        roomDatabase = Room.databaseBuilder(getApplicationContext(),RoomDatabase.class,"maindb").allowMainThreadQueries().build();

        if(getIntent() != null){
            getUid = getIntent().getStringExtra(Constants.Companion.getSELLER_GET_UID());
            marketName = getIntent().getStringExtra("market");
            loadShopDetails(getUid);
            setUpProducts(getUid);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpProducts(String getUid){
        setOnclickListenerAdapter();

        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.child(getUid).child("Product")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){
                            ProductModel modelProduct = ds.getValue(ProductModel.class);
                            productsList.add(modelProduct);
                        }
                        adapterSellerProduct = new AdapterSellerProduct(CustomerStoreDetailsActivity.this, productsList,mListener);
                        seller_product_list.setAdapter(adapterSellerProduct);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerStoreDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        seller_product_list.setLayoutManager(gridLayoutManager);
    }

    //showing productDialog
    private void setOnclickListenerAdapter(){
        mListener = new AdapterSellerProduct.OnAdapterClick() {
            @Override
            public void onAdapterClick(View v, int position) {
                String getProdAvailable = productsList.get(position).getProd_avail();
                String getProductCategory = productsList.get(position).getProd_category();
                String getProdAvail = productsList.get(position).getProd_avail();

                if(getProdAvail.equals("Available")){

                    if(getProdAvailable.equals("Available") && getProductCategory.equals("Bundle") || getProductCategory.equals("Pieces") || getProductCategory.equals("Kilo")){
                        showAddCartDialogBundle(position);
                    } else{
                        //don't show add to cart when product is unavailable
                    }

                }else{
                    Toast.makeText(CustomerStoreDetailsActivity.this, "Out of stock", Toast.LENGTH_SHORT).show();
                }



            }
        };
    }

    private double setQuantity = 1;
    private double setSubTotal = 0;
    private int bundlePieces = 1;
    private double getKilo = 0;
    @SuppressLint("SetTextI18n")
    private void showAddCartDialogBundle(int position){
        View view = LayoutInflater.from(CustomerStoreDetailsActivity.this).inflate(R.layout.add_cart_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        setQuantity = 1;
        setSubTotal = 0;
        bundlePieces = 1;
        getKilo = 0;

        ImageView addCover1 = view.findViewById(R.id.add_cart_cover1);
        ImageView addCover2 = view.findViewById(R.id.add_cart_cover2);
        ImageView subQuantity = view.findViewById(R.id.add_cart_subtract);
        ImageView addQuantity = view.findViewById(R.id.add_cart_add);
        Button addToCart = view.findViewById(R.id.add_cart_push);
        TextView prodName = view.findViewById(R.id.add_cart_name);
        TextView prodDescription = view.findViewById(R.id.add_cart_description);
        TextView prodPrice = view.findViewById(R.id.add_cart_price);
        TextView addSubtotal = view.findViewById(R.id.add_cart_subtotal);
        TextView quantity = view.findViewById(R.id.add_cart_quantity);
        TextView addCategory = view.findViewById(R.id.add_cart_category);

        String getProdID = productsList.get(position).getProd_id();
        String getSellerID = productsList.get(position).getProd_seller();
        String getProdName = productsList.get(position).getProd_name();
        String getProdDescription = productsList.get(position).getProd_desc();
        String getProdCover1 = productsList.get(position).getProd_image1();
        String getProdCover2 = productsList.get(position).getProd_image2();
        String getProdCategory = productsList.get(position).getProd_category();
        String getProdAvailable = productsList.get(position).getProd_avail();
        String getProdPrice = productsList.get(position).getProd_price();

        addCategory.setText(getProdCategory);
        quantity.setText(String.valueOf((int)setQuantity));
        prodName.setText(getProdName);
        prodDescription.setText(getProdDescription);
        prodPrice.setText("₱ "+getProdPrice);
        addSubtotal.setText("₱ " + setSubTotal);

        bundlePieces = (int) setQuantity;
        setSubTotal = bundlePieces * Double.parseDouble(getProdPrice);
        addSubtotal.setText("₱ " + setSubTotal);

        if(getProdCategory.equals("Pieces") || getProdCategory.equals("Bundle")){
            quantity.setText(String.valueOf((int)setQuantity));
            prodName.setText(getProdName);
            prodDescription.setText(getProdDescription);
            prodPrice.setText("₱ "+getProdPrice);
            addSubtotal.setText("₱ " + setSubTotal);
            Glide.with(this).load(getProdCover1).into(addCover1);
            Glide.with(this).load(getProdCover2).into(addCover2);

            addQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setQuantity++;
                    bundlePieces = (int) setQuantity;

                    setSubTotal = bundlePieces * Double.parseDouble(getProdPrice);
                    quantity.setText(String.valueOf(bundlePieces));
                    addSubtotal.setText("₱ " + setSubTotal);
                }
            });

            subQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(setQuantity > 0.25){
                        setQuantity--;
                        bundlePieces = (int) setQuantity;

                        setSubTotal = bundlePieces * Double.parseDouble(getProdPrice);
                        quantity.setText(String.valueOf(bundlePieces));
                        addSubtotal.setText("₱ " + setSubTotal);
                    }
                }
            });

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Globals.userCart = new CustomerCartModel(
                            0,
                            getProdID,
                            getSellerID,
                            marketName,
                            getProdName,
                            getProdDescription,
                            getProdCategory,
                            Double.parseDouble(getProdPrice),
                            bundlePieces,
                            setSubTotal);

                    if(roomDatabase.dbDao().checkExistingCart(Globals.INSTANCE.getUserCart().getProductID())){
                        roomDatabase.dbDao().addCustomerCartExist(
                                Globals.INSTANCE.getUserCart().getProductID(),
                                Globals.INSTANCE.getUserCart().getQuantity(),
                                Globals.INSTANCE.getUserCart().getSubtotal());
                    }else{
                        roomDatabase.dbDao().insertCustomerCart(new CustomerCartModel(
                                0,
                                Globals.INSTANCE.getUserCart().getProductID(),
                                Globals.INSTANCE.getUserCart().getProductSellerID(),
                                Globals.INSTANCE.getUserCart().getMarket_name(),
                                Globals.INSTANCE.getUserCart().getProductName(),
                                Globals.INSTANCE.getUserCart().getProduct_Desc(),
                                Globals.INSTANCE.getUserCart().getProduct_category(),
                                Globals.INSTANCE.getUserCart().getPrice_each(),
                                Globals.INSTANCE.getUserCart().getQuantity(),
                                Globals.INSTANCE.getUserCart().getSubtotal()
                        ));
                    }
                    setQuantity = 1;
                    Toast.makeText(CustomerStoreDetailsActivity.this, "Added to the cart", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else{
            quantity.setText(String.valueOf(setQuantity));
            prodName.setText(getProdName);
            prodDescription.setText(getProdDescription);
            prodPrice.setText("₱ "+getProdPrice);
            addSubtotal.setText("₱ " + setSubTotal);
            Glide.with(this).load(getProdCover1).into(addCover1);
            Glide.with(this).load(getProdCover2).into(addCover2);

            addQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setQuantity = setQuantity + 0.25;
                    getKilo = setQuantity;

                    setSubTotal = getKilo * Double.parseDouble(getProdPrice);
                    quantity.setText(String.valueOf(getKilo));
                    addSubtotal.setText("₱ " + setSubTotal);
                }
            });

            subQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(setQuantity > 1){
                        setQuantity = setQuantity - 0.25;
                        getKilo = setQuantity;

                        setSubTotal = getKilo * Double.parseDouble(getProdPrice);
                        quantity.setText(String.valueOf(getKilo));
                        addSubtotal.setText("₱ " + setSubTotal);
                    }
                }
            });

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Globals.userCart = new CustomerCartModel(
                            0,
                            getProdID,
                            getSellerID,
                            marketName,
                            getProdName,
                            getProdDescription,
                            getProdCategory,
                            Double.parseDouble(getProdPrice),
                            getKilo,
                            setSubTotal
                    );

                    if(roomDatabase.dbDao().checkExistingCart(Globals.INSTANCE.getUserCart().getProductID())){
                        roomDatabase.dbDao().addCustomerCartExist(
                                Globals.INSTANCE.getUserCart().getProductID(),
                                Globals.INSTANCE.getUserCart().getQuantity(),
                                Globals.INSTANCE.getUserCart().getSubtotal());
                    }else{
                        roomDatabase.dbDao().insertCustomerCart(new CustomerCartModel(
                                0,
                                Globals.INSTANCE.getUserCart().getProductID(),
                                Globals.INSTANCE.getUserCart().getProductSellerID(),
                                Globals.INSTANCE.getUserCart().getMarket_name(),
                                Globals.INSTANCE.getUserCart().getProductName(),
                                Globals.INSTANCE.getUserCart().getProduct_Desc(),
                                Globals.INSTANCE.getUserCart().getProduct_category(),
                                Globals.INSTANCE.getUserCart().getPrice_each(),
                                Globals.INSTANCE.getUserCart().getQuantity(),
                                Globals.INSTANCE.getUserCart().getSubtotal()
                        ));
                    }
                    setQuantity = 1;
                    Toast.makeText(CustomerStoreDetailsActivity.this, "Added to the cart", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }


    }


    private void loadShopDetails(String getUid){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(getUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            SellerStoreModel sellerStoreModel = ds.getValue(SellerStoreModel.class);

                            sellerName.setText(sellerStoreModel.getStore_name());
                            Glide.with(getApplicationContext()).load(sellerStoreModel.getCover_photo()).into(sellerCover);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerStoreDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadShopDetails(getUid);
        setUpProducts(getUid);
    }
}