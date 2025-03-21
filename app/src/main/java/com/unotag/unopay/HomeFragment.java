package com.unotag.unopay;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private HorizontalScrollView horizontalScrollView;
    private CardView addfund;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private TextView bonusamount, fundwalletamount, income_wallet, total_income, today_income;
    private LinearLayout prepaid_mobile,cibil,sbi,icici,hdfc,postpaid_mobile,dth_recharge,fastag,electricity_bill,gas_cylinder,water_bill,cable_tv,money_transfer,credit_card,
            loan_repayment,atm_locator,bike_insurance,car_insurance,family_insurance,tax_calculation,irctc,confirm_tkt,spot_train,parivahan,redbus,makemytrip,
            ola,uber,aadhar,pan_card,income_tax,ecard,voter_card,passport,post_office,rashan,amazon,flipkart,meesho,zomato,swiggy,vishal_mart,bookmyshow,tata1mg,
            entertainment,kuku,business,money,full_plan,kyc,add_money,computer,pro_class,support,whatsapp,facebook,telegram,instagram,youtube;
    @SuppressLint({"MissingInflatedId", "LocalSuppress","ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        TextView marquee_text = view.findViewById(R.id.marquee_text);

        addfund=view.findViewById(R.id.addfund);
        prepaid_mobile = view.findViewById(R.id.prepaid_mobile);
        cibil = view.findViewById(R.id.cibil);
        sbi = view.findViewById(R.id.sbi);
        icici = view.findViewById(R.id.icici);
        hdfc = view.findViewById(R.id.hdfc);
        postpaid_mobile = view.findViewById(R.id.postpaid_mobile);
        dth_recharge = view.findViewById(R.id.dth_recharge);
        fastag = view.findViewById(R.id.fastag_recharge);
        electricity_bill = view.findViewById(R.id.electricity_bill);
        gas_cylinder = view.findViewById(R.id.gas_cylinder);
        water_bill = view.findViewById(R.id.water_bill);
        cable_tv = view.findViewById(R.id.cable_tv);
        money_transfer = view.findViewById(R.id.money_transfer);
        credit_card = view.findViewById(R.id.credit_card);
        loan_repayment = view.findViewById(R.id.loan_repayment);
        atm_locator = view.findViewById(R.id.atm_locator);
        bike_insurance = view.findViewById(R.id.bike_insurance);
        car_insurance = view.findViewById(R.id.car_insurance);
        family_insurance = view.findViewById(R.id.family_insurance);
        tax_calculation = view.findViewById(R.id.tax_calculation);
        irctc = view.findViewById(R.id.irctc);
        confirm_tkt = view.findViewById(R.id.confirm_tkt);
        spot_train = view.findViewById(R.id.spot_train);
        parivahan = view.findViewById(R.id.parivahan);
        redbus = view.findViewById(R.id.redbus);
        makemytrip = view.findViewById(R.id.makemytrip);
        ola = view.findViewById(R.id.ola);
        uber = view.findViewById(R.id.uber);
        aadhar = view.findViewById(R.id.aadhar);
        pan_card = view.findViewById(R.id.pan_card);
        income_tax = view.findViewById(R.id.income_tax);
        ecard = view.findViewById(R.id.ecard);
        voter_card = view.findViewById(R.id.voter_card);
        passport = view.findViewById(R.id.passport);
        post_office = view.findViewById(R.id.post_office);
        rashan = view.findViewById(R.id.rashan);
        amazon = view.findViewById(R.id.amazon);
        flipkart = view.findViewById(R.id.flipkart);
        meesho = view.findViewById(R.id.meesho);
        zomato = view.findViewById(R.id.zomato);
        swiggy = view.findViewById(R.id.swiggy);
        vishal_mart = view.findViewById(R.id.vishal_mart);
        bookmyshow = view.findViewById(R.id.bookmyshow);
        tata1mg = view.findViewById(R.id.tata1mg);
        entertainment = view.findViewById(R.id.entertainment);
        kuku = view.findViewById(R.id.kuku);
        business = view.findViewById(R.id.business);
        money = view.findViewById(R.id.money);
        full_plan = view.findViewById(R.id.full_plan);
        kyc = view.findViewById(R.id.kyc);
        add_money = view.findViewById(R.id.add_money);
        computer = view.findViewById(R.id.computer);
        pro_class = view.findViewById(R.id.pro_class);
        support = view.findViewById(R.id.support);
        whatsapp = view.findViewById(R.id.whatsapp);
        facebook = view.findViewById(R.id.facebook);
        telegram = view.findViewById(R.id.telegram);
        instagram = view.findViewById(R.id.instagram);
        youtube = view.findViewById(R.id.youtube);

        horizontalScrollView = view.findViewById(R.id.horizonatalScrollView);

        if (isAdded() && getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        }

        bonusamount = view.findViewById(R.id.bonusamount);
        fundwalletamount = view.findViewById(R.id.fundwalletamount);
        income_wallet = view.findViewById(R.id.income_wallet);
        total_income = view.findViewById(R.id.totalincome);
        today_income = view.findViewById(R.id.today_income);

        if (isAdded()) {
            updateUI();
        }

        preferenceChangeListener = (sharedPreferences, key) -> {
            if ("flexi_wallet".equals(key) || "commission_wallet".equals(key) || "today_income".equals(key) || "total_income".equals(key)) {
                if (isAdded()) {
                    updateUI();
                }
            }
        };

        if (sharedPreferences != null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        }


        LottieAnimationView lottieAnimationzoom = view.findViewById(R.id.zoomAnimation);
        lottieAnimationzoom.playAnimation();
        lottieAnimationzoom.addAnimatorListener(new Animator.AnimatorListener() {
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
        lottieAnimationzoom.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ZoomMeeting.class);
            startActivity(intent);
        });

        LottieAnimationView lottieAnimationqr = view.findViewById(R.id.qrAnimation);
        lottieAnimationqr.playAnimation();
        lottieAnimationqr.addAnimatorListener(new Animator.AnimatorListener() {
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

        lottieAnimationqr.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), addfund.class);
            startActivity(intent);
        });

        LottieAnimationView lottieAnimationqrscan = view.findViewById(R.id.qrscanAnimation);
        lottieAnimationqrscan.playAnimation();
        lottieAnimationqrscan.addAnimatorListener(new Animator.AnimatorListener() {
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
        lottieAnimationqrscan.setOnClickListener(v->{
            String sponsor_id = sharedPreferences.getString("memberId","");
            ShareUtil.shareApp(requireContext(), sponsor_id);
        });

        LottieAnimationView lottieAnimationprime = view.findViewById(R.id.primeAnimation);
        lottieAnimationprime.playAnimation();
        lottieAnimationprime.addAnimatorListener(new Animator.AnimatorListener() {
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
        lottieAnimationprime.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Plans_Activity.class);
                startActivity(intent);
        });

        LottieAnimationView lottieAnimationnoti = view.findViewById(R.id.notificationAnimation);
        lottieAnimationnoti.playAnimation();
        lottieAnimationnoti.addAnimatorListener(new Animator.AnimatorListener() {
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

        marquee_text.setSelected(true);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.a, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.b, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.c, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.d, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.e, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.f, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        Runnable autoScroll = new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.smoothScrollBy(5, 0);
                horizontalScrollView.postDelayed(this, 35);

                if (horizontalScrollView.getScrollX() >= horizontalScrollView.getChildAt(0).getWidth() - horizontalScrollView.getWidth()) {
                    horizontalScrollView.scrollTo(0, 0);
                }
            }
        };
        horizontalScrollView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    horizontalScrollView.removeCallbacks(autoScroll);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    horizontalScrollView.post(autoScroll);
                    break;
            }
            return false;
        });

