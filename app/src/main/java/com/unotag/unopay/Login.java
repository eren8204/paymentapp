package com.unotag.unopay;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class Login extends BaseActivity {

    private boolean isAutoLogin = false;
    private TextView signup, error_msg, help;
    private EditText email, password, otp_edittext;
    private Button login;
    private ProgressBar progressbarlogin;
    private String username = "";
    private InstallReferrerClient referrerClient;
    private TextView forgetpassword;
    private String pass = "", otp = "", androidId = "";
    private String url = BuildConfig.api_url + "login2";
    private boolean passwordVisible, otpVisible = false;
    private AppUpdateManager appUpdateManager;
    private static final int UPDATE_REQUEST_CODE = 6969;
    private boolean isnotificationpermissiongranted=false;
    private boolean isexactalarmgranted=false;
    String[] ranks = {"Not Achieved", "Opal", "Topaz", "Jasper", "Alexander", "Diamond", "Blue Diamond", "Crown Diamond"};
    private LinearLayout whatsapp,youtube,telegram,facebook,instagram;
    private GridLayout social;

    private SharedPreferences sharedPreferences;

    @SuppressLint({"ClickableViewAccessibility", "HardwareIds"})
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
        whatsapp = findViewById(R.id.whatsapp);
        youtube = findViewById(R.id.youtube);
        telegram = findViewById(R.id.telegram);
        facebook = findViewById(R.id.facebook);
        instagram = findViewById(R.id.instagram);
        social = findViewById(R.id.social);

        //ye websocket ke liye hai
//        Intent serviceIntent = new Intent(this, WebSocketService.class);
//        startService(serviceIntent);

        // permissions
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestNotificationPermissionWithDialog();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            requestExactAlarmPermissionWithDialog();
//        }

        referrerClient = InstallReferrerClient.newBuilder(this).build();

        Uri data = getIntent().getData();
        SharedPreferences referPref = getSharedPreferences("referPref", MODE_PRIVATE);

        if (data != null && data.getQueryParameter("sponsor_id") != null) {
            String referrerId = data.getQueryParameter("sponsor_id");
            referPref.edit().putString("sponsor_id", referrerId).apply();
            Log.d("DeepLink", "Stored sponsor_id from deep link: " + referrerId);
        } else {
            if (referrerClient != null) {
                referrerClient.startConnection(new InstallReferrerStateListener() {
                    @Override
                    public void onInstallReferrerSetupFinished(int responseCode) {
                        if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                            try {
                                ReferrerDetails response = referrerClient.getInstallReferrer();
                                String referrerUrl = response.getInstallReferrer();
                                Log.d("InstallReferrer", "Referrer URL: " + referrerUrl);

                                if (referrerUrl != null) {
                                    Uri referrerUri = Uri.parse("https://fake.com?" + referrerUrl);
                                    String referrerId = referrerUri.getQueryParameter("utm_campaign");

                                    if (referrerId != null) {
                                        referPref.edit().putString("sponsor_id", referrerId).apply();
                                        Log.d("InstallReferrer", "Stored sponsor_id: " + referrerId);
                                    } else {
                                        Log.e("InstallReferrer", "utm_campaign not found in referrer URL");
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("InstallReferrer", "Error retrieving referrer", e);
                            }
                        }
                        referrerClient.endConnection();
                    }

                    @Override
                    public void onInstallReferrerServiceDisconnected() {
                        Log.w("InstallReferrer", "Install Referrer service disconnected");
                    }
                });
            } else {
                Log.e("InstallReferrer", "InstallReferrerClient is null");
            }
        }

        forgetpassword.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgetPassword.class);
            startActivity(intent);
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

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
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
            isAutoLogin = true;
        }
        checkForUpdate();
        signup.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
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
            loginIdPass();
        });

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

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://whatsapp.com/channel/0029VaHzaUo42DcdqW2IGI1C"));
                intent.setPackage("com.whatsapp");

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029VaHzaUo42DcdqW2IGI1C"));
                    startActivity(browserIntent);
                }

            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/share/1DcMLnBuv2/"));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/share/1DcMLnBuv2/"));

                try {
                    startActivity(facebookIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(browserIntent);
                }

            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telegramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=unotagofficial"));
                telegramIntent.setPackage("org.telegram.messenger");

                try {
                    startActivity(telegramIntent);
                } catch (ActivityNotFoundException e) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/unotagofficial"));
                    startActivity(browserIntent);
                }

            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/unotag.official.unopay"));
                instaIntent.setPackage("com.instagram.android");

                try {
                    startActivity(instaIntent);
                } catch (ActivityNotFoundException e) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/unotag.official.unopay?utm_source=qr&igsh=MXV6eXIxaHQ2NHc1Yg=="));
                    startActivity(browserIntent);
                }

            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://www.youtube.com/@unotagofficialunopay"));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@unotagofficialunopay?si=Uv6_T34rh18QREPe"));

                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(webIntent);
                }
            }
        });
    }
