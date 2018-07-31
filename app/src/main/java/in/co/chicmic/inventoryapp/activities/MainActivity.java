package in.co.chicmic.inventoryapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;

import in.co.chicmic.inventoryapp.R;
import in.co.chicmic.inventoryapp.adapters.StockCursorAdapter;
import in.co.chicmic.inventoryapp.database.InventoryDbHelper;
import in.co.chicmic.inventoryapp.dataModel.StockItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private InventoryDbHelper mDbHelper;
    private StockCursorAdapter mAdapter;
    private int mLastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new InventoryDbHelper(this);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);


        final ListView listView = findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        Cursor cursor = mDbHelper.readStock();

        mAdapter = new StockCursorAdapter(this, cursor);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView pView, int pScrollState) {
                if(pScrollState == 0) return;
                    final int currentFirstVisibleItem = pView.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastVisibleItem) {
                        fab.show();
                    } else if (currentFirstVisibleItem < mLastVisibleItem) {
                        fab.hide();
                    }
                mLastVisibleItem = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView pView, int pFirstVisibleItem
                    , int pVisibleItemCount, int pTotalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.swapCursor(mDbHelper.readStock());
    }

    public void clickOnViewItem(long pId) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getString(R.string.item_id), pId);
        startActivity(intent);
    }

    public void clickOnSale(long pId, int pQuantity) {
        mDbHelper.sellOneItem(pId, pQuantity);
        mAdapter.swapCursor(mDbHelper.readStock());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        getMenuInflater().inflate(R.menu.menu_main, pMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem pItem) {
        switch (pItem.getItemId()) {
            case R.id.action_add_dummy_data:
                // add dummy data for testing
                addDummyData();
                mAdapter.swapCursor(mDbHelper.readStock());
        }
        return super.onOptionsItemSelected(pItem);
    }


    private void addDummyData() {
        String string = "\u20B9";
        try{
            byte[] utf8;
            utf8 = string.getBytes("UTF-8");
            string = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            string = "";
        }
        StockItem gummibears = new StockItem(
            "Gummibears",
                "10 "+ string,
                1,
                "Sonu Tiwari",
                "+91 000 000 0000",
                "sonu.tiwari@chicmic.co.in",
                "android.resource://in.co.chicmic.inventoryapp/drawable/gummibear");
        mDbHelper.insertItem(gummibears);

        StockItem peaches = new StockItem(
                "Peaches",
                "10 "+ string,
                2,
                "Sonu Tiwari",
                "+91 000 000 0000",
                "sonu.tiwari@chicmic.co.in",
                "android.resource://in.co.chicmic.inventoryapp/drawable/peach");
        mDbHelper.insertItem(peaches);

        StockItem cherries = new StockItem(
                "Cherries",
                "11 "+ string,
                3,
                "Sonu Tiwari",
                "+91 000 000 0000",
                "sonu.tiwari@chicmic.co.in",
                "android.resource://in.co.chicmic.inventoryapp/drawable/cherry");
        mDbHelper.insertItem(cherries);

        StockItem cola = new StockItem(
                "Cola",
                "13 "+ string,
                4,
                "Sonu Tiwari",
                "+91 000 000 0000",
                "sonu.tiwari@chicmic.co.in",
                "android.resource://in.co.chicmic.inventoryapp/drawable/cola");
        mDbHelper.insertItem(cola);

        StockItem fruitSalad = new StockItem(
                "Fruit salad",
                "20 "+ string,
                5,
                "Sonu Tiwari",
                "+91 000 000 0000",
                "sonu.tiwari@chicmic.co.in",
                "android.resource://in.co.chicmic.inventoryapp/drawable/fruit_salad");
        mDbHelper.insertItem(fruitSalad);

        StockItem smurfs = new StockItem(
                "Smurfs",
                "12 "+ string,
                6,
                "Sonu Tiwari",
                "+91 000 000 0000",
                "sonu.tiwari@chicmic.co.in",
                "android.resource://in.co.chicmic.inventoryapp/drawable/smurfs");
        mDbHelper.insertItem(smurfs);

        StockItem fresquito = new StockItem(
                "Fresquito",
                "9 "+ string,
                7,
                "Fiesta S.A.",
                "+91 000 000 0000",
                "abc@example.com",
                "android.resource://in.co.chicmic.inventoryapp/drawable/fresquito");
        mDbHelper.insertItem(fresquito);

        StockItem hotChillies = new StockItem(
                "Hot chillies",
                "13 "+ string,
                8,
                "Fiesta S.A.",
                "+91 000 000 0000",
                "abc@example.com",
                "android.resource://in.co.chicmic.inventoryapp/drawable/hot_chillies");
        mDbHelper.insertItem(hotChillies);

        StockItem lolipopStrawberry = new StockItem(
                "Lolipop strawberry",
                "12 "+ string,
                9,
                "Fiesta S.A.",
                "+91 000 000 0000",
                "abc@example.com",
                "android.resource://in.co.chicmic.inventoryapp/drawable/lolipop");
        mDbHelper.insertItem(lolipopStrawberry);

        StockItem heartGummy = new StockItem(
                "Heart gummy jellies",
                "10 "+ string,
                10,
                "Fiesta S.A.",
                "+91 000 000 0000",
                "abc@example.com",
                "android.resource://in.co.chicmic.inventoryapp/drawable/heart_gummy");
        mDbHelper.insertItem(heartGummy);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
        }
    }
}
