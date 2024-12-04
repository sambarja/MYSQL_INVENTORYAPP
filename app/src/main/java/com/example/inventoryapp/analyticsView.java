package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.connection.RealCall;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class analyticsView extends AppCompatActivity {

    ImageView menu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView sales, inQuantity, outQuantity;
    Button downloadBtn;
    List<InvoiceResponse.Invoice> invoiceList;
    String selectedMonth;  // Variable to hold the selected month

    int userId;
    String username, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_analytics_view);

        // Retrieve the selected month passed from analytics activity
        selectedMonth = getIntent().getStringExtra("selectedMonth");  // Get the month selected

        menu = findViewById(R.id.menuImage);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_inventory);
        sales = findViewById(R.id.total_sales);
        inQuantity = findViewById(R.id.quantity_in);
        outQuantity = findViewById(R.id.quantity_out);
        downloadBtn = findViewById(R.id.download_excel_btn);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        username = sharedPreferences.getString("username", "Guest");
        name = sharedPreferences.getString("name", "Unknown User");

        // Update navigation header with user info
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.username);
        TextView nameText = headerView.findViewById(R.id.name);

        usernameText.setText(username);
        nameText.setText(name);


        menu.setOnClickListener(view -> drawerLayout.open());

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(analyticsView.this, home.class));
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(analyticsView.this, inventory.class));
            } else if (itemId == R.id.nav_inbound) {
                startActivity(new Intent(analyticsView.this, inbound.class));
            } else if (itemId == R.id.nav_outbound) {
                startActivity(new Intent(analyticsView.this, outbound.class));
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(analyticsView.this, addproduct.class));
            } else if (itemId == R.id.nav_delete) {
                startActivity(new Intent(analyticsView.this, deleteproduct.class));
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(analyticsView.this, analytics.class));
            } else if (itemId == R.id.logout) {
                logout.logout(analyticsView.this);
            }
            drawerLayout.close();
            return false;
        });

        // Fetch the invoice data for the selected month
        fetchInvoiceData();

        downloadBtn.setOnClickListener(view -> {
            if (invoiceList != null && !invoiceList.isEmpty()) {
                try {
                    generateExcelFile("invoices.xlsx", invoiceList);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error generating Excel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No data to export.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchInvoiceData() {;

        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();

        // Fetch all invoices for the user
        apiService.getAllInvoicesByUser(userId).enqueue(new Callback<InvoiceResponse>() {
            @Override
            public void onResponse(Call<InvoiceResponse> call, Response<InvoiceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    invoiceList = response.body().getInvoices();
                    Log.d("Analytics", "Raw Response: " + new Gson().toJson(invoiceList));  // Log the full response body

                    // Group invoices by month
                    Map<String, List<InvoiceResponse.Invoice>> invoicesByMonth = groupInvoicesByMonth(invoiceList);

                    // Get the invoices for the selected month
                    List<InvoiceResponse.Invoice> selectedMonthInvoices = invoicesByMonth.get(selectedMonth);

                    if (selectedMonthInvoices != null && !selectedMonthInvoices.isEmpty()) {
                        updateViews(selectedMonthInvoices);
                    } else {
                        Toast.makeText(analyticsView.this, "No invoices found for the selected month.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(analyticsView.this, "Failed to fetch invoices.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InvoiceResponse> call, Throwable t) {
                Toast.makeText(analyticsView.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateViews(List<InvoiceResponse.Invoice> invoices) {
        int inboundQuantity = 0;
        int outboundQuantity = 0;
        final double[] totalSales = {0};  // Use a simple variable for total sales instead of an array

        sales.setText(String.valueOf(totalSales[0]));

        Log.d("Analytics", "Starting to update views with " + invoices.size() + " invoices");

        for (InvoiceResponse.Invoice invoice : invoices) {
            String activityType = invoice.getActivityType();
            int quantity = invoice.getQuantity();

            // Debugging: Log each invoice's activity type and quantity
            Log.d("Analytics", "Invoice activity type: " + activityType + ", Quantity: " + quantity);

            if ("inbound".equals(activityType)) {
                inboundQuantity += quantity;
                Log.d("Analytics", "Inbound quantity updated: " + inboundQuantity);
            } else if ("outbound".equals(activityType)) {
                outboundQuantity += quantity;
                Log.d("Analytics", "Outbound quantity updated: " + outboundQuantity);

                // Fetch the price for the product using the model number
                Api api = RetrofitClient.getInstance(getApplicationContext()).getApi();
                Call<ProductResponse> call = api.findUserProducts(userId,invoice.getModelNumber());
                call.enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ProductResponse productResponse = response.body();
                            if (!productResponse.isError() && productResponse.getProducts() != null && !productResponse.getProducts().isEmpty()) {
                                ProductResponse.Product product = productResponse.getProducts().get(0);
                                double price = product.getPrice();

                                // Calculate total sales for outbound products
                                totalSales[0] += quantity * price;
                                Log.d("Analytics", "Total sales updated: " + totalSales[0]);
                                sales.setText(String.valueOf(totalSales[0]));

                            } else {
                                Log.e("Analytics", "Product not found or error in product response");
                            }
                        } else {
                            Log.e("Analytics", "Failed to fetch product price, Response: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        Log.e("Analytics", "Error fetching product price: ", t);
                    }
                });
            }
        }

        // Debugging: Log the final quantities before updating the UI
        Log.d("Analytics", "Final Inbound Quantity: " + inboundQuantity);
        Log.d("Analytics", "Final Outbound Quantity: " + outboundQuantity);

        // Update the quantities for inbound and outbound in the UI
        if (inQuantity != null) {
            inQuantity.setText(String.valueOf(inboundQuantity));
        } else {
            Log.e("Analytics", "Inbound quantity TextView is null");
        }

        if (outQuantity != null) {
            outQuantity.setText(String.valueOf(outboundQuantity));
        } else {
            Log.e("Analytics", "Outbound quantity TextView is null");
        }


    }




    public static Map<String, List<InvoiceResponse.Invoice>> groupInvoicesByMonth(List<InvoiceResponse.Invoice> invoices) {
        Map<String, List<InvoiceResponse.Invoice>> invoicesByMonth = new HashMap<>();

        // Correct DateTimeFormatter pattern to include both date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (InvoiceResponse.Invoice invoice : invoices) {
            try {
                // Parse the date-time string with the correct formatter
                LocalDateTime dateTime = LocalDateTime.parse(invoice.getDate(), formatter);

                // Extract the month from the dateTime
                String monthName = dateTime.getMonth().toString(); // Get month name

                // Add the invoice to the list corresponding to the month
                invoicesByMonth.computeIfAbsent(monthName, k -> new ArrayList<>()).add(invoice);
            } catch (DateTimeParseException e) {
                Log.e("Date Parsing", "Failed to parse date: " + invoice.getDate(), e);
            }
        }

        return invoicesByMonth;
    }


    public void generateExcelFile(String fileName, List<InvoiceResponse.Invoice> invoices) throws IOException {
        // Create a new workbook and sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Invoices");

        // Set the header row
        String[] header = {"SI", "Model Number", "User ID", "Quantity", "Activity Type", "Date", "Price"};
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
        }

        // Add invoice data to rows
        int rowIndex = 1;
        for (InvoiceResponse.Invoice invoice : invoices) {
            XSSFRow row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(invoice.getSi());
            row.createCell(1).setCellValue(invoice.getModelNumber());
            row.createCell(2).setCellValue(invoice.getUserId());
            row.createCell(3).setCellValue(invoice.getQuantity());
            row.createCell(4).setCellValue(invoice.getActivityType());
            row.createCell(5).setCellValue(invoice.getDate());
            // Optionally, you can add the price here if you have access to it
        }

        // Determine the directory and file path
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        // Save the file
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();

        // Show confirmation message
        Toast.makeText(this, "File saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        // Open the Excel file
        openExcelFile(file);
    }

    private void openExcelFile(File file) {
        Uri uri = FileProvider.getUriForFile(this, "com.example.inventoryapp.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("ExcelFile", "Error opening Excel file", e);
            Toast.makeText(this, "Unable to open file", Toast.LENGTH_SHORT).show();
        }
    }

}
