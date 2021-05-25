package com.arrenaid.mmarketREST.controllermultiagent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.*;

public class AuctionCore {
    private static class Participant{
        private AID participant;
        private Double volume;
        private Double cost;
//        private ACLMessage message;
//        private Double buyerVCGPrice;
    }
    private List<Participant> participantList;
    private List<AID> participant;
    private ArrayList<Double> volume;
    private LinkedList<Double> cost;
//    private LinkedList<ACLMessage> message;
//    private LinkedList<Double> buyerVCGPrice;

    private double sellerVolume;
    private double sellerPrice;


    AuctionCore(AID [] participant, Double [] volume, Double [] cost){
        this.participant = Arrays.asList(participant);
        this.volume =  new ArrayList<>(Arrays.asList(volume));
        this.cost = new LinkedList<>(Arrays.asList(cost));
    }

    private double getPrice(double volume1, double price1, double volume2,double price2){
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
    private int getWinner(int cnt111){
        double sumCost = 0;
        double sumVolume = 0;
//        Iterator<Double> iterator = cost.iterator();
//        while (iterator.hasNext()){
//            sumCost += iterator.next();
//        }
        for(double i: cost){ sumCost +=i; }
        for(double i: volume){ sumVolume+=i; }
        double  oddsCost = getPrice(sumVolume,sellerPrice,sumVolume/volume.size(),sumCost/cost.size());
        double odds;
        double bestOdds = 0;
        int result = 0;
        for(int i = 0; i < cost.size(); i++){
            odds = getPrice(sellerVolume,sellerPrice,volume.get(i),cost.get(i)) - oddsCost;
            if((odds*odds) >= (bestOdds*bestOdds)){
                bestOdds = odds;
                result = i;
            }
        }
        return result;
    }
    private int getWinnerFirstPriceAuction(int cnt){
        int winner = 0;
        double bestPrice = cost.get(0);
        for(int i = 1; i < cnt; i++){
            if((cost.get(i)) > (bestPrice)){
                bestPrice = cost.get(i);
                winner = i;
            }
        }
        return winner;
    }

    public double getFirstPrice(){//int cnt
        double firstPrice = cost.get(0);
        for(int i = 1; i < cost.size(); i++){
            if((cost.get(i)) > (firstPrice)){
                firstPrice = cost.get(i);
            }
        }
        return firstPrice;
    }
    public double getSecondPrice(){
        double bestPrice = cost.get(0);
        double secondPrice = cost.get(0);
        for(int i = 1; i < cost.size(); i++){
            if((cost.get(i)) > (bestPrice)){
                secondPrice = bestPrice;
                bestPrice = cost.get(i);
            }
            else
            if(cost.get(i) > secondPrice && cost.get(i) != bestPrice )
                secondPrice = cost.get(i);

        }
        return secondPrice;
    }

}
