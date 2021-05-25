package com.arrenaid.mmarketREST.model.entity;

import com.arrenaid.mmarketREST.model.Auction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

//@Entity
//@Table(name = "trade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

//    @Column(name = "id_trade")
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTrade;
//    @Column(name = "seller_name")
    private String sellerName;
//    @Column(name = "buyer_name")
    private String buyerName;
//    @Column
    private double volume;
//    @Column
    private double price;
//    @Column(name = "trade_date")
    private Date dt;
//    @Column(name = "trade_time")
    private Time time;
//    @Column
    private Auction auction;

//    //для нового класса
    public Trade(String sellerName, String buyerName, double volume, double price,Auction auction ) {
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.volume = volume;
        this.price = price;
        this.dt = new Date(Calendar.getInstance().getTime().getTime());
        this.time =  new Time(Calendar.getInstance().getTime().getTime());
        this.auction = auction;
    }
//    //Для существующего
//    public Trade(int idTrade, String sellerName, String buyerName, double volume, double price,Date dt,Time time) {
//        this.sellerName = sellerName;
//        this.buyerName = buyerName;
//        this.volume = volume;
//        this.price = price;
//        this.dt = dt; //Date dt = new Date(Calendar.getInstance().getTime().getTime());
//        this.time = time;// new Time(Calendar.getInstance().getTime().getTime());
//        this.idTrade = idTrade;
//    }
//    public int getIdTrade() {
//        return idTrade;
//    }
//
//    public void setIdTrade(int idTrade) {
//        this.idTrade = idTrade;
//    }
//    public String getSellerName() {
//        return sellerName;
//    }
//
//    public void setSellerName(String sellerName) {
//        this.sellerName = sellerName;
//    }
//
//    public String getBuyerName() {
//        return buyerName;
//    }
//
//    public void setBuyerName(String buyerName) {
//        this.buyerName = buyerName;
//    }
//
//    public double getVolume() {
//        return volume;
//    }
//
//    public void setVolume(double volume) {
//        this.volume = volume;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public Date getDt() {
//        return dt;
//    }
//
//    public void setDt(Date dt) {
//        this.dt = dt;
//    }
//
//    public String getAuction() {
//        return auction;
//    }
//
//    public void setAuction(String auction) {
//        this.auction = auction;
//    }
//
//    public Time getTime() {
//        return time;
//    }
//
//    public void setTime(Time time) {
//        this.time = time;
//    }
}
