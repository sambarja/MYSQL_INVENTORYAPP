package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class analyticsView extends AppCompatActivity {

    private InventoryDataSource dataSource;
    ImageView menu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    TextView sales, inQuantity, outQuantity;
    Button downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_analytics_view);


        dataSource = new InventoryDataSource(this);
        menu = findViewById(R.id.menuImage);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_inventory);
        sales = findViewById(R.id.total_sales);
        inQuantity = findViewById(R.id.quantity_in);
        outQuantity = findViewById(R.id.quantity_out);
        downloadBtn = findViewById(R.id.download_excel_btn);

        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.username);
        TextView nameText = headerView.findViewById(R.id.name);

        User user = SessionData.getInstance().user;
        usernameText.setText(user.getUsername());
        nameText.setText(user.getName());

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.nav_home) {

                    startActivity(new Intent(analyticsView.this, home.class));
                }
                if (itemId == R.id.nav_inventory) {

                }
                if (itemId == R.id.nav_inbound) {
                    startActivity(new Intent(analyticsView.this, inbound.class));

                }
                if (itemId == R.id.nav_outbound) {
                    startActivity(new Intent(analyticsView.this, outbound.class));

                }
                if (itemId == R.id.nav_add) {
                    startActivity(new Intent(analyticsView.this, addproduct.class));

                }
                if (itemId == R.id.nav_delete) {
                    startActivity(new Intent(analyticsView.this, deleteproduct.class));

                }
                if (itemId == R.id.nav_analytics) {
                    startActivity(new Intent(analyticsView.this, analyticsView.class));

                }
                if (itemId == R.id.logout) {
                    startActivity(new Intent(analyticsView.this, logout.class));

                }

                drawerLayout.close();

                return false;
            }
        });
        setViews();
        downloadBtn.setOnClickListener(
                view -> {
                    String fileName = SessionData.getInstance().selectedMonth;
                    String[] header = {"User ID", "Model Number", "SI Number", "Quantity", "Date", "Type"};
                    String userID = SessionData.getInstance().user.getUsername();
                    List<Invoice> invoiceList = SessionData.getInstance().invoicesByMonth.get(fileName);

                    int dataLength = invoiceList.size() + 1; // Add 1 for the header row
                    String[][] data = new String[dataLength][header.length];
                    data[0] = header;

                    int rowIndex = 1;
                    for (Invoice invoice : invoiceList) {
                        String modelNumber = invoice.product.getModelNumber();
                        String date = invoice.getDate();
                        String type = invoice.getActivityType();
                        int si = invoice.getSi();
                        int quantity = invoice.getQuantity();
                        data[rowIndex] = new String[]{userID, modelNumber, String.valueOf(si), String.valueOf(quantity), date, type};
                        rowIndex++;
                    }

                    try {
                        generateExcelFile(fileName + "_invoices.xlsx", data);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("excel+_generate", e.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void generateExcelFile(String fileName, String[][] data) throws IOException {

        // Create a new workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Add a worksheet
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        // Loop through data and write to cells
        for (int i = 0; i < data.length; i++) {
            XSSFRow row = sheet.createRow(i);
            for (int j = 0; j < data[i].length; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(data[i][j]);
            }
        }

        // Get the external storage directory
        File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create a directory path
        String directoryPath = externalStorageDirectory.getAbsolutePath();

        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // mkdirs() creates parent directories if not exists
        }

        // Create a File object for the destination file
        File file = new File(directory, fileName);

        // Create a FileOutputStream to write to the file
        FileOutputStream outputStream = new FileOutputStream(file);

        // Write the workbook data to the output stream
        workbook.write(outputStream);

        // Close the output stream
        outputStream.close();

        Log.i("Excel", "Excel file generated successfully!");
        Toast.makeText(getApplicationContext(), "Saved on" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }

    private Void setViews() {
        SessionData sessionData = SessionData.getInstance();
        int inboundQuantity = 0;
        int outboundQuantity = 0;
        double totalSales = 0;
        List<Invoice> invoiceList = sessionData.invoicesByMonth.get(sessionData.selectedMonth);
        for (Invoice invoice : invoiceList) {
            String activity_type = invoice.getActivityType();
            int quantity = invoice.getQuantity();
            if (activity_type.equals("inbound")) {
                inboundQuantity += quantity;
            } else {
                outboundQuantity += quantity;
                product product = invoice.product;
                double sales = product.getPrice() * quantity;
                totalSales += sales;
            }
        }
        Log.d("activity_type", String.valueOf(inboundQuantity));
        Log.d("activity_type", String.valueOf(outboundQuantity));
        Log.d("activity_type", String.valueOf(totalSales));
        sales.setText(String.valueOf(totalSales));
        inQuantity.setText(String.valueOf(inboundQuantity));
        outQuantity.setText(String.valueOf(outboundQuantity));
        return null;
    }

}


