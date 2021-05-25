package com.arrenaid.mmarketREST.model.entity;

//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.DoubleProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;

import com.arrenaid.mmarketREST.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//import javax.persistence.*;

//@Entity
//@Table(name = "grid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grid {
//    @Column(name = "id_grid")
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idGrid;
//    @Column
    private String name;
//    @Column(name = "max_volume")
    private double maxVolume;
//    @Column(name = "current_volume")
    private double currentVolume;
//    @Column
    private double cost;
//    @Column
    private boolean status;
//    @Column
    private Role role;
//    @Column(name = "min_cost")
    private double minCost;
//    @Column(name = "max_cost")
    private double maxCost;

    public Grid(String name, double maxVolume, double currentVolume, double maxCost, double minCost,boolean status, String role) {
        this.name = name;
        this.maxVolume = maxVolume;
        this.currentVolume = currentVolume;
        //this.cost = cost;
        this.status = status;
        this.maxCost = maxCost;
        this.minCost = minCost;
        if(currentVolume >= (maxVolume/2)) {
            this.role = Role.SELLER;
            this.cost = minCost;
        }
        else {
            this.role = Role.BUYER;
            this.cost = maxCost;
        }
    }
//
//    public Grid() {
//
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public double getMaxVolume() {
//        return maxVolume;
//    }
//
//    public void setMaxVolume(double maxVolume) {
//        this.maxVolume = maxVolume;
//    }
//
//    public double getCurrentVolume() {
//        return currentVolume;
//    }
//
//    public void setCurrentVolume(double currentVolume) {
//        this.currentVolume = currentVolume;
//    }
//
//    public double getCost() {
//        return cost;
//    }
//
//    public void setCost(double cost) {
//        this.cost = cost;
//    }
//    public void setStatus(boolean status) {
//        this.status = status;
//    }
//    public boolean isStatus() {
//        return status;
//    }
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
//
//    public double getMinCost() {
//        return minCost;
//    }
//
//    public void setMinCost(double minCost) {
//        this.minCost = minCost;
//    }
//
//    public double getMaxCost() {
//        return maxCost;
//    }
//
//    public void setMaxCost(double maxCost) {
//        this.maxCost = maxCost;
//    }
    ////////////
//    private  StringProperty name;
//    private  DoubleProperty maxVolume;
//    private  DoubleProperty currentVolume;
//    private  DoubleProperty cost;
//    private  BooleanProperty status;
//
//    public Grid(StringProperty name, DoubleProperty maxVolume, DoubleProperty currentVolume, DoubleProperty cost, BooleanProperty status) {
//        this.name = name;
//        this.maxVolume = maxVolume;
//        this.currentVolume = currentVolume;
//        this.cost = cost;
//        this.status = status;
//    }
//
//    public String getName() {
//        return name.get();
//    }
//
//    public StringProperty nameProperty() {
//        if (name == null) name = new SimpleStringProperty(this, "name");
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name.set(name);
//    }
//
//    public double getMaxVolume() {
//        return maxVolume.get();
//    }
//
//    public DoubleProperty maxVolumeProperty() {
//        return maxVolume;
//    }
//
//    public void setMaxVolume(double maxVolume) {
//        this.maxVolume.set(maxVolume);
//    }
//
//    public double getCurrentVolume() {
//        return currentVolume.get();
//    }
//
//    public DoubleProperty currentVolumeProperty() {
//        return currentVolume;
//    }
//
//    public void setCurrentVolume(double currentVolume) {
//        this.currentVolume.set(currentVolume);
//    }
//
//    public double getCost() {
//        return cost.get();
//    }
//
//    public DoubleProperty costProperty() {
//        return cost;
//    }
//
//    public void setCost(double cost) {
//        this.cost.set(cost);
//    }
//
//    public boolean isStatus() {
//        return status.get();
//    }
//
//    public BooleanProperty statusProperty() {
//        return status;
//    }
//
//    public void setStatus(boolean status) {
//        this.status.set(status);
//    }


}
