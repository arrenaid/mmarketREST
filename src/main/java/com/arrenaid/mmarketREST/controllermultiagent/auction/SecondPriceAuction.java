package com.arrenaid.mmarketREST.controllermultiagent.auction;

import java.util.List;

public class SecondPriceAuction extends CoreAuction {
    @Override
    public CoreAuction getWinner(List<CoreAuction> participantList, List sellersList) {
        CoreAuction winner = new FirstPriceAuction().getWinner(participantList,sellersList);
        winner.setCalculatedPrice(getSecondPrice(participantList));
        return winner;
    }
}
