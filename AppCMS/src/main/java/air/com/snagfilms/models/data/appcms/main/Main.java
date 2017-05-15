
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {
    private String oldVersion;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("main")
    @Expose
    private Main_ main;

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Main_ getMain() {
        return main;
    }

    public void setMain(Main_ main) {
        this.main = main;
    }

}
