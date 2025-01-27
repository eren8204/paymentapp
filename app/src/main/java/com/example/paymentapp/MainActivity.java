package com.example.paymentapp;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Dialog dialog,dialog2;
    private TextView membership;
    @SuppressLint({"NonConstantResourceId","MissingInflatedId", "LocalSuppress","CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        LottieAnimationView lottieAnimationshare = findViewById(R.id.shareAnimation);
        lottieAnimationshare.playAnimation();
        lottieAnimationshare.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        fetchDataSequentially(memberId);

        memberName = findViewById(R.id.memberName);
        userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);  // Disable default indicator
        toggle.setHomeAsUpIndicator(R.drawable.baseline_menu_open_24);  // Set your custom icon
        toggle.syncState();

        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId(); // Get the item ID

            if (id == R.id.drawer_item1) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.drawer_item2) {
                selectedFragment = new IDFragment();
            } else if (id == R.id.drawer_item3) {
                selectedFragment = new EditProfileFragment();
            } else if (id == R.id.drawer_item4) {
                selectedFragment = new BankKYCFragment();
            }else if (id == R.id.drawer_item5) {
                selectedFragment = new GalleryFragment();
            }else if (id == R.id.drawer_item6) {
                selectedFragment = new PlanPDFVideoFragment();
            }else if (id == R.id.drawer_item7) {
                selectedFragment = new ForgetTpinFragment();
            }else if (id == R.id.drawer_item8) {
                selectedFragment = new ChangeTpinFragment();
            }else if (id == R.id.drawer_item9) {
                selectedFragment = new ChangeLoginPasswordFragment();
            }
            else if (id == R.id.drawer_item11) {
                selectedFragment = new Transactionfragment();
            }
            else if (id == R.id.drawer_item10) {

                dialog2 = new Dialog(this);
                dialog2.setContentView(R.layout.dialog_logout_confirm);
                dialog2.setCancelable(true);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(Objects.requireNonNull(dialog2.getWindow()).getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog2.getWindow().setAttributes(layoutParams);
                Objects.requireNonNull(dialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView accept_logout = dialog2.findViewById(R.id.accept_logout);
                TextView reject_logout = dialog2.findViewById(R.id.reject_logout);

                accept_logout.setOnClickListener(v->{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                    finish();
                    dialog2.dismiss();
                    finishAffinity();
                });

                reject_logout.setOnClickListener(v->{
                    dialog2.dismiss();
                });
                dialog2.show();
            }

            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, selectedFragment);
                transaction.commit();
                drawerLayout.closeDrawers();
            }

            return true;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.my_business) {
                selectedFragment = new BusinessFragment();
            } else if (item.getItemId() == R.id.education) {
                selectedFragment = new EducationFragment();
            } else if (item.getItemId() == R.id.more) {
                selectedFragment = new MoreFragment();
            } else {
                selectedFragment = new HomeFragment();
            }
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, selectedFragment);
                transaction.commit();
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.drawer_item1);
            Fragment defaultFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, defaultFragment);
            transaction.commit();
        }

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            ImageView headerImageView = headerView.findViewById(R.id.imageView);
            TextView headerUserName = headerView.findViewById(R.id.userName);
            TextView headerUserEmail = headerView.findViewById(R.id.userEmail);
            membership = headerView.findViewById(R.id.membership);

            headerImageView.setImageResource(R.drawable.baseline_person_24);
            String header_username = sharedPreferences.getString("username", "Hello, !");
            String header_memberId = sharedPreferences.getString("memberId", "UP000000");
            headerUserName.setText(header_username);
            headerUserEmail.setText(header_memberId);
            updateMembership();
            preferenceChangeListener = (sharedPreferences, key) -> {
                if ("membership".equals(key)) {
                    updateMembership();
                }
            };
            sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }

    private void fetchDataSequentially(String memberId) {
        fetchWalletBalance(memberId, ()->fetchKYCStatus(memberId));
    }
    private void fetchWalletBalance(String memberId, Runnable onComplete){
            String url = BuildConfig.api_url+"user-wallet-wise-balance";
            Map<String, String> params = new HashMap<>();
            params.put("member_id", memberId);

        JSONObject requestBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,

                new Response.Listener<JSONObject>() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("true")) {
                                String flexi_wallet = response.getString("flexi_wallet");
                                String commission_wallet = response.getString("commission_wallet");
                                String signup_bonus = response.getString("signup_bonus");

                                flexi_wallet = String.format("%.2f", Double.parseDouble(flexi_wallet));
                                commission_wallet = String.format("%.2f", Double.parseDouble(commission_wallet));
                                signup_bonus = String.format("%.2f", Double.parseDouble(signup_bonus));

                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("flexi_wallet",flexi_wallet);
                                editor.putString("commission_wallet",commission_wallet);
                                editor.putString("signup_bonus",signup_bonus);
                                editor.apply();

                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            if (onComplete != null) {
                                onComplete.run();
                            }
                        }
                    }
                },
                error -> {
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    private void fetchKYCStatus(String memberId) {
        String url = BuildConfig.api_url+"userkycstatus";

        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);

        JSONObject requestBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("true")) {
                                if(response.has("data")) {
                                    JSONObject data = response.getJSONObject("data");
                                    String kycStatus = data.getString("Kyc_status");
                                    String kycMessage = data.getString("Kyc_message");

                                    if (kycStatus.equals("rejected"))
                                        showReminderDialog();
                                }
                                else
                                    showReminderDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (!(currentFragment instanceof HomeFragment)) {
            Fragment selectedFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, selectedFragment);
            transaction.commit();

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void showReminderDialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_kyc_reminder);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView close = dialog.findViewById(R.id.close);
        Button go = dialog.findViewById(R.id.go);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Fragment selectedFragment = null;
                selectedFragment = new BankKYCFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, selectedFragment);
                transaction.commit();
            }
        });
        dialog.show();
    }

    private void updateMembership(){
        String status = sharedPreferences.getString("membership","FREE");
        membership.setText(status);
    }
}
