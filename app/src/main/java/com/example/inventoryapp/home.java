package com.example.inventoryapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;




import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

public class home extends AppCompatActivity {

    CardView choice1, choice2, choice3, choice4, choice5, choice6, cardView;
    ImageView menu;

    NavigationView navigationView;

    TextView usernameText,nameText;


    DrawerLayout drawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_home);





        drawerLayout = findViewById(R.id.drawer_home);
        navigationView = findViewById(R.id.nav_view);
        usernameText = findViewById(R.id.username);
        nameText = findViewById(R.id.name);







        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        choice5 = findViewById(R.id.choice5);
        choice6 = findViewById(R.id.choice6);
        cardView = findViewById(R.id.cardView);
        menu = findViewById(R.id.menuImage);

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

                if (itemId == R.id.nav_home){


                }
                if (itemId == R.id.nav_inventory){
                    startActivity(new Intent(home.this, inventory.class));

                }
                if (itemId == R.id.nav_inbound){
                    startActivity(new Intent(home.this, inbound.class));

                }
                if (itemId == R.id.nav_outbound){
                    startActivity(new Intent(home.this, outbound.class));

                }
                if (itemId == R.id.nav_add){
                    startActivity(new Intent(home.this, addproduct.class));

                }
                if (itemId == R.id.nav_delete){
                    startActivity(new Intent(home.this, deleteproduct.class));

                }
                if (itemId == R.id.nav_analytics){
                    startActivity(new Intent(home.this, Analytics.class));

                }
                if (itemId == R.id.logout){
                    logout.logout(home.this);

                }

                drawerLayout.close();

                return false;
            }
        });




        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, inventory.class));
            }

        });
        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(home.this, inbound.class));
            }

        });

        choice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, outbound.class));
            }

        });

        choice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, addproduct.class));
            }

        });

        choice5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,deleteproduct.class));
            }

        });

        choice6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Analytics.class));
            }

        });


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Analytics.class));
            }

        });


    }


}