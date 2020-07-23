package ru.ringsplus.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.StockCollection;

import static ru.ringsplus.app.model.StockCollection.FIREBASE_CONNECTION_INFO;
import static ru.ringsplus.app.model.StockCollection.FIREBASE_RINGS_PATH;

public class StockActivity extends AppCompatActivity implements StockViewAdapter.DeleteClickListener {

    private StockViewAdapter mStockViewAdapter;
    private Toolbar mToolbar;
    private RecyclerView recyclerStock;
    private FloatingActionButton mAddRingItemButton;

    private ProgressBar progressBar;

    private DatabaseReference mRingsReference;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock2);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerStock = findViewById(R.id.stockList);
        recyclerStock.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(FIREBASE_CONNECTION_INFO);
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    showProgressBar(false);
                } else {
                    showProgressBar(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRingsReference = FirebaseDatabase.getInstance().getReference(FIREBASE_RINGS_PATH);
        mRingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StockCollection.getInstance().onRingsCollectionDataChange(dataSnapshot);

                mStockViewAdapter.notifyDataSetChanged();

                showProgressBar(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showProgressBar(false);
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        mAddRingItemButton = findViewById(R.id.add_ring_item);
        mAddRingItemButton.setOnClickListener(view -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.add_item_dialog_title);

            final EditText inputEdit = new EditText(this);
            inputEdit.setInputType(InputType.TYPE_CLASS_TEXT);
            dialogBuilder.setView(inputEdit);

            dialogBuilder.setPositiveButton(R.string.add_dialog_ok, (dialog, which) -> {
                String addRingName = inputEdit.getText().toString().trim();

                RingItem addRingItem = new RingItem(UUID.randomUUID().toString(), addRingName);

                showProgressBar(true);

                mRingsReference.child(addRingItem.getId()).setValue(addRingItem, (error, ref) -> {
                    if (error == null) {
                        showProgressBar(false);

                        String mStatusMsg = String.format(getString(R.string.add_item_ballon), addRingItem.getName());

                        Toast.makeText(getBaseContext(), mStatusMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });

            dialogBuilder.setNegativeButton(R.string.add_dialog_cancel, (dialog, which) -> dialog.cancel());

            dialogBuilder.show();
        });
    }

    @Override
    public void onDeleteButtonClick(View view, int position) {
        RingItem mDeleteRingItem = mStockViewAdapter.getItem(position);

        showProgressBar(true);

        mRingsReference.child(mDeleteRingItem.getId()).removeValue((error, ref) -> {
            if (error == null) {
                showProgressBar(false);

                String mStatusMsg = String.format(getString(R.string.delete_item_ballon), mDeleteRingItem.getName());

                Toast.makeText(getBaseContext(), mStatusMsg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mStockViewAdapter = new StockViewAdapter(this, StockCollection.getInstance().getRingItems());
        mStockViewAdapter.setDeleteClickListener(this);
        recyclerStock.setAdapter(mStockViewAdapter);
    }

    private void showProgressBar(Boolean visible) {

        if (visible) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerStock.setVisibility(View.GONE);
            mAddRingItemButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerStock.setVisibility(View.VISIBLE);
            mAddRingItemButton.setVisibility(View.VISIBLE);
        }

    }

}