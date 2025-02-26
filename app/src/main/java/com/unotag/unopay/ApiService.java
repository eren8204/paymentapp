package com.unotag.unopay;
import com.google.gson.annotations.SerializedName;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("userAddFundRequest/")
    @Multipart
    Call<ResponseBody> sendAddFundRequest(
            @Header("Authorization") String token,
            @Part("utr_number") RequestBody utrNumber,
            @Part("member_id") RequestBody memberId,
            @Part("to_upi_id") RequestBody toUpiId,
            @Part("amount") RequestBody amount,
            @Part MultipartBody.Part screenshot
    );

    @POST("getTeamList")
    Call<TeamResponse> fetchTeamData(
            @Header("Authorization") String token
            ,@Body MemberRequest memberRequest);

    @POST("submitUserBankKycDetails/")
    @Multipart
    Call<ResponseBody> submitUserBankKycDetails(
            @Header("Authorization") String token,
            @Part("member_id") RequestBody memberId,
            @Part("FullName") RequestBody fullName,
            @Part("PanCard_Number") RequestBody panNumber,
            @Part("IFSC_Code") RequestBody ifscCode,
            @Part("Bank_Name") RequestBody bankName,
            @Part("Account_number") RequestBody accountNumber,
            @Part("Aadhar_Number") RequestBody aadharNumber,
            @Part("Nominee_name") RequestBody nomineeName,
            @Part("Nominee_relation") RequestBody nomineeRelation,
            @Part("kycOtp") RequestBody otp,
            @Part MultipartBody.Part pancard,
            @Part MultipartBody.Part aadharcard,
            @Part MultipartBody.Part aadharcardback,
            @Part MultipartBody.Part userImage,
            @Part MultipartBody.Part passbook

    );



}
