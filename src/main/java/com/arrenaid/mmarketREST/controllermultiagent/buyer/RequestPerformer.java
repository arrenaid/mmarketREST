package com.arrenaid.mmarketREST.controllermultiagent.buyer;

//import db.DatabaseHandler;
//import db.Trade;
import com.arrenaid.mmarketREST.controllermultiagent.Market;
import com.arrenaid.mmarketREST.model.entity.Grid;
import com.arrenaid.mmarketREST.model.entity.Trade;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
//import sample.Controller;

/**
 Inner class RequestPerformer.
 This is the behaviour used by Book-buyer agents to request seller
 agents the target book.
 */
public class RequestPerformer extends CyclicBehaviour {//Behaviour done
    private AID bestSeller; // The agent who provides the best offer
    private AID[] sellerAgents;
    private ACLMessage bestOrder;
    private ACLMessage [] orders;
    private double targetVolume = 8;
    private double maxCost = 11;

    private double bestPrice; // The best offered price
    private int repliesCnt = 0; // The counter of replies from seller agents
    private MessageTemplate mt; // The template to receive replies
    private int step = 0;
    private int countRefuse = 0;
    private int ordersCnt =0;
    private boolean stop = false;



    public RequestPerformer(double vol, double price){
        targetVolume = vol;
        maxCost = price;
    }
    public void action() {
        updateDataAgent();
        if(targetVolume <=0){
            myAgent.doDelete();
        }

        switch (step) {
            case 0:
                clearMsg();
                //zeroing();
                //sellerAgents = new AID[255];
                //bestSeller = new AID();
                //bestPrice =0;
                //bestOrder = new ACLMessage();
                orders = new ACLMessage[99];
                ordersCnt = 0;
                //repliesCnt = 0;
                if(getAgentSellerFromYellowPages()){
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < sellerAgents.length; ++i) {
                        cfp.addReceiver(sellerAgents[i]);
                    }
                    cfp.setContent(toString().valueOf(targetVolume)+";"+toString().valueOf(maxCost));
                    cfp.setConversationId("electricity-trade");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    //printSendMsg(cfp);///
                    mt = MessageTemplate.MatchConversationId("electricity-trade");
                    bestSeller = null;
                    bestPrice = 0;
                    step = 1;
                }
                else
                    step =4;
                break;
            case 1:
                bestReceive();
                //firstReceive();
                break;
            case 2: // Send the purchase order to the seller that provided the best offer
                for(int i= 0;i < ordersCnt; i++){
                    if(!orders[i].equals(bestOrder)){
                        sendReply(orders[i],"electricity-trade",ACLMessage.DISCONFIRM,"sorry, not best order.");
                    }
                }
                System.out.println("RP> - CONFIRM\tPrice - " + bestPrice+"\tb- "+myAgent.getLocalName()+ "\ts- " + bestOrder.getSender().getLocalName());
                //Main.controller.addTextAgent("RP> - CONFIRM\tPrice - " + bestPrice+"\tb- "+myAgent.getLocalName() + "\ts- " + bestOrder.getSender().getLocalName());
                clearMsg();
                sendReply(bestOrder,"electricity-trade",ACLMessage.CONFIRM,toString().valueOf(targetVolume)+";"+toString().valueOf(bestPrice));
                //---end
                newTradeBuyer(bestOrder);
                dataChange();
                //---end
                step = 3;
                break;
            case 3:// Receive the purchase order reply
                //block();
                ACLMessage reply = myAgent.receive(mt);
                if (reply != null) {// Purchase order reply received
                    if (reply.getPerformative() == ACLMessage.PROPOSE) {// Purchase successful. We can terminate
                        System.out.println("RP> - successfully SUCCESSFULLY purchased\tPrice - " + bestPrice+" -- "+myAgent.getLocalName());
//                        Main.controller.addTextAgent("RP> - SUCCESSFULLY purchased\tPrice - " + bestPrice+" -- "+myAgent.getLocalName() + "\t- " + reply.getSender().getLocalName());

                        stop = true;
                        step = 4;
                        myAgent.doDelete();
                    }
                    if(reply.getPerformative() == ACLMessage.REFUSE){
                        //printGotMsg(reply);
                        System.out.println("RP>  -- not successfully noNONONONONON -- "+myAgent.getLocalName());
                        //Main.controller.addTextAgent("RP>  -- not successfully noNONONONONON -- "+myAgent.getLocalName());
                        step = 4;
                    }
                } else {
                    block();
                }
                break;
        }
        if(stop){
            myAgent.doDelete();
        }
    }

    private boolean getAgentSellerFromYellowPages() {
        // Update the list of seller agents
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("electricity-trade");
        template.addServices(sd);
        try {
            //System.out.println("Buyer-agent " + getAID().getName() + " -- START find DF step -- "+ currentStep);
            DFAgentDescription[] result = DFService.search(myAgent, template);
            sellerAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                sellerAgents[i] = result[i].getName();
            }
            return result.length>0;
        } catch (FIPAException fe) {
            fe.printStackTrace();
            return false;
        }
    }

    private void zeroing(){
        sellerAgents = new AID[255];
        bestSeller = new AID();
        bestPrice =0;
        bestOrder = new ACLMessage();
        orders = new ACLMessage[255];
        ordersCnt = 0;
        repliesCnt = 0;
    }
    public void clearMsg(){
        while(true){
            ACLMessage msg = myAgent.receive();
            if(msg !=null) {
                printGotMsg(msg);
            }
            else return;
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
    public void printGotMsg(ACLMessage msg){
        String str = "RP> -- step - " + step + " To -- " + myAgent.getLocalName() +
                " , got content -- " + msg.getContent() + ",\n\tForm -- " + msg.getSender().getLocalName()
                + " -- ConversationId -- "+ msg.getConversationId() + " -- Performative -- "+ msg.getPerformative();
        System.out.println(str);
        //Main.controller.addTextAgent(str);
    }
    public void printSendMsg(ACLMessage msg){
        String str ="RP> -- step - " + step + " Form -- " + myAgent.getLocalName()
                +" Send -- " + msg.getContent() + ",\n\tTo -- " + msg.getSender().getLocalName()
                + " -- ConversationId -- "+ msg.getConversationId() + " -- Performative -- "+ msg.getPerformative();
        System.out.println(str);
        //Main.controller.addTextAgent(str);
    }

    public void dataChange(){
        //DatabaseHandler dbh = new DatabaseHandler();
        for(int i = 0; i < Market.gridsList.size(); i++ ) {
            if (myAgent.getLocalName().equals(Market.gridsList.get(i).getName())) {
                Market.gridsList.get(i).setCurrentVolume( Market.gridsList.get(i).getCurrentVolume() +targetVolume);
                //dbh.gridUpdate(Controller.gridsList.get(i));
            }
        }
    }
    public void updateDataAgent(){
//        try {
//            for (int i = 0; i < Market.gridsList.size(); i++) {
//                if (myAgent.getLocalName().equals(Market.gridsList.get(i).getName())) {
//                    targetVolume = Market.gridsList.get(i).getMaxVolume() - Market.gridsList.get(i).getCurrentVolume();
//                    maxCost = Market.gridsList.get(i).getCost();
//                    if( Market.gridsList.get(i).getCurrentVolume()>= Market.gridsList.get(i).getMaxVolume()){
//                        myAgent.doDelete();
//                    }
//                    break;
//                }
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            updateDataAgent();
//        }
        Grid grid = Market.findGridInGridList(myAgent.getLocalName());
        if(grid == null || grid.getCurrentVolume() >= grid.getMaxVolume()){
            myAgent.doDelete();
        }
        targetVolume = grid.getMaxVolume() - grid.getCurrentVolume();
        maxCost = grid.getCost();
    }
    public void firstReceive(){
        ACLMessage order = myAgent.receive(mt);
        if (order != null) {
            printGotMsg(order);
            if (order.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {// This is an offer
                System.out.println("RP>-\t -- ACCEPT -- \ts- " + myAgent.getLocalName()+ "\tbs- " + bestSeller);
                double price = Double.parseDouble(order.getContent());
//                        if ((bestSeller == null || price < bestPrice) && price <= maxCost) {// This is the best offer at present
                if (bestSeller == null /*|| price < bestPrice*/) {
                    System.out.println("RP>-\t -- PROPOSAL -- \ts- " + myAgent.getLocalName());
                    bestPrice = price;
                    bestSeller = order.getSender();
                    bestOrder = order;
                    step = 2;
                }
                orders[ordersCnt] = order;
                ordersCnt++;
            }
        }
        else {
            block();
        }
    }
    public void bestReceive(){
        //block();
        ACLMessage order = myAgent.receive(mt);
        if (order != null) {
            //printGotMsg(reply);
            if (order.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {// This is an offer
                double price = Double.parseDouble(order.getContent());
//                        if ((bestSeller == null || price < bestPrice) && price <= maxCost) {// This is the best offer at present
                if (bestSeller == null || price <= bestPrice){
                    bestPrice = price;
                    bestSeller = order.getSender();
                    bestOrder = order;
                }
                orders[ordersCnt] = order;
                ordersCnt++;
            }
            if(order.getPerformative() == ACLMessage.INFORM){
                //countRefuse++;
            }
            repliesCnt++;
            if (repliesCnt >= sellerAgents.length && bestSeller != null) {//// We received all replies
                step = 2;
                if(ordersCnt == 0)step = 4;
            }
        }
        else {
            block();
        }
    }
    public void newTradeBuyer(ACLMessage msg){
        try {
            Trade trade = new Trade(msg.getSender().getLocalName(), myAgent.getLocalName(), targetVolume, bestPrice, Market.getAuctionUse());
            Market.tradesList.add(trade);
//            DatabaseHandler dbh = new DatabaseHandler();
//            dbh.addTrade(trade);
        }catch (Exception e){e.printStackTrace();}
    }
} // End of inner class RequestPerf
