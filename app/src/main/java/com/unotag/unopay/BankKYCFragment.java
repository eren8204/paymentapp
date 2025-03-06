package com.unotag.unopay;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BankKYCFragment extends Fragment {

    private Uri imageUripan, imageUriaadhar, imageUripassbook, imageUriimage, imageUriaadharback;
    private ImageView pancardcheck,Aadharcardcheck,passbookcheck,imagecheck,kyc_img,Aadharcardcheckback;
    private EditText username, Pannumber, IFSCcode, BankName, AccountNumber, ConfirmAccountNumber, Aadharnumber, nomineeName, nomineeRelation;

    private SharedPreferences sharedPreferences;
    private Button user_details_save,pancard_save,aadharcard_save,bank_details_save;
    private CardView user_details_card,pan_details_card,aadhar_details_card,bank_details_card;
    private int latestRequestCode = -1;
    private static final int REQUEST_GALLERY = 101;
    private static final int REQUEST_CAMERA = 102;
    private Uri cameraImageUri;
    private Dialog dialog2;
    private Uri selectedImageUri;
    private String url = BuildConfig.api_url;
    private ImageView confirm_img;
    private TextView panview,Aadharfrontview,Aadharbackview,passbookview,userimageView,user_details_msg,pan_details_msg,aadhar_details_msg,bank_details_msg;
    private LinearLayout form,kyc_main_progress;
    private ProgressBar user_details_progress,pan_details_progress,aadhar_details_progress,bank_details_progress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_k_y_c, container, false);
        // Initialize EditText fields
        username = view.findViewById(R.id.User_name);
        Pannumber = view.findViewById(R.id.Pan_number);
        IFSCcode = view.findViewById(R.id.IFSCcode);
        BankName = view.findViewById(R.id.BankName);
        AccountNumber = view.findViewById(R.id.AccountNumber);
        ConfirmAccountNumber = view.findViewById(R.id.ConfirmAccountNumber);
        Aadharnumber = view.findViewById(R.id.Aadharnumber);
        nomineeName = view.findViewById(R.id.NomineeName);
        nomineeRelation = view.findViewById(R.id.NomineeRelation);
        pancardcheck = view.findViewById(R.id.pancardcheck);
        Aadharcardcheck = view.findViewById(R.id.Aadharcardcheck);
        Aadharcardcheckback = view.findViewById(R.id.Aadharcardbackcheck);
        passbookcheck = view.findViewById(R.id.passbookcheck);
        imagecheck = view.findViewById(R.id.imagecheck);
        kyc_img = view.findViewById(R.id.status_img);
        user_details_save = view.findViewById(R.id.user_details_save);
        pancard_save = view.findViewById(R.id.pancard_save);
        aadharcard_save = view.findViewById(R.id.aadharcard_save);
        bank_details_save = view.findViewById(R.id.bank_details_save);
        user_details_card = view.findViewById(R.id.user_details_card);
        pan_details_card = view.findViewById(R.id.pan_details_card);
        aadhar_details_card = view.findViewById(R.id.aadhar_details_card);
        bank_details_card = view.findViewById(R.id.bank_details_card);
        user_details_msg = view.findViewById(R.id.user_details_msg);
        pan_details_msg = view.findViewById(R.id.pan_details_msg);
        aadhar_details_msg = view.findViewById(R.id.aadhar_details_msg);
        bank_details_msg = view.findViewById(R.id.bank_details_msg);
        kyc_main_progress = view.findViewById(R.id.kyc_main_progress);
        confirm_img = view.findViewById(R.id.confirm_img);
        panview = view.findViewById(R.id.panview);
        Aadharfrontview = view.findViewById(R.id.Adharfrontview);
        Aadharbackview = view.findViewById(R.id.Adharbackview);
        passbookview = view.findViewById(R.id.passbookview);
        userimageView = view.findViewById(R.id.imageview);
        user_details_progress = view.findViewById(R.id.user_details_progress);
        pan_details_progress = view.findViewById(R.id.pan_details_progress);
        aadhar_details_progress = view.findViewById(R.id.aadhar_details_progress);
        bank_details_progress = view.findViewById(R.id.bank_details_progress);

        dialog2 = new Dialog(view.getContext());

        form=view.findViewById(R.id.form);
        kyc_main_progress.setVisibility(VISIBLE);
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String mail = sharedPreferences.getString("email","mail not found");
        String name = sharedPreferences.getString("username","");
        username.setText(name);

        setViewGroupEnabled(user_details_card,false);
        setViewGroupEnabled(pan_details_card,false);
        setViewGroupEnabled(aadhar_details_card,false);
        setViewGroupEnabled(bank_details_card,false);

        pancardcheck.setEnabled(false);
        Aadharcardcheck.setEnabled(false);
        Aadharcardcheckback.setEnabled(false);
        passbookcheck.setEnabled(false);
        imagecheck.setEnabled(false);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        setUpImageUploadListeners(view);

        fetchKYCStatus(memberId);

        user_details_save.setOnClickListener(v -> {
            if(!isAdded()) return;
            String nominee_name=nomineeName.getText().toString().trim();
            String nominee_relation=nomineeRelation.getText().toString().trim();
            String user_ka_name = username.getText().toString().trim();
            if(user_ka_name.isEmpty()){
                username.setError("Enter Name");
                return;
            }
            else if(nominee_name.isEmpty()){
                nomineeName.setError("Enter Nominee Name");
                return;
            }
            else if(nominee_relation.isEmpty()){
                nomineeRelation.setError("Enter Nominee Relation");
                return;
            } else if (imageUriimage==null) {
                Toast.makeText(requireContext(), "Select user image", Toast.LENGTH_SHORT).show();
                return;
            } else{
                user_details_save.setVisibility(GONE);
                user_details_progress.setVisibility(VISIBLE);
                submitUserDetails(memberId,user_ka_name,nominee_name,nominee_relation);
            }

        });

        pancard_save.setOnClickListener(v->{
            if(!isAdded()) return;
            String pan_no=Pannumber.getText().toString().trim();
            if(pan_no.length()<10){
                Pannumber.setError("Enter valid PAN number");
                return;
            } else if (imageUripan==null) {
                Toast.makeText(requireContext(), "Select PAN Card Image", Toast.LENGTH_SHORT).show();
                return;
            } else{
                pancard_save.setVisibility(GONE);
                pan_details_progress.setVisibility(VISIBLE);
                submitUserPanCard(memberId,pan_no);
            }
        });

        aadharcard_save.setOnClickListener(v->{
            if(!isAdded()) return;
            String aadhar_no=Aadharnumber.getText().toString().trim();
            if(aadhar_no.length()<12){
                Aadharnumber.setError("Enter valid Aadhar number");
                return;
            } else if (imageUriaadhar==null || imageUriaadharback==null) {
                Toast.makeText(requireContext(), "Select Aadhar Images", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                aadharcard_save.setVisibility(GONE);
                aadhar_details_progress.setVisibility(VISIBLE);
                submitUserAadhar(memberId,aadhar_no);
            }
        });

        bank_details_save.setOnClickListener(v->{
            if(!isAdded()) return;
            String bank_name=BankName.getText().toString().trim();
            String ifsc_code=IFSCcode.getText().toString().trim();
            String acc_no=AccountNumber.getText().toString().trim();
            String cnfrm_acc = ConfirmAccountNumber.getText().toString().trim();
            if(bank_name.isEmpty()){
                BankName.setError("Enter Bank Name");
                return;
            }
            else if(ifsc_code.isEmpty()){
                IFSCcode.setError("Enter IFSC Code");
                return;
            }
            else if(acc_no.length()<9){
                AccountNumber.setError("Enter Account Number");
                return;
            }
            else if(!cnfrm_acc.equals(acc_no)){
                ConfirmAccountNumber.setError("Account number does not match");
                return;
            }
            else if (imageUripassbook==null) {
                Toast.makeText(requireContext(), "Select Bank Passbook image", Toast.LENGTH_SHORT).show();
                return;
            } else{
                bank_details_save.setVisibility(GONE);
                bank_details_progress.setVisibility(VISIBLE);
                submitBankDetails(memberId,bank_name,ifsc_code,acc_no);
            }
        });

        return view;
    }

    private void fetchImageData(String member_id, int imagecode) {
        if(!isAdded()) return;
        kyc_main_progress.setVisibility(VISIBLE);
        String url = "";
        if(imagecode == 1){
            url = BuildConfig.api_url + "user-image";
        }
        else if(imagecode == 2){
            url = BuildConfig.api_url + "pancard-image";
        }
        else if(imagecode == 3){
            url = BuildConfig.api_url + "aadhar-front-image";
        }
        else if(imagecode == 4){
            url = BuildConfig.api_url + "aadhar-back-image";
        }
        else if(imagecode == 5){
            url = BuildConfig.api_url + "bank-image";
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("member_id", member_id);
        } catch (JSONException e) {
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                url,
                jsonBody,
                response -> Log.e("Gallery", "Unexpected JSON response: " + response),
                error -> Log.e("Gallery", "Volley Error: " + error.getMessage())) {
            @Override
            protected com.android.volley.Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
                    if (bitmap != null) {
                        requireActivity().runOnUiThread(() -> showImageDialog(bitmap));
                    }
                }
                return com.android.volley.Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token", "");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        kyc_main_progress.setVisibility(GONE);
        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }

    private void fetchKYCStatus(String memberId) {
        if(!isAdded()) return;
        String url = BuildConfig.api_url+"kyc-status";

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
                            //Log.e("kyc_details_response", response.toString());
                            String status = response.optString("status","");
                            if (status.equals("true")) {
                                    JSONObject data = response.optJSONObject("userdata");
                                    if(data.has("user_details")){
                                        JSONObject userDetails = data.optJSONObject("user_details");
                                        String userDetailsStatus = userDetails.optString("status","");
                                        if(userDetailsStatus.equals("rejected")){
                                            setViewGroupEnabled(user_details_card,true);
                                            String msg = userDetails.optString("message","Please retry!");
                                            user_details_msg.setText(msg);
                                            user_details_msg.setVisibility(VISIBLE);
                                        } else if (userDetailsStatus.equals("not_filled")) {
                                            setViewGroupEnabled(user_details_card,true);
                                        }
                                        else{
                                            String name = userDetails.optString("FullName","");
                                            String nominee_name = userDetails.optString("Nominee_name","");
                                            String nominee_relation = userDetails.optString("Nominee_relation","");
                                            username.setText(name);
                                            nomineeName.setText(nominee_name);
                                            nomineeRelation.setText(nominee_relation);
                                            setViewGroupEnabled(user_details_card,false);
                                            userimageView.setEnabled(true);
                                            user_details_save.setVisibility(GONE);
                                            userimageView.setOnClickListener(v->{
                                                fetchImageData(memberId,1);
                                            });
                                            if (userDetailsStatus.equals("pending")) {
                                                user_details_msg.setVisibility(VISIBLE);
                                                user_details_msg.setText("Pending for approval");
                                                user_details_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.startColor));
                                            }
                                            else if(userDetailsStatus.equals("approved")){
                                                user_details_msg.setVisibility(VISIBLE);
                                                user_details_msg.setText("Approved");
                                                user_details_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.accept));
                                            }
                                        }
                                    }
                                    if(data.has("pancard_details")){
                                        JSONObject pancardDetails = data.optJSONObject("pancard_details");
                                        String pancardStatus = pancardDetails.optString("status","");
                                        if(pancardStatus.equals("rejected")) {
                                            setViewGroupEnabled(pan_details_card, true);
                                            String msg = pancardDetails.optString("message", "Please retry!");
                                            pan_details_msg.setText(msg);
                                            pan_details_msg.setVisibility(VISIBLE);
                                        }
                                        else if (pancardStatus.equals("not_filled")) {
                                            setViewGroupEnabled(pan_details_card, true);
                                        }
                                        else{
                                            setViewGroupEnabled(pan_details_card,false);
                                            panview.setEnabled(true);
                                            pancard_save.setVisibility(GONE);
                                            panview.setOnClickListener(v->{
                                                fetchImageData(memberId,2);
                                            });
                                            String pan_no = pancardDetails.optString("PanCard_Number","");
                                            Pannumber.setText(pan_no);
                                            if (pancardStatus.equals("pending")) {
                                                pan_details_msg.setVisibility(VISIBLE);
                                                pan_details_msg.setText("Pending for approval");
                                                pan_details_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.startColor));
                                            }
                                            else if(pancardStatus.equals("approved")) {
                                                pan_details_msg.setVisibility(VISIBLE);
                                                pan_details_msg.setText("Approved");
                                                pan_details_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.accept));
                                            }
                                        }
                                    }
                                    if(data.has("aadhar_details")) {
                                        JSONObject aadharDetails = data.optJSONObject("aadhar_details");
                                        String aadharStatus = aadharDetails.optString("status", "");
                                        if (aadharStatus.equals("rejected")) {
                                            setViewGroupEnabled(aadhar_details_card, true);
                                            String msg = aadharDetails.optString("message", "Please retry!");
                                            aadhar_details_msg.setText(msg);
                                            aadhar_details_msg.setVisibility(VISIBLE);
                                        }
                                        else if (aadharStatus.equals("not_filled")) {
                                            setViewGroupEnabled(aadhar_details_card, true);
                                        }
                                        else {
                                            setViewGroupEnabled(aadhar_details_card, false);
                                            Aadharfrontview.setEnabled(true);
                                            Aadharbackview.setEnabled(true);
                                            aadharcard_save.setVisibility(GONE);
                                            Aadharfrontview.setOnClickListener(v -> {
                                                fetchImageData(memberId, 3);
                                            });
                                            Aadharbackview.setOnClickListener(v -> {
                                                fetchImageData(memberId, 4);
                                            });
                                            String aadhar_no = aadharDetails.optString("Aadhar_Number", "");
                                            Aadharnumber.setText(aadhar_no);
                                            if (aadharStatus.equals("pending")) {
                                                aadhar_details_msg.setVisibility(VISIBLE);
                                                aadhar_details_msg.setText("Pending for approval");
                                                aadhar_details_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.startColor));
                                            }
                                            else if(aadharStatus.equals("approved")) {
                                                aadhar_details_msg.setVisibility(VISIBLE);
                                                aadhar_details_msg.setText("Approved");
                                                aadhar_details_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.accept));
                                            }
                                        }
                                    }
                                    if(data.has("bank_details")) {
                                        JSONObject bankDetails = data.optJSONObject("bank_details");
                                        String bankStatus = bankDetails.optString("status", "");
                                        if (bankStatus.equals("rejected")) {
                                            setViewGroupEnabled(bank_details_card, true);
                                            String msg = bankDetails.optString("message", "Please retry!");
                                            bank_details_msg.setText(msg);
                                            bank_details_msg.setVisibility(VISIBLE);
                                        }
                                        else if (bankStatus.equals("not_filled")) {
                                            setViewGroupEnabled(bank_details_card, true);
                                        }
                                        else {
                                            setViewGroupEnabled(bank_details_card, false);
                                            passbookview.setEnabled(true);
                                            bank_details_save.setVisibility(GONE);
                                            passbookview.setOnClickListener(v -> {
                                                fetchImageData(memberId, 5);
                                            });
                                            String bank_name = bankDetails.optString("Bank_Name", "");
                                            String ifsc_code = bankDetails.optString("IFSC_Code", "");
                                            String acc_no = bankDetails.optString("Account_number", "");
                                            IFSCcode.setText(ifsc_code);
                                            AccountNumber.setText(acc_no);
                                            ConfirmAccountNumber.setVisibility(GONE);
                                            BankName.setText(bank_name);
                                            if (bankStatus.equals("pending")) {
                                                bank_details_msg.setVisibility(VISIBLE);
                                                bank_details_msg.setText("Pending for approval");
                                                bank_details_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.startColor));
                                            }
                                            else if(bankStatus.equals("approved")) {
                                                bank_details_msg.setVisibility(VISIBLE);
                                                bank_details_msg.setText("Approved");
                                                bank_details_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.accept));
                                            }
                                        }
                                    }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            kyc_main_progress.setVisibility(GONE);
                        }
                    }
                },
                error -> {
                        kyc_main_progress.setVisibility(GONE);
                        form.setVisibility(GONE);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void submitUserDetails(String memberId, String name, String nomineeName, String nomineeRelation){
        if(!isAdded()) return;
        try{
            File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BankKYC");
            if (!directory.exists()) directory.mkdirs();
            File imageFileUser = saveImageToFile(imageUriimage, directory, "userImage.jpg");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);

            MultipartBody.Part partUserImage = prepareMultipartBody(imageFileUser, "kycUserImage");
            String token = sharedPreferences.getString("token","");
            Call<ResponseBody> call = apiService.submitUserDetails(
                    "Bearer " +token,
                    RequestBody.create(MediaType.parse("text/plain"), memberId),
                    RequestBody.create(MediaType.parse("text/plain"), name),
                    RequestBody.create(MediaType.parse("text/plain"), nomineeName),
                    RequestBody.create(MediaType.parse("text/plain"), nomineeRelation),
                    partUserImage
            );
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    Log.d("user_details_ka_response", response.toString());
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "User details submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        form.setEnabled(true);
                        Log.e("User_details", "Submission failed: " + response.message());
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("User_details", "Error body: " + errorBody);
                        } catch (IOException e) {
                            Log.e("User_details", "Error reading error body", e);
                        }
                        Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    form.setEnabled(true);
                    Log.e("User_details", "Error: " + t.getMessage(), t);
                    Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("User_details", "File processing error: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show();
        }
        finally {
            user_details_msg.setVisibility(GONE);
            user_details_progress.setVisibility(GONE);
            setViewGroupEnabled(user_details_card,false);
        }
    }

    private void submitUserPanCard(String memberId, String pancard_no){
        if(!isAdded()) return;
        try{
            File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BankKYC");
            if (!directory.exists()) directory.mkdirs();
            File imageFileUser = saveImageToFile(imageUripan, directory, "pancard.jpg");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);

            MultipartBody.Part partPanCard = prepareMultipartBody(imageFileUser, "kycPancard");
            String token = sharedPreferences.getString("token","");
            Call<ResponseBody> call = apiService.submitPanDetails(
                    "Bearer " +token,
                    RequestBody.create(MediaType.parse("text/plain"), memberId),
                    RequestBody.create(MediaType.parse("text/plain"), pancard_no),
                    partPanCard
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    Log.d("pancard_ka_response",response.toString());
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "PAN Card details submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        form.setEnabled(true);
                        Log.e("pancard", "Submission failed: " + response.message());
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("pancard", "Error body: " + errorBody);
                        } catch (IOException e) {
                            Log.e("pancard", "Error reading error body", e);
                        }
                        Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    form.setEnabled(true);
                    Log.e("pancard", "Error: " + t.getMessage(), t);
                    Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("pancard", "File processing error: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show();
        }
        finally {
            pan_details_msg.setVisibility(GONE);
            pan_details_progress.setVisibility(GONE);
            setViewGroupEnabled(pan_details_card,false);
        }
    }

    private void submitUserAadhar(String memberId, String Aadhar){
        if(!isAdded()) return;
        try{
            File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BankKYC");
            if (!directory.exists()) directory.mkdirs();
            File imageFileUser = saveImageToFile(imageUriaadhar, directory, "aadharfront.jpg");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);

            MultipartBody.Part partAaadhar = prepareMultipartBody(imageFileUser, "kycAadharFront");
            String token = sharedPreferences.getString("token","");
            Call<ResponseBody> call = apiService.submitAadharDetails(
                    "Bearer " +token,
                    RequestBody.create(MediaType.parse("text/plain"), memberId),
                    RequestBody.create(MediaType.parse("text/plain"), Aadhar),
                    partAaadhar
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    Log.d("aadhar_ka_response",response.toString());
                    if (response.isSuccessful()) {
                        submitUserAadharBack(memberId);
                    } else {
                        form.setEnabled(true);
                        Log.e("aadhar", "Submission failed: " + response.message());
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("aadhar", "Error body: " + errorBody);
                        } catch (IOException e) {
                            Log.e("aadhar", "Error reading error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    form.setEnabled(true);
                    Log.e("aadhar", "Error: " + t.getMessage(), t);
                }
            });

        } catch (Exception e) {
            Log.e("aadhar", "File processing error: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Error processing images", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitUserAadharBack(String memberId){
        if(!isAdded()) return;
        try{
            File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BankKYC");
            if (!directory.exists()) directory.mkdirs();
            File imageFileUser = saveImageToFile(imageUriaadharback, directory, "aadharback.jpg");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);

            MultipartBody.Part partAaadharBack = prepareMultipartBody(imageFileUser, "kycAadharBack");
            String token = sharedPreferences.getString("token","");
            Call<ResponseBody> call = apiService.submitAadharBackDetails(
                    "Bearer " +token,
                    RequestBody.create(MediaType.parse("text/plain"), memberId),
                    partAaadharBack
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    Log.d("aadharback_ka_response",response.toString());
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "User details submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        form.setEnabled(true);
                        Log.e("aadharback", "Submission failed: " + response.message());
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("aadharback", "Error body: " + errorBody);
                        } catch (IOException e) {
                            Log.e("aadharback", "Error reading error body", e);
                        }
                        Toast.makeText(getActivity(), "Submission failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    form.setEnabled(true);
                    Log.e("aadharback", "Error: " + t.getMessage(), t);
                    Toast.makeText(getActivity(), "Error submitting KYC", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("aadharback", "File processing error: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Error processing images", Toast.LENGTH_SHORT).show();
        }
        finally {
            aadhar_details_msg.setVisibility(GONE);
            aadhar_details_progress.setVisibility(GONE);
            setViewGroupEnabled(aadhar_details_card,false);
        }
    }

    private void submitBankDetails(String memberId, String Bank_name, String IFSC, String Acc_no){
        if(!isAdded()) return;
        try{
            File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BankKYC");
            if (!directory.exists()) directory.mkdirs();
            File imageFileUser = saveImageToFile(imageUripassbook, directory, "passbook.jpg");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);

            MultipartBody.Part partBankPassbook = prepareMultipartBody(imageFileUser, "kycPassbook");
            String token = sharedPreferences.getString("token","");
            Call<ResponseBody> call = apiService.submitBankDetails(
                    "Bearer " +token,
                    RequestBody.create(MediaType.parse("text/plain"), memberId),
                    RequestBody.create(MediaType.parse("text/plain"), Bank_name),
                    RequestBody.create(MediaType.parse("text/plain"), IFSC),
                    RequestBody.create(MediaType.parse("text/plain"), Acc_no),
                    partBankPassbook
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    Log.d("bank_details_ka_response",response.toString());
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "Bank details submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        form.setEnabled(true);
                        Log.e("bank_details", "Submission failed: " + response.message());
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("bank_details", "Error body: " + errorBody);
                        } catch (IOException e) {
                            Log.e("bank_details", "Error reading error body", e);
                        }
                        Toast.makeText(getActivity(), "Submission failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    form.setEnabled(true);
                    Log.e("bank_details", "Error: " + t.getMessage(), t);
                    Toast.makeText(getActivity(), "Error submitting KYC", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("bank_details", "File processing error: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Error processing images", Toast.LENGTH_SHORT).show();
        }
        finally {
            bank_details_msg.setVisibility(GONE);
            bank_details_progress.setVisibility(GONE);
            setViewGroupEnabled(bank_details_card, false);
        }
    }

    private void setUpImageUploadListeners(View view) {
        view.findViewById(R.id.uploadPancard).setOnClickListener(v -> openImagePicker(1));
        view.findViewById(R.id.uploadAadharcard).setOnClickListener(v -> openImagePicker(2));
        view.findViewById(R.id.uploadAadharcardback).setOnClickListener(v -> openImagePicker(3));
        view.findViewById(R.id.uploadUserimage).setOnClickListener(v -> openImagePicker(4));
        view.findViewById(R.id.uploadPassBook).setOnClickListener(v -> openImagePicker(5));

    }

    private void openImagePicker(int requestCode) {
        if(!isAdded()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Image Source")
                .setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera(requestCode + 100); // Camera (Add 100 to differentiate)
                    } else {
                        openGallery(requestCode); // Gallery (Keep requestCode same)
                    }
                })
                .show();
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setType("image/*");
        startActivityForResult(intent, requestCode); // Keep requestCode unchanged for Gallery
    }

    private void openCamera(int requestCode) {
        if(!isAdded()) return;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {

            File imageFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "IMG_" + System.currentTimeMillis() + ".jpg");

            cameraImageUri = FileProvider.getUriForFile(requireContext(),
                    requireActivity().getPackageName()+".fileprovider",
                    imageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(intent, requestCode + 100);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFileName);
    }

    private File saveImageToFile(Uri imageUri, File directory, String fileName) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
        File imageFile = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, fos);
        }
        return imageFile;
    }

    private MultipartBody.Part prepareMultipartBody(File file, String formFieldName) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        return MultipartBody.Part.createFormData(formFieldName, file.getName(), requestFile);
    }

    public void imagePreview(Uri selectedImageUri, TextView btn_id, int i){
            btn_id.setOnClickListener(v -> {
                if(!isAdded()) return;
            dialog2.setContentView(R.layout.img_confirm_dialog);
            dialog2.setCancelable(true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(dialog2.getWindow()).getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog2.getWindow().setAttributes(layoutParams);

            confirm_img = dialog2.findViewById(R.id.confirm_img);
            confirm_img.setImageURI(selectedImageUri);

            Button change = dialog2.findViewById(R.id.change);
            Button ok = dialog2.findViewById(R.id.ok);
            Button crop = dialog2.findViewById(R.id.crop);
                crop.setOnClickListener(s -> {
                    if (selectedImageUri == null) {
                        Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CropImageOptions options = new CropImageOptions();
                    options.guidelines = CropImageView.Guidelines.ON; // Show grid guidelines
                    options.fixAspectRatio = false; // Free crop (no fixed aspect ratio)
                    options.multiTouchEnabled = false; // Enable pinch-to-zoom
                    options.activityTitle = "Crop Image"; // Set title for crop screen
                    options.cropMenuCropButtonTitle = "Confirm"; // Set confirm button text
                    Uri fixedUri = getContentUri(selectedImageUri);
                    cropImage.launch(new CropImageContractOptions(fixedUri, options));
                });

                change.setOnClickListener(s->{
                    openImagePicker(i);
                    dialog2.dismiss();
                });
                ok.setOnClickListener(s ->{
                dialog2.dismiss();
            });

            dialog2.show();
        });
    dialog2.setContentView(R.layout.img_confirm_dialog);
    dialog2.setCancelable(true);
    if(!isAdded()) return;
    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    layoutParams.copyFrom(Objects.requireNonNull(dialog2.getWindow()).getAttributes());
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    dialog2.getWindow().setAttributes(layoutParams);

    ImageView img = dialog2.findViewById(R.id.confirm_img);
    img.setImageURI(selectedImageUri);

    Button change = dialog2.findViewById(R.id.change);
    Button ok = dialog2.findViewById(R.id.ok);
    Button crop = dialog2.findViewById(R.id.crop);

    change.setOnClickListener(v->{
            openImagePicker(i);
            dialog2.dismiss();
    });

        crop.setOnClickListener(s -> {
            if(!isAdded()) return;
            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
                return;
            }

            CropImageOptions options = new CropImageOptions();
            options.guidelines = CropImageView.Guidelines.ON; // Show grid guidelines
            options.fixAspectRatio = false;
            options.multiTouchEnabled = false; // Enable pinch-to-zoom
            options.activityTitle = "Crop Image"; // Set title for crop screen
            options.cropMenuCropButtonTitle = "Confirm"; // Set confirm button text
            Uri fixedUri = getContentUri(selectedImageUri);
            cropImage.launch(new CropImageContractOptions(fixedUri, options));
        });


        ok.setOnClickListener(s ->{
        dialog2.dismiss();
    });

    dialog2.show();
    if(!isAdded()) return;
    Toast.makeText(requireView().getContext(), "Image selected successfully", Toast.LENGTH_SHORT).show();
}

    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), result -> {
                if (result.isSuccessful()) {
                    Uri resultUri = result.getUriContent();

                    if (resultUri != null) {
                        selectedImageUri = resultUri;
                        if (confirm_img != null) {
                            confirm_img.setImageURI(null);
                            confirm_img.setImageURI(selectedImageUri);
                        }
                        if (latestRequestCode != -1) {
                            switch (latestRequestCode % 100) {
                                case 1: imageUripan = selectedImageUri; pancardcheck.setVisibility(View.VISIBLE); imagePreview(selectedImageUri, panview, 1); break;
                                case 2: imageUriaadhar = selectedImageUri; Aadharcardcheck.setVisibility(View.VISIBLE); imagePreview(selectedImageUri, Aadharfrontview, 2); break;
                                case 3: imageUriaadharback = selectedImageUri; Aadharcardcheckback.setVisibility(View.VISIBLE); imagePreview(selectedImageUri, Aadharbackview, 3); break;
                                case 4: imageUriimage = selectedImageUri; imagecheck.setVisibility(View.VISIBLE); imagePreview(selectedImageUri, userimageView, 4); break;
                                case 5: imageUripassbook = selectedImageUri; passbookcheck.setVisibility(View.VISIBLE); imagePreview(selectedImageUri, passbookview, 5); break;
                            }
                        }
                    }
                } else {
                    // Handle error
                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri originalUri = null;

            if (requestCode >= 100) { // Camera Image
                originalUri = cameraImageUri;
            } else { // Gallery Image
                originalUri = (data != null) ? data.getData() : null;
            }

            if (originalUri == null) return;

            latestRequestCode = requestCode;

            // Launch CropImage
            CropImageOptions options = new CropImageOptions();
            options.guidelines = CropImageView.Guidelines.ON;
            options.fixAspectRatio = false;
            options.multiTouchEnabled = false;
            options.activityTitle = "Crop Image";
            options.cropMenuCropButtonTitle = "Confirm";

            cropImage.launch(new CropImageContractOptions(originalUri, options));
        }
    }

    private Uri getContentUri(Uri uri) {
        if (uri.getScheme().equals("file")) {
            return FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName() +".fileprovider", new File(uri.getPath()));
        }
        return uri;
    }

    private long getFileSize(Uri uri) {
        Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
        long size = 0;
        if (cursor != null) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (sizeIndex != -1) {
                cursor.moveToFirst();
                size = cursor.getLong(sizeIndex);
            }
            cursor.close();
        }
        return size;
    }

    private void setViewGroupEnabled(ViewGroup viewGroup, boolean enabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enabled);

            if (child instanceof ViewGroup) {
                setViewGroupEnabled((ViewGroup) child, enabled);
            }
        }
    }

    private void showImageDialog(Bitmap bitmap) {
        if(!isAdded()) return;
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.setContentView(R.layout.gallery_image);

        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }
}