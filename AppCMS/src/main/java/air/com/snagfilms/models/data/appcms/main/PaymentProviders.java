
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentProviders {

    @SerializedName("amazon")
    @Expose
    private Amazon amazon;
    @SerializedName("payPal")
    @Expose
    private PayPal payPal;
    @SerializedName("stripe")
    @Expose
    private Stripe stripe;

    public Amazon getAmazon() {
        return amazon;
    }

    public void setAmazon(Amazon amazon) {
        this.amazon = amazon;
    }

    public PayPal getPayPal() {
        return payPal;
    }

    public void setPayPal(PayPal payPal) {
        this.payPal = payPal;
    }

    public Stripe getStripe() {
        return stripe;
    }

    public void setStripe(Stripe stripe) {
        this.stripe = stripe;
    }

}
