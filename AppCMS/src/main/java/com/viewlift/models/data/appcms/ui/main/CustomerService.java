package com.viewlift.models.data.appcms.ui.main;

import java.io.Serializable;

/**
 * Created by nitin.tyagi on 9/11/2017.
 */

public class CustomerService implements Serializable {

    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String phone;



}
