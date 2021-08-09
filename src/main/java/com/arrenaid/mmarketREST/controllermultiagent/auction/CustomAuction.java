package com.arrenaid.mmarketREST.controllermultiagent.auction;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CustomAuction extends CoreAuction {

    @Override
    public CoreAuction getWinner(List<CoreAuction> participantList, List sellersList) {
        ///
        double volume = (double) sellersList.get(2);//currentVolume
        double cost = (double) sellersList.get(3);//price
        ///
        CoreAuction winner = null;
        double totalCost = 0;
        double totalVolume = 0;
        double bestOdds = 0;
        int number = 0;
        Iterator<CoreAuction> iterator = participantList.iterator();
        while (iterator.hasNext()){
            CoreAuction participant = iterator.next();
            if(participant.isParticipant()){
                totalCost += participant.getCost();
                totalVolume += participant.getVolume();
                number++;
            }
        }
        double  oddsCost = getPrice(volume,cost,totalVolume/number,totalCost/number);
        ListIterator<CoreAuction> listIterator = participantList.listIterator();
        while(listIterator.hasNext()){
            CoreAuction entry = listIterator.next();
            if(entry.isParticipant()){
                double odds = getPrice(volume, cost, entry.getVolume(), entry.getCost() - oddsCost);
                entry.setCalculatedPrice(odds);
                listIterator.set(entry);
                if(Math.pow(odds,2) >= Math.pow(bestOdds,2)){
                    bestOdds = odds;
                    winner = entry;
                }
            }
        }
        return winner;
    }
}
