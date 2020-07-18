package ru.ringsplus.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.StockCollection;

public class StockActivity extends AppCompatActivity implements StockViewAdapter.DeleteClickListener {

    private StockViewAdapter mStockViewAdapter;
    private Toolbar mToolbar;
    private RecyclerView recyclerStock;
    private FloatingActionButton mAddRingItemButton;

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

        mAddRingItemButton = findViewById(R.id.add_ring_item);
        mAddRingItemButton.setOnClickListener(view -> {
            final String[] addRingName = new String[1];

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.add_item_dialog_title);

            final EditText inputEdit = new EditText(this);
            inputEdit.setInputType(InputType.TYPE_CLASS_TEXT);
            dialogBuilder.setView(inputEdit);

            dialogBuilder.setPositiveButton(R.string.add_dialog_ok, (dialog, which) -> {
                addRingName[0] = inputEdit.getText().toString();

                AddRingItemTask addItemTask = new AddRingItemTask(this);
                addItemTask.execute(addRingName[0]);
            });

            dialogBuilder.setNegativeButton(R.string.add_dialog_cancel, (dialog, which) -> dialog.cancel());

            dialogBuilder.show();
        });
    }

    @Override
    public void onDeleteButtonClick(View view, int position) {
        RingItem mDeleteRingItem = mStockViewAdapter.getItem(position);

        DeleteRingItemTask deleteItemTask = new DeleteRingItemTask(this);
        deleteItemTask.execute(mDeleteRingItem);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mStockViewAdapter = new StockViewAdapter(this, StockCollection.getInstance().getRingItems());
        mStockViewAdapter.setDeleteClickListener(this);
        recyclerStock.setAdapter(mStockViewAdapter);
    }

    class DeleteRingItemTask extends AsyncTask<RingItem, Void, String> {

        private ProgressDialog dialog;
        private Activity mParentActivity;

        public DeleteRingItemTask(StockActivity activity) {
            mParentActivity = activity;

            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage(getString(R.string.delete_item_wait));
            dialog.show();
        }

        @Override
        protected String doInBackground(RingItem... ringItems) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String mStatusMsg =  String.format(getString(R.string.delete_item_ballon), ringItems[0].getName());

            try {
                StockCollection.getInstance().getRingItems().remove(ringItems[0]);
            } catch (Exception e) {
                mStatusMsg = String.format(getString(R.string.delete_item_err), e.getMessage());
            }

            return mStatusMsg;
        }

        @Override
        protected void onPostExecute(String statusMsg) {
            super.onPostExecute(statusMsg);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            mStockViewAdapter.notifyDataSetChanged();

            Toast.makeText(mParentActivity, statusMsg, Toast.LENGTH_SHORT).show();
        }
    }

    class AddRingItemTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        private Activity mParentActivity;

        public AddRingItemTask(StockActivity activity) {
            mParentActivity = activity;

            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage(getString(R.string.add_item_wait));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... ringNames) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String mStatusMsg =  String.format(getString(R.string.add_item_ballon), ringNames[0]);

            try {
                StockCollection.getInstance().getRingItems().add(new RingItem(ringNames[0]));
            } catch (Exception e) {
                mStatusMsg = String.format(getString(R.string.add_item_err), e.getMessage());
            }

            return mStatusMsg;
        }

        @Override
        protected void onPostExecute(String statusMsg) {
            super.onPostExecute(statusMsg);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            mStockViewAdapter.notifyItemInserted(StockCollection.getInstance().getRingItems().size());

            recyclerStock.scrollToPosition(mStockViewAdapter.getItemCount() - 1);

            Toast.makeText(mParentActivity, statusMsg, Toast.LENGTH_SHORT).show();
        }
    }


}