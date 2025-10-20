package com.example.bim.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bim.R;
import com.example.bim.data.DatabaseHelper;
import com.example.bim.data.Product;
import com.example.bim.data.Sale;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private TextView tvTotalSales, tvTotalProfit, tvInventoryValue;
    private Button btnExportCSV, btnFilterDate;
    private Spinner spinnerGraphType;
    private LineChart lineChart;
    private BarChart barChart;
    private DatabaseHelper dbHelper;

    private Date startDate, endDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalSales = view.findViewById(R.id.tvTotalSales);
        tvTotalProfit = view.findViewById(R.id.tvTotalProfit);
        tvInventoryValue = view.findViewById(R.id.tvInventoryValue);
        btnExportCSV = view.findViewById(R.id.btnExportCSV);
        btnFilterDate = view.findViewById(R.id.btnFilterDate);
        spinnerGraphType = view.findViewById(R.id.spinnerGraphType);

        lineChart = view.findViewById(R.id.lineChart);
        barChart = view.findViewById(R.id.barChart);

        dbHelper = new DatabaseHelper(getContext());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.graph_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGraphType.setAdapter(adapter);

        spinnerGraphType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateAnalytics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnFilterDate.setOnClickListener(v -> showDatePicker());
        btnExportCSV.setOnClickListener(v -> exportSalesToCSV());

        calculateAnalytics();
    }

    private void calculateAnalytics() {
        List<Sale> salesList;
        if (startDate != null && endDate != null) {
            salesList = dbHelper.getSalesBetween(startDate.getTime(), endDate.getTime());
        } else {
            salesList = dbHelper.getAllSales();
        }

        if (salesList.isEmpty()) {
            lineChart.clear();
            barChart.clear();
            lineChart.setVisibility(View.GONE);
            barChart.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No sales found for selected date range", Toast.LENGTH_SHORT).show();
        }

        List<Product> productList = dbHelper.getAllProducts();
        double totalSales = 0;
        double totalProfit = 0;
        double inventoryValue = 0;

        for (Sale sale : salesList) {
            Product p = dbHelper.getProductById(sale.getProductId());
            if (p == null) continue;

            totalSales += sale.getTotal();
            totalProfit += sale.getTotal() - (p.getCostPrice() * sale.getQuantity());
        }

        for (Product p : productList) {
            inventoryValue += p.getPrice() * p.getStock();
        }

        tvTotalSales.setText(String.format(Locale.getDefault(), "R %.2f", totalSales));
        tvTotalProfit.setText(String.format(Locale.getDefault(), "R %.2f", totalProfit));
        tvInventoryValue.setText(String.format(Locale.getDefault(), "R %.2f", inventoryValue));

        String graphType = spinnerGraphType.getSelectedItem() != null ?
                spinnerGraphType.getSelectedItem().toString() : "Line";

        if (graphType.equalsIgnoreCase("Line")) {
            showLineChart(salesList);
            lineChart.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
        } else {
            showBarChart(salesList);
            lineChart.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
        }
    }

    private void showLineChart(List<Sale> salesList) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < salesList.size(); i++) {
            Sale s = salesList.get(i);
            entries.add(new Entry(i, (float) s.getTotal()));
            labels.add(new SimpleDateFormat("MM-dd", Locale.getDefault())
                    .format(new Date(s.getSaleDate())));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Sales");
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setCircleColor(getResources().getColor(R.color.colorAccent));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextColor(getResources().getColor(R.color.colorTextPrimary));
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.colorPrimary));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(R.color.colorTextSecondary));
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < labels.size()) ? labels.get(index) : "";
            }
        });

        lineChart.getAxisLeft().setTextColor(getResources().getColor(R.color.colorTextPrimary));
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(getResources().getColor(R.color.colorTextPrimary));
        lineChart.animateY(1000);
        lineChart.invalidate();
    }

    private void showBarChart(List<Sale> salesList) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < salesList.size(); i++) {
            Sale s = salesList.get(i);
            entries.add(new BarEntry(i, (float) s.getTotal()));
            labels.add(new SimpleDateFormat("MM-dd", Locale.getDefault())
                    .format(new Date(s.getSaleDate())));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Sales");
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setValueTextColor(getResources().getColor(R.color.colorTextPrimary));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(R.color.colorTextSecondary));
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < labels.size()) ? labels.get(index) : "";
            }
        });

        barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.colorTextPrimary));
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setTextColor(getResources().getColor(R.color.colorTextPrimary));
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month, dayOfMonth, 0, 0, 0);
            startDate = startCal.getTime();

            new DatePickerDialog(getContext(), (v, y, m, d) -> {
                Calendar endCal = Calendar.getInstance();
                endCal.set(y, m, d, 23, 59, 59);
                endDate = endCal.getTime();
                calculateAnalytics();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void exportSalesToCSV() {
        List<Sale> salesList;
        if (startDate != null && endDate != null) {
            salesList = dbHelper.getSalesBetween(startDate.getTime(), endDate.getTime());
        } else {
            salesList = dbHelper.getAllSales();
        }

        if (salesList.isEmpty()) {
            Toast.makeText(getContext(), "No sales to export", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fileName = "sales_" + System.currentTimeMillis() + ".csv";
        java.io.File file = new java.io.File(requireContext().getExternalFilesDir(null), fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.append("ProductID,Quantity,Total,Date\n");
            for (Sale s : salesList) {
                writer.append(s.getProductId() + "," + s.getQuantity() + "," +
                        s.getTotal() + "," + sdf.format(new Date(s.getSaleDate())) + "\n");
            }
            writer.flush();
            Toast.makeText(getContext(), "CSV exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Export failed", Toast.LENGTH_SHORT).show();
        }
    }
}