// Start the auto-scroll
        horizontalScrollView.post(autoScroll);
        addfund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), addfund.class);
                startActivity(intent);
            }
        });


        cibil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.bajajfinserv.in/check-free-cibil-score");
            }
        });
        sbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.sbicard.com/en/personal/credit-cards.page");
            }
        });
        icici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://campaigns.icicibank.com/credit-card/platinumCreditCard/index.html?utm_source=sem&utm_medium=search&utm_campaign=icicibank-a4-search-credit-card-core-cc-platinum-brand-category-exact-apr&utm_content=sem-search-icicibank-a4-search-credit-card-core-cc-platinum-brand-category-exact-apr-performance-na-cpc-Generic-General-na-icici-credit-card&utm_term=icici-credit-card&utm_lms=sem-search-icicibank-a4-search-credit-card-core-cc-platinum-brand-category-exact-apr-performance-na-cpc-Generic-General-na-icici-credit-card&gad_source=1&gclid=Cj0KCQjw8MG1BhCoARIsAHxSiQkI6U_ouuBZymFBOPLXwJSPdHmGJmwy1Ok4vuOx90Ix_R0IzjlibLYaAgfHEALw_wcB");
            }
        });
        hdfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://applyonline.hdfcbank.com/cards/credit-cards.html?utm_content=DGPI&Channel=DSA&DSACode=XRKD&SMCode=H5399&LGCode=MMM2&LCCode=RKS002&LC2=RKS002#nbb");
            }
        });
        irctc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.irctc.co.in/nget/train-search");
            }
        });
        confirm_tkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.confirmtkt.com/rbooking-d/");
            }
        });
        spot_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.railyatri.in/live-train-status");
            }
        });
        parivahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://parivahan.gov.in/parivahan/");
            }
        });
        redbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.redbus.in/");
            }
        });
        makemytrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.makemytrip.com/");
            }
        });
        ola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.olacabs.com/");
            }
        });
        uber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.uber.com/in/en/");
            }
        });
        aadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://uidai.gov.in/");
            }
        });
        pan_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.pan.utiitsl.com/");
            }
        });
        income_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.incometax.gov.in/iec/foportal/");
            }
        });
        ecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://eshram.gov.in/");
            }
            });
        passport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.passportindia.gov.in/AppOnlineProject/welcomeLink");
            }
        });
        post_office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.indiapost.gov.in/vas/Pages/IndiaPostHome.aspx");
            }
        });
        rashan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://nfsa.gov.in/portal/apply_ration_card");
            }
            });
        amazon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.amazon.in/");
            }
        });
        flipkart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.flipkart.com/");
            }
        });
        meesho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.meesho.com");
            }
        });
        zomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.zomato.com/");
            }
        });
        swiggy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.swiggy.com/");
            }
        });
        vishal_mart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.vishalmegamart.com/");
            }
        });
        bookmyshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.bookmyshow.com/");
            }
        });
        tata1mg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.1mg.com/");
            }
        });
        entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //something
            }
            });
        kuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://kukufm.com/");
            }
        });
        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //something
            }
        });
        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //something
            }
        });
        full_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //something
            }
        });
        kyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //something
            }
        });
        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //something
            }
        });
        computer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.skillindiadigital.gov.in/courses");
            }
        });
        pro_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //something
            }
        });
        marquee_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "6395427453";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + "+91"+number));
                startActivity(intent);
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "6395427453";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + "+91"+number));
                startActivity(intent);
            }
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
        atm_locator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.npci.org.in/atm/locator");
            }
        });
        bike_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.bajajfinserv.in/insurance/bajaj-allianz-two-wheeler-insurance");
            }
        });
        car_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://www.bajajallianz.com/motor-insurance/car-insurance-online.html");
            }
        });
        family_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebActivity("https://licindia.in/protect-my-family-term-plan");
            }
        });
        prepaid_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), prepaid_mobile.class);
                startActivity(intent);
            }
        });
        postpaid_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        dth_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), recharge_plans.class);
                startActivity(intent);
            }
        });
        fastag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        electricity_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        gas_cylinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        water_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        cable_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        money_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        credit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        loan_repayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        tax_calculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coming_soon();
            }
        });
        return  view;
    }
    private void openWebActivity(String url) {
        Intent intent = new Intent(getActivity(), web_activity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
    private void coming_soon(){
        Intent intent = new Intent(getActivity(), coming_soon.class);
        startActivity(intent);
    }
    @SuppressLint("SetTextI18n")
    private void updateUI(){
        if (isAdded() && getActivity() != null && sharedPreferences!=null) {
            String flexi = sharedPreferences.getString("flexi_wallet","0.0");
            String commission = sharedPreferences.getString("commission_wallet","0.0");
            String bonus = sharedPreferences.getString("signup_bonus","0.0");
            String today = sharedPreferences.getString("today_income","0.0");
            String total = sharedPreferences.getString("total_income","0.0");
            bonusamount.setText(bonus);
            fundwalletamount.setText("₹ "+flexi);
            income_wallet.setText("₹ "+commission);
            today_income.setText("₹ "+today);
            total_income.setText("₹ "+total);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedPreferences != null && preferenceChangeListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }

}