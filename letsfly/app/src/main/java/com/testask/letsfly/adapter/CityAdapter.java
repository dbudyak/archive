package com.testask.letsfly.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.testask.letsfly.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbudyak on 24.03.17.
 */
public class CityAdapter extends ArrayAdapter<City> {

    private List<City> cities;
    private int layoutResId;

    public CityAdapter(@NonNull Context context, @LayoutRes int resource, List<City> cities) {
        super(context, resource);
        this.cities = cities;
        this.layoutResId = resource;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public City getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cities.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(layoutResId, parent, false);
        }
        ((TextView) convertView).setText(getItem(position).getCity());
        return convertView;

    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((City) (resultValue)).getCity();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = cities;
                    filterResults.count = cities.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<City> filteredList = (ArrayList<City>) results.values;
                if (results.count > 0) {
                    clear();
                    for (City c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            }
        };
    }
}
