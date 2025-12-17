package com.example.electrabill;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnit, etRebate;
    Button btnCalculate, btnViewList, btnAbout;
    TextView tvTotalCharges, tvFinalCost;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("ElectraBill");

        dbHelper = new DBHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnit = findViewById(R.id.etUnit);
        etRebate = findViewById(R.id.etRebate);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewList = findViewById(R.id.btnViewList);
        btnAbout = findViewById(R.id.btnAbout);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);

        // Month spinner
        ArrayList<String> months = new ArrayList<>();
        months.add("January 2025");
        months.add("February 2025");
        months.add("March 2025");
        months.add("April 2025");
        months.add("May 2025");
        months.add("June 2025");
        months.add("July 2025");
        months.add("August 2025");
        months.add("September 2025");
        months.add("October 2025");
        months.add("November 2025");
        months.add("December 2025");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        btnCalculate.setOnClickListener(v -> calculateAndSave());

        btnViewList.setOnClickListener(v ->
                startActivity(new Intent(this, ListActivity.class)));

        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
    }

    private void calculateAndSave() {

        if (etUnit.getText().toString().isEmpty()) {
            Toast.makeText(this,
                    "Please enter electricity usage (kWh)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (etRebate.getText().toString().isEmpty()) {
            Toast.makeText(this,
                    "Please enter rebate percentage (0–5)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int unit = Integer.parseInt(etUnit.getText().toString());
        int rebateInput = Integer.parseInt(etRebate.getText().toString());

        if (rebateInput < 0 || rebateInput > 5) {
            Toast.makeText(this,
                    "Rebate must be between 0% and 5%",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String month = spinnerMonth.getSelectedItem().toString();
        double total = calculateElectricityBill(unit);
        double rebate = rebateInput / 100.0;
        double finalCost = total - (total * rebate);

        tvTotalCharges.setText(
                "Total Charges: RM " + String.format("%.2f", total));
        tvFinalCost.setText(
                "Final Cost After Rebate: RM " + String.format("%.2f", finalCost));

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month", month);
        cv.put("unit", unit);
        cv.put("total", total);
        cv.put("rebate", rebate);
        cv.put("final_cost", finalCost);
        db.insert("bill", null, cv);

        Toast.makeText(this,
                "Bill saved successfully",
                Toast.LENGTH_SHORT).show();
    }

    private double calculateElectricityBill(int unit) {
        if (unit <= 200) {
            return unit * 0.218;
        } else if (unit <= 300) {
            return (200 * 0.218) + ((unit - 200) * 0.334);
        } else if (unit <= 600) {
            return (200 * 0.218) + (100 * 0.334) + ((unit - 300) * 0.516);
        } else {
            return (200 * 0.218) + (100 * 0.334)
                    + (300 * 0.516) + ((unit - 600) * 0.546);
        }
    }
}
