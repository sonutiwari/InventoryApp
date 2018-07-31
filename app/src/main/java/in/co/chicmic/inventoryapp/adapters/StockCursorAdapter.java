package in.co.chicmic.inventoryapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.chicmic.inventoryapp.R;
import in.co.chicmic.inventoryapp.activities.MainActivity;
import in.co.chicmic.inventoryapp.database.StockContract;

public class StockCursorAdapter extends CursorAdapter {

    private final MainActivity mMainActivity;

    public StockCursorAdapter(MainActivity pContext, Cursor pCursor) {
        super(pContext, pCursor, 0);
        this.mMainActivity = pContext;
    }

    @Override
    public View newView(Context pContext, Cursor pCursor, ViewGroup pViewGroup) {
        return LayoutInflater.from(pContext)
                .inflate(R.layout.list_item, pViewGroup, false);
    }

    @Override
    public void bindView(View pView, final Context pContext, final Cursor pCursor) {
        TextView nameTextView = pView.findViewById(R.id.product_name);
        TextView quantityTextView = pView.findViewById(R.id.quantity);
        TextView priceTextView = pView.findViewById(R.id.price);
        ImageView sale = pView.findViewById(R.id.sale);
        ImageView image = pView.findViewById(R.id.image_view);

        String name = pCursor
                .getString(pCursor.getColumnIndex(StockContract.StockEntry.COLUMN_NAME));
        final int quantity = pCursor
                .getInt(pCursor.getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY));
        String price = pCursor
                .getString(pCursor.getColumnIndex(StockContract.StockEntry.COLUMN_PRICE));
        image.setImageURI(Uri.parse(pCursor
                .getString(pCursor.getColumnIndex(StockContract.StockEntry.COLUMN_IMAGE))));

        nameTextView.setText(name);
        quantityTextView.setText(String.valueOf(quantity));
        priceTextView.setText(price);

        final long id = pCursor.getLong(pCursor.getColumnIndex(StockContract.StockEntry._ID));

        pView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.clickOnViewItem(id);
            }
        });

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.clickOnSale(id,
                        quantity);
            }
        });
    }
}
