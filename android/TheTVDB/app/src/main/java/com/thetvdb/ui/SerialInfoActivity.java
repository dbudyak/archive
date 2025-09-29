package com.thetvdb.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.thetvdb.R;
import com.thetvdb.model.Image;
import com.thetvdb.model.ImagesData;
import com.thetvdb.ui.adapter.SerialInfoPagerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dbudyak on 28.06.16.
 */
public class SerialInfoActivity extends BaseActivity implements Callback<ImagesData> {

    @BindView(R.id.materialViewPager) MaterialViewPager pager;

    public final static String KEY_NAME = "name";
    public final static String KEY_BANNER = "banner";
    public final static String KEY_FIRST_AIRED = "fAired";
    public final static String KEY_ID = "id";
    public final static String KEY_NETWORK = "network";
    public final static String KEY_OVERVIEW = "overview";
    public final static String KEY_STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_info);
        ButterKnife.bind(this);
        injector.inject(this);

        Bundle extras = getIntent().getExtras();
        String serName = extras.getString(KEY_NAME);
        setTitle(serName);

        String serId = extras.getString(KEY_ID);
        tvDbRestApi.serialImages(serId, "fanart", "1280x720", "subKey=graphical").enqueue(this);
        setPager();
    }


    private void setPager() {
        Toolbar toolbar = pager.getToolbar();

        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
        pager.getViewPager().setAdapter(new SerialInfoPagerAdapter(this, getSupportFragmentManager(), getIntent().getExtras()));
        pager.getPagerTitleStrip().setViewPager(pager.getViewPager());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResponse(Call<ImagesData> call, Response<ImagesData> response) {
        if (!isDestroyed())
            if (response.isSuccessful() && response.body() != null) {
                List<Image> images = response.body().getData();
                if (images != null && !images.isEmpty()) {
                    int imgCount = images.size();
                    final String URL = "http://thetvdb.com/banners/";
                    final String[] urlImgArr = new String[3];
                    switch (imgCount) {
                        case 1:
                            Image i1 = images.get(0);
                            urlImgArr[0] = URL + i1.getFileName();
                            urlImgArr[1] = URL + i1.getFileName();
                            urlImgArr[2] = URL + i1.getFileName();
                            break;
                        case 2:
                            i1 = images.get(0);
                            Image i2 = images.get(1);
                            urlImgArr[0] = URL + i1.getFileName();
                            urlImgArr[1] = URL + i2.getFileName();
                            urlImgArr[2] = URL + i1.getFileName();
                            break;
                        case 3:
                            i1 = images.get(0);
                            i2 = images.get(1);
                            Image i3 = images.get(2);
                            urlImgArr[0] = URL + i1.getFileName();
                            urlImgArr[1] = URL + i2.getFileName();
                            urlImgArr[2] = URL + i3.getFileName();
                            break;
                        default:
                            Random rnd = new Random();
                            i1 = images.get(rnd.nextInt(imgCount - 1));
                            i2 = images.get(rnd.nextInt(imgCount - 1));
                            i3 = images.get(rnd.nextInt(imgCount - 1));
                            urlImgArr[0] = URL + i1.getFileName();
                            urlImgArr[1] = URL + i2.getFileName();
                            urlImgArr[2] = URL + i3.getFileName();
                            break;
                    }
                    setMaterialViewPagerHeaderDesign(urlImgArr);
                } else {
                    Log.e("DEBUG", "images error");
                }
            } else {
                Log.e("DEBUG", "no response");
            }
    }

    private void setMaterialViewPagerHeaderDesign(final String[] urlImgArr) {
        Log.d("DEBUG", "Loaded header images: " + Arrays.toString(urlImgArr));
        pager.setMaterialViewPagerListener(page -> {
            switch (page) {
                case 0:
                    return HeaderDesign.fromColorResAndUrl(
                            R.color.primary,
                            urlImgArr[page]);
                case 1:
                    return HeaderDesign.fromColorResAndUrl(
                            R.color.primary,
                            urlImgArr[page]);
                case 2:
                    return HeaderDesign.fromColorResAndUrl(
                            R.color.primary,
                            urlImgArr[page]);
            }
            return null;
        });
        pager.getViewPager().setCurrentItem(1, true);
    }

    @Override
    public void onFailure(Call<ImagesData> call, Throwable t) {
        Log.e("DEBUG", t.getLocalizedMessage());
    }
}
