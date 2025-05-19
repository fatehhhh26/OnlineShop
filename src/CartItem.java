import java.text.NumberFormat;
import java.util.Locale;

class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    public String getFormattedSubtotal() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return currencyFormat.format(getSubtotal());
    }

    @Override
    public String toString() {
        return String.format("%s x%d = %s", product.getName(), quantity, getFormattedSubtotal());
    }
}