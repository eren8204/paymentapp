package com.example.paymentapp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("auth/userAddFundRequest/")
    @Multipart
    Call<ResponseBody> sendAddFundRequest(
            @Part("transaction_id") RequestBody transactionId,
            @Part("utr_number") RequestBody utrNumber,
            @Part("member_id") RequestBody memberId,
            @Part("to_upi_id") RequestBody toUpiId,
            @Part("amount") RequestBody amount,
            @Part MultipartBody.Part screenshot
    );
}
