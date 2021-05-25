package com.arrenaid.mmarketREST.controllermultiagent.seller;

import com.arrenaid.mmarketREST.controllermultiagent.Market;
import com.arrenaid.mmarketREST.model.Auction;
import com.arrenaid.mmarketREST.model.Conversation;
import com.arrenaid.mmarketREST.model.entity.Grid;
import com.arrenaid.mmarketREST.model.entity.Loyalty;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 Inner class OfferRequestsServer.
 This is the behaviour used by Book-seller agents to serve incoming requests
 for offer from buyer agents.
 If the requested book is in the local catalogue the seller agent replies
 with a PROPOSE message specifying the price. Otherwise a REFUSE message is
 sent back.
 */
public class OfferRequestsServer extends CyclicBehaviour {
    private int MAX_STEPS = 99;
    private int Step = 0;
    private int buyersCnt = 0;
    private int buyersCfpCnt = 0;
    private int count;
    private Auction auction = Auction.SECOND;

    protected double maxVolume=10;
    protected double currentVolume;
    private double price;
    private boolean stop = false;

    private String [] receiveContent;
    private double receiveVolume;
    private double receiveCost;

    private AID[] buyerAgents;
    private double []buyerVolume;
    private double [] buyerCost;
    private ACLMessage [] buyerMsg;
    private double [] buyerVCGPrice;

    private MessageTemplate mt; // The template to receive replies

