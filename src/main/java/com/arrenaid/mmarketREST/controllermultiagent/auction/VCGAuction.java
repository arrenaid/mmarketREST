package com.arrenaid.mmarketREST.controllermultiagent.auction;

import com.arrenaid.mmarketREST.controllermultiagent.Market;
import com.arrenaid.mmarketREST.model.entity.Loyalty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class VCGAuction extends CoreAuction {
    @Override
    public CoreAuction getWinner(List<CoreAuction> participantList, List sellersList) {
        ///
        String agentName = (String) sellersList.get(0);
        double maxVolume = (double) sellersList.get(1);
        ///
        CoreAuction winner = null;
        double bestOffer = 0;
        double totalAmount = 0;//общее влияние
        List<Integer> rankList = new ArrayList<>();//показатель совместимости покупателя и продавца
        int matrix[][] = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9} };
        int index = 0;
        Iterator<CoreAuction> iterator = participantList.iterator();//выясняем рейтинг и влияние
        while (iterator.hasNext()){
            CoreAuction participant = iterator.next();
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
        ListIterator<CoreAuction> listIterator = participantList.listIterator();//получаем цену для каждого учасника
        while (listIterator.hasNext()){
            CoreAuction next = listIterator.next();
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
        Iterator<CoreAuction> finalIterator = participantList.iterator();//побеждает самая крупная сумма ( должен победить тот кто наносит наименьшее влияние)
        while (finalIterator.hasNext()){
            CoreAuction holder = finalIterator.next();
            if(holder.isParticipant()){
                if(holder.getCalculatedPrice() > bestOffer){
                    winner = holder;
                    bestOffer = holder.getCalculatedPrice();
                }
            }
        }
        return winner;
    }
    private int getRankLoyalty(String sellerName, String buyerName){
        Loyalty loyalty = Market.findLoyalty(sellerName, buyerName);
        if (loyalty.getTotaltrade() >= 1)
            return  1;
        if (loyalty.getTotaltrade() >= 3)
            return  2;
        return 0;
    }
}
