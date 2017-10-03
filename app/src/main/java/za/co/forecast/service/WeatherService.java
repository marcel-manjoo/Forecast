package za.co.forecast.service;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import za.co.forecast.model.WeatherData;

public interface WeatherService {

    @GET("weather")
    Observable<WeatherData> getWeatherData(@Query("lat") double lat,
                                           @Query("lon") double lon,
                                           @Query("units") String metric,
                                           @Query("appid") String key);
}