    public OfferRequestsServer(double vol, double p,ACLMessage []str,int cnt){
        currentVolume = vol;
        price = p;
        buyerMsg= str;
        count = cnt;
        if(cnt == 0) return;
    }
    public void action() {
        updateDataAgent();
            switch (Step) {
                case 0:
                    clearMsg();
                    buyersCnt = count;
                    buyersCfpCnt = 0;
                    buyerAgents = new AID [buyersCnt];
                    buyerVolume = new double[buyersCnt];
                    buyerCost = new double[buyersCnt];
                    // Message received. Process it
                    // Prepare the template to get proposals
                    mt = MessageTemplate.MatchConversationId("electricity-trade");
                    for(int i = 0; i < buyersCnt; i++) {
                        if(buyerMsg[i] != null) {
                            if (buyerMsg[i].getPerformative() == ACLMessage.CFP) {
                                receiveContent = buyerMsg[i].getContent().split(";");
                                receiveVolume = Double.parseDouble(receiveContent[0]);
                                receiveCost = Double.parseDouble(receiveContent[1]);
                                if (receiveVolume <= currentVolume /*&& receiveCost >= price*/) {
                                    if(receiveCost >= price) {
                                        System.out.println("ORS>-\t -- cfp-- \ts- " + myAgent.getLocalName()
                                                + "\tb- " + buyerMsg[i].getSender().getLocalName());
                                        buyerAgents[buyersCfpCnt] = buyerMsg[i].getSender();
                                        buyerVolume[buyersCfpCnt] = receiveVolume;
                                        buyerCost[buyersCfpCnt] = receiveCost;
                                        buyersCfpCnt++;
                                    }
                                    else
                                        sendReply(buyerMsg[i], buyerMsg[i].getConversationId(), ACLMessage.REFUSE, "PRICE");
                                } else {
                                    sendReply(buyerMsg[i], buyerMsg[i].getConversationId(), ACLMessage.REFUSE, "VOLUME");
                                }
                            }
                        }
                    }
                    if(buyersCnt <= 0) Step = 4;
                    else Step = 1;
                    break;
                case 1:
                    //block();
                    if(buyersCfpCnt >0) {
                        switch (auction){
                            case CUSTOM:
                                customAuction();
                                break;
                            case FIRST:
                                firstPriceAuction();
                                break;
                            case SECOND:
                                secondPriseAuction();
                                break;
                            case VSG:
                                VCGAuction();
                                break;
                            default:
                                System.out.println("ORS>-\t -- ERROR ENUM -- ERROR ENUM -- -- AUCTION -- \ts- " + myAgent.getLocalName());
                        }
                    }
                    else
                        Step = 4;
                    break;
                case 2:
                    //block();
                    ACLMessage msg = myAgent.receive(mt);
                    if(msg != null) {
                        if (msg.getPerformative() == ACLMessage.CONFIRM) {
                            receiveContent = msg.getContent().split(";");
                            receiveVolume = Double.parseDouble(receiveContent[0]);
                            receiveCost = Double.parseDouble(receiveContent[1]);
                            if(receiveVolume <= currentVolume /*&& receiveCost >= price*/) {//vcg price all <
                                System.out.println("ORS>-\t -- NICE CONFIRM PROPOSE -- \ts- " + myAgent.getLocalName()
                                        + "\tb- "+msg.getSender().getLocalName());
//                                Main.controller.addTextAgent("-\t NICE CONFIRM PROPOSE\ts- "
//                                        + myAgent.getLocalName() +"&\tb- "+ msg.getSender().getLocalName());
                                sendReply(msg, msg.getConversationId(), ACLMessage.PROPOSE, "END");

//                                currentVolume -= receiveVolume;
                                dataChangeCurrentVolume(receiveVolume);
                                if(auction.equals(Auction.VSG)) {
                                    if (findLoyalty(msg) >= 1)
                                        updateLoyalty(msg, receiveCost, receiveVolume);
                                    else
                                        newLoyalty(msg, receiveCost, receiveVolume);
                                }
                                Step = 4;
                                stop = true;
                            }
                            else{
                                System.out.println("ORS>-\t -- NO NO REFUSE -- \ts- " + myAgent.getLocalName()
                                        + "\tb- "+msg.getSender().getLocalName());
//                                Main.controller.addTextAgent("-\t NO NO REFUSE \ts- "
//                                        + myAgent.getLocalName() +"&\tb- "+ msg.getSender().getLocalName());
                                sendReply(msg, msg.getConversationId(), ACLMessage.REFUSE, " NO NO NO ERROR ошибка, покупка не состоялась. нехватает объема");
                                Step = 4;
                            }

                        }
                        if(msg.getPerformative() == ACLMessage.DISCONFIRM){
                            System.out.println("ORS>\t -- DISCONFIRM сделка сорвалась -- \ts- " + myAgent.getLocalName()
                                    + "\tb- "+msg.getSender().getLocalName());
//                            Main.controller.addTextAgent("ORS>\t -- DISCONFIRM сделка сорвалась -- \ts- " + myAgent.getLocalName()
//                                    + "\tb- "+msg.getSender().getLocalName());
                            Step = 4;
                        }
                    }
                    else {
                        block();
                    }
                    break;
                /*default:
                   System.out.println("ORS> \t -- --- PROBLEM "+ Step + " --- -- \t- " + myAgent.getLocalName());
                   return;*/
            }
            if(stop){
                //myAgent.doDelete();
                if(currentVolume < (40)) {
                    Step = 5;
                    myAgent.doDelete();
                    return;
                }
            }
            if(Step == 4) return;
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
   }
    public void printGotMsg(ACLMessage msg){
        String str = "ORS> -- step - " + Step + " To -- " + myAgent.getLocalName() +
                " , got content -- " + msg.getContent() + ",\n\tForm -- " + msg.getSender().getLocalName()
                + " -- ConversationId -- "+ msg.getConversationId() + " -- Performative -- "+ msg.getPerformative();
       System.out.println(str);

       //Main.controller.addTextAgent(str);
   }
    public void printSendMsg(ACLMessage msg){
             System.out.println("ORS> -- step - " + Step + " Form -- " + myAgent.getLocalName()
                     +" Send -- " + msg.getContent() + ",\n\tTo -- " + msg.getSender().getLocalName()
                     + " -- ConversationId -- "+ msg.getConversationId() + " -- Performative -- "+ msg.getPerformative());
    }

