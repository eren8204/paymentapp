package com.example.paymentapp;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Login extends AppCompatActivity {

    private TextView signup, error_msg, help;
    private EditText email, password, otp_edittext;
    private Button login;
    private ProgressBar progressbarlogin;
    private String username = "";

    private TextView forgetpassword;
    private String pass = "", otp = "", androidId = "";
    private String url = BuildConfig.api_url + "login2";
    private boolean passwordVisible, otpVisible = false;

    private boolean isnotificationpermissiongranted=false;
    private boolean isexactalarmgranted=false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));

        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        forgetpassword = findViewById(R.id.forgetpassword);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressbarlogin = findViewById(R.id.progressbarlogin);
        error_msg = findViewById(R.id.login_error_text);
        otp_edittext = findViewById(R.id.otp);
        help = findViewById(R.id.help);

        //ye websocket ke liye hai
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        startService(serviceIntent);


        // permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionWithDialog();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestExactAlarmPermissionWithDialog();
        }

        forgetpassword.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgetPassword.class);
            startActivity(intent);
            finish();
        });

        help.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, LoginHelp.class);
            startActivity(intent);
        });

        LottieAnimationView lottieAnimationshare = findViewById(R.id.secureAnimation);
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

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        if (sharedPreferences.contains("memberId") && sharedPreferences.contains("password")) {
            pass = sharedPreferences.getString("password", "");
            username = sharedPreferences.getString("memberId", "");
            email.setText(username);
            password.setText(pass);
            forgetpassword.setEnabled(false);
            help.setEnabled(false);
            email.setEnabled(false);
            password.setEnabled(false);
            login.setEnabled(true);
            signup.setEnabled(false);
        }
        if(isnotificationpermissiongranted && isexactalarmgranted) {
                loginIdPass();

        }
        else {
           // Toast.makeText(this, "Notification permission is required for daily reminders.", Toast.LENGTH_SHORT).show();
        }

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });

        login.setOnClickListener(v -> loginIdPass());

        password.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[2].getBounds().width())) {
                    passwordVisible = !passwordVisible;
                    if (passwordVisible) {
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else {
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    password.setSelection(password.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermissionWithDialog() {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle("Notification Permission")
                    .setMessage("This app requires notification permission to send daily reminders. Please allow it.")
                    .setPositiveButton("Allow", (dialog, which) -> {
                        // Redirect the user to the app's notification settings
                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(intent);
                    })
                    .setNegativeButton("Deny", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            isnotificationpermissiongranted = true;
            setDailyNotification();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestExactAlarmPermissionWithDialog() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (!alarmManager.canScheduleExactAlarms()) {
            new AlertDialog.Builder(this)
                    .setTitle("Alarm Permission required")
                    .setMessage("This app requires exact alarm permission to schedule daily notifications. Please allow it.")
                    .setPositiveButton("Allow", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                    })
                    .setNegativeButton("Deny", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            isexactalarmgranted=true;
            setDailyNotification();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setDailyNotification();
            } else {
               // Toast.makeText(this, "Notification permission is required for daily reminders.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,6);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    public void loginIdPass() {
        username = email.getText().toString().trim();
        pass = password.getText().toString().trim();
        otp = otp_edittext.getText().toString().trim();
        if (username.length() < 8 || pass.length() < 8) {
            Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (otpVisible && otp.length() < 6) {
            Toast.makeText(Login.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        login.setVisibility(View.GONE);
        progressbarlogin.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("identifier", username);
            requestBody.put("password", pass);
            requestBody.put("device_id", androidId);
            requestBody.put("otp", otp);
            Log.d("login_request", requestBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        if (response.has("status") && response.getString("status").equals("true")) {
                            String message = response.getString("message");
                            String memberId = response.getString("memberid");
                            String userName = response.getString("username");
                            String membership = response.getString("membership");
                            String mob_no = response.getString("phoneNo");
                            String email = response.getString("email");
                            String doj = response.getString("date_of_joining");
                            String newDoj = formatDate(doj);
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("password", pass);
                            editor.putString("username", userName);
                            editor.putString("memberId", memberId);
                            editor.putString("membership", membership);
                            editor.putString("doj", newDoj);
                            editor.putString("mobile", mob_no);
                            editor.putString("email", email);
                            editor.apply();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.has("status") && response.getString("status").equals("false")) {
                            progressbarlogin.setVisibility(View.GONE);
                            login.setVisibility(View.VISIBLE);
                            login.setEnabled(true);
                            forgetpassword.setEnabled(true);
                            help.setEnabled(true);
                            String msg = response.getString("message");
                            error_msg.setVisibility(View.VISIBLE);
                            error_msg.setText(msg);
                            if (!msg.equalsIgnoreCase("Wrong password") && !msg.equalsIgnoreCase("User not registered")) {
                                otp_edittext.setVisibility(View.VISIBLE);
                                otpVisible = true;
                                email.setEnabled(true);
                                password.setEnabled(true);
                            }
                        }
                    } catch (JSONException e) {
                        progressbarlogin.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        login.setEnabled(true);
                        email.setEnabled(true);
                        password.setEnabled(true);
                        forgetpassword.setEnabled(true);
                        help.setEnabled(true);
                        signup.setEnabled(true);
                        Log.d("login_error", e.toString());
                        Toast.makeText(Login.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressbarlogin.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    email.setEnabled(true);
                    password.setEnabled(true);
                    forgetpassword.setEnabled(true);
                    help.setEnabled(true);
                    signup.setEnabled(true);
                    Log.d("loginkierror", error.getMessage());
                    Toast.makeText(Login.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(jsonObjectRequest);
    }

    public static String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }


}