package com.example.btl_android.Activity;

<<<<<<< HEAD
=======
import android.Manifest;
import android.app.AlertDialog;
>>>>>>> main
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.Adapter.ViewPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ViewPager viewPager;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView imageViewMenu, imageViewCart;
    StorageReference storageRef;  // Declare the storageRef
    ImageView avatarImageView;
    TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        // Initialize FirebaseStorage reference
        storageRef = FirebaseStorage.getInstance().getReference();

        viewPager = findViewById(R.id.view_pager);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewCart = findViewById(R.id.imageViewCart);
        loadAvatarImage();

        // Gán sự kiện click cho ImageView để mở DrawerLayout
        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Thêm sự kiện click cho giỏ hàng để chuyển đến Cart Activity
        imageViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, Cart.class);
                startActivity(intent);
            }
        });

        // Tìm headerView từ NavigationView và các thành phần avatar, txtName
        View headerView = navigationView.getHeaderView(0);
        avatarImageView = headerView.findViewById(R.id.avatar);
        txtName = headerView.findViewById(R.id.txt_name);

        // Lấy uid từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", "");

        // Tải thông tin username từ Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("taikhoan").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    txtName.setText(username); // Hiển thị tên người dùng
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Homepage.this, "Lỗi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
            }

        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.getMenu().findItem(R.id.nav_homepage).setChecked(true);
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.nav_phone).setChecked(true);
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.nav_laptop).setChecked(true);
                        break;
                    case 3:
                        navigationView.getMenu().findItem(R.id.nav_pc).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadAvatarImage() {
        // Lấy uid từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", "");
        if (uid != null && !uid.isEmpty()) {
            StorageReference avatarRef = storageRef.child("avatars/" + uid + ".jpg");

            avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get()
                        .load(uri)
                        .transform(new CircleTransform())
                        .into(avatarImageView);
            }).addOnFailureListener(exception -> {

            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Đóng ứng dụng thay vì quay lại Login
            finishAffinity(); // Đóng tất cả các activity và thoát ứng dụng
            // không gọi super.onBackPressed() vì bạn đã thay thế hành vi mặc định
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        // Set up SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý tìm kiếm khi nhấn Enter
                Toast.makeText(Homepage.this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
                // Bạn có thể thực hiện hành động tìm kiếm thực tế ở đây
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi người dùng nhập văn bản vào ô tìm kiếm
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_homepage) {
            viewPager.setCurrentItem(0);
        }
        if (item.getItemId() == R.id.nav_phone) {
            viewPager.setCurrentItem(1);
        }
        if (item.getItemId() == R.id.nav_laptop) {
            viewPager.setCurrentItem(2);
        }
        if (item.getItemId() == R.id.nav_pc) {
            viewPager.setCurrentItem(3);
        }
        if (item.getItemId() == R.id.nav_order) {
            Toast.makeText(this, "Order", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DonHang.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.nav_setting) {
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
<<<<<<< HEAD
=======

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Quyền bị từ chối
                // Thông báo cho người dùng và hướng dẫn họ cấp quyền
                new AlertDialog.Builder(this)
                        .setTitle("Quyền bị từ chối")
                        .setMessage("Để gửi thông báo, bạn cần cấp quyền cho ứng dụng. Vui lòng vào cài đặt để cấp quyền.")
                        .setPositiveButton("Cài đặt", (dialog, which) -> {
                            // Mở cài đặt ứng dụng để người dùng cấp quyền
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        }
    }

    private void GetTokenDevice() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("GetTokenDevice", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                   Log.e("GetTokenDevice", "Token: " + token);
                });
    }
>>>>>>> main
}
