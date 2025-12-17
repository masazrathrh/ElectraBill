package com.example.electrabill;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listViewBills;
    DBHelper dbHelper;

    ArrayList<Integer> billIds = new ArrayList<>();
    ArrayList<String> billMonths = new ArrayList<>();
    ArrayList<String> billAmounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle("Saved Bills");

        listViewBills = findViewById(R.id.listViewBills);
        dbHelper = new DBHelper(this);

        loadSavedBills();

        listViewBills.setOnItemClickListener((parent, view, position, id) -> {
            showActionDialog(position);
        });
    }

    private void loadSavedBills() {
        billIds.clear();
        billMonths.clear();
        billAmounts.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bill", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String fullMonth = cursor.getString(1); // e.g. January 2025
            double finalCost = cursor.getDouble(5);

            billIds.add(id);
            billMonths.add(fullMonth);
            billAmounts.add("RM " + String.format("%.2f", finalCost));
        }

        cursor.close();

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_list_item_2, android.R.id.text1, billIds) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
                }

                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text1.setText(billMonths.get(position));   // January 2025
                text2.setText(billAmounts.get(position));  // RM xx.xx

                return view;
            }
        };

        listViewBills.setAdapter(adapter);
    }

    private void showActionDialog(int position) {
        String[] options = {"View Bill Details", "Delete Bill"};

        new AlertDialog.Builder(this)
                .setTitle("Select Action")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(this, DetailActivity.class);
                        intent.putExtra("id", billIds.get(position));
                        startActivity(intent);
                    } else {
                        deleteBill(position);
                    }
                })
                .show();
    }

    private void deleteBill(int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("bill", "id=?",
                new String[]{String.valueOf(billIds.get(position))});
        loadSavedBills();
    }
}