//
//    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
//    private void requestNotificationPermissionWithDialog() {
//        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Notification Permission")
//                    .setMessage("This app requires notification permission to send daily reminders. Please allow it.")
//                    .setPositiveButton("Allow", (dialog, which) -> {
//                        // Redirect the user to the app's notification settings
//                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                        startActivity(intent);
//                    })
//                    .setNegativeButton("Deny", (dialog, which) -> dialog.dismiss())
//                    .show();
//        } else {
//            isnotificationpermissiongranted = true;
//            setDailyNotification();
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.S)
//    private void requestExactAlarmPermissionWithDialog() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        if (!alarmManager.canScheduleExactAlarms()) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Alarm Permission required")
//                    .setMessage("This app requires exact alarm permission to schedule daily notifications. Please allow it.")
//                    .setPositiveButton("Allow", (dialog, which) -> {
//                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
//                        startActivity(intent);
//                    })
//                    .setNegativeButton("Deny", (dialog, which) -> dialog.dismiss())
//                    .show();
//        } else {
//            isexactalarmgranted=true;
//            setDailyNotification();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1001) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                setDailyNotification();
//            } else {
//               // Toast.makeText(this, "Notification permission is required for daily reminders.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void setDailyNotification() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, NotificationReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                this,
//                0,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY,6);
//        calendar.set(Calendar.MINUTE, 00);
//        calendar.set(Calendar.SECOND, 0);
//
//        if (Calendar.getInstance().after(calendar)) {
//            calendar.add(Calendar.DATE, 1);
//        }
//
////        alarmManager.setExactAndAllowWhileIdle(
////                AlarmManager.RTC_WAKEUP,
////                calendar.getTimeInMillis(),
////                pendingIntent
////        );
//    }

    private void checkForUpdate() {
        if (BuildConfig.DEBUG) {
            if (isAutoLogin) {
                loginIdPass();
            }
            return;
        }

        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            boolean isUpdateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE);

            if (isUpdateAvailable) {
                redirectToPlayStore();
            } else if (isAutoLogin) {
                loginIdPass();
            }
        });

        appUpdateInfoTask.addOnFailureListener(e -> {
            if (isAutoLogin) {
                loginIdPass();
            }
        });
    }


    private void redirectToPlayStore() {
        Toast.makeText(this, "Please update the app to continue", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAndRemoveTask();
    }


    public void loginIdPass() {
        login.setVisibility(View.GONE);
        progressbarlogin.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("identifier", username);
            requestBody.put("password", pass);
            requestBody.put("device_id", androidId);
            requestBody.put("otp", otp);
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
                            String token = "";
                            if(response.has("userLoginToken")){
                                token = response.getString("userLoginToken");
                            }
                            String message = response.optString("message","");
                            String memberId = response.optString("memberid","");
                            String userName = response.optString("username","");
                            String membership = response.optString("membership","FREE");
                            String mob_no = response.optString("phoneNo","");
                            String email = response.optString("email","");
                            String doj = response.optString("date_of_joining","0");
                            String sponsor_id = response.optString("sponserid","");
                            String sponsor_name = response.optString("sponserName","");
                            String sponsor_no = response.optString("sponserNo","");
                            String activation_date = response.optString("activationDate","");
                            String newDoj = formatDate(doj);
                            String ifsc = "";
                            String acc_no = "";
                            String bank = "";
                            String aadhar = "";
                            String pan = "";
                            String nominee_name = "";
                            String relation = "";
                            String rank = ranks[response.optInt("rank_no",0)];
                            if(response.has("Kyc_status") && response.getString("Kyc_status").equals("approved")){
                                ifsc = response.optString("IFSC_Code","");
                                acc_no = response.optString("Account_number","");
                                bank = response.optString("Bank_Name","");
                                aadhar = response.optString("Aadhar_Number");
                                pan = response.optString("PanCard_Number","");
                                nominee_name = response.optString("Nominee_name","");
                                relation = response.optString("Nominee_relation","");
                            }
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("password", pass);
                            editor.putString("username", userName.toUpperCase());
                            editor.putString("memberId", memberId);
                            editor.putString("membership", membership);
                            editor.putString("doj", newDoj);
                            editor.putString("mobile", mob_no);
                            editor.putString("email", email);
                            editor.putString("token", token);
                            editor.putString("ifsc",ifsc);
                            editor.putString("acc_no",acc_no);
                            editor.putString("bank",bank);
                            editor.putString("aadhar",aadhar);
                            editor.putString("pan",pan);
                            editor.putString("nominee_name",nominee_name);
                            editor.putString("relation",relation);
                            editor.putString("rank",rank);
                            editor.putString("sponsor_id",sponsor_id);
                            editor.putString("sponsor_name",sponsor_name.toUpperCase());
                            editor.putString("sponsor_no",sponsor_no);
                            editor.putString("doa",activation_date);
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
                            }
                            password.setText("");
                            email.setEnabled(true);
                            password.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        password.setText("");
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
                    password.setText("");
                    progressbarlogin.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    email.setEnabled(true);
                    password.setEnabled(true);
                    forgetpassword.setEnabled(true);
                    help.setEnabled(true);
                    signup.setEnabled(true);
                    Log.d("loginkierror", Objects.requireNonNull(error.getMessage()));
                    Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
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

        // Input is in UTC
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Output should be in IST
        outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        try {
            Date date = inputFormat.parse(dateString);
            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

}