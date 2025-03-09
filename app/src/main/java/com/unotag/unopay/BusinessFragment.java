package com.unotag.unopay;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BusinessFragment extends Fragment {
    private Uri imageUri,cameraImageUri;
    private Dialog dialog2;
    private Button ok;
    private ProgressBar upload_progress;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private LinearLayout rank_layout,level_status,doa_layout,doj_layout;
    private TextView uname,uid,id_status,u_number,id_doa,id_doj,flexi_text,commission_text,bonus_text,rank_graph,user_ki_rank,today_income,filename;
    private CardView my_team,income;
    private ImageView view_image,change_image;
    @SuppressLint({"CutPasteId", "MissingInflatedId", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_business, container, false);
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        dialog2 = new Dialog(requireContext());
        uname = view.findViewById(R.id.id_card_uname);
        uid  = view.findViewById(R.id.id_card_uid);
        id_status = view.findViewById(R.id.id_card_status);
        my_team = view.findViewById(R.id.my_team);
        u_number = view.findViewById(R.id.id_card_no);
        id_doj = view.findViewById(R.id.id_card_doj);
        id_doa = view.findViewById(R.id.id_card_doa);
        doa_layout = view.findViewById(R.id.doa_layout);
        doj_layout = view.findViewById(R.id.doj_layout);
        income = view.findViewById(R.id.income);
        rank_layout = view.findViewById(R.id.rank_layout);
        flexi_text = view.findViewById(R.id.flexi_wallet_text);
        commission_text = view.findViewById(R.id.total_income_text);
        bonus_text = view.findViewById(R.id.bonus_wallet_text);
        rank_graph = view.findViewById(R.id.rank_graph);
        user_ki_rank = view.findViewById(R.id.user_ki_rank);
        today_income = view.findViewById(R.id.today_income_text);
        level_status = view.findViewById(R.id.level_status);
        view_image = view.findViewById(R.id.view_image);
        change_image = view.findViewById(R.id.change_image);
        user_ki_rank.setText("Not Achieved");
        TextView marquee_text = view.findViewById(R.id.marquee_text);
        marquee_text.setSelected(true);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.a, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.b, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.c, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.d, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.e, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.f, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        updateUI();
        preferenceChangeListener = (sharedPreferences, key) -> {
            if ("flexi_wallet".equals(key) || "commission_wallet".equals(key) || "membership".equals(key) || "rank".equals(key) || "doa".equals(key)) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isAdded()) {
                        updateUI();
                    }
                });
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        Bitmap profileBitmap = loadImageFromInternalStorage();
        if (profileBitmap != null) {
            view_image.setImageBitmap(profileBitmap);
        }
        view_image.setOnClickListener(v -> {
            if(!isAdded()) return;
            Bitmap newprofileBitmap = loadImageFromInternalStorage();
            if(newprofileBitmap!=null)
                showImageDialog(newprofileBitmap);
        });
        change_image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });
        my_team.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyTeam.class);
            startActivity(intent);
        });

        income.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyIncome.class);
            startActivity(intent);
        });

        rank_layout.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), Rank.class);
            startActivity(intent);
        });
        rank_graph.setOnClickListener(v->{
            Intent intent = new Intent(requireContext(), RankGraph.class);
            startActivity(intent);
        });
        level_status.setOnClickListener(v->{
            Intent intent = new Intent(requireContext(), LevelStatusGraph.class);
            startActivity(intent);
        });
        marquee_text.setOnClickListener(v -> {
            String number = "6395427453";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + "+91"+number));
            startActivity(intent);
        });
        return  view;
    }

    private void showImageDialog(Bitmap bitmap) {
        if(!isAdded()) return;
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.setContentView(R.layout.gallery_image);

        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }

    private Bitmap loadImageFromInternalStorage() {
        try {
            FileInputStream fis = requireContext().openFileInput("profile_image.png");
            return BitmapFactory.decodeStream(fis);
        } catch (IOException e) {
            Log.e("Gallery", "Error loading image: " + e.getMessage());
            return null;
        }
    }

    private void uploadProfile(String memberId) {
        if (!isAdded()) return;
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "UserProfile");
                if (!directory.exists()) directory.mkdirs();

                // Save Image to File (Heavy Operation)
                File imageFileUser = saveImageToFile(imageUri, directory);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BuildConfig.api_url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);

                MultipartBody.Part partUserPhoto = prepareMultipartBody(imageFileUser);
                String token = sharedPreferences.getString("token", "");

                Call<ResponseBody> call = apiService.submitProfilePhoto(
                        "Bearer " + token,
                        RequestBody.create(MediaType.parse("text/plain"), memberId),
                        partUserPhoto
                );

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                        if(!isAdded()) return;
                        if (response.isSuccessful()) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                if (imageUri != null) {
                                    if (!isAdded()) return;
                                    saveImageFromUriToInternalStorage(imageUri);
                                    requireActivity().runOnUiThread(() -> {
                                        Bitmap profileBitmap = loadImageFromInternalStorage();
                                        if (profileBitmap != null) {
                                            view_image.setImageBitmap(profileBitmap);
                                        }
                                    });
                                    if(!isAdded()) return;
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(getActivity(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                                    );
                                } else {
                                    if(!isAdded()) return;
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show()
                                    );
                                }
                                if(!isAdded()) return;
                                if (dialog2 != null) dialog2.dismiss();
                            });

                        } else {
                            if(!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                if (dialog2 != null) dialog2.dismiss();
                            });
                            Log.e("userProfile", "Submission failed: " + response.message());
                            Toast.makeText(getActivity(), "Error try again later", Toast.LENGTH_SHORT).show();
                            handleError(response);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        if(!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            if (dialog2 != null) dialog2.dismiss();
                        });
                        Toast.makeText(getActivity(), "Error try again later", Toast.LENGTH_SHORT).show();
                        Log.e("userProfile", "Error: " + t.getMessage(), t);
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show()
                        );
                    }
                });

            } catch (Exception e) {
                if(!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (dialog2 != null) dialog2.dismiss();
                });
                Log.e("userProfile", "File processing error: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // Handle error response
    private void handleError(retrofit2.Response<ResponseBody> response) {
        if(!isAdded()) return;
        try {
            assert response.errorBody() != null;
            String errorBody = response.errorBody().string();
            Log.e("userProfile", "Error body: " + errorBody);
        } catch (IOException e) {
            Log.e("userProfile", "Error reading error body", e);
        }
        requireActivity().runOnUiThread(() ->
                Toast.makeText(getActivity(), "Error Try Again Later", Toast.LENGTH_SHORT).show()
        );
    }


    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!isAdded()) return;
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                long imageSize = getFileSize(imageUri);
                if (imageSize > 5 * 1024 * 1024) { // 5MB in bytes
                    Toast.makeText(requireActivity(), "Image size exceeds 5MB. Please select a smaller image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with showing the dialog
                dialog2.setContentView(R.layout.image_confirm_profile);
                dialog2.setCancelable(true);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(Objects.requireNonNull(dialog2.getWindow()).getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog2.getWindow().setAttributes(layoutParams);

                ImageView img = dialog2.findViewById(R.id.confirm_img);
                img.setImageURI(imageUri);

                Button change = dialog2.findViewById(R.id.change);
                ok = dialog2.findViewById(R.id.ok);
                upload_progress = dialog2.findViewById(R.id.upload_progress);

                change.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                    dialog2.dismiss();
                });

                ok.setOnClickListener(v ->{
                    ok.setVisibility(GONE);
                    upload_progress.setVisibility(VISIBLE);
                    uploadProfile(uid.getText().toString());
                });

                dialog2.show();
                Toast.makeText(requireActivity(), "Image selected successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageFromUriToInternalStorage(Uri image) {
        try {
            if (isAdded() && getActivity() != null){
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), image);
                saveImageToInternalStorage(bitmap);
            }
        } catch (IOException e) {
            Log.e("Gallery", "Error converting Uri to Bitmap: " + e.getMessage());
        }
    }

    private void saveImageToInternalStorage(Bitmap bitmap) {
        if(!isAdded()) return;
        try {
            FileOutputStream fos = requireContext().openFileOutput("profile_image.png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.d("Gallery", "Image saved successfully: " + "profile_image.png");
        } catch (IOException e) {
            Log.e("Gallery", "Error saving image: " + e.getMessage());
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFileName);
    }

    private File saveImageToFile(Uri imageUri, File directory) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
        File imageFile = new File(directory, "user_profile.jpg");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos);
        }
        return imageFile;
    }

    private MultipartBody.Part prepareMultipartBody(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        return MultipartBody.Part.createFormData("profileImage", file.getName(), requestFile);
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

    @SuppressLint("SetTextI18n")
    private void updateUI(){
        if (!isAdded() || getActivity() == null) {
            return;
        }
        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username","User");
        String memberId = sharedPreferences.getString("memberId","UP000000");
        String status = sharedPreferences.getString("membership","FREE");
        String mno = sharedPreferences.getString("mobile","-");
        String doj = sharedPreferences.getString("doj","-");
        String flexi = sharedPreferences.getString("flexi_wallet","0.0");
        String total = sharedPreferences.getString("total_income","0.0");
        String bonus = sharedPreferences.getString("signup_bonus","0.0");
        String rank = sharedPreferences.getString("rank","Not Achieved");
        String today = sharedPreferences.getString("today_income","0.0");
        String dateOfActivation = sharedPreferences.getString("doa", "-");
        Log.d("user_ki_rank",rank);

        uname.setText(username);
        uid.setText(memberId);
        switch (status) {
            case "FREE":
                id_status.setTextColor(getResources().getColor(R.color.startColor));
                break;
            case "BASIC":
                id_status.setTextColor(getResources().getColor(R.color.endColor));
                break;
            case "PREMIUM":
                id_status.setTextColor(getResources().getColor(R.color.accept));
                break;
            default:
                id_status.setTextColor(getResources().getColor(R.color.start_bg));
                break;
        }
        if(!rank.equals("Not Achieved"))
            user_ki_rank.setTextColor(getResources().getColor(R.color.endColor));
        else
            user_ki_rank.setTextColor(getResources().getColor(R.color.msg_grey));
        if(dateOfActivation.equals("-")) {
            doa_layout.setVisibility(GONE);
            doj_layout.setVisibility(VISIBLE);
        }
        else {
            id_doa.setText(dateOfActivation);
            doa_layout.setVisibility(VISIBLE);
            doj_layout.setVisibility(GONE);
        }
        id_status.setText(status);
        u_number.setText(mno);
        id_doj.setText(doj);
        flexi_text.setText("₹ "+flexi);
        commission_text.setText("₹ "+total);
        bonus_text.setText(bonus);
        user_ki_rank.setText(rank);
        today_income.setText("₹ "+today);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedPreferences != null && preferenceChangeListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }

}