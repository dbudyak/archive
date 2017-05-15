package com.testask.letsfly.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.testask.letsfly.R;
import com.testask.letsfly.adapter.CityAdapter;
import com.testask.letsfly.api.Api;
import com.testask.letsfly.model.City;
import com.testask.letsfly.model.ResponseData;
import com.testask.letsfly.util.TextInputListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.testask.letsfly.util.Utils.isValidInput;

public class StartActivity extends BaseActivity implements TextInputListener.OnTextListener {

    @Inject
    Api api;

    @BindView(R.id.search_dest)
    AutoCompleteTextView searchDest;
    @BindView(R.id.search_source)
    AutoCompleteTextView searchSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        component().inject(this);

        TextInputListener textInputListener = new TextInputListener(this);
        AdapterView.OnItemClickListener selectCityListener = (parent, view, position, id) -> {
            City item = (City) parent.getAdapter().getItem(position);
            if (searchSource.isFocused()) {
                searchSource.setTag(item);
            } else if (searchDest.isFocused()) {
                searchDest.setTag(item);
            }
        };

        searchSource.addTextChangedListener(textInputListener);
        searchDest.addTextChangedListener(textInputListener);

        searchSource.setOnItemClickListener(selectCityListener);
        searchDest.setOnItemClickListener(selectCityListener);
    }

    @OnClick(R.id.find_button)
    public void onFindTrip() {
        if (isValidInput(this, searchSource) && isValidInput(this, searchDest)) {
            Intent intent = new Intent(StartActivity.this, FlightActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(FlightActivity.CITY1_KEY, (City) searchSource.getTag());
            bundle.putParcelable(FlightActivity.CITY2_KEY, (City) searchDest.getTag());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onText(String input) {
        api.getData(input).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    List<City> cities = response.body().getCities();
                    if (!cities.isEmpty()) {
                        setSuggestedCities(cities);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                log(t);
            }
        });
    }

    private void setSuggestedCities(List<City> cities) {
        CityAdapter adapter = new CityAdapter(StartActivity.this, android.R.layout.simple_dropdown_item_1line, cities);
        if (searchDest.isFocused()) {
            searchDest.setAdapter(adapter);
        } else if (searchSource.isFocused()) {
            searchSource.setAdapter(adapter);
        }
    }

}
