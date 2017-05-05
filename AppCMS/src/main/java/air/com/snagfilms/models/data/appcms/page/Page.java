
package air.com.snagfilms.models.data.appcms.page;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Page {

    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("apiURL")
    @Expose
    private String apiURL;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("moduleList")
    @Expose
    private List<ModuleList> moduleList = null;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ModuleList> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<ModuleList> moduleList) {
        this.moduleList = moduleList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
