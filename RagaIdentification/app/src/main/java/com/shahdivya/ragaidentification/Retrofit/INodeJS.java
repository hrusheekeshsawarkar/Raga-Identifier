package com.shahdivya.ragaidentification.Retrofit;

import com.shahdivya.ragaidentification.models.RagaPredicted;
import com.shahdivya.ragaidentification.models.Task;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface INodeJS
{
    @GET("task")
    Call<List<Task>> getTasks();

    @Headers ("Accept: application/json")
    @Multipart
    @POST("task/")
    Call<Task> uploadAudio(@Part("audio\"; filename=\"audio.wav\" ") RequestBody audio);

    @Headers ("Accept: application/json")
    @Multipart
    @POST("predict")
    Call<RagaPredicted> uploaded(@Part MultipartBody.Part audio);
}
