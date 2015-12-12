package com.maxiee.heartbeat.ui.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.database.api.GetCountByNameApi;
import com.maxiee.heartbeat.database.api.GetCountSpecDayApi;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.support.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by maxiee on 15-7-15.
 */
public class StatisticsFragment extends Fragment{
    private static final String TAG = StatisticsFragment.class.getSimpleName();
    private final static int WEEK_COUNT = 7;
    private final static int DISTRIBUTION_COUNT = 10;

    @Bind(R.id.chart) CombinedChart mChart;
    @Bind(R.id.pie_chart) PieChart mPieChart;
    @Bind(R.id.tv_event_count) TextView mTvEventCount;
    @Bind(R.id.tv_thought_count) TextView mTvThoughtCount;
    @Bind(R.id.distribution_hint) TextView mTvDistriHint;
    private int mAccentColor;
    private int mPrimaryColor;
    private int mPrimaryColorDark;
    private long mThoughtCount;
    private long mEventCount;
    private ArrayList<Map.Entry<Event, Integer>> mEvents;
    private int mWasteBookCount; // 流水帐数目
    private CombinedData mCombinedData;
    private PieData mPieData;
    private DataManager mDataManager;

    private UpdateCountTask mUpdateCountTask;
    private UpdateChartTask mUpdateChartTask;
    private UpdatePieChartTask mUpdatePieChartTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, v);

        TypedValue accentValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, accentValue, true);
        mAccentColor = accentValue.data;
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, accentValue, true);
        mPrimaryColor = accentValue.data;
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimaryDark, accentValue, true);
        mPrimaryColorDark = accentValue.data;

        mDataManager = DataManager.getInstance(getContext());

        mUpdateCountTask = new UpdateCountTask();
        mUpdateChartTask = new UpdateChartTask();
        mUpdatePieChartTask = new UpdatePieChartTask();

        mUpdateChartTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mUpdateCountTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mUpdatePieChartTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mUpdateCountTask != null && mUpdateCountTask.getStatus() == AsyncTask.Status.RUNNING)
            mUpdateCountTask.cancel(true);
        if (mUpdateChartTask != null && mUpdateChartTask.getStatus() == AsyncTask.Status.RUNNING)
            mUpdateChartTask.cancel(true);
        if (mUpdatePieChartTask != null && mUpdatePieChartTask.getStatus() == AsyncTask.Status.RUNNING)
            mUpdatePieChartTask.cancel(true);
    }

    private class IntValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }

    private class UpdateCountTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            StopWatch watch = new StopWatch(TAG, UpdateCountTask.class.getSimpleName());
            mEventCount = new GetCountByNameApi(getActivity())
                    .exec(GetCountByNameApi.EVENT);
            mThoughtCount = new GetCountByNameApi(getActivity())
                    .exec(GetCountByNameApi.THOUGHT);
            watch.stop();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mTvEventCount.setText(
                    String.valueOf(mEventCount)
            );

            mTvThoughtCount.setText(
                    String.valueOf(mThoughtCount)
            );
        }
    }

    private class UpdateChartTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mChart.setDoubleTapToZoomEnabled(false);
            mChart.setTouchEnabled(false);
            mChart.setDragEnabled(false);
            mChart.setScaleEnabled(false);
            mChart.setDescription("");
            mChart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.window_background));
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
        }

        @Override
        protected Void doInBackground(Void... params) {
            StopWatch watch = new StopWatch(TAG, UpdateChartTask.class.getSimpleName());
            mCombinedData = new CombinedData(TimeUtils.getWeekDateString());
            mCombinedData.setData(getWeekEventData());
            mCombinedData.setData(getWeekThoughtData());
            watch.stop();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mChart.setData(mCombinedData);
            mChart.invalidate();
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
    }

    private class UpdatePieChartTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mPieChart.setTouchEnabled(false);
            mPieChart.setUsePercentValues(true);
            mPieChart.setDescription("");
            mPieChart.setDrawHoleEnabled(true);
            mPieChart.setHoleColorTransparent(true);
            mPieChart.setTransparentCircleColor(Color.WHITE);
            mPieChart.setHoleRadius(40f);
            mPieChart.setTransparentCircleRadius(45f);
            mPieChart.setDrawCenterText(false);
            Legend legend = mPieChart.getLegend();
            legend.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            StopWatch watch = new StopWatch(TAG, UpdatePieChartTask.class.getSimpleName());
            getDistributionData();
            watch.stop();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mPieChart.setData(mPieData);
            mPieChart.invalidate();
            String averageHead = getString(R.string.distri_hint_average_head);
            float averageCountV = 1f * mThoughtCount / mEventCount;
            if (mThoughtCount == 0) averageCountV = 0;
            String averageCount = String.valueOf(averageCountV);
            String averageTail = getString(R.string.distri_hint_average_tail);
            String wasteBook = getString(R.string.disti_hint_waste_book);
            String wasteCount = String.valueOf(mWasteBookCount);
            if (averageCount.length()>3) {
                averageCount = averageCount.substring(0, 3);
            }
            mTvDistriHint.setText(averageHead + averageCount + averageTail + wasteBook +wasteCount);
        }

        private void getDistributionData() {
            Map<Integer, Integer> freqCount = new TreeMap<>();
            Map<Event, Integer> eventsMap = new HashMap<>();

            ArrayList<Event> events = mDataManager.getEventManager().getEvents();
            for (Event event: events) {
                int count = ThoughtUtils.getEventCount(getActivity(), event.getId());
                eventsMap.put(event, count);
                if (!freqCount.containsKey(count)) {
                    freqCount.put(count, 1);
                } else {
                    int freq = freqCount.get(count) + 1;
                    freqCount.put(count, freq);
                }
            }

            ArrayList<Map.Entry<Integer, Integer>> freqCountSort = new ArrayList<>(freqCount.entrySet());
            Collections.sort(freqCountSort, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> lhs, Map.Entry<Integer, Integer> rhs) {
                    return rhs.getValue() - lhs.getValue();
                }
            });
            mEvents = new ArrayList<>(eventsMap.entrySet());
            Collections.sort(mEvents, new Comparator<Map.Entry<Event, Integer>>() {
                @Override
                public int compare(Map.Entry<Event, Integer> lhs, Map.Entry<Event, Integer> rhs) {
                    return rhs.getValue() - lhs.getValue();
                }
            });

            // 获取流水帐数目 fix bug4.
            if (!freqCount.isEmpty() && freqCount.containsKey(1)) {
                mWasteBookCount = freqCount.get(1);
            } else {
                mWasteBookCount = 0;
            }
            ArrayList<String> xVals = new ArrayList<>();
            ArrayList<Entry> yVals = new ArrayList<>();
            int distributionCount =
                    freqCountSort.size() < DISTRIBUTION_COUNT ? freqCountSort.size() : DISTRIBUTION_COUNT;
            for (int i=0; i<distributionCount; i++) {
                if (1.0f * freqCountSort.get(i).getValue() / mEventCount < 0.1) {
                    xVals.add(String.valueOf(freqCountSort.get(i).getKey()));
                } else {
                    xVals.add(String.valueOf(freqCountSort.get(i).getKey()) + getString(R.string.thought_count_pie));
                }
                yVals.add(new Entry(freqCountSort.get(i).getValue(), i));
            }
            PieDataSet set = new PieDataSet(yVals, "事件数目");
            set.setSliceSpace(3f);

            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)    colors.add(c);
            for (int c : ColorTemplate.JOYFUL_COLORS)       colors.add(c);
            for (int c : ColorTemplate.COLORFUL_COLORS)     colors.add(c);
            for (int c : ColorTemplate.LIBERTY_COLORS)      colors.add(c);
            for (int c : ColorTemplate.PASTEL_COLORS)       colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            set.setColors(colors);
            PieData d =  new PieData(xVals, set);
            d.setValueFormatter(new PercentFormatter());
            d.setValueTextSize(11f);
            d.setValueTextColor(mPrimaryColorDark);
            mPieData =  d;
        }
    }
}
