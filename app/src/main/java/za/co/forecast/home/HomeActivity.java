package za.co.forecast.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.forecast.R;
import za.co.forecast.data.WeatherRepo;

public class HomeActivity extends AppCompatActivity implements HomeView{

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 112;
    private static String TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.loading_progress)
    ProgressBar progressBar;
    @BindView(R.id.date_header)
    TextView dateHeaderTV;
    @BindView(R.id.weather_description)
    TextView weatherDescriptionTV;
    @BindView(R.id.imageView)
    ImageView weatherImage;
    @BindView(R.id.max_temp)
    TextView maxTempTV;
    @BindView(R.id.min_temp)
    TextView minTempTV;
    @BindView(R.id.city)
    TextView cityTV;

    private HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        initHomePresenter();
    }

    private void initHomePresenter() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        homePresenter = new HomePresenter(this,
                locationManager,
                getString(R.string.today),
                getString(R.string.errorMsg),
                getString(R.string.celsius),
                getString(R.string.max),
                getString(R.string.min),
                getString(R.string.acquiringLoc));
        if(homePresenter.isLocationEnabled()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            } else {
                homePresenter.getCurrentLocation();
            }

        } else {
            enableLocation();
        }

    }

    private void enableLocation() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.enable_location))
                .setMessage(R.string.enable_location_description)
                .setPositiveButton(getString(R.string.location_settings), (paramDialogInterface, paramInt) -> {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                })
                .setNegativeButton(getString(R.string.cancel), (paramDialogInterface, paramInt) -> {
                });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WeatherRepo.destroyInstance();
    }

    @Override
    public void displayProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setDateHeader(String description) {
        dateHeaderTV.setText(description);
    }

    @Override
    public void onError(String errorMessage) {
        hideProgress();
        dateHeaderTV.setText(errorMessage);
        clearText(weatherDescriptionTV);
        clearText(maxTempTV);
        clearText(minTempTV);
        clearText(cityTV);
    }

    @Override
    public void getWeather() {

    }

    @Override
    public void displayWeather(String desc, String tempMax, String tempMin, String locationDescription) {
        weatherDescriptionTV.setText(desc);
        maxTempTV.setText(tempMax);
        minTempTV.setText(tempMin);
        cityTV.setText(locationDescription);
    }

    private void clearText(TextView textView){
        textView.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    homePresenter.getCurrentLocation();
                } else {
                    Log.d(TAG, "not enabled");
                }
            }
        }
    }


}
