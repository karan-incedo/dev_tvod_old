
package air.com.snagfilms.models.data.appcms.main;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("categories")
    @Expose
    private List<String> categories = null;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
