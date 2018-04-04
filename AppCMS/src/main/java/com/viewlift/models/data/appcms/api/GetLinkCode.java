package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class GetLinkCode {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("activationCode")
    @Expose
    private String activationCode;

    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;

    @SerializedName("code")
    @Expose
    private String code;

    public String getErrorMessage ()
    {
        return errorMessage;
    }

    public void setErrorMessage (String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getActivationCode ()
    {
        return activationCode;
    }

    public void setActivationCode (String activationCode)
    {
        this.activationCode = activationCode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status = "+status+", activationCode = "+activationCode + " [errorMessage = "+errorMessage+", status = "+status+", code = "+code+"]";
    }
}
