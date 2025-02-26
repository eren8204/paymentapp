package com.unotag.unopay;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginHelp extends AppCompatActivity {
    private ImageButton back;
    private Button submit;
    private ProgressBar submit_progress;
    private EditText email_edit,userid,issue_details;
    private LinearLayout issue_layout,success_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_help);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));

        submit = findViewById(R.id.submit_issue);
        submit_progress = findViewById(R.id.issue_progress);
        email_edit = findViewById(R.id.email_edit);
        userid = findViewById(R.id.userid);
        issue_details = findViewById(R.id.issue_details);
        issue_layout = findViewById(R.id.issue_layout);
        success_layout = findViewById(R.id.issue_success_layout);

        issue_layout.setVisibility(View.VISIBLE);
        success_layout.setVisibility(View.GONE);

        back = findViewById(R.id.back_button);
        back.setOnClickListener(v->{
            finish();
        });

        submit.setOnClickListener(v -> {
            submit.setVisibility(View.GONE);
            submit_progress.setVisibility(View.VISIBLE);
            String email = email_edit.getText().toString().trim();
            String mem = userid.getText().toString().trim();
            String msg = issue_details.getText().toString().trim();
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                email_edit.setError("Enter valid email");
                submit.setVisibility(View.VISIBLE);
                submit_progress.setVisibility(View.GONE);
                return;
            }
            if (msg.isEmpty()) {
                issue_details.setError("Describe your issue");
                submit.setVisibility(View.VISIBLE);
                submit_progress.setVisibility(View.GONE);
                return;
            }
            sendIssue(email, mem, msg);
        });

    }
    public void sendIssue(String email, String member_id, String message){
        String url =BuildConfig.api_url_non_auth+"raiseTicket/login-issue";
        try{
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("email", email);
            jsonInput.put("member_id", member_id);
            jsonInput.put("message_by_user", message);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonInput,
                    response -> {
                        try {
                            if(response.has("status") && response.getString("status").equals("true")){
                                submit_progress.setVisibility(View.GONE);
                                submit.setVisibility(View.VISIBLE);
                                Toast.makeText(this, "Issue submitted successfully", Toast.LENGTH_SHORT).show();
                                issue_layout.setVisibility(View.GONE);
                                success_layout.setVisibility(View.VISIBLE);
                            }
                            else{
                                submit_progress.setVisibility(View.GONE);
                                submit.setVisibility(View.VISIBLE);
                                String msg = "Error submitting issue";
                                if(response.has("message")){
                                    msg = response.getString("message");
                                }
                                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            submit_progress.setVisibility(View.GONE);
                            submit.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "Error submitting issue", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        submit_progress.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Error submitting issue", Toast.LENGTH_SHORT).show();
                    }
            );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e){
            submit_progress.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Error submitting issue", Toast.LENGTH_SHORT).show();
        }
    }
}