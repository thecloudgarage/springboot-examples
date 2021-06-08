package com.example.gps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "gpsinventory")
public class Gpsinventory {

    private long id;
    private String gpsterminalId;
    private String customerId;
    private String customerName;
	private double lat;
	private double lon;
    public Gpsinventory() {
  
    }
 
    public Gpsinventory(String gpsterminalId, String customerId, String customerName, double lat, double lon) {
         this.gpsterminalId = gpsterminalId;
         this.customerId = customerId;
         this.customerName = customerName;
		 this.lat = lat;
		 this.lon = lon;
    }
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
 
    @Column(name = "gpsterminalId", nullable = true)
    public String getgpsterminalId() {
        return gpsterminalId;
    }
    public void setgpsterminalId(String gpsterminalId) {
        this.gpsterminalId = gpsterminalId;
    }
 
    @Column(name = "customerId", nullable = true)
    public String getcustomerId() {
        return customerId;
    }
    public void setcustomerId(String customerId) {
        this.customerId = customerId;
    }
 
    @Column(name = "customerName", nullable = true)
    public String getcustomerName() {
        return customerName;
    }
    public void setcustomerName(String customerName) {
        this.customerName = customerName;
    }
	
    @Column(name = "lat", nullable = true)
    public double getlat() {
        return lat;
    }
    public void setlat(double lat) {
        this.lat = lat;
    }

    @Column(name = "lon", nullable = true)
    public double getlon() {
        return lon;
    }
    public void setlon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Gpsinventory [id=" + id + ", gpsterminalId=" + gpsterminalId + ", customerId=" + customerId + ", customerName=" + customerName
       + "]";
    }
 
}
