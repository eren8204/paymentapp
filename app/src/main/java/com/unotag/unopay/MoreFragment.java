package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class MoreFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private CardView companydocument,aboutus,privacyPolicy,termsandco,refundPolicy,account_deletion,raiseticket,gallery,rateus;
    private ImageView userProfile;
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        TextView userName = view.findViewById(R.id.userName);
        TextView userId = view.findViewById(R.id.userId);
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        userName.setText(username);
        userId.setText(memberId);

        privacyPolicy = view.findViewById(R.id.privacyPolicy);
        termsandco = view.findViewById(R.id.termsandco);
        refundPolicy = view.findViewById(R.id.refundPolicy);
        account_deletion = view.findViewById(R.id.account_deletion);
        raiseticket = view.findViewById(R.id.raiseticket);
        gallery = view.findViewById(R.id.gallery);
        userProfile = view.findViewById(R.id.userProfile);


//        companydocument.setOnClickListener(v -> {
//            assert getFragmentManager() != null;
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.replace(R.id.nav_host_fragment, new Company_Document());
//            transaction.commit();
//        });

        Bitmap profileBitmap = loadImageFromInternalStorage();
        if (profileBitmap != null) {
            userProfile.setImageBitmap(profileBitmap);
        }

        userProfile.setOnClickListener(v -> {
            if(profileBitmap!=null)
                showImageDialog(profileBitmap);
        });

        companydocument=view.findViewById(R.id.companydocument);
        companydocument.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),companydocument.class);
            startActivity(intent);
        });

        aboutus=view.findViewById(R.id.aboutus);
        aboutus.setOnClickListener(v -> {
          Intent intent=new Intent(getActivity(),Aboutus_Activity.class);
          startActivity(intent);
        });

        privacyPolicy.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), PrivacyPolicy.class);
            startActivity(intent);
        });
        termsandco.setOnClickListener(v-> {
            Intent intent = new Intent(getActivity(), Terms.class);
            startActivity(intent);
        });
        refundPolicy.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), RefundPolicy.class);
            startActivity(intent);
        });
        account_deletion.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), AccountDeletion.class);
            startActivity(intent);
        });

        raiseticket.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),RaiseTicket_Activity.class);
            startActivity(intent);
        });

        gallery.setOnClickListener(v->{
            Intent intent=new Intent(getActivity(),Gallery.class);
            startActivity(intent);
        });

        return view;
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

    private void showImageDialog(Bitmap bitmap) {
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.setContentView(R.layout.gallery_image);

        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }

}