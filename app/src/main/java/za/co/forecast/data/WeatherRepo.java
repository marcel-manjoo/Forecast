package za.co.forecast.data;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.forecast.model.WeatherData;
import za.co.forecast.service.WeatherService;

public class WeatherRepo {

    private static String TAG = WeatherRepo.class.getSimpleName();

    private final String WEATHER_API_KEY = "OPEN_WEATHER_KEY";
    private final String METRIC = "metric";

    private static WeatherRepo INSTANCE = null;

    public static WeatherRepo getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new WeatherRepo();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public Observable<WeatherData> getWeatherData(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);
        Observable<WeatherData> weatherData = weatherService.getWeatherData(latitude,
                longitude, METRIC, WEATHER_API_KEY);

        return weatherData;
    }
}
