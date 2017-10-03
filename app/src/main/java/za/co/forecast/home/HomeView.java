package za.co.forecast.home;

public interface HomeView {

    void displayProgress();

    void hideProgress();

    void setDateHeader(String description);

    void onError(String errorMessage);

    void getWeather();

    void displayWeather(String desc, String tempMax, String tempMin, String locationDescription);
}