    private void auction( int winner, double finalPrice){
        if (buyerVolume[winner] <= currentVolume) {

            System.out.println("ORS>-\t -- ACCEPT_PROPOSAL -- \ts- " + myAgent.getLocalName());
            System.out.print("\twin- " + buyerAgents[winner].getLocalName() + "\tv- "
                    + buyerVolume[winner] + "\tp- "
                    + toString().valueOf(finalPrice));
            String replyW = "order" + System.currentTimeMillis();
            sendOrder(ACLMessage.ACCEPT_PROPOSAL, buyerAgents[winner],"electricity-trade",
                    "you lose",replyW);
//            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//            order.addReceiver(buyerAgents[winner]);
//            order.setContent(toString().valueOf(finalPrice));
//            order.setReplyWith("order" + System.currentTimeMillis());
//            order.setConversationId("electricity-trade");
//            myAgent.send(order);
            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("electricity-trade"),
                    MessageTemplate.MatchInReplyTo(replyW));
            Step = 2;
        } else {
            winner = -1;
            Step = 4;
        }
        for (int i = 0; i < buyersCfpCnt; i++) {
            if (i != winner) {
                sendOrder(ACLMessage.INFORM, buyerAgents[i],"electricity-trade",
                        "you lose","order" + System.currentTimeMillis());
//                ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
//                answer.addReceiver(buyerAgents[i]);
//                answer.setContent("you lose");
//                answer.setReplyWith("order" + System.currentTimeMillis());
//
//                answer.setConversationId("electricity-trade");
//                myAgent.send(answer);
                //printSendMsg(answer);
            }
        }
    }
    private void secondPriseAuction(){
        auction(getWinnerFirstPriceAuction(buyersCfpCnt),getSecondPrice(buyersCfpCnt));

//        int winner = getWinnerFirstPriceAuction(buyersCfpCnt);
//        if (buyerVolume[winner] <= currentVolume) {
//
//            System.out.println("ORS>-\t -- ACCEPT_PROPOSAL -- \ts- " + myAgent.getLocalName());
//            System.out.print("\twin- " + buyerAgents[winner].getLocalName() + "\tv- "
//                    + buyerVolume[winner] + "\tp- "
//                    + toString().valueOf(getPrice(currentVolume, price, buyerVolume[winner], buyerCost[winner])));
//
//            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//            order.addReceiver(buyerAgents[winner]);
//            order.setContent(toString().valueOf(getSecondPrice(buyersCfpCnt)));
//            order.setReplyWith("order" + System.currentTimeMillis());
//            order.setConversationId("electricity-trade");
//            myAgent.send(order);
//            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("electricity-trade"),
//                    MessageTemplate.MatchInReplyTo(order.getReplyWith()));
//            Step = 2;
//
//        } else {
//            winner = -1;
//            Step = 4;
//        }
//        for (int i = 0; i < buyersCfpCnt; i++) {
//            if (i != winner) {
//                ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
//                answer.addReceiver(buyerAgents[i]);
//                answer.setContent("you lose");
//                answer.setReplyWith("order" + System.currentTimeMillis());
//
//                answer.setConversationId("electricity-trade");
//                myAgent.send(answer);
//                //printSendMsg(answer);
//            }
//        }
    }
    private void firstPriceAuction(){
        auction(getWinnerFirstPriceAuction(buyersCfpCnt),getFirstPrice(buyersCfpCnt));

//        int winner = getWinnerFirstPriceAuction(buyersCfpCnt);
//        if (buyerVolume[winner] <= currentVolume) {
//
//            System.out.println("ORS>-\t -- ACCEPT_PROPOSAL -- \ts- " + myAgent.getLocalName());
//            System.out.print("\twin- " + buyerAgents[winner].getLocalName() + "\tv- "
//                    + buyerVolume[winner] + "\tp- "
//                    + toString().valueOf(getPrice(currentVolume, price, buyerVolume[winner], buyerCost[winner])));
//
//            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//            order.addReceiver(buyerAgents[winner]);
//            order.setContent(toString().valueOf(getFirstPrice(buyersCfpCnt)));
//            order.setReplyWith("order" + System.currentTimeMillis());
//            order.setConversationId("electricity-trade");
//            myAgent.send(order);
//            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("electricity-trade"),
//                    MessageTemplate.MatchInReplyTo(order.getReplyWith()));
//            Step = 2;
//
//        } else {
//            winner = -1;
//            Step = 4;
//        }
//        for (int i = 0; i < buyersCfpCnt; i++) {
//            if (i != winner) {
//                ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
//                answer.addReceiver(buyerAgents[i]);
//                answer.setContent("you lose");
//                answer.setReplyWith("order" + System.currentTimeMillis());
//
//                answer.setConversationId("electricity-trade");
//                myAgent.send(answer);
//                //printSendMsg(answer);
//            }
//        }
    }
    private void VCGAuction(){
        int winner = VSG();
        auction(winner,buyerVCGPrice[winner]);
//
//        if (buyerVolume[winner] <= currentVolume) {
//
//            System.out.println("ORS>-\t -- ACCEPT_PROPOSAL -- \ts- " + myAgent.getLocalName());
//            System.out.print("\twin VCG- " + buyerAgents[winner].getLocalName() + "\tv- "
//                    + buyerVolume[winner] + "\tp- "
//                    + toString().valueOf(getPrice(currentVolume, price, buyerVolume[winner], buyerCost[winner])));
//
//            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//            order.addReceiver(buyerAgents[winner]);
//            order.setContent(toString().valueOf(buyerVCGPrice[winner]));
//            order.setReplyWith("order" + System.currentTimeMillis());
//            order.setConversationId("electricity-trade");
//            myAgent.send(order);
//            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("electricity-trade"),
//                    MessageTemplate.MatchInReplyTo(order.getReplyWith()));
//            Step = 2;
//
//        } else {
//            winner = -1;
//            Step = 4;
//        }
//        for (int i = 0; i < buyersCfpCnt; i++) {
//            if (i != winner) {
//                ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
//                answer.addReceiver(buyerAgents[i]);
//                answer.setContent("you lose");
//                answer.setReplyWith("order" + System.currentTimeMillis());
//
//                answer.setConversationId("electricity-trade");
//                myAgent.send(answer);
//                //printSendMsg(answer);
//            }
//        }
    }
    private void customAuction() {
        int winner = getWinner(buyersCfpCnt);
        if (buyerVolume[winner] <= currentVolume) {

            System.out.println("ORS>-\t -- ACCEPT_PROPOSAL -- \ts- " + myAgent.getLocalName());
            System.out.print("\twin- " + buyerAgents[winner].getLocalName() + "\tv- "
                    + buyerVolume[winner] + "\tp- "
                    + toString().valueOf(getPrice(currentVolume, price, buyerVolume[winner], buyerCost[winner])));
//                        Main.controller.addTextAgent("ORS>-\t ACCEPT_PROPOSAL\ts- "
//                                + myAgent.getLocalName() +"&\tb- "+ winner+"\tp- "
//                                +toString().valueOf(getPrice(currentVolume, price, buyerVolume[winner], buyerCost[winner])));

            String replyWith = "order" + System.currentTimeMillis();
            sendOrder(ACLMessage.ACCEPT_PROPOSAL, buyerAgents[winner],"electricity-trade",
                    toString().valueOf(getPrice(currentVolume, price, buyerVolume[winner], buyerCost[winner])),
                    replyWith);
//            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//            order.addReceiver(buyerAgents[winner]);
//            order.setContent(toString().valueOf(getPrice(currentVolume, price, buyerVolume[winner], buyerCost[winner])));
//            order.setReplyWith("order" + System.currentTimeMillis());
//            order.setConversationId("electricity-trade", Conversation.electricity_trade);
//            myAgent.send(order);
            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("electricity-trade"),
                    MessageTemplate.MatchInReplyTo(replyWith));
            Step = 2;

        } else {
            winner = -1;
            Step = 4;
        }
        for (int i = 0; i < buyersCfpCnt; i++) {
            if (i != winner) {
                sendOrder(ACLMessage.INFORM, buyerAgents[i],"electricity-trade",
                        "you lose","order" + System.currentTimeMillis());
//                ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
//                answer.addReceiver(buyerAgents[i]);
//                answer.setContent("you lose");
//                answer.setReplyWith("order" + System.currentTimeMillis());
//
//                answer.setConversationId("electricity-trade");
//                myAgent.send(answer);
                //printSendMsg(answer);
            }
        }
    }
    protected double getPrice(double volume1, double price1, double volume2,double price2){
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
    protected int getWinner(int cnt){
        double sumCost = 0;
        double sumVolume = 0;
        for(int i = 0; i < cnt; i++){
            sumCost += buyerCost[i];
            sumVolume += buyerVolume[i];
        }
        double  oddsCost = getPrice(currentVolume,price,sumVolume/cnt,sumCost/cnt);
        double odds;
        double bestOdds = 0;
        int result = 0;
        for(int i = 0; i < cnt; i++){
            odds = getPrice(currentVolume,price,buyerVolume[i],buyerCost[i]) - oddsCost;
            if((odds*odds) >= (bestOdds*bestOdds)){
                bestOdds = odds;
                result = i;
            }
        }
        return result;
    }
    public int getWinnerFirstPriceAuction(int cnt){
        int winner = 0;
        double bestPrice = buyerCost[0];
        for(int i = 1; i < cnt; i++){
            if((buyerCost[i]) > (bestPrice)){
                bestPrice = buyerCost[i];
                winner = i;
            }
        }
        return winner;
    }

    public double getFirstPrice(int cnt){
        double firstPrice = buyerCost[0];
        for(int i = 1; i < cnt; i++){
            if((buyerCost[i]) > (firstPrice)){
                firstPrice = buyerCost[i];
            }
        }
        return firstPrice;
    }
    public double getSecondPrice(int cnt){
        double bestPrice = buyerCost[0];
        double secondPrice = buyerCost[0];
        for(int i = 1; i < cnt; i++){
            if((buyerCost[i]) > (bestPrice)){
                secondPrice = bestPrice;
                bestPrice = buyerCost[i];
            }
            else
                if(buyerCost[i] > secondPrice && buyerCost[i] != bestPrice )
                secondPrice = buyerCost[i];

        }
        return secondPrice;
    }
//    public int secondPriceAuction(int cnt){
//        int winner = 0;
//        double bestPrice = buyerCost[0];
//        double secondPrice = buyerCost[0];
//        for(int i = 1; i < cnt; i++){
//            if((buyerCost[i]) > (bestPrice)){
//                secondPrice = bestPrice;
//                bestPrice = buyerCost[i];
//                winner = i;
//            }
//        }
//        return winner;
//    }

    public void dataChangeCurrentVolume(double change){
        currentVolume -=change;
       //DatabaseHandler dbh = new DatabaseHandler();
        for(int i = 0; i < Market.gridsList.size(); i++ ) {
            if (myAgent.getLocalName().equals(Market.gridsList.get(i).getName())) {
                Market.gridsList.get(i).setCurrentVolume(currentVolume);
                //dbh.gridUpdate(Controller.gridsList.get(i));
            }

        }
    }
    public void updateDataAgent(){
//        try {
//            for (int i = 0; i < Market.gridsList.size() ; i++) {
//                if (myAgent.getLocalName().equals(Market.gridsList.get(i).getName())) {
//                    currentVolume = Market.gridsList.get(i).getCurrentVolume();
//                    price = Market.gridsList.get(i).getCost();
//                    maxVolume = Market.gridsList.get(i).getMaxVolume();
//                    auction = Market.getAuctionUse();
//                    break;
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            try{updateDataAgent();}
//            catch (Exception ex){
//                ex.printStackTrace();
//            }
//
//        }
        Grid grid = Market.findGridInGridList(myAgent.getLocalName());
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
            if(msg !=null) printGotMsg(msg);
            else return;
        }
    }
    public int VSG(){
        int winner = 0;

        buyerVCGPrice = new double[buyersCfpCnt];
        double amountNo[] = new double[buyersCfpCnt];
        double amountAll = 0;

        int loyal[] = new int [buyersCfpCnt];
        int volumeSector[]=new int[buyersCfpCnt];
        int rating[] = new int[buyersCfpCnt];

        int index[][] = { {1, 2, 3},{4, 5, 6},{7,8,9}};

        for(int i = 0; i <buyersCfpCnt;i++){
            loyal[i] = findLoyalty(buyerMsg[i]);
            int sector = 0;
            if(buyerVolume[i] > (maxVolume/3))
                sector = 1;
            if(buyerVolume[i] > (maxVolume/3)*2)
                sector =2;
            volumeSector[i] = sector;
            rating[i] = index[loyal[i]][sector];
            amountAll+=buyerCost[i]*rating[i];

        }
        for(int i = 0; i <buyersCfpCnt;i++){
            amountNo[i] = amountAll - (buyerCost[i]*rating[i]);
            buyerVCGPrice[i] = amountAll/( rating[i] + amountNo[i]);///rating[i]
            System.out.println("ORS>-\tWCG\ts- " + myAgent.getLocalName()+"\tb-"
                    + buyerAgents[i].getLocalName().toString() + "\trating- "+rating[i]
                    + "\tprice- "+buyerVCGPrice[i]);
        }
        double best = buyerVCGPrice[0];
        for(int i = 0; i <buyersCfpCnt;i++){
            if(buyerVCGPrice[i]>best) {
                best = buyerVCGPrice[i];
                winner = i;
            }
        }
        return winner;
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
                if( myAgent.getLocalName().equals(Market.loyalties.get(i).getSellerloyalty()) && msg.getSender().equals(Market.loyalties.get(i).getSellerloyalty())){
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
    public int findLoyalty(ACLMessage msg){
//        DatabaseHandler dbh = new DatabaseHandler();
//        dbh.getAllLoyalty();
        int result = 0;
        Loyalty loyalty = new Loyalty();
        loyalty.setTotalamount(-1.0);
        for(int i = 0; i < Market.loyalties.size(); i++) {
            if( myAgent.getLocalName().equals(Market.loyalties.get(i).getSellerloyalty()) && msg.getSender().equals(Market.loyalties.get(i).getSellerloyalty())){
                loyalty = Market.loyalties.get(i);
            }
        }
        if(loyalty.getTotalamount() == -1.0)
            return 0;
        else {
            if (loyalty.getTotaltrade() >= 1)
                result = 1;
            if (loyalty.getTotaltrade() >= 3)
                result = 2;
        }
        return result;
    }

} // End of inner class OfferRequestsSe