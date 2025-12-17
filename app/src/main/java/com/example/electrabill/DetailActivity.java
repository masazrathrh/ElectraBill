package com.example.electrabill;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    TextView tvMonth, tvUnit, tvTotal, tvRebate, tvFinal;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("Bill Details");

        tvMonth = findViewById(R.id.tvMonth);
        tvUnit = findViewById(R.id.tvUnit);
        tvTotal = findViewById(R.id.tvTotal);
        tvRebate = findViewById(R.id.tvRebate);
        tvFinal = findViewById(R.id.tvFinal);

        dbHelper = new DBHelper(this);

        int billId = getIntent().getIntExtra("id", -1);
        if (billId != -1) {
            loadBillDetails(billId);
        }
    }

    private void loadBillDetails(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM bill WHERE id = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {

            String month = cursor.getString(1);
            int unit = cursor.getInt(2);
            double total = cursor.getDouble(3);
            double rebateValue = cursor.getDouble(4);
            double finalCost = cursor.getDouble(5);

            // ✅ FIX: convert rebate correctly for display
            int rebatePercent;
            if (rebateValue <= 1) {
                rebatePercent = (int) (rebateValue * 100);
            } else {
                rebatePercent = (int) rebateValue;
            }

            tvMonth.setText("Month: " + month);
            tvUnit.setText("Electricity Unit: " + unit + " kWh");
            tvTotal.setText("Total Charges: RM " + String.format("%.2f", total));
            tvRebate.setText("Rebate: " + rebatePercent + "%");
            tvFinal.setText("Final Cost: RM " + String.format("%.2f", finalCost));
        }

        cursor.close();
    }
}
