package com.figitaki.weatherdojo;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class ForecastFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] values = {"a", "b", "c"};
        int[] views = {android.R.id.text1};
        ListAdapter mAdapter = new ArrayAdapter<String>(inflater.getContext(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(mAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
