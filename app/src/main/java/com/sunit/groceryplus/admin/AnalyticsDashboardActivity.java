package com.sunit.groceryplus.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/** AnalyticsDashboardActivity - Visual overview of business performance including revenue, orders, and customer metrics. */
public class AnalyticsDashboardActivity extends AppCompatActivity {

    // Data Helper
    private DatabaseHelper dbHelper;
    
    // UI Components (Metric Cards)
    private TextView totalRevenueTv;
    private TextView totalOrdersTv;
    private TextView totalCustomersTv;
    private TextView totalProductsTv;
    private TextView completedOrdersTv;
    private TextView pendingOrdersTv;
    private TextView avgOrderValueTv;
    private TextView todayRevenueTv;
    
    // Chart Components
    private PieChart orderStatusChart;
    private BarChart revenueChart;
    private LineChart salesTrendChart;
    
    // Data refresh
    private Handler refreshHandler;
    private Runnable refreshRunnable;

    /** Initializes the activity, toolbar, and data helper. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_dashboard);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        refreshHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupCharts();
        loadAnalyticsData();
        startAutoRefresh();
    }

    /** Binds UI components to their respective layout IDs. */
    private void initViews() {
        totalRevenueTv = findViewById(R.id.totalRevenueTv);
        totalOrdersTv = findViewById(R.id.totalOrdersTv);
        totalCustomersTv = findViewById(R.id.totalCustomersTv);
        totalProductsTv = findViewById(R.id.totalProductsTv);
        completedOrdersTv = findViewById(R.id.completedOrdersTv);
        pendingOrdersTv = findViewById(R.id.pendingOrdersTv);
        avgOrderValueTv = findViewById(R.id.avgOrderValueTv);
        todayRevenueTv = findViewById(R.id.todayRevenueTv);
        
        // Chart components
        orderStatusChart = findViewById(R.id.orderStatusChart);
        revenueChart = findViewById(R.id.revenueChart);
        salesTrendChart = findViewById(R.id.salesTrendChart);
    }
    
    /** Sets up chart configurations and styling. */
    private void setupCharts() {
        setupPieChart();
        setupBarChart();
        setupLineChart();
    }
    
    /** Configures the order status pie chart. */
    private void setupPieChart() {
        orderStatusChart.setUsePercentValues(true);
        orderStatusChart.getDescription().setEnabled(false);
        orderStatusChart.setExtraOffsets(5, 10, 5, 5);
        orderStatusChart.setDragDecelerationFrictionCoef(0.95f);
        orderStatusChart.setDrawHoleEnabled(true);
        orderStatusChart.setHoleColor(getResources().getColor(android.R.color.white));
        orderStatusChart.setTransparentCircleColor(getResources().getColor(android.R.color.white));
        orderStatusChart.setTransparentCircleAlpha(110);
        orderStatusChart.setHoleRadius(58f);
        orderStatusChart.setTransparentCircleRadius(61f);
        orderStatusChart.setDrawCenterText(true);
        orderStatusChart.setRotationAngle(0);
        orderStatusChart.setRotationEnabled(true);
        orderStatusChart.setHighlightPerTapEnabled(true);
    }
    
    /** Configures the revenue bar chart. */
    private void setupBarChart() {
        revenueChart.getDescription().setEnabled(false);
        revenueChart.setDrawGridBackground(false);
        revenueChart.setDrawBarShadow(false);
        revenueChart.setHighlightFullBarEnabled(false);
    }
    
    /** Configures the sales trend line chart. */
    private void setupLineChart() {
        salesTrendChart.getDescription().setEnabled(false);
        salesTrendChart.setDrawGridBackground(false);
        salesTrendChart.setTouchEnabled(true);
        salesTrendChart.setHighlightPerDragEnabled(true);
    }

    /** Fetches aggregated performance metrics and updates UI. */
    private void loadAnalyticsData() {
        try {
            // Total Revenue
            double revenue = dbHelper.getTotalRevenue();
            totalRevenueTv.setText(String.format("Rs. %.2f", revenue));

            // Total Orders
            int orderCount = dbHelper.getTotalOrdersCount();
            totalOrdersTv.setText(String.valueOf(orderCount));

            // Total Customers
            int customerCount = dbHelper.getTotalCustomersCount();
            totalCustomersTv.setText(String.valueOf(customerCount));

            // Total Products
            int productCount = dbHelper.getTotalProductsCount();
            totalProductsTv.setText(String.valueOf(productCount));

            // Completed Orders
            int deliveredCount = dbHelper.getOrderCountByStatus("Delivered");
            completedOrdersTv.setText(String.valueOf(deliveredCount));
            
            // Pending Orders
            int pendingCount = dbHelper.getOrderCountByStatus("Pending");
            pendingOrdersTv.setText(String.valueOf(pendingCount));
            
            // Average Order Value
            double avgOrderValue = orderCount > 0 ? revenue / orderCount : 0;
            avgOrderValueTv.setText(String.format("Rs. %.2f", avgOrderValue));
            
            // Today's Revenue
            double todayRevenue = dbHelper.getTodayRevenue();
            todayRevenueTv.setText(String.format("Rs. %.2f", todayRevenue));
            
            // Load chart data
            loadOrderStatusChart();
            loadRevenueChart();
            loadSalesTrendChart();
            
        } catch (Exception e) {
            Toast.makeText(this, "Error loading analytics data", Toast.LENGTH_SHORT).show();
        }
    }
    
