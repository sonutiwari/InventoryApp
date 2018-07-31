package in.co.chicmic.inventoryapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import in.co.chicmic.inventoryapp.R;
import in.co.chicmic.inventoryapp.dataModel.StockItem;
import in.co.chicmic.inventoryapp.database.InventoryDbHelper;
import in.co.chicmic.inventoryapp.database.StockContract;
import in.co.chicmic.inventoryapp.utilities.AppConstants;


public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private InventoryDbHelper mDbHelper;
    private EditText mNameEdit;
    private EditText mPriceEdit;
    private EditText mQuantityEdit;
    private EditText mSupplierNameEdit;
    private EditText mSupplierPhoneEdit;
    private EditText mSupplierEmailEdit;
    private long mCurrentItemId;
    private Button mImageBtn;
    private ImageView mImageView;
    private Uri mActualUri;
    private Boolean mInfoItemHasChanged = false;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mNameEdit = findViewById(R.id.product_name_edit);
        mPriceEdit = findViewById(R.id.price_edit);
        mQuantityEdit = findViewById(R.id.quantity_edit);
        mSupplierNameEdit = findViewById(R.id.supplier_name_edit);
        mSupplierPhoneEdit = findViewById(R.id.supplier_phone_edit);
        mSupplierEmailEdit = findViewById(R.id.supplier_email_edit);
        mImageBtn = findViewById(R.id.select_image);
        mImageView = findViewById(R.id.image_view);

        mDbHelper = new InventoryDbHelper(this);
        mCurrentItemId = getIntent().getLongExtra("itemId", 0);

        if (mCurrentItemId == 0) {
            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            addValuesToEditItem(mCurrentItemId);
        }

        findViewById(R.id.decrease_quantity).setOnClickListener(this);
        findViewById(R.id.increase_quantity).setOnClickListener(this);
        mImageBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (!mInfoItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog (
            DialogInterface.OnClickListener pDiscardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, pDiscardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void subtractOneToQuantity() {
        String previousValueString = mQuantityEdit.getText().toString();
        int previousValue;
        if ((!(previousValueString.equals("0"))) && (!(previousValueString.isEmpty()))) {
            previousValue = Integer.parseInt(previousValueString);
            mQuantityEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    private void sumOneToQuantity() {
        String previousValueString = mQuantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mQuantityEdit.setText(String.valueOf(previousValue + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        getMenuInflater().inflate(R.menu.menu_details, pMenu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu pMenu) {
        super.onPrepareOptionsMenu(pMenu);
        if (mCurrentItemId == 0) {
            MenuItem deleteOneItemMenuItem = pMenu.findItem(R.id.action_delete_item);
            MenuItem deleteAllMenuItem = pMenu.findItem(R.id.action_delete_all_data);
            MenuItem orderMenuItem = pMenu.findItem(R.id.action_order);
            deleteOneItemMenuItem.setVisible(false);
            deleteAllMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem pItem) {
        switch (pItem.getItemId()) {
            case R.id.action_save:
                // save item in DB
                if (!addItemToDb()) {
                    // saying to onOptionsItemSelected that user clicked button
                    return true;
                }
                finish();
                return true;
            case android.R.id.home:
                if (!mInfoItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_order:
                // dialog with phone and email
                showOrderConfirmationDialog();
                return true;
            case R.id.action_delete_item:
                // delete one item
                showDeleteConfirmationDialog(mCurrentItemId);
                return true;
            case R.id.action_delete_all_data:
                //delete all data
                showDeleteConfirmationDialog(0);
                return true;
        }
        return super.onOptionsItemSelected(pItem);
    }

    private boolean addItemToDb() {
        boolean isAllOk = true;
        if (checkIfValueSet(mNameEdit, getString(R.string.name))) {
            isAllOk = false;
        }
        if (checkIfValueSet(mPriceEdit, getString(R.string.price))) {
            isAllOk = false;
        }
        if (checkIfValueSet(mQuantityEdit, getString(R.string.quantity))) {
            isAllOk = false;
        }
        if (checkIfValueSet(mSupplierNameEdit, getString(R.string.supplier_name))) {
            isAllOk = false;
        }
        if (checkIfValueSet(mSupplierPhoneEdit, getString(R.string.supplier_phone))) {
            isAllOk = false;
        }
        if (checkIfValueSet(mSupplierEmailEdit, getString(R.string.supplier_email))) {
            isAllOk = false;
        }
        if (mActualUri == null && mCurrentItemId == 0) {
            isAllOk = false;
            mImageBtn.setError(getString(R.string.missing_image));
        }
        if (!isAllOk) {
            return false;
        }

        if (mCurrentItemId == 0) {
            StockItem item = new StockItem(
                    mNameEdit.getText().toString().trim(),
                    mPriceEdit.getText().toString().trim(),
                    Integer.parseInt(mQuantityEdit.getText().toString().trim()),
                    mSupplierNameEdit.getText().toString().trim(),
                    mSupplierPhoneEdit.getText().toString().trim(),
                    mSupplierEmailEdit.getText().toString().trim(),
                    mActualUri.toString());
            mDbHelper.insertItem(item);
        } else {
            int quantity = Integer.parseInt(mQuantityEdit.getText().toString().trim());
            mDbHelper.updateItem(mCurrentItemId, quantity);
        }
        return true;
    }

    private boolean checkIfValueSet(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError(getString(R.string.missing_product) + description);
            return true;
        } else {
            text.setError(null);
            return false;
        }
    }

    private void addValuesToEditItem(long itemId) {
        Cursor cursor = mDbHelper.readItem(itemId);
        cursor.moveToFirst();
        mNameEdit.setText(cursor.getString(cursor
                .getColumnIndex(StockContract.StockEntry.COLUMN_NAME)));
        mPriceEdit.setText(cursor.getString(cursor
                .getColumnIndex(StockContract.StockEntry.COLUMN_PRICE)));
        mQuantityEdit.setText(cursor.getString(cursor
                .getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY)));
        mSupplierNameEdit.setText(cursor.getString(cursor
                .getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_NAME)));
        mSupplierPhoneEdit.setText(cursor.getString(cursor
                .getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_PHONE)));
        mSupplierEmailEdit.setText(cursor.getString(cursor
                .getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL)));
        mImageView.setImageURI(Uri.parse(cursor.getString(cursor
                .getColumnIndex(StockContract.StockEntry.COLUMN_IMAGE))));
        mNameEdit.setEnabled(false);
        mPriceEdit.setEnabled(false);
        mSupplierNameEdit.setEnabled(false);
        mSupplierPhoneEdit.setEnabled(false);
        mSupplierEmailEdit.setEnabled(false);
        mImageBtn.setEnabled(false);
    }

    private void showOrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneEdit.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:"
                        + mSupplierEmailEdit.getText().toString().trim()));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.recuurent_new_order));
                String bodyMessage = getString(R.string.message_send_soon) +
                        mNameEdit.getText().toString().trim() +
                        getString(R.string.exclamation_mark);
                intent.putExtra(Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllRowsFromTable() {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        database.delete(StockContract.StockEntry.TABLE_NAME, null, null);
    }

    private void deleteOneItemFromTable(long pItemId) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String selection = StockContract.StockEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(pItemId) };
        database.delete(
                StockContract.StockEntry.TABLE_NAME, selection, selectionArgs);
    }

    private void showDeleteConfirmationDialog(final long pItemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (pItemId == 0) {
                    deleteAllRowsFromTable();
                } else {
                    deleteOneItemFromTable(pItemId);
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent
                , getString(R.string.select_picture)), AppConstants.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int pRequestCode
            , @NonNull String pPermissions[], @NonNull int[] pGrantResults) {
        switch (pRequestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (pGrantResults.length > 0
                        && pGrantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int pRequestCode, int pResultCode, Intent pResultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (pRequestCode == AppConstants.PICK_IMAGE_REQUEST && pResultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "pResultData.getData()"

            if (pResultData != null) {
                mActualUri = pResultData.getData();
                mImageView.setImageURI(mActualUri);
                mImageView.invalidate();
            }
        }
    }

    @Override
    public void onClick(View pView) {
        switch (pView.getId()){
            case R.id.decrease_quantity:
                subtractOneToQuantity();
                mInfoItemHasChanged = true;
                break;
            case R.id.increase_quantity:
                sumOneToQuantity();
                mInfoItemHasChanged = true;
                break;
            case R.id.select_image:
                tryToOpenImageSelector();
                mInfoItemHasChanged = true;
                break;

        }
    }
}
