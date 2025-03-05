package com.unotag.unopay;
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

    @POST("submit-user-details")
    @Multipart
    Call<ResponseBody> submitUserDetails(
            @Header("Authorization") String token,
            @Part("member_id") RequestBody memberId,
            @Part("FullName") RequestBody fullName,
            @Part("Nominee_name") RequestBody nomineeName,
            @Part("Nominee_relation") RequestBody nomineeRelation,
            @Part MultipartBody.Part kycUserImage

    );

    @POST("submit-pancard")
    @Multipart
    Call<ResponseBody> submitPanDetails(
            @Header("Authorization") String token,
            @Part("member_id") RequestBody memberId,
            @Part("PanCard_Number") RequestBody pancard_no,
            @Part MultipartBody.Part kycPancard
    );

    @POST("submit-aadhar-front")
    @Multipart
    Call<ResponseBody> submitAadharDetails(
            @Header("Authorization") String token,
            @Part("member_id") RequestBody memberId,
            @Part("Aadhar_Number") RequestBody aadhar_no,
            @Part MultipartBody.Part kycAadharFront
    );

    @POST("submit-aadhar-back")
    @Multipart
    Call<ResponseBody> submitAadharBackDetails(
            @Header("Authorization") String token,
            @Part("member_id") RequestBody memberId,
            @Part MultipartBody.Part kycAadharBack
    );

    @POST("submit-bank-details")
    @Multipart
    Call<ResponseBody> submitBankDetails(
            @Header("Authorization") String token,
            @Part("member_id") RequestBody memberId,
            @Part("Bank_Name") RequestBody fullName,
            @Part("IFSC_Code") RequestBody nomineeName,
            @Part("Account_number") RequestBody nomineeRelation,
            @Part MultipartBody.Part kycPassbook

    );

    @POST("upload-profile-image")
    @Multipart
    Call<ResponseBody> submitProfilePhoto(
            @Header("Authorization") String token,
            @Part("member_id") RequestBody memberId,
            @Part MultipartBody.Part kycPancard
    );

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
            @Part MultipartBody.Part pancard,
            @Part MultipartBody.Part aadharcard,
            @Part MultipartBody.Part aadharcardback,
            @Part MultipartBody.Part userImage,
            @Part MultipartBody.Part passbook

    );



}
