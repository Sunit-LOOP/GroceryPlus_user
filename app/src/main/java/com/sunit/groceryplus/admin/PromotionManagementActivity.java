package com.sunit.groceryplus.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminPromotionAdapter;
import com.sunit.groceryplus.models.Promotion;

import java.util.ArrayList;
import java.util.List;

public class PromotionManagementActivity extends AppCompatActivity {

    private RecyclerView promotionsRv;
    private FloatingActionButton addPromotionFab;
    private AdminPromotionAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_management);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        
        promotionsRv = findViewById(R.id.promotionsRv);
        addPromotionFab = findViewById(R.id.addPromotionFab);

        setupRecyclerView();
        loadPromotions();

        addPromotionFab.setOnClickListener(v -> showAddPromotionDialog());
    }

    private void setupRecyclerView() {
        promotionsRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminPromotionAdapter(this, new ArrayList<>(), new AdminPromotionAdapter.OnPromotionActionListener() {
            @Override
            public void onDeleteClick(Promotion promotion) {
                deletePromotion(promotion);
            }
        });
        promotionsRv.setAdapter(adapter);
    }

    private void loadPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        Cursor cursor = dbHelper.getAllPromotions();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_PROMO_ID));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_CODE));
                double discount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_DISCOUNT_PERCENTAGE));
                String validUntil = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_VALID_UNTIL));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_IMAGE_URL));
                int active = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_IS_ACTIVE));

                promotions.add(new Promotion(id, code, discount, validUntil, imageUrl, active == 1));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        adapter.updatePromotions(promotions);
    }

    private void showAddPromotionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_promotion, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextInputEditText codeEt = dialogView.findViewById(R.id.promoCodeEt);
        TextInputEditText discountEt = dialogView.findViewById(R.id.promoDiscountEt);
        TextInputEditText validUntilEt = dialogView.findViewById(R.id.promoValidUntilEt);
        TextInputEditText imageUrlEt = dialogView.findViewById(R.id.promoImageUrlEt);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        saveBtn.setOnClickListener(v -> {
            String code = codeEt.getText().toString().trim();
            String discountStr = discountEt.getText().toString().trim();
            String validUntil = validUntilEt.getText().toString().trim();
            String imageUrl = imageUrlEt.getText().toString().trim();

            if (code.isEmpty() || discountStr.isEmpty() || validUntil.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double discount = Double.parseDouble(discountStr);
                long result = dbHelper.addPromotion(code, discount, validUntil, imageUrl);
                if (result != -1) {
                    Toast.makeText(this, "Promotion added", Toast.LENGTH_SHORT).show();
                    loadPromotions();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Error adding promotion", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid discount value", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void deletePromotion(Promotion promotion) {
        if (dbHelper.deletePromotion(promotion.getPromoId())) {
            Toast.makeText(this, "Promotion deleted", Toast.LENGTH_SHORT).show();
            loadPromotions();
        } else {
            Toast.makeText(this, "Error deleting promotion", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
