package za.co.forecast.home;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import za.co.forecast.data.WeatherRepo;
import za.co.forecast.model.WeatherData;

public class HomePresenter implements LocationListener {

    private static String TAG = HomePresenter.class.getSimpleName();

    private HomeView homeView;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
    private LocationManager locationManager;
    private String today, errorMsg, celsius, maxDesc, minDesc, acquiringLoc;


    public HomePresenter(HomeView homeView,
                         LocationManager locationManager,
                         String today, String errorMsg, String celsius,
                         String maxDesc, String minDesc, String acquiringLoc) {
        this.homeView = homeView;
        this.locationManager = locationManager;
        this.today = today;
        this.errorMsg = errorMsg;
        this.celsius = celsius;
        this.maxDesc = maxDesc;
        this.minDesc = minDesc;
        this.acquiringLoc = acquiringLoc;
    }

    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void getWeather(double latitude, double longitude) {
        Observable<WeatherData> data = WeatherRepo.getInstance().getWeatherData(latitude,
                longitude);

        data.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn((Throwable ex) -> {
                    WeatherData weatherData = new WeatherData();
                    weatherData.setErrorMsg(ex.getLocalizedMessage());
                    return new WeatherData();
                })
                .subscribe(weatherData -> {
                    setDateHeader();
                    if (weatherData.getWeather() != null) {
                        Log.e(TAG, weatherData.getWeather()
                                .get(0)
                                .getDescription());
                        setValues(weatherData);
                    } else {
                        homeView.onError(errorMsg);
                    }
                });

    }

    void setDateHeader() {
        Date now = new Date();
        String dateFormatted = sdf.format(now);
        homeView.setDateHeader(today + " " + dateFormatted);
        homeView.hideProgress();
    }

    void setValues(WeatherData weatherData) {
        weatherData.getWeather().get(0).getIcon();
        weatherData.getWeather().get(0).getDescription();
        double tempMax = weatherData.getMain().getTempMax();
        double tempMin = weatherData.getMain().getTempMin();
        String name = weatherData.getName();
        String country = weatherData.getSys().getCountry();
        String locationDescription = createLocationDescription(name, country);
        homeView.displayWeather(weatherData.getWeather().get(0).getDescription(),
                maxDesc + " " +convertToCelsius(tempMax),
                minDesc + " " +convertToCelsius(tempMin),
                locationDescription);
    }

    String createLocationDescription(String name, String country) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(", ");
        sb.append(country);
        return sb.toString();
    }

    String convertToCelsius(double value) {
        return value + celsius;
    }

    public void getCurrentLocation() {
        homeView.displayProgress();
        homeView.setDateHeader(acquiringLoc);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        try {
            locationManager.requestLocationUpdates(provider, 2 * 60 * 1000, 10, this);
        } catch (SecurityException se) {
            homeView.onError(se.getMessage());
        }

    }

    public String getToday() {
        return today;
    }

    @Override
    public void onLocationChanged(Location location) {
        getWeather(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
