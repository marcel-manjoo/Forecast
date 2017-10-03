package za.co.forecast.home;

import android.content.Context;
import android.location.LocationManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import za.co.forecast.R;
import za.co.forecast.data.WeatherRepo;
import za.co.forecast.model.Weather;
import za.co.forecast.model.WeatherData;

import static org.mockito.Mockito.verify;


public class HomePresenterTest {

    @Mock
    WeatherRepo weatherRepo;

    @Mock
    HomeView homeView;

    @Mock
    WeatherData weatherData;

    @InjectMocks
    Weather weather;

    @Mock
    LocationManager locationManager;

    @Mock
    Context context;
    private HomePresenter homePresenter;

    @Before
    public void setupMocksAndView() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Scheduler.Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);

        MockitoAnnotations.initMocks(this);

        homePresenter = new HomePresenter(homeView,
                locationManager,
                context.getString(R.string.today),
                context.getString(R.string.errorMsg),
                context.getString(R.string.celsius),
                context.getString(R.string.max),
                context.getString(R.string.min),
                context.getString(R.string.acquiringLoc));
    }


    @Test
    public void testThatSetDateHeaderCalled() {
        homePresenter.getWeather(21.00, 4200);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Date now = new Date();
        String dateFormatted = sdf.format(now);
        verify(homeView, Mockito.times(1)).setDateHeader(homePresenter.getToday() + " " + dateFormatted);
    }

    @Test
    public void testThatOnErrorIsNotCalled() {
        homePresenter.getWeather(21.00, 4200);
        verify(homeView, Mockito.times(0)).onError("");
    }
}