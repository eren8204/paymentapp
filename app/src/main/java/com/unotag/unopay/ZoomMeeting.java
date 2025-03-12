package com.unotag.unopay;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ZoomMeeting extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private TextView meeting_title,date_text,time_text;
    private LinearLayout date_layout,time_layout,progress_layout,all_details;
    private CardView join_meeting;
    private String link;
    @SuppressLint({"MissingInflatedId","SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_meeting);
        meeting_title = findViewById(R.id.meeting_title);
        date_text = findViewById(R.id.date);
        time_text = findViewById(R.id.time);
        date_layout = findViewById(R.id.date_layout);
        time_layout = findViewById(R.id.time_layout);
        join_meeting = findViewById(R.id.join_meeting);
        all_details = findViewById(R.id.all_details);
        progress_layout = findViewById(R.id.progress_layout);

        progress_layout.setVisibility(VISIBLE);
        all_details.setVisibility(GONE);
        join_meeting.setVisibility(GONE);
        date_layout.setVisibility(GONE);
        time_layout.setVisibility(GONE);
        getMeeting();
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        pre();
        join_meeting.setOnClickListener(v->{
            if(!link.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                intent.setPackage("us.zoom.videomeetings");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(webIntent);
                }
            }
            else{
                Toast.makeText(this, "No Meeting Scheduled", Toast.LENGTH_SHORT).show();
            }
        });
        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));
    }

    private void getMeeting(){
        String url = BuildConfig.api_url + "getMeetingDetails";
        JSONObject requestBody = new JSONObject();
        @SuppressLint({"SetTextI18n", "ApplySharedPref"}) JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try{
                        if(response.has("status") && response.getString("status").equals("true")){
                            progress_layout.setVisibility(GONE);
                            all_details.setVisibility(VISIBLE);
                            if(response.has("meetingDetails")){
                                JSONObject meetingDetails = response.getJSONObject("meetingDetails");
                                meeting_title.setText(meetingDetails.getString("title"));
                                link = meetingDetails.getString("link");
                                String date = formatDate(meetingDetails.getString("date_time"));
                                String time = formatTime(meetingDetails.getString("date_time"));
                                date_text.setText(date);
                                time_text.setText(time);
                                date_layout.setVisibility(VISIBLE);
                                time_layout.setVisibility(VISIBLE);
                                join_meeting.setVisibility(VISIBLE);
                            }
                            else{
                                meeting_title.setText("No Meeting Scheduled");
                                date_layout.setVisibility(GONE);
                                time_layout.setVisibility(GONE);
                                join_meeting.setVisibility(GONE);
                            }
                        }
                        else if(response.has("error")){
                            Toast.makeText(ZoomMeeting.this, "Session Expired, Please Login Again", Toast.LENGTH_SHORT).show();
//                            sharedPreferences.edit().clear().apply();
//                            Intent intent = new Intent(ZoomMeeting.this, Login.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                            finish();
                        }
                        else{
                            progress_layout.setVisibility(GONE);
                            all_details.setVisibility(VISIBLE);
                            meeting_title.setText("No Meeting Scheduled");
                            date_layout.setVisibility(GONE);
                            time_layout.setVisibility(GONE);
                            join_meeting.setVisibility(GONE);
                        }
                    }
                    catch (Exception e){
                        progress_layout.setVisibility(GONE);
                        all_details.setVisibility(VISIBLE);
                        meeting_title.setText("No Meeting Scheduled");
                        date_layout.setVisibility(GONE);
                        time_layout.setVisibility(GONE);
                        join_meeting.setVisibility(GONE);
                    }
                },
                error -> {
                    progress_layout.setVisibility(GONE);
                    all_details.setVisibility(VISIBLE);
                    meeting_title.setText("No Meeting Scheduled");
                    date_layout.setVisibility(GONE);
                    time_layout.setVisibility(GONE);
                    join_meeting.setVisibility(GONE);
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public static String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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

    private String formatTime(String utcDateString) {
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String outputFormat = "hh:mm a";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        try {
            Date date = inputDateFormat.parse(utcDateString);
            if (date != null) {

                return outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return utcDateString;
    }
    private void pre(){
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            finish();
        });
    }
}