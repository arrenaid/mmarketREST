package com.arrenaid.mmarketREST.controllermultiagent.buyer;


import com.arrenaid.mmarketREST.controllermultiagent.Market;
import com.arrenaid.mmarketREST.model.entity.Grid;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import com.arrenaid.mmarketREST.controllermultiagent.RandValue;
//import sample.Controller;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class AgentBuyer extends Agent {
    public double targetVolume;
    private AID[] sellerAgents;
    // Put agent initializations HERE
    public double currentVolume;
    public double maxVolume;
    public double maxCost;
    //time
    private long tick = TimeUnit.SECONDS.toMillis(5);
    private int countTick = 0;

    protected void setup() {
        //hi
        AID id = new AID("Maxim", AID.ISLOCALNAME);
        init();
        info();
    ///поведение
        addBehaviour(new TickerBehaviour(this, tick) {
            private int MAX_STEPS = 8;
            private int currentStep = 0;
             @Override
             protected void onTick() {
                 //главное
                 addBehaviour(new RequestPerformer(targetVolume,maxCost));
                 currentStep++;
                 if(countTick > 5) {
                     this.myAgent.addBehaviour(new WakerBehaviour(this.myAgent, tick) {
                         @Override
                         protected void onWake() {
                             System.out.println(" -- waiting -- " + currentStep + "\tcountTick -- "
                                     + countTick + " -- " + this.myAgent.getLocalName());
                             super.onWake();
                         }
                     });

                     tick = TimeUnit.SECONDS.toMillis(5);
                     countTick = 0;
                 }
                 else{
                     countTick++;
                     tick += TimeUnit.SECONDS.toMillis(countTick);
                 }
                //if (currentStep <= MAX_STEPS) currentStep++;
                 //else this.stop();


             }
        });
    }


    protected void takeDown(){
        info();
        //dataChange();
//        for(int i = 0; i < Market.gridsList.size(); i++ ) {
//            if (this.getLocalName().equals(Market.gridsList.get(i).getName())) {
//                Market.gridsList.get(i).setStatus(false);
//                Market.oldGridsList.add(Market.gridsList.get(i));
//                dataChange(Market.gridsList.get(i));
//                try {
//                    Market.gridsList.remove(i);
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                    try{
//                        Market.gridsList.remove(i);
//                    }catch (Exception ex){
//                        ex.printStackTrace();
//                        Market.gridsList.remove(i);
//                    }
//                }
//            }
//        }
        Market.grids.remove(this.getLocalName());
//        Controller.updateTableViewGrids();
        Market.setCountBuyer(Market.getCountBuyer()-1);
        System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
    }

    public AID [] searchSellers(){
        // Update the list of seller agents
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling");
        template.addServices(sd);
        try {
            //System.out.println("Buyer-agent " + getAID().getName() + " -- START find DF step -- "+ currentStep);
            DFAgentDescription[] result = DFService.search(this, template);//myAgent
            sellerAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                sellerAgents[i] = result[i].getName();
                System.out.println(this.getLocalName() + " -- find Sellers-agent -- " + sellerAgents[i].getLocalName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return sellerAgents;
    }
    public void info(){
        String str = this.getName()+this.getLocalName() + " is ready\n\t" + maxVolume +
                " -- maxVolume\n\t" + currentVolume + " -- currentVolume\n\t" +
                maxCost + " -- maxCost";
        System.out.println(str) ;

        //Main.controller.addTextAgent(str);

    }
    protected void initRand(){
        RandValue r = new RandValue();
        maxVolume = r.RandValue(maxVolume/2,maxVolume);
        currentVolume = r.RandValue(maxVolume);
        maxCost = r.RandValue(3,99);
        targetVolume = maxVolume - currentVolume;
    }
    private void init(){
        Grid grid = Market.grids.get(this.getLocalName());
        if(grid == null){
            System.out.println("Error: class - AgentBuyer: don`t find grid in grids. don`t find - " + this.getLocalName());
            initRand();
        }
        else{
            maxVolume = grid.getMaxVolume();
            currentVolume = grid.getCurrentVolume();
            maxCost = grid.getCost();
            targetVolume = maxVolume - currentVolume;
            //Market.gridsList.get(Market.gridsList.indexOf(grid)).setStatus(true);
            grid.setStatus(true);
            Market.grids.put(this.getLocalName(),grid);
        }
    }
//    private void init(){
//        boolean isFilled = false;
//        for(int i = 0; i < Market.gridsList.size();i++ ) {
//            if (this.getLocalName().equals( Market.gridsList.get(i).getName())){
//                maxVolume = Market.gridsList.get(i).getMaxVolume();
//                currentVolume = Market.gridsList.get(i).getCurrentVolume();
//                maxCost = Market.gridsList.get(i).getCost();
//                targetVolume = maxVolume - currentVolume;
//                Market.gridsList.get(i).setStatus(true);
//                isFilled = true;
//                break;
//            }
//        }
//        if(!isFilled){
//            initRand();
//        }
//    }
    public void dataChange(){
////        DatabaseHandler dbh = new DatabaseHandler();
//        for(int i = 0; i < Market.gridsList.size(); i++ ) {
//            if (this.getLocalName().equals(Market.gridsList.get(i).getName())) {
////                dbh.gridUpdate(Controller.gridsList.get(i));
//            }
//        }


    }
    public void dataChange(Grid grid){
//        DatabaseHandler dbh = new DatabaseHandler();
//        dbh.gridUpdate(grid);
//        //dbh.addLoyalty();

    }

    public double getTargetVolume(){return targetVolume;}
    protected boolean isRun(){
        if (currentVolume > (maxVolume * 0.7))
            return false;
        else return true;
    }
}
