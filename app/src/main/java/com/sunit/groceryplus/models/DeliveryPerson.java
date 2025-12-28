package com.sunit.groceryplus.models;

public class DeliveryPerson {
    private int personId;
    private String name;
    private String phone;
    private String status;

    public DeliveryPerson(int personId, String name, String phone, String status) {
        this.personId = personId;
        this.name = name;
        this.phone = phone;
        this.status = status;
    }

    public int getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }
}
