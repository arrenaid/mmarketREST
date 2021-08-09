package com.arrenaid.mmarketREST.controllermultiagent;

import com.arrenaid.mmarketREST.controllermultiagent.auction.AuctionState;
import com.arrenaid.mmarketREST.model.entity.Grid;
import com.arrenaid.mmarketREST.model.entity.Loyalty;
import com.arrenaid.mmarketREST.model.entity.Trade;
import jade.lang.acl.ACLMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Market {
    //static public LinkedList<Grid> gridsList;
    static public Map<String,Grid> grids;
    //static public ArrayList<Grid> waitGridsList;
    //static public ArrayList<Grid> oldGridsList;
    static public LinkedList<Trade> tradesList;
//    static public LinkedList<Trade> tradesListAll;
    static public LinkedList<Loyalty> loyalties;
    static public final String conversation = "electricity-trade";

    @Getter
    @Setter
    static private int countBuyer = 0;
    @Getter
    @Setter
    static private int countSeller = 0;
    @Getter
    @Setter
    static private AuctionState auctionUse = AuctionState.SECOND;

//    public static Grid findGridInGridList(String name){
//        Iterator<Grid> iterator = gridsList.iterator();
//        while(iterator.hasNext()){
//            Grid result = iterator.next();
//            if(result.getName().equals(name))
//                return result;
//        }
//        return null;
//    }

//    public static Grid findGridInMap(String name){
//        return mapGrids.get(name);
//    }

    public static void initMarket() {
        grids = new HashMap<>();
        //gridsList = new LinkedList<>();
        tradesList = new LinkedList<>();
        loyalties = new LinkedList<>();
    }
    public static void printGrids(){
        for(Map.Entry<String,Grid> map : grids.entrySet()){
            System.out.println(map.getKey() + " "+ map.getValue().getRole() + " "+ map.getValue().isStatus()
                    + "\n\tcost: "+ map.getValue().getCost()
                    + "\n\tmax volume: " + map.getValue().getMaxVolume()
                    + "\n\tcurrent volume: "+ map.getValue().getCurrentVolume());
        }
    }

//    public static boolean removeGrid(String name) {
//        Iterator<Grid> iterator = Market.gridsList.iterator();
//        while (iterator.hasNext()){
//            if(iterator.next().getName().equals(name)){
//                iterator.remove();
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void sendReply(ACLMessage msg, String conversationId, int perf, String content){
//        ACLMessage reply = msg.createReply();
//        reply.setConversationId(conversationId);
//        reply.setPerformative(perf);
//        reply.setContent(content);
//        myAgent.send(reply);
//        //printSendMsg(reply);
//    }
//    public void sendOrder(Agent agent, int acl, AID receiver, String conversationId, String content, String replyWith){
//        ACLMessage order = new ACLMessage(acl);//ACLMessage.ACCEPT_PROPOSAL
//        order.addReceiver(receiver);
//        order.setReplyWith(replyWith);
//        order.setContent(content);
//        order.setConversationId(conversationId);//"electricity-trade"
//        Agent.send(order)
//    }
//    public void printGotMsg(ACLMessage msg){
//        String str = "ORS> -- step - " + Step + " To -- " + myAgent.getLocalName() +
//                " , got content -- " + msg.getContent() + ",\n\tForm -- " + msg.getSender().getLocalName()
//                + " -- ConversationId -- "+ msg.getConversationId() + " -- Performative -- "+ msg.getPerformative();
//        System.out.println(str);
//
//        //Main.controller.addTextAgent(str);
//    }
    public static void printMsg(String place, String step, ACLMessage msg){
        System.out.println(place + "> -- step - " + step + "msg\n\t\tForm -- "
                + msg.getSender().getLocalName() +  ",\n\t\tTo -- "+ msg.getAllReceiver()
                + "\n\tPerformative -- "+ msg.getPerformative() + "\n\tContent -- " + msg.getContent());

    }
    public static Loyalty findLoyalty(String sellerName, String buyerName) {
        Loyalty loyalty = null;
        Iterator<Loyalty> iterator = loyalties.iterator();
        while (iterator.hasNext()){
            Loyalty obj = iterator.next();
            if(obj.getSellerloyalty().equals(sellerName) && obj.getBuyerloyalty().equals(buyerName)){
                loyalty = obj;
            }
        }
        return loyalty;
    }
}
