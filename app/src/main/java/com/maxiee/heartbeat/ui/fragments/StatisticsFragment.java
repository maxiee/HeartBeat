package com.maxiee.heartbeat.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.database.api.GetCountByNameApi;
import com.maxiee.heartbeat.database.api.GetCountSpecDayApi;

import java.util.ArrayList;


/**
 * Created by maxiee on 15-7-15.
 */
public class StatisticsFragment extends Fragment{
    private final static int WEEK_COUNT = 7;

    private CombinedChart mChart;
    private PieChart mPieChart;
    private TextView mTvEventCount;
    private TextView mTvThoughtCount;
    private int mAccentColor;
    private int mPrimaryColor;
    private int mPrimaryColorDark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);

        mTvEventCount = (TextView) v.findViewById(R.id.tv_event_count);
        mTvThoughtCount = (TextView) v.findViewById(R.id.tv_thought_count);
        mChart = (CombinedChart) v.findViewById(R.id.chart);
        mPieChart = (PieChart) v.findViewById(R.id.pie_chart);

        TypedValue accentValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, accentValue, true);
        mAccentColor = accentValue.data;
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, accentValue, true);
        mPrimaryColor = accentValue.data;
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimaryDark, accentValue, true);
        mPrimaryColorDark = accentValue.data;

        getCount();
        initChart();

        return v;
    }

    private void getCount() {
        mTvEventCount.setText(
                String.valueOf(new GetCountByNameApi(getActivity())
                        .exec(GetCountByNameApi.EVENT))
        );
        mTvThoughtCount.setText(
                String.valueOf(new GetCountByNameApi(getActivity())
                        .exec(GetCountByNameApi.THOUGHT))
        );
    }

    private void initChart() {
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDescription("");
        mChart.setBackgroundColor(getResources().getColor(R.color.window_background));
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawOrder(
                new CombinedChart.DrawOrder[]{
                        CombinedChart.DrawOrder.BAR,
                        CombinedChart.DrawOrder.LINE
                });
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new IntValueFormatter());
        leftAxis.setDrawGridLines(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        CombinedData data = new CombinedData(TimeUtils.getWeekDateString());
        data.setData(getWeekEventData());
        data.setData(getWeekThoughtData());
        mChart.setData(data);
        mChart.invalidate();
    }

    private void initPieChart() {

    }

    private BarData getWeekEventData() {
        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i=0; i<WEEK_COUNT; i++) {
            entries.add(
                    new BarEntry(
                            new GetCountSpecDayApi(getActivity()).exec(
                                    TimeUtils.calendarDaysBefore(WEEK_COUNT-i),
                                    GetCountSpecDayApi.EVENT
                            ),i
                    )
            );
        }

        BarDataSet set = new BarDataSet(entries, getString(R.string.event_count_text));
        set.setColor(mPrimaryColorDark);
        set.setValueFormatter(new IntValueFormatter());
        d.addDataSet(set);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return d;
    }

    private LineData getWeekThoughtData() {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();

        for (int i=0; i<WEEK_COUNT; i++) {
            entries.add(
                    new BarEntry(
                            new GetCountSpecDayApi(getActivity()).exec(
                                    TimeUtils.calendarDaysBefore(WEEK_COUNT-i),
                                    GetCountSpecDayApi.THOUGHT
                            ),i
                    )
            );
        }

        LineDataSet set = new LineDataSet(entries, getString(R.string.thought_count_text));
        set.setColor(mAccentColor);
        set.setLineWidth(2.5f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setCircleColor(mPrimaryColorDark);
        set.setCircleSize(5f);
        set.setFillColor(mPrimaryColor);
        set.setDrawValues(true);
        set.setValueFormatter(new IntValueFormatter());
        d.addDataSet(set);
        return d;
    }

    private class IntValueFormatter implements ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }
}
