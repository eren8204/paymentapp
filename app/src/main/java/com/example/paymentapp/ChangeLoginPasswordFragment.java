package com.example.paymentapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Environment;
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
public class ChangeLoginPasswordFragment extends Fragment {


private EditText old_password,new_password,confirm_password;
private Button update_password;
private ProgressBar progressBar;

private boolean passwordVisible;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_change_login_password, container, false);

        old_password=view.findViewById(R.id.old_password);
        new_password=view.findViewById(R.id.new_password);
        confirm_password=view.findViewById(R.id.confirm_password);
        update_password=view.findViewById(R.id.update_password);
        progressBar=view.findViewById(R.id.progressbar);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView name=view.findViewById(R.id.textView2);
        name.setText(memberId);

        progressBar.setVisibility(View.GONE);
        update_password.setVisibility(View.VISIBLE);
        update_password.setOnClickListener(v -> {
            try {
                progressBar.setVisibility(View.VISIBLE);
                update_password.setVisibility(View.GONE);
                submitFormData(memberId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        old_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (old_password.getRight() - old_password.getCompoundDrawables()[2].getBounds().width())) {
                        passwordVisible = !passwordVisible;
                        if (passwordVisible) {
                            old_password.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            old_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        old_password.setSelection(old_password.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });


        new_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (new_password.getRight() - new_password.getCompoundDrawables()[2].getBounds().width())) {
                        passwordVisible = !passwordVisible;
                        if (passwordVisible) {
                            new_password.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        new_password.setSelection(new_password.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });


        confirm_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirm_password.getRight() - confirm_password.getCompoundDrawables()[2].getBounds().width())) {
                        passwordVisible = !passwordVisible;
                        if (passwordVisible) {
                            confirm_password.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        confirm_password.setSelection(confirm_password.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });





        return view;
    }


    private void submitFormData(String memberId) throws JSONException {
        // Collect input data
        String oldPass = old_password.getText().toString().trim();
        String newPass = new_password.getText().toString().trim();
        String confirmPass = confirm_password.getText().toString().trim();


        Log.d("KYC", "Member ID: " + memberId);
        // Validate inputs
        if (!validateInputs(oldPass,newPass,confirmPass)) {
            return;
        }

        // Submit data via API
        sendRequest(oldPass,newPass,memberId);
    }

    private boolean validateInputs(String oldPass, String newPass,String confirmPass) {
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            update_password.setVisibility(View.VISIBLE);
            return false;
        }
        if (oldPass.length() < 8 || newPass.length() < 8) {
            Toast.makeText(getActivity(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            update_password.setVisibility(View.VISIBLE);
            return false;
        }

        if (!newPass.equals(confirmPass) ) {
            Toast.makeText(getActivity(), "Confirm Password and New Password do not match", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            update_password.setVisibility(View.VISIBLE);
            return false;
        } else if (oldPass.equals(newPass)) {
            Toast.makeText(getActivity(), "Old Password and New Password can not be same", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            update_password.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }

    private void sendRequest(String oldPass, String newPass, String memberId) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/changeUserPassword";

        // Create JSON body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("member_id", memberId);
            jsonObject.put("oldPassword", oldPass);
            jsonObject.put("newPassword", newPass);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error creating request body", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("JSON Body: " + jsonObject);

        // Create a request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")) {
                                progressBar.setVisibility(View.GONE);
                                update_password.setVisibility(View.VISIBLE);
                                Intent intent=new Intent(requireContext(),Login.class);
                                startActivity(intent);
                                requireActivity().finish();
                                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                update_password.setVisibility(View.VISIBLE);
                                String errorMessage = response.optString("error", "Server error");
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            update_password.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Error processing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                        update_password.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("API", "Error: " + error.getMessage(), error);
                    }
                }
        );

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }


}