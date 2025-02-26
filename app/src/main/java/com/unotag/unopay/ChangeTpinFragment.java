package com.unotag.unopay;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChangeTpinFragment extends Fragment {


    private SharedPreferences sharedPreferences;
    private EditText oldTpin,newTpin,confirmTpin;
    private Button update_Tpin;
    private ProgressBar progressBar;
    private boolean passwordVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_change_tpin, container, false);

        oldTpin=view.findViewById(R.id.oldTpin);
        newTpin=view.findViewById(R.id.newTpin);
        confirmTpin=view.findViewById(R.id.confirmTpin);
        update_Tpin=view.findViewById(R.id.Update_Tpin);
        progressBar=view.findViewById(R.id.progressbar);

        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        TextView name=view.findViewById(R.id.textView2);
        name.setText(memberId);

        oldTpin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (oldTpin.getRight() - oldTpin.getCompoundDrawables()[2].getBounds().width())) {
                    passwordVisible = !passwordVisible;
                    if (passwordVisible) {
                        oldTpin.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else {
                        oldTpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    oldTpin.setSelection(oldTpin.getText().length());
                    return true;
                }
            }
            return false;
        });

        newTpin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (newTpin.getRight() - newTpin.getCompoundDrawables()[2].getBounds().width())) {
                    passwordVisible = !passwordVisible;
                    if (passwordVisible) {
                        newTpin.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else {
                        newTpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    newTpin.setSelection(newTpin.getText().length());
                    return true;
                }
            }
            return false;
        });

        confirmTpin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmTpin.getRight() - confirmTpin.getCompoundDrawables()[2].getBounds().width())) {
                    passwordVisible = !passwordVisible;
                    if (passwordVisible) {
                        confirmTpin.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else {
                        confirmTpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    confirmTpin.setSelection(confirmTpin.getText().length());
                    return true;
                }
            }
            return false;
        });

        progressBar.setVisibility(View.GONE);
        update_Tpin.setVisibility(View.VISIBLE);
        update_Tpin.setOnClickListener(v -> {
            try {
                progressBar.setVisibility(View.VISIBLE);
                update_Tpin.setVisibility(View.GONE);
                submitFormData(memberId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });


        return view;
    }

    private void submitFormData(String memberId) throws JSONException {
        // Collect input data
        String old_Tpin = oldTpin.getText().toString().trim();
        String new_Tpin = newTpin.getText().toString().trim();
        String confirm_Pass = confirmTpin.getText().toString().trim();


        Log.d("KYC", "Member ID: " + memberId);
        // Validate inputs
        if (!validateInputs(old_Tpin,new_Tpin,confirm_Pass)) {
            return;
        }

        sendRequest(old_Tpin,new_Tpin,memberId);
    }

    private boolean validateInputs(String old_Tpin,String new_Tpin,String confirm_Pass) {
        if (old_Tpin.isEmpty() || new_Tpin.isEmpty() || confirm_Pass.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            update_Tpin.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (old_Tpin.length() < 4 || new_Tpin.length() < 4) {
            Toast.makeText(getActivity(), "Pin must be at least 4 characters long", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            update_Tpin.setVisibility(View.VISIBLE);
            return false;
        }

        if (!new_Tpin.equals(confirm_Pass) ) {
            progressBar.setVisibility(View.GONE);
            update_Tpin.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Confirm Tpin and New Tpin do not match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (old_Tpin.equals(new_Tpin)) {
            progressBar.setVisibility(View.GONE);
            update_Tpin.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Old Pin and New Pin can not be same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void sendRequest(String old_Tpin, String new_Tpin, String memberId) {
        String url = BuildConfig.api_url_non_auth+"forget/changeUserTpin";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("member_id", memberId);
            jsonObject.put("oldtpin", old_Tpin);
            jsonObject.put("newtpin", new_Tpin);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error creating request body", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        if (response.getString("status").equals("true")) {
                            progressBar.setVisibility(View.GONE);
                            update_Tpin.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Tpin updated successfully", Toast.LENGTH_SHORT).show();
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.nav_host_fragment, new HomeFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            update_Tpin.setVisibility(View.VISIBLE);
                            String errorMessage = response.optString("error", "Server error");
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        update_Tpin.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage;
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorResponse = new JSONObject(errorBody);
                            errorMessage = errorResponse.optString("error", "Submission failed");
                        } catch (Exception e) {
                            errorMessage = "Error processing error response";
                        }
                    } else {
                        errorMessage = "Submission failed";
                    }
                    progressBar.setVisibility(View.GONE);
                    update_Tpin.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("API", "Error: " + error.getMessage(), error);
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(jsonObjectRequest);
    }
}