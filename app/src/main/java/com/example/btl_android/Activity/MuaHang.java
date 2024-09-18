package com.example.btl_android.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.Adapter.MuaHangAdapter;
import com.example.btl_android.R;
import com.example.btl_android.item.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MuaHang extends AppCompatActivity {

    EditText etCustomerName, etPhoneNumber, etAddress;
    TextView tvTotalAmount, tvOrderDate;
    RecyclerView rvProducts;
    MuaHangAdapter adapter;
    Button Btn_thanhtoan;
    List<CartItem> cartItemList = new ArrayList<>();
    long totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mua_hang);

        // Ánh xạ view
        etCustomerName = findViewById(R.id.et_customer_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etAddress = findViewById(R.id.et_address);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        rvProducts = findViewById(R.id.rv_products);
        tvOrderDate = findViewById(R.id.tv_order_date);
        Btn_thanhtoan = findViewById(R.id.btn_thanh_toan);

        ImageView backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MuaHang.this, CartItem.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Setup RecyclerView
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MuaHangAdapter(this, cartItemList);
        rvProducts.setAdapter(adapter);

        // Kiểm tra nguồn dữ liệu (Cart hoặc ProductDetailActivity)
        if (getIntent().hasExtra("from_cart") && getIntent().getBooleanExtra("from_cart", false)) {
            // Trường hợp từ giỏ hàng
            loadCartItems();
        } else {
            // Trường hợp từ ProductDetailActivity
            loadSingleProduct();
            // Tải thông tin khách hàng
            loadCustomerInfo();
        }

        // Xử lý sự kiện nhấn nút thanh toán
        Btn_thanhtoan.setOnClickListener(v -> createOrder());
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void loadSingleProduct() {
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String productName = intent.getStringExtra("ten_san_pham");
        long productPrice = intent.getLongExtra("gia", 0);
        String productImage = intent.getStringExtra("hinh_anh");

        // Tạo một CartItem từ dữ liệu nhận được
        CartItem singleItem = new CartItem(productName, productPrice, 1, productImage);

        // Thêm vào danh sách sản phẩm
        cartItemList.clear();
        cartItemList.add(singleItem);
        adapter.notifyDataSetChanged();

        // Cập nhật tổng tiền
        totalAmount = productPrice;
        tvTotalAmount.setText("Tổng tiền: " + totalAmount + " VND");

        // Hiển thị ngày đặt hàng
        loadOrderDate();
    }

    // Phương thức hiện tại để tải danh sách sản phẩm từ giỏ hàng
    private void loadCartItems() {
        String uid = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("uid", null);
        if (uid != null) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);
            cartRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cartItemList.clear();  // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                    totalAmount = 0;  // Đặt lại tổng tiền về 0
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        CartItem item = itemSnapshot.getValue(CartItem.class);
                        if (item != null) {
                            cartItemList.add(item);
                            totalAmount += item.getPrice() * item.getQuantity();
                        }
                    }

                    // Thông báo adapter rằng dữ liệu đã thay đổi
                    adapter.notifyDataSetChanged();

                    // Hiển thị tổng tiền
                    tvTotalAmount.setText("Tổng tiền: " + totalAmount + " VND");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MuaHang", "Error loading cart items", error.toException());
                }
            });
        }
    }

    private void createOrder() {
        String uid = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("uid", null);
        if (uid != null) {
            // Lấy thông tin khách hàng từ EditText
            String customerName = etCustomerName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Kiểm tra thông tin khách hàng
            if (customerName.isEmpty()) {
                Toast.makeText(MuaHang.this, "Tên khách hàng không được để trống.", Toast.LENGTH_SHORT).show();
                etCustomerName.requestFocus();
                return;
            }

            if (phoneNumber.isEmpty()) {
                Toast.makeText(MuaHang.this, "Số điện thoại không được để trống.", Toast.LENGTH_SHORT).show();
                etPhoneNumber.requestFocus();
                return;
            }

            if (address.isEmpty()) {
                Toast.makeText(MuaHang.this, "Địa chỉ không được để trống.", Toast.LENGTH_SHORT).show();
                etAddress.requestFocus();
                return;
            }

            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("don_hang").child(uid);
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);

            // Tạo mã đơn hàng duy nhất
            String orderId = ordersRef.push().getKey();

            // Tạo dữ liệu đơn hàng
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("maDonHang", orderId);
            orderData.put("trangThai", "Đã đặt hàng");
            orderData.put("ngayDatHang", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
            orderData.put("tongTien", totalAmount);
            orderData.put("tenKhachHang", customerName);
            orderData.put("soDienThoai", phoneNumber);
            orderData.put("diaChi", address);

            // Thêm danh sách sản phẩm vào đơn hàng
            List<Map<String, Object>> products = new ArrayList<>();
            for (CartItem item : cartItemList) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("tenSanPham", item.getName());
                productData.put("gia", item.getPrice());
                productData.put("soLuong", item.getQuantity());
                products.add(productData);
            }
            orderData.put("sanPham", products);

            // Lưu dữ liệu đơn hàng vào Firebase
            assert orderId != null;
            ordersRef.child(orderId).setValue(orderData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Xóa giỏ hàng sau khi đơn hàng được tạo thành công
                            cartRef.removeValue()
                                    .addOnCompleteListener(cartTask -> {
                                        if (cartTask.isSuccessful()) {
                                            // Thông báo thành công
                                            Toast.makeText(MuaHang.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                            Log.d("MuaHang", "Đơn hàng đã được lưu thành công và giỏ hàng đã được xóa!");

                                            // Chuyển về trang chủ
                                            Intent intent = new Intent(MuaHang.this, Homepage.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Thông báo thất bại
                                            Toast.makeText(MuaHang.this, "Xóa giỏ hàng thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                            Log.e("MuaHang", "Lỗi khi xóa giỏ hàng", cartTask.getException());
                                        }
                                    });
                        } else {
                            // Thông báo thất bại
                            Toast.makeText(MuaHang.this, "Đặt hàng thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                            Log.e("MuaHang", "Lỗi khi lưu đơn hàng", task.getException());
                        }
                    });
        } else {
            Log.e("MuaHang", "UID không tồn tại trong SharedPreferences");
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadOrderDate() {
        // Lấy ngày hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Hiển thị ngày đặt hàng lên giao diện
        tvOrderDate.setText("Ngày đặt hàng: " + currentDate);
    }

    private void loadCustomerInfo() {
        String uid = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("uid", null);
        if (uid != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("taikhoan").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Lấy thông tin từ Firebase
                        String name = snapshot.child("username").getValue(String.class);
                        String phone = snapshot.child("sdt").getValue(String.class);
                        String address = snapshot.child("diachi").getValue(String.class);

                        // Hiển thị thông tin lên EditText với định dạng "Tên: ...", "Số điện thoại: ...", "Địa chỉ: ..."
                        etCustomerName.setText(name != null ? "Tên: " + name : "Tên: ");
                        etPhoneNumber.setText(phone != null ? "Số điện thoại: " + phone : "Số điện thoại: ");
                        etAddress.setText(address != null ? "Địa chỉ: " + address : "Địa chỉ: ");
                    } else {
                        Log.e("MuaHang", "Không tìm thấy thông tin người dùng với UID: " + uid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MuaHang", "Error loading user info", error.toException());
                }
            });
        } else {
            Log.e("MuaHang", "UID không tồn tại trong SharedPreferences");
        }
    }




}
