package com.arrenaid.mmarketREST.controllermultiagent.seller;


import com.arrenaid.mmarketREST.controllermultiagent.Market;
import com.arrenaid.mmarketREST.controllermultiagent.RandValue;
import com.arrenaid.mmarketREST.model.entity.Grid;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class AgentSeller extends Agent {
    public static   double maxVolume;
    public static   double currentVolume;
    public static   double price;
    //public ACLMessage[] buyerMsg;
    //private AID[] buyerAgents;//del
    private int buyerCnt;
    private long tick = TimeUnit.SECONDS.toMillis(3);
    private int countTick = 0;


    @Override
    protected void setup(){
        //  int id = Integer.parseInt(getAID().getLocalName());
        AID id  = new AID("Victor ",AID.ISLOCALNAME);

        init();
        info();
        buyerCnt = 0;
        // Register the book-selling service in the yellow pages
        this.getRegistration();

        this.addBehaviour(new TickerBehaviour(this,tick) {
//            private int MAX_STEPS = 5;
//            private int MAX_MSG = 99;
//            private int currentStep1 = 0;
            @Override
            protected void onTick() {

                ACLMessage [] hop = new ACLMessage[99];
                buyerCnt = 0;
//                if (currentStep1 < MAX_STEPS) {
                    // Message received. Process it
                    // Prepare the template to get proposals
                    MessageTemplate mt = MessageTemplate.MatchConversationId("electricity-trade");
                    boolean run = true;
                    while(run) {//buyerCnt < MAX_MSG
                        ACLMessage msg = myAgent.receive(mt);
                        if(msg != null) {
                            if (msg.getPerformative() == ACLMessage.CFP) {
                                hop[buyerCnt] = msg;
                                buyerCnt++;
                            }
                        }
                        else{
                            run = false;
                        }
                    }
//                }
//                else {
//                    this.stop();
//                }
                if(buyerCnt > 0)
                    addBehaviour(new OfferRequestsServer(currentVolume, price, hop, buyerCnt));//цикл
                else
                    block();
            }
        });
    }


    // Put agent clean-up operations here
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
// Close the GUI
       // myGui.dispose();
// Printout a dismissal message
        //info();
        info();
//        //dataChange();
//        for(int i = 0; i < Market.gridsList.size(); i++ ) {
//            if (this.getLocalName().equals(Market.gridsList.get(i).getName())) {
//                Market.gridsList.get(i).setStatus(false);
//                Market.oldGridsList.add(Market.gridsList.get(i));
//                dataChange(Market.gridsList.get(i));
//                Market.gridsList.remove(i);
//            }
//        }
        Market.removeGrid(this.getLocalName());
        //Controller.updateTableViewGrids();
        Market.setCountSeller(Market.getCountSeller() -1);
        System.out.println("Seller-agent "+getAID().getName()+" terminating.");
    }
    private void initRand(){
        RandValue r = new RandValue();
        maxVolume = r.RandValue(70,199);
        currentVolume =r.RandValue(70,maxVolume);
        price = r.RandValue(3,29);

    }
    private void init(){
        Grid grid = Market.findGridInGridList(this.getLocalName());
        if(grid == null){
            initRand();
        }
        else{
                maxVolume = grid.getMaxVolume();
                currentVolume = grid.getCurrentVolume();
                price = grid.getCost();
        }
    }
//    private void init(){
//        boolean isFilled = false;
//        for(int i = 0; i < Market.gridsList.size(); i++ ) {
//            if (this.getLocalName().equals( Market.gridsList.get(i).getName())){
//                maxVolume = Market.gridsList.get(i).getMaxVolume();
//                currentVolume = Market.gridsList.get(i).getCurrentVolume();
//                price = Market.gridsList.get(i).getCost();
//                isFilled = true;
//            }
//        }
//        if(!isFilled){
//            initRand();
//        }
//    }

    // Register the book-selling service in the yellow pages
    protected void getRegistration(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("electricity-trade");
        sd.setName("JADE-electricity-trade");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
    protected void info(){
        String str =this.getName()+this.getLocalName() + " is ready\n\t" + maxVolume +
                " -- maxVolume\n\t" + currentVolume + " -- currentVolume\n\t" +
                price + " -- price";
        System.out.println() ;//getAID().getName()
       //Main.controller.addTextAgent(str);
    }
    public void dataChange(){
//        DatabaseHandler dbh = new DatabaseHandler();
        for(int i = 0; i < Market.gridsList.size(); i++ ) {
            if (this.getLocalName().equals(Market.gridsList.get(i).getName())) {
//                dbh.gridUpdate(Controller.gridsList.get(i));
            }
        }
    }
    public void dataChange(Grid grid){
//        DatabaseHandler dbh = new DatabaseHandler();
//        dbh.gridUpdate(grid);
    }

    /**
     This is invoked by the GUI when the user adds a new book for sale
     */
}
