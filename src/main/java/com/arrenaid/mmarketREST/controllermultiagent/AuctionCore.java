package com.arrenaid.mmarketREST.controllermultiagent;

import com.arrenaid.mmarketREST.controllermultiagent.seller.Content;
import com.arrenaid.mmarketREST.model.entity.Loyalty;
import java.util.*;

public class AuctionCore {
    private static double getPrice(double volume1, double price1, double volume2,double price2){
        double res1= (((volume1+volume2)/2)*((price1+price2)/2)) ;
        double res2 = ((volume1+volume2)/2)*price1;
        double res3 = ((volume1+volume2)/2)*price2;
        double res4  =((res1 + res3)/2) - ((res1 + res2)/2);
        double oddsVol = res4;
        res1 = ((price1+price2)/2)*((volume1+volume2)/2) ;
        res2 = ((price1+price2)/2)*volume1;
        res3 = ((price1+price2)/2)*volume2;
        res4  = ((res1 + res2 )/2)-((res1+res3)/2);
        double res = (price1+price2)/2;
        double res5 = res4 / oddsVol;
        return res + res5;
    }
    public static Content getWinnerCustomAuction(List<Content> participantList, double volume, double cost){
        Content winner = null;
        double totalCost = 0;
        double totalVolume = 0;
        double bestOdds = 0;
        int number = 0;
        Iterator<Content> iterator = participantList.iterator();
        while (iterator.hasNext()){
            Content participant = iterator.next();
            if(participant.isParticipant()){
                totalCost += participant.getCost();
                totalVolume += participant.getVolume();
                number++;
            }
        }
        double  oddsCost = getPrice(volume,cost,totalVolume/number,totalCost/number);
        ListIterator<Content> listIterator = participantList.listIterator();
        while(listIterator.hasNext()){
            Content entry = listIterator.next();
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
    public static Content getWinnerFirstPriceAuction(List<Content> participantList){
        Content winner = null;
        double bestOffer = 0;
        Iterator<Content> iterator = participantList.iterator();
        while(iterator.hasNext()){
            Content participant = iterator.next();
            if(participant.isParticipant()) {
                if (participant.getCost() > bestOffer) {
                    bestOffer = participant.getCost();
                    winner = participant;
                }
            }
        }
        return winner;
    }
    public static double getFirstPrice(List<Content> participantList){
        double bestOffer = 0;
        Iterator<Content> iterator = participantList.iterator();
        while (iterator.hasNext()) {
            Content participant = iterator.next();
            if(participant.isParticipant()) {
                if (participant.getCost() > bestOffer) {
                    bestOffer = participant.getCost();
                }
            }
        }
        return bestOffer;
    }
    public static double getSecondPrice(List<Content> participantList){
        double bestOffer = 0;
        double secondOffer = 0;
        Iterator<Content> iterator = participantList.iterator();
        while (iterator.hasNext()) {
            Content participant = iterator.next();
            if (participant.isParticipant()) {
                if (participant.getCost() > bestOffer) {
                    secondOffer = bestOffer;
                    bestOffer = participant.getCost();
                } else if (participant.getCost() > secondOffer && participant.getCost() != bestOffer) {
                    secondOffer = participant.getCost();
                }
            }
        }
        return secondOffer;
    }
    public static Content getWinnerVSGAuction(List<Content> participantList,String agentName,double maxVolume){
        Content winner = null;
        double bestOffer = 0;
        double totalAmount = 0;//общее влияние
        List<Integer> rankList = new ArrayList<>();//показатель совместимости покупателя и продавца
        int matrix[][] = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9} };
        int index = 0;
        Iterator<Content> iterator = participantList.iterator();//выясняем рейтинг и влияние
        while (iterator.hasNext()){
            Content participant = iterator.next();
            if(participant.isParticipant()){
                int rank = getRankLoyalty(agentName, participant.getAgent().getLocalName());
                int sector = 0;
                if(participant.getVolume() > (maxVolume/3)) {
                    sector = 1;
                }
                if(participant.getVolume()  > (maxVolume/3)*2) {
                    sector = 2;
                }
                rankList.add(matrix[rank][sector]);
                totalAmount += participant.getCost() * rankList.get(index);
                index++;
            }
        }
        index = 0;
        ListIterator<Content> listIterator = participantList.listIterator();//получаем цену для каждого учасника
        while (listIterator.hasNext()){
            Content next = listIterator.next();
            if(next.isParticipant()){
                double difference = totalAmount - (next.getCost() * rankList.get(index));//вычитаем влияние учасника из общего
                next.setCalculatedPrice(totalAmount / (rankList.get(index) + difference));//делим общее влияние на сумму чужего с нашим рангом. очень спорный момент.
                listIterator.set(next);
                System.out.println("ORS> --\tWCG\nseller - " + agentName +"\tbuyer - " + next.getAgent().getLocalName()
                        + "\ntotalAmount: " + totalAmount + "\trating: " + rankList.get(index) + "\tdifference: "
                        + difference + "\n\t\tprice: "+next.getCalculatedPrice());
                index++;
            }
        }
        Iterator<Content> finalIterator = participantList.iterator();//побеждает самая крупная сумма ( должен победить тот кто наносит наименьшее влияние)
        while (finalIterator.hasNext()){
            Content holder = finalIterator.next();
            if(holder.isParticipant()){
                if(holder.getCalculatedPrice() > bestOffer){
                    winner = holder;
                    bestOffer = holder.getCalculatedPrice();
                }
            }
        }
        return winner;
    }
    private static int getRankLoyalty(String sellerName, String buyerName){
        Loyalty loyalty = Market.findLoyalty(sellerName, buyerName);
        if (loyalty.getTotaltrade() >= 1)
            return  1;
        if (loyalty.getTotaltrade() >= 3)
            return  2;
        return 0;
    }

}
