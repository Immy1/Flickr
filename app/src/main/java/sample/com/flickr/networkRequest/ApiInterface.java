package sample.com.flickr.networkRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sample.com.flickr.responce.PublicPhotosResponce;

public interface ApiInterface {
    @GET("services/feeds/photos_public.gne?format=json")
    Call<PublicPhotosResponce> getPublicPhotos(@Query("id")String id,@Query("lang")String lang,@Query("format")String format,
                                               @Query("nojsoncallback")String jsonCallBack);

}



