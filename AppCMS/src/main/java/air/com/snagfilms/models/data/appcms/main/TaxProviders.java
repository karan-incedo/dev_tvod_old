
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxProviders {

    @SerializedName("alavara")
    @Expose
    private Alavara alavara;

    public Alavara getAlavara() {
        return alavara;
    }

    public void setAlavara(Alavara alavara) {
        this.alavara = alavara;
    }

}
