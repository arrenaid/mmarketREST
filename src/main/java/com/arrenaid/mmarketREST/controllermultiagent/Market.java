package com.arrenaid.mmarketREST.controllermultiagent;

import com.arrenaid.mmarketREST.model.Auction;
import com.arrenaid.mmarketREST.model.entity.Grid;
import com.arrenaid.mmarketREST.model.entity.Loyalty;
import com.arrenaid.mmarketREST.model.entity.Trade;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

@Getter
@Setter
public class Market {
    static public LinkedList<Grid> gridsList;
    //static public ArrayList<Grid> waitGridsList;
    //static public ArrayList<Grid> oldGridsList;
    static public LinkedList<Trade> tradesList;
//    static public LinkedList<Trade> tradesListAll;
    static public LinkedList<Loyalty> loyalties;

    @Getter
    @Setter
    static private int countBuyer = 0;
    @Getter
    @Setter
    static private int countSeller = 0;
    @Getter
    @Setter
    static private Auction auctionUse = Auction.SECOND;

    public static Grid findGridInGridList(String name){
        Iterator<Grid> iterator = gridsList.iterator();
        while(iterator.hasNext()){
            Grid result = iterator.next();
            if(result.getName().equals(name))
                return result;
        }
        return null;
    }

    public static void initMarket() {
        gridsList = new LinkedList<>();
        tradesList = new LinkedList<>();
        loyalties = new LinkedList<>();
    }

    public static boolean removeGrid(String name) {
        Iterator<Grid> iterator = Market.gridsList.iterator();
        while (iterator.hasNext()){
            if(iterator.next().getName().equals(name)){
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}
