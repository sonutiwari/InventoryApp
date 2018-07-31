package in.co.chicmic.inventoryapp.dataModel;

public class StockItem {

    private final String mProductName;
    private final String mPrice;
    private final int mQuantity;
    private final String mSupplierName;
    private final String mSupplierPhone;
    private final String mSupplierEmail;
    private final String mImage;

    public StockItem(String pProductName, String pPrice, int pQuantity, String pSupplierName
            , String pSupplierPhone, String pSupplierEmail, String pImage) {
        this.mProductName = pProductName;
        this.mPrice = pPrice;
        this.mQuantity = pQuantity;
        this.mSupplierName = pSupplierName;
        this.mSupplierPhone = pSupplierPhone;
        this.mSupplierEmail = pSupplierEmail;
        this.mImage = pImage;
    }

    public String getProductName() {
        return mProductName;
    }

    public String getPrice() {
        return mPrice;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public String getSupplierName() {
        return mSupplierName;
    }

    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    public String getSupplierEmail() {
        return mSupplierEmail;
    }

    public String getImage() {
        return mImage;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "mProductName='" + mProductName + '\'' +
                ", mPrice='" + mPrice + '\'' +
                ", mQuantity=" + mQuantity +
                ", mSupplierName='" + mSupplierName + '\'' +
                ", mSupplierPhone='" + mSupplierPhone + '\'' +
                ", mSupplierEmail='" + mSupplierEmail + '\'' +
                ", mImage='" + mImage + '\'' +
                '}';
    }
}
