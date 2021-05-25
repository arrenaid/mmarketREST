//package com.arrenaid.mmarketREST.model.entity;
//
//import sample.Controller;
//
//import java.sql.*;
//
//public class DatabaseHandler extends Configs{
//    Connection dbConnection;
//
//    public Connection getDbConnection() throws ClassNotFoundException, SQLException{
//        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?serverTimezone=UTC";
//        //Class.forName("com.mysql.jdbc.Driver");
//        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
//        return dbConnection;
//    }
//
//    public void singUpGrid(String name, double maxVolume, double currentVoleme, double maxCost, double minCost){
//        String insert = "INSERT INTO "+ Const.GRID_TABLE + "(" +Const.GRID_NAME +"," + Const.GRID_MAXVOLUME
//                +"," + Const.GRID_CURRENTVOLUME +"," + Const.GRID_COST+"," + Const.GRID_MINCOST+"," + Const.GRID_STATUS+")"
//                +"VALUES(?,?,?,?,?,?)";
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            ps.setString(1,name);
//            ps.setDouble(2,maxVolume);
//            ps.setDouble(3,currentVoleme);
//            ps.setDouble(4,maxCost);
//            ps.setDouble(5,minCost);
//            ps.setBoolean(6,false);
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public ResultSet getGrid(Grid grid){
//        ResultSet resSet = null;
//        String insert = "SELECT * FROM "+ Const.GRID_TABLE + " WHERE " +Const.GRID_NAME +"=?";
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            ps.setString(1,grid.getName());
//            resSet = ps.executeQuery();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return resSet;
//    }
//    public void getAllGrid(){
//        ResultSet resSet = null;
//        String insert = "SELECT * FROM "+ Const.GRID_TABLE ;
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            resSet = ps.executeQuery();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        int counters = 0;
//        try {
//            while (resSet.next()) {
//                int id = resSet.getInt(1);
//                String name = resSet.getString(2);
//                double maxVolume = resSet.getDouble(3);
//                double currentVolume = resSet.getDouble(4);
//                double maxCost = resSet.getDouble(5);
//                double minCost = resSet.getDouble(6);
//                Grid grid = new Grid(name,maxVolume,currentVolume,maxCost,minCost,false,"unknown");
//                if(currentVolume >= (maxVolume/2))
//                    grid.setRole(Const.ROLE_SELLER);
//                else
//                    grid.setRole(Const.ROLE_BUYER);
//                Controller.waitGridsList.add(grid);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    public void gridUpdate(Grid grid){
//        String insert = "UPDATE "+ Const.GRID_TABLE + " SET " + Const.GRID_MAXVOLUME
//                +"=?, " + Const.GRID_CURRENTVOLUME +"=?, " + Const.GRID_COST+"=?, "+ Const.GRID_MINCOST+"=?, " + Const.GRID_STATUS+"=? WHERE "
//                +Const.GRID_NAME  + "=?";
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            ps.setDouble(1,grid.getMaxVolume());
//            ps.setDouble(2,grid.getCurrentVolume());
//            ps.setDouble(3,grid.getMaxCost());
//            ps.setDouble(4,grid.getMinCost());
//            ps.setBoolean(5,grid.isStatus());
//            ps.setString(6,grid.getName());
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //__________________________TRADE____________________________
//
//    public void addTrade(String sellerName, String buyerName, double volume, double price, Date dt){
//        String insert = "INSERT INTO "+ Const.TRADE_TABLE + "(" +Const.TRADE_SELLER +"," + Const.TRADE_BUYER
//                +"," + Const.TRADE_VOLUME +"," + Const.TRADE_PRICE+"," + Const.TRADE_DATE+")"
//                +"VALUES(?,?,?,?,?)";
//
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            ps.setString(1,sellerName);
//            ps.setString(2,buyerName);
//            ps.setDouble(3,volume);
//            ps.setDouble(4,price);
//            ps.setDate(5,dt);
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public void addTrade(Trade trade){
//        String insert = "INSERT INTO "+ Const.TRADE_TABLE + "(" +Const.TRADE_SELLER +"," + Const.TRADE_BUYER
//                +"," + Const.TRADE_VOLUME +"," + Const.TRADE_PRICE+"," + Const.TRADE_DATE+"," + Const.TRADE_TIME+")"
//                +"VALUES(?,?,?,?,?,?)";
//
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            ps.setString(1,trade.getSellerName());
//            ps.setString(2,trade.getBuyerName());
//            ps.setDouble(3,trade.getVolume());
//            ps.setDouble(4,trade.getPrice());
//            ps.setDate(5,trade.getDt());
//            ps.setTime(6,trade.getTime());
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public void tradeUpdate(Trade trade){
//        String insert = "UPDATE "+ Const.TRADE_TABLE + " SET " + Const.TRADE_SELLER
//                +"=?, " + Const.TRADE_BUYER +"=?, " + Const.TRADE_VOLUME+"=?, " + Const.TRADE_PRICE
//                +"=?, " + Const.TRADE_DATE+"=?, " + Const.TRADE_TIME +"=? WHERE " +Const.TRADE_ID  + "=?";
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            ps.setString(1,trade.getSellerName());
//            ps.setString(2,trade.getBuyerName());
//            ps.setDouble(3,trade.getVolume());
//            ps.setDouble(4,trade.getPrice());
//            ps.setDate(5,trade.getDt());
//            ps.setTime(6,trade.getTime());
//            ps.setInt(7,trade.getIdTrade());
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public void getAllTrade(){
//        ResultSet resSet = null;
//        String insert = "SELECT * FROM "+ Const.TRADE_TABLE ;
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            resSet = ps.executeQuery();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        int counters = 0;
//        try {
//            while (resSet.next()) {
//                int idTrade = resSet.getInt(1);
//                String seller = resSet.getString(2);
//                String buyer = resSet.getString(3);
//                double volume = resSet.getDouble(4);
//                double price = resSet.getDouble(5);
//                Date dt = resSet.getDate(6);
//                Time time = resSet.getTime(7);
//                System.out.println( idTrade+ "\t" + seller+ "\t" + buyer + "\t" + volume + "\t" + price + "\t" + dt);
//                Trade trade = new Trade(idTrade,seller,buyer,volume,price,dt,time);
//                Controller.tradesList.add(trade);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    //-----------------------------------------Loyalty
//    public void addLoyalty(Loyalty loyalty){
//        String insert = "INSERT INTO "+ Const.LOYALTY_TABLE + "(" +Const.LOYALTY_SELLER +"," + Const.LOYALTY_BUYER
//                +"," + Const.LOYALTY_TOTALAMOUNT +"," + Const.LOYALTY_TOTALVALUE+"," + Const.LOYALTY_TOTALTRADE+")"
//                +"VALUES(?,?,?,?,?)";
//
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            ps.setString(1,loyalty.getSellerloyalty());
//            ps.setString(2,loyalty.getBuyerloyalty());
//            ps.setDouble(3,loyalty.getTotalamount());
//            ps.setDouble(4,loyalty.getTotalvalume());
//            ps.setDouble(5,loyalty.getTotaltrade());
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public void loyaltyUpdate(Loyalty loyalty){
//        String insert = "UPDATE "+ Const.LOYALTY_TABLE + " SET " + Const.LOYALTY_SELLER
//                +"=?, " + Const.LOYALTY_BUYER +"=?, " + Const.LOYALTY_TOTALAMOUNT+"=?, " + Const.LOYALTY_TOTALVALUE
//                +"=?, " + Const.LOYALTY_TOTALTRADE+"=? WHERE " +Const.LOYALTY_ID  + "=?";
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            ps.setString(1,loyalty.getSellerloyalty());
//            ps.setString(2,loyalty.getBuyerloyalty());
//            ps.setDouble(3,loyalty.getTotalamount());
//            ps.setDouble(4,loyalty.getTotalvalume());
//            ps.setDouble(5,loyalty.getTotaltrade());
//            ps.setInt(6,loyalty.getIdloyalty());
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public void getAllLoyalty(){
//        ResultSet resSet = null;
//        String insert = "SELECT * FROM "+ Const.LOYALTY_TABLE ;
//        try {
//            PreparedStatement ps = getDbConnection().prepareStatement(insert);
//            resSet = ps.executeQuery();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            while (resSet.next()) {
//                int idLoyalty = resSet.getInt(1);
//                String seller = resSet.getString(2);
//                String buyer = resSet.getString(3);
//                double tAmount = resSet.getDouble(4);
//                double tVolume = resSet.getDouble(5);
//                double tTrade = resSet.getDouble(6);
//                System.out.println( idLoyalty+ "\t" + seller+ "\t" + buyer + "\t" + tAmount + "\t" + tVolume + "\t" + tTrade);
//                Loyalty loyalty = new Loyalty(idLoyalty,seller,buyer,tAmount,tVolume,tTrade);
//                Controller.loyalties.add(loyalty);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//}
