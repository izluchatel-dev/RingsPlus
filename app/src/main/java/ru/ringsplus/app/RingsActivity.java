package ru.ringsplus.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.firebase.FireBaseConnnection;
import ru.ringsplus.app.firebase.FireBaseRings;
import ru.ringsplus.app.model.RingItem;

public class RingsActivity extends AppCompatActivity implements RingsViewAdapter.DeleteClickListener {

    private RecyclerView mRingsRecyclerView;
    private FloatingActionButton mAddRingItemButton;

    private FireBaseRings mFireBaseRings;

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
        setContentView(R.layout.activity_rings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mRingsRecyclerView = findViewById(R.id.stockList);
        mRingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

                mFireBaseRings.addRingItem(this, addRingItem);
            });

            dialogBuilder.setNegativeButton(R.string.add_dialog_cancel, (dialog, which) -> dialog.cancel());

            dialogBuilder.show();
        });

        FireBaseConnnection.setConnectedChecker(this, true);
        mFireBaseRings = new FireBaseRings(mRingsRecyclerView, this);
    }


    @Override
    public void onDeleteButtonClick(View view, int position) {
        RingsViewAdapter ringsViewAdapter = (RingsViewAdapter) mRingsRecyclerView.getAdapter();
        RingItem mDeleteRingItem = ringsViewAdapter.getItem(position);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        String removeQuestionMsg = String.format(getString(R.string.remove_item_dialog_title), mDeleteRingItem.getName());
        dialogBuilder.setTitle(removeQuestionMsg);

        dialogBuilder.setPositiveButton(R.string.item_dialog_yes, (dialog, which) -> {
            mFireBaseRings.deleteRingItem(this, mDeleteRingItem);
        });

        dialogBuilder.setNegativeButton(R.string.item_dialog_cancel, (dialog, which) -> dialog.cancel());

        dialogBuilder.show();
    }

}