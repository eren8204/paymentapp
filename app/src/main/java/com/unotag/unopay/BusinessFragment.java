package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.Objects;

public class BusinessFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private LinearLayout rank_layout;
    private TextView uname,uid,id_status,u_number,id_doj,flexi_text,commission_text,bonus_text,rank_graph,user_ki_rank,today_income;
    private CardView my_team,income;
    @SuppressLint({"CutPasteId", "MissingInflatedId", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_business, container, false);
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        uname = view.findViewById(R.id.id_card_uname);
        uid  = view.findViewById(R.id.id_card_uid);
        id_status = view.findViewById(R.id.id_card_status);
        my_team = view.findViewById(R.id.my_team);
        u_number = view.findViewById(R.id.id_card_no);
        id_doj = view.findViewById(R.id.id_card_doj);
        income = view.findViewById(R.id.income);
        rank_layout = view.findViewById(R.id.rank_layout);
        flexi_text = view.findViewById(R.id.flexi_wallet_text);
        commission_text = view.findViewById(R.id.total_income_text);
        bonus_text = view.findViewById(R.id.bonus_wallet_text);
        rank_graph = view.findViewById(R.id.rank_graph);
        user_ki_rank = view.findViewById(R.id.user_ki_rank);
        today_income = view.findViewById(R.id.today_income_text);
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
            if ("flexi_wallet".equals(key) || "commission_wallet".equals(key) || "membership".equals(key) || "rank".equals(key)) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isAdded()) {
                        updateUI();
                    }
                });
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

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
        marquee_text.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://whatsapp.com/channel/0029VaHzaUo42DcdqW2IGI1C"));
            intent.setPackage("com.whatsapp");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029VaHzaUo42DcdqW2IGI1C"));
                startActivity(browserIntent);
            }
        });
        return  view;
    }
    private void updateUI(){
        if (!isAdded() || getActivity() == null) {
            return;
        }
        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","Hello!");
        String memberId = sharedPreferences.getString("memberId","UP000000");
        String status = sharedPreferences.getString("membership","FREE");
        String mno = sharedPreferences.getString("mobile","9999999999");
        String doj = sharedPreferences.getString("doj","01 Jan 25");
        String flexi = sharedPreferences.getString("flexi_wallet","0.0");
        String commission = sharedPreferences.getString("commission_wallet","0.0");
        String bonus = sharedPreferences.getString("signup_bonus","0.0");
        String rank = sharedPreferences.getString("rank","Not Achieved");
        String today = sharedPreferences.getString("today_income","0.0");
        Log.d("user_ki_rank",rank);

        uname.setText(username);
        uid.setText(memberId);
        id_status.setText(status);
        u_number.setText(mno);
        id_doj.setText(doj);
        flexi_text.setText("₹ "+flexi);
        commission_text.setText("₹ "+commission);
        bonus_text.setText("₹ "+bonus);
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