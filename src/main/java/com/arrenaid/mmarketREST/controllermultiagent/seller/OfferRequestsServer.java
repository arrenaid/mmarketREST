package com.arrenaid.mmarketREST.controllermultiagent.seller;

import com.arrenaid.mmarketREST.controllermultiagent.auction.*;
import com.arrenaid.mmarketREST.controllermultiagent.Market;
import com.arrenaid.mmarketREST.model.entity.Grid;
import com.arrenaid.mmarketREST.model.entity.Loyalty;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OfferRequestsServer extends CyclicBehaviour {
    private SellerState level = SellerState.ENTRY;
    private AuctionState auction = AuctionState.SECOND;
    private double maxVolume;
    private double currentVolume;
    private double price;
    private List<CoreAuction> gears;
    private MessageTemplate mt; // The template to receive replies

    public OfferRequestsServer(double volume, double price, ACLMessage [] msg, int cnt){
        if(cnt <= 0) level = SellerState.EXIT;
        this.currentVolume = volume;
        this.price = price;
        List<ACLMessage> listMsg = new LinkedList<>(Arrays.asList(msg));
        this.gears = new LinkedList<>();
        for(int i = 0; i < listMsg.size();i++){
            gears.get(i).setMessage(listMsg.get(i));
        }
    }
    public void action() {
        updateDataAgent();
        switch (level) {
            case ENTRY:
                int countCfp = 0;
                clearMsg();
                // Message received. Process it
                // Prepare the template to get proposals
                mt = MessageTemplate.MatchConversationId(Market.conversation);
                Iterator<CoreAuction> iterator = gears.iterator();
                while (iterator.hasNext()){
                    CoreAuction content = iterator.next();
                    if(!content.getMessage().equals(null)){
                        if(content.getMessage().getPerformative() == ACLMessage.CFP){
                            String [] receiveContent = content.getMessage().getContent().split(";");;
                            double receiveVolume = Double.parseDouble(receiveContent[0]);
                            double receiveCost = Double.parseDouble(receiveContent[1]);
                            if (receiveVolume <= currentVolume /*&& receiveCost >= price*/) {
                                if(receiveCost >= price) {
//                                        System.out.println("ORS>-\t -- cfp-- \ts- " + myAgent.getLocalName()
//                                                + "\tb- " + buyerMsg[i].getSender().getLocalName());
                                    content.setAgent(content.getMessage().getSender());
                                    content.setVolume(receiveVolume);
                                    content.setCost(receiveCost);
                                    content.setParticipant(true);
                                    countCfp++;
                                }
                                else {
                                    content.setParticipant(false);
                                    sendReply(content.getMessage(), content.getMessage().getConversationId(),
                                            ACLMessage.REFUSE, "PRICE");
                                }
                            } else {
                                content.setParticipant(false);
                                sendReply(content.getMessage(), content.getMessage().getConversationId(),
                                        ACLMessage.REFUSE, "VOLUME");
                            }
                        }
                    }
                }
                if(countCfp == 0) level = SellerState.EXIT;
                else level = SellerState.AUCTION;
                break;
            case AUCTION:
                List agentData = List.of(myAgent.getLocalName(), maxVolume,currentVolume, price);
                CoreAuction winner = null;
                switch (auction){
                    case CUSTOM:
                        winner = new CustomAuction().getWinner(gears,agentData);
                        break;
                    case FIRST:
                        winner = new FirstPriceAuction().getWinner(gears,agentData);
                        break;
                    case SECOND:
                        winner = new SecondPriceAuction().getWinner(gears,agentData);
                        break;
                    case VSG:
                        winner = new VCGAuction().getWinner(gears,agentData);
                        break;
                    default:
                        System.out.println("ORS>-\t -- ERROR ENUM -- ERROR ENUM -- -- AUCTION -- \ts- " + myAgent.getLocalName());
                }
                auctionDataProcessing(winner,winner.getCalculatedPrice());
                break;
            case RESULT:
                //block();
                ACLMessage msg = myAgent.receive(mt);
                if(msg != null) {
                    if (msg.getPerformative() == ACLMessage.CONFIRM) {
                        String [] receiveContent = msg.getContent().split(";");
                        double receiveVolume = Double.parseDouble(receiveContent[0]);
                        double receiveCost = Double.parseDouble(receiveContent[1]);
                        if(receiveVolume <= currentVolume /*&& receiveCost >= price*/) {//vcg price all <
                            System.out.println("ORS>-\t -- NICE CONFIRM PROPOSE -- \ts- " + myAgent.getLocalName()
                                    + "\tb- "+msg.getSender().getLocalName());
//                                Main.controller.addTextAgent("-\t NICE CONFIRM PROPOSE\ts- "
//                                        + myAgent.getLocalName() +"&\tb- "+ msg.getSender().getLocalName());
                            sendReply(msg, msg.getConversationId(), ACLMessage.PROPOSE, "END");
//                                currentVolume -= receiveVolume;
                            dataChangeCurrentVolume(receiveVolume);
                            if(auction.equals(AuctionState.VSG)) {
                                if (Market.findLoyalty(myAgent.getLocalName(),msg.getSender().getLocalName()) != null)
                                    updateLoyalty(msg, receiveCost, receiveVolume);
                                else
                                    newLoyalty(msg, receiveCost, receiveVolume);
                            }
                        }
                        else{
                            System.out.println("ORS>-\t -- NO NO REFUSE -- \ts- " + myAgent.getLocalName()
                                    + "\tb- "+msg.getSender().getLocalName());
//                                Main.controller.addTextAgent("-\t NO NO REFUSE \ts- "
//                                        + myAgent.getLocalName() +"&\tb- "+ msg.getSender().getLocalName());
                            sendReply(msg, msg.getConversationId(), ACLMessage.REFUSE, " NO, ERROR, покупка не состоялась. нехватает объема");
                        }

                    }
                    if(msg.getPerformative() == ACLMessage.DISCONFIRM){
                        Market.printMsg("ORS",level.toString(),msg);
                    }
                    level = SellerState.EXIT;
                }
                else {
                    block();
                }
                break;
            case EXIT:
                if(currentVolume < (maxVolume/4)) {//для того что бы аген не висел бесконечно с малым обьемом
                    myAgent.doDelete();
                }
                myAgent.removeBehaviour(myEvent.getSource());
                break;
            default:
               System.out.println("ORS> -- ERROR PROBLEM --- level: "+ level + "\t- " + myAgent.getLocalName());
               level = SellerState.EXIT;
        }
    }
    public void sendReply(ACLMessage msg, String conversationId, int perf, String content){
       ACLMessage reply = msg.createReply();
       reply.setConversationId(conversationId);
       reply.setPerformative(perf);
       reply.setContent(content);
       myAgent.send(reply);
       //printSendMsg(reply);
   }
   public void sendOrder(int acl, AID receiver,String conversationId, String content, String replyWith){
       ACLMessage order = new ACLMessage(acl);//ACLMessage.ACCEPT_PROPOSAL
       order.addReceiver(receiver);
       order.setReplyWith(replyWith);
       order.setContent(content);
       order.setConversationId(conversationId);//"electricity-trade"
       myAgent.send(order);
       Market.printMsg("ORS", level.toString(),order);
   }

    private void auctionDataProcessing(CoreAuction winner, double finalPrice){
        boolean verification = true;
        if(!(winner.getVolume() <= currentVolume)){
            verification = false;
            level = SellerState.EXIT;
        }
        Iterator<CoreAuction> iterator = gears.iterator();
        while(iterator.hasNext()){
            CoreAuction element = iterator.next();
            if(element.isParticipant()){
               if(element.equals(winner) && verification){
                   String replyW = "order" + System.currentTimeMillis();
                   sendOrder(ACLMessage.ACCEPT_PROPOSAL, winner.getAgent(),Market.conversation,
                           String.valueOf(finalPrice),replyW);
                   mt = MessageTemplate.and(MessageTemplate.MatchConversationId(Market.conversation),
                           MessageTemplate.MatchInReplyTo(replyW));
                   level = SellerState.RESULT;
               }
               else {
                   sendOrder(ACLMessage.INFORM, element.getAgent(), Market.conversation,
                           "you lose", "order" + System.currentTimeMillis());
               }
            }
        }
    }

    public void dataChangeCurrentVolume(double change){
        currentVolume -=change;
//       //DatabaseHandler dbh = new DatabaseHandler();
//        for(int i = 0; i < Market.gridsList.size(); i++ ) {
//            if (myAgent.getLocalName().equals(Market.gridsList.get(i).getName())) {
//                Market.gridsList.get(i).setCurrentVolume(currentVolume);
//                //dbh.gridUpdate(Controller.gridsList.get(i));
//            }
//        }
        Grid changes = Market.grids.get(myAgent.getLocalName());
        changes.setCurrentVolume(currentVolume);
        Market.grids.put(myAgent.getLocalName(),changes);
    }
    public void updateDataAgent(){
        Grid grid = Market.grids.get(myAgent.getLocalName());
        if(grid == null){
            myAgent.doDelete();
        }
        currentVolume = grid.getCurrentVolume();
        price = grid.getCost();
        maxVolume = grid.getMaxVolume();
        auction = Market.getAuctionUse();
    }
    public void clearMsg(){
        while(true){
            ACLMessage msg = myAgent.receive(mt);
            if(msg !=null) Market.printMsg("ORS",level.toString(),msg);
            else return;
        }
    }

    public void newLoyalty(ACLMessage msg,double price,double volume){
        try {
            Loyalty loyalty = new Loyalty(myAgent.getLocalName(),msg.getSender().getLocalName(),price,volume,1.0);
            Market.loyalties.add(loyalty);
//            DatabaseHandler dbh = new DatabaseHandler();
//            dbh.addLoyalty(loyalty);
        }catch (Exception e){e.printStackTrace();}
    }
    public void updateLoyalty(ACLMessage msg,double price,double volume){
        try {
            Loyalty loyalty = new Loyalty();
            for(int i = 0; i < Market.loyalties.size(); i++) {
                if( myAgent.getLocalName().equals(Market.loyalties.get(i).getSellerloyalty())
                        && msg.getSender().equals(Market.loyalties.get(i).getSellerloyalty())){
                    loyalty = Market.loyalties.get(i);
                }
            }
                loyalty.setTotalamount(loyalty.getTotalamount() + price);
                loyalty.setTotalvalume(loyalty.getTotalvalume() + volume);
                loyalty.setTotaltrade(loyalty.getTotaltrade() + 1);
//                DatabaseHandler dbh = new DatabaseHandler();
//                dbh.loyaltyUpdate(loyalty);
        }catch (Exception e){e.printStackTrace();}
    }
} // End of inner class OfferRequestsSe