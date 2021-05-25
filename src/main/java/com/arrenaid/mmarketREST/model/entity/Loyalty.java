package com.arrenaid.mmarketREST.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//import javax.persistence.*;

//@Entity
//@Table(name = "loyalty")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loyalty {
//    @Column(name = "id_loyalty")
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idloyalty;
//    @Column(name = "seller_loyalty")
    private String sellerloyalty;
//    @Column(name = "buyer_loyalty")
    private String buyerloyalty;
//    @Column(name = "total_amount")
    private Double totalamount;
//    @Column(name = "total_value")
    private Double totalvalume;
//    @Column(name = "total_trade")
    private Double totaltrade;

//    public Loyalty(int idloyalty, String sellerloyalty, String buyerloyalty, Double totalamount, Double totalvalume, Double totaltrade) {
//        this.idloyalty = idloyalty;
//        this.sellerloyalty = sellerloyalty;
//        this.buyerloyalty = buyerloyalty;
//        this.totalamount = totalamount;
//        this.totalvalume = totalvalume;
//        this.totaltrade = totaltrade;
//    }
//
    public Loyalty( String sellerloyalty, String buyerloyalty, Double totalamount, Double totalvalume, Double totaltrade) {
        //this.idloyalty = idloyalty;
        this.sellerloyalty = sellerloyalty;
        this.buyerloyalty = buyerloyalty;
        this.totalamount = totalamount;
        this.totalvalume = totalvalume;
        this.totaltrade = totaltrade;
    }
//
//    public Loyalty() {
//
//    }
//
//    public int getIdloyalty() {
//        return idloyalty;
//    }
//
//    public void setIdloyalty(int idloyalty) {
//        this.idloyalty = idloyalty;
//    }
//
//    public String getSellerloyalty() {
//        return sellerloyalty;
//    }
//
//    public void setSellerloyalty(String sellerloyalty) {
//        this.sellerloyalty = sellerloyalty;
//    }
//
//    public String getBuyerloyalty() {
//        return buyerloyalty;
//    }
//
//    public void setBuyerloyalty(String buyerloyalty) {
//        this.buyerloyalty = buyerloyalty;
//    }
//
//    public Double getTotalamount() {
//        return totalamount;
//    }
//
//    public void setTotalamount(Double totalamount) {
//        this.totalamount = totalamount;
//    }
//
//    public Double getTotalvalume() {
//        return totalvalume;
//    }
//
//    public void setTotalvalume(Double totalvalume) {
//        this.totalvalume = totalvalume;
//    }
//
//    public Double getTotaltrade() {
//        return totaltrade;
//    }
//
//    public void setTotaltrade(Double totaltrade) {
//        this.totaltrade = totaltrade;
//    }
}
