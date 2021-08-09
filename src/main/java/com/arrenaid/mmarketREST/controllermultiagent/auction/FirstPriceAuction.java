package com.arrenaid.mmarketREST.controllermultiagent.auction;

import java.util.Iterator;
import java.util.List;

public class FirstPriceAuction extends CoreAuction {
    @Override
    public CoreAuction getWinner(List<CoreAuction> participantList, List sellersList) {
        CoreAuction winner = null;
        double bestOffer = 0;
        Iterator<CoreAuction> iterator = participantList.iterator();
        while(iterator.hasNext()){
            CoreAuction participant = iterator.next();
            if(participant.isParticipant()) {
                if (participant.getCost() > bestOffer) {
                    bestOffer = participant.getCost();
                    winner = participant;
                }
            }
        }
        winner.setCalculatedPrice(bestOffer);
        return winner;
    }
}