    /** Loads and displays order status distribution in pie chart. */
    private void loadOrderStatusChart() {
        try {
            List<PieEntry> entries = new ArrayList<>();
            
            int pending = dbHelper.getOrderCountByStatus("Pending");
            int confirmed = dbHelper.getOrderCountByStatus("Confirmed");
            int preparing = dbHelper.getOrderCountByStatus("Preparing");
            int shipped = dbHelper.getOrderCountByStatus("Shipped");
            int delivered = dbHelper.getOrderCountByStatus("Delivered");
            int cancelled = dbHelper.getOrderCountByStatus("Cancelled");
            
            if (pending > 0) entries.add(new PieEntry(pending, "Pending"));
            if (confirmed > 0) entries.add(new PieEntry(confirmed, "Confirmed"));
            if (preparing > 0) entries.add(new PieEntry(preparing, "Preparing"));
            if (shipped > 0) entries.add(new PieEntry(shipped, "Shipped"));
            if (delivered > 0) entries.add(new PieEntry(delivered, "Delivered"));
            if (cancelled > 0) entries.add(new PieEntry(cancelled, "Cancelled"));
            
            PieDataSet dataSet = new PieDataSet(entries, "Order Status");
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);
            
            // Add colors
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(getResources().getColor(R.color.pending_color));
            colors.add(getResources().getColor(R.color.confirmed_color));
            colors.add(getResources().getColor(R.color.preparing_color));
            colors.add(getResources().getColor(R.color.shipped_color));
            colors.add(getResources().getColor(R.color.delivered_color));
            colors.add(getResources().getColor(R.color.cancelled_color));
            dataSet.setColors(colors);
            
            PieData data = new PieData(dataSet);
            data.setValueTextSize(11f);
            data.setValueTextColor(getResources().getColor(android.R.color.black));
            
            orderStatusChart.setData(data);
            orderStatusChart.invalidate();
        } catch (Exception e) {
            // Handle chart loading error
        }
    }
    
    /** Loads and displays monthly revenue in bar chart. */
    private void loadRevenueChart() {
        try {
            List<BarEntry> entries = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            
            // Get last 6 months data
            for (int i = 5; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -i);
                String month = sdf.format(cal.getTime());
                double revenue = dbHelper.getMonthRevenue(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
                entries.add(new BarEntry(5 - i, (float) revenue));
            }
            
            BarDataSet dataSet = new BarDataSet(entries, "Monthly Revenue");
            dataSet.setColor(getResources().getColor(R.color.primary));
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
            
            BarData data = new BarData(dataSet);
            revenueChart.setData(data);
            revenueChart.invalidate();
        } catch (Exception e) {
            // Handle chart loading error
        }
    }
    
    /** Loads and displays sales trend in line chart. */
    private void loadSalesTrendChart() {
        try {
            List<Entry> entries = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            
            // Get last 7 days data
            for (int i = 6; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -i);
                String date = sdf.format(cal.getTime());
                double revenue = dbHelper.getDailyRevenue(cal.get(Calendar.DAY_OF_MONTH), 
                                                       cal.get(Calendar.MONTH) + 1, 
                                                       cal.get(Calendar.YEAR));
                entries.add(new Entry(6 - i, (float) revenue));
            }
            
            LineDataSet dataSet = new LineDataSet(entries, "Daily Sales");
            dataSet.setColor(getResources().getColor(R.color.accent));
            dataSet.setLineWidth(2f);
            dataSet.setCircleColor(getResources().getColor(R.color.accent));
            dataSet.setCircleRadius(4f);
            dataSet.setValueTextSize(10f);
            dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(getResources().getColor(R.color.accent_light));
            
            LineData data = new LineData(dataSet);
            salesTrendChart.setData(data);
            salesTrendChart.invalidate();
        } catch (Exception e) {
            // Handle chart loading error
        }
    }
    
    /** Starts automatic data refresh every 30 seconds. */
    private void startAutoRefresh() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadAnalyticsData();
                refreshHandler.postDelayed(this, 30000); // Refresh every 30 seconds
            }
        };
        refreshHandler.post(refreshRunnable);
    }
    
    /** Stops automatic data refresh. */
    private void stopAutoRefresh() {
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }

    /** Handles toolbar menu selections. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoRefresh();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        stopAutoRefresh();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        startAutoRefresh();
    }
}
