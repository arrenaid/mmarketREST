package com.arrenaid.mmarketREST.controllermultiagent.auction;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CoreAuction {
    private AID agent;
    private double volume;
    private double cost;
    private ACLMessage message;
    private double calculatedPrice;
    private boolean isParticipant;

    public abstract CoreAuction getWinner(List<CoreAuction> participantList, List sellersList);

    protected double getPrice(double volume1, double price1, double volume2, double price2){
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
    private double getFirstPrice(List<CoreAuction> participantList){
        double bestOffer = 0;
        Iterator<CoreAuction> iterator = participantList.iterator();
        while (iterator.hasNext()) {
            CoreAuction participant = iterator.next();
            if(participant.isParticipant()) {
                if (participant.getCost() > bestOffer) {
                    bestOffer = participant.getCost();
                }
            }
        }
        return bestOffer;
    }
    protected double getSecondPrice(List<CoreAuction> participantList){
        double bestOffer = 0;
        double secondOffer = 0;
        Iterator<CoreAuction> iterator = participantList.iterator();
        while (iterator.hasNext()) {
            CoreAuction participant = iterator.next();
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
}