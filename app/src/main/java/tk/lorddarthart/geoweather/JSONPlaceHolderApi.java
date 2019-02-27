package tk.lorddarthart.geoweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {
    @Headers({
            "connection: keep-alive",
            "content-type: application/octet-stream",
            "X-Yandex-API-Key: 06a362a9-ef2d-4c96-9667-1fa0c0e1625f"
    })
    @GET("/v1/forecast")
    Call<WeatherSample> getCurrentForecastForMyPlace(@Query(value = "lat", encoded = true) String lat, @Query(value = "lon", encoded = true) String lon, @Query(value = "lang", encoded = true) String lang);
}
