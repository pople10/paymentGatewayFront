package com.trackiness.fastpay;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.BalanceService;
import com.trackiness.services.StatisticService;
import com.trackiness.services.models.BalanceStatistic;
import com.trackiness.services.models.StatisticData;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatisticFragment extends Fragment {

    private LineChart chart;
    private int idBalance;
    private AutoCompleteTextView balanceName;
    private TextView totalBalance,totalIncome,totalExpense;
    private ScrollView scrollView;
    private LinearLayout loader;
    private StatisticService statisticService;
    private List<BalanceStatistic> balancesIDLabel=new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistic, container,false);
        chart = root.findViewById(R.id.chart_a);
        Utility.authMiddleware(root.getContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        root.getContext().registerReceiver(br, filter);
        scrollView =((ScrollView) root.findViewById(R.id.scrollView_statistic));
        loader =((LinearLayout) root.findViewById(R.id.loader));
        balanceName = root.findViewById(R.id.balance_name);
        totalBalance = root.findViewById(R.id.total_balance);
        totalIncome = root.findViewById(R.id.income_amount);
        totalExpense = root.findViewById(R.id.expense_amount);
        statisticService = new StatisticService(getContext());
        initializeChart();

        return root;
    }

    public ArrayList<Entry> getDataChart(List<Double> d){
        ArrayList<Entry> data = new ArrayList<Entry>();
        int n =d.size();
        if(n>0)
            for(int i =0;i<n;i++){
                double v  =d.get(i);
                data.add(new Entry(i,(float) v ));
            }
        return data;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
                    statisticService.getStaticData(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                try {
                    setInitialBalancesData(statisticService.getStatisticData());
                }
                catch (Exception e){}

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void beforeSend() {

            }

            @Override
            public void onFinish() {

            }
        });

        }catch (Exception e){}

    }

    public void setInitialBalancesData(List<StatisticData> sd) {
        String[] data = new String[sd.size()];
        int i = 0;
        for(StatisticData b :sd){
            balancesIDLabel.add(new BalanceStatistic(i,b.getBalance().getLabel(), b.getBalance().generateLabel(),generateLabel(b.getIncome(),b.getBalance().getCurrency()),generateLabel(b.getExpense(),b.getBalance().getCurrency()),getDataChart(b.getExpenseData()),getDataChart(b.getIncomeData())));
            if(i==0){
                changeBalance(balancesIDLabel.get(i));
                balanceName.setText(b.getBalance().getLabel());
            }
            idBalance = i;
            data[i++] =b.getBalance().getLabel();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, data);
        balanceName.setAdapter(adapter);

        balanceName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                changeBalance(balancesIDLabel.get((int)id));
            }
        });
        hideIndicator();
    }
    public  void changeBalance(BalanceStatistic balance){
        setchartData(balance.getIncomeData(),balance.getExpenseData());
        totalBalance.setText(balance.getTotal());
        totalIncome.setText(balance.getIncome());
        totalExpense.setText(balance.getExpense());
    }
    public void initializeChart(){
        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(250);
        XAxis x = chart.getXAxis();
        x.setEnabled(false);
        YAxis y = chart.getAxisLeft();
        y.setTextColor(getResources().getColor(R.color.TextRegisterG));
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
    }
    public void setchartData(ArrayList<Entry> DataIncome,ArrayList<Entry> DataExpense){
        LineDataSet set1,set2;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(DataIncome);
            set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            set2.setValues(DataExpense);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(DataIncome, "Income");
            set2 = new LineDataSet(DataExpense, "Expense");
            setLineData(set1,getResources().getColor(R.color.primary));
            setLineData(set2,getResources().getColor(R.color.danger));
        }
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
        lineDataSets.add(set1);
        lineDataSets.add(set2);

        LineData data = new LineData(lineDataSets);
        data.setDrawValues(false);
        chart.setData(data);
        chart.invalidate();

    }
    public void setLineData(LineDataSet setl,int a){
        setl.setDrawCircles(true);
        setl.setFillAlpha(0);
        setl.setCircleColor(getResources().getColor(R.color.white));
        setl.setCircleColorHole(a);
        setl.setDrawHighlightIndicators(false);
        setl.setColor(a);
        setl.setDrawHorizontalHighlightIndicator(false);
        setl.setDrawValues(true);
        setl.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) { return chart.getAxisLeft().getAxisMinimum(); }
        });
        setl.setFillColor(getResources().getColor(R.color.primary));
        setl.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setl.setDrawFilled(true);
        setl.setLineWidth(2.8f);
    }
    public void  showIndicator(){
        scrollView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }
    public void hideIndicator(){
        scrollView.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }
    public String generateLabel(Double amount,String currency)
    {
        String preffix="";
        if(amount<0)
            preffix="- ";
        if(currency.equals("USD"))
            return preffix+"$"+amount;
        if(currency.equals("EUR"))
            return preffix+"€"+amount;
        if(currency.equals("GBP"))
            return preffix+"£"+amount;
        return preffix+amount+" "+currency;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        statisticService=null;
    }
}