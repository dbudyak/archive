import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.testask.letsfly.model.City;
import com.testask.letsfly.model.Location;
import com.testask.letsfly.ui.FlightActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

/**
 * Created by dbudyak on 22.03.17.
 */
@RunWith(AndroidJUnit4.class)
public class TestOnMap {

    @Rule
    public ActivityTestRule<FlightActivity> activityRule = new ActivityTestRule<>(
            FlightActivity.class, true, false);

    @Test
    public void showShortFlight() throws InterruptedException {
        Intent intent = buildIntent(40.75603, -73.986956, 42.35843, -71.05977, "NYC", "BOS");
        waitUntilDestroy(activityRule.launchActivity(intent));
    }

    @Test
    public void showFlight1() throws InterruptedException {
        Intent intent = buildIntent(6.6986, -1.6243, -8.833333, 13.233333, "KMS", "LAD");
        waitUntilDestroy(activityRule.launchActivity(intent));
    }

    @Test
    public void showFlight2() throws InterruptedException {
        Intent intent = buildIntent(55.752041, 37.617508, 40.416876, -3.704255, "MOW", "MAD");
        waitUntilDestroy(activityRule.launchActivity(intent));
    }

    @Test
    public void showLongestFlight() throws InterruptedException {
        Intent intent = buildIntent(40.416876, -3.704255, -41.288518, 174.777287, "MAD", "WLG");
        waitUntilDestroy(activityRule.launchActivity(intent));
    }

    @Test
    public void showLongestFlight2() throws InterruptedException {
        Intent intent = buildIntent(68.963225, 33.077903, -33.92584, 18.42322, "MMK", "CPT");
        waitUntilDestroy(activityRule.launchActivity(intent));
    }

    @NonNull
    private Intent buildIntent(double x1, double y1, double x2, double y2, String iata1, String iata2) {
        Intent flyIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(FlightActivity.CITY1_KEY,
                new City(new Location(x1, y1), Collections.singletonList(iata1)));
        bundle.putParcelable(FlightActivity.CITY2_KEY,
                new City(new Location(x2, y2), Collections.singletonList(iata2)));
        flyIntent.putExtras(bundle);
        return flyIntent;
    }

    private void waitUntilDestroy(FlightActivity flightActivity) throws InterruptedException {
        final CountDownLatch lock = new CountDownLatch(1);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (flightActivity.isDestroyed()) {
                lock.countDown();
            }
        }, 2, 5, TimeUnit.SECONDS);
        lock.await();
    }
}
