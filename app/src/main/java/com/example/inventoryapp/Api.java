package com.example.inventoryapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    // User Operations
    @FormUrlEncoded
    @POST("createuser")
    Call<DefaultResponse> createUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("name") String name
    );

    @FormUrlEncoded
    @POST("userlogin")
    Call<UserResponse> userLogin(
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("users/{username}")
    Call<UserResponse> getUserByUsername(@Path("username") String username);

    // Product Operations
    @FormUrlEncoded
    @POST("createproduct")
    Call<DefaultResponse> createProduct(
            @Field("product_name") String product_name,
            @Field("price") double price,
            @Field("model_number") String model_number,
            @Field("brand") String brand,
            @Field("quantity") int quantity,
            @Field("userid") int userid
    );

    // Update product (using qr_id in the body as the identifier)
    @FormUrlEncoded
    @POST("updateproduct")
    Call<DefaultResponse> updateProduct(
            @Field("qr_id") int qr_id,  // qr_id used as part of the body now
            @Field("product_name") String product_name,
            @Field("price") double price,
            @Field("model_number") String model_number
    );

    // Delete product by model number
    @DELETE("deleteproduct/{model_number}/{user_id}")
    Call<DefaultResponse> deleteProduct(@Path("model_number") String model_number, @Path("user_id") int user_id);

    // Get products by user ID
    @GET("productsbyuser/{user_id}")
    Call<ProductResponse> getUserProducts(@Path("user_id") int user_id);

    // Fetch product price (and other details) by model number
    @GET("productprice/{model_number}")
    Call<ProductResponse> getPriceByModelNumber(@Path("model_number") String modelNumber);

    // Get products by user ID with search term filter
    @GET("products/byuser/{user_id}/{search_term}")
    Call<ProductResponse> findUserProducts(@Path("user_id") int user_id, @Path("search_term") String search_term);

    // Get a single product by QR ID
    @GET("productbyqr/{qr_ID}")
    Call<ProductResponse> getProductByQR(@Path("qr_ID") String qr_ID);

    // Invoice Operations
    @FormUrlEncoded
    @POST("createinvoice")
    Call<DefaultResponse> createInvoice(
            @Field("si") int si,
            @Field("model_number") String model_number,
            @Field("user_id") int user_id,
            @Field("quantity") int quantity,
            @Field("activity_type") String activity_type
    );

    // Get invoices by user ID
    @GET("invoices/{user_id}")
    Call<InvoiceResponse> getAllInvoicesByUser(@Path("user_id") int user_id);
}
