package com.arrenaid.mmarketREST.controllermultiagent;

import com.arrenaid.mmarketREST.model.entity.Grid;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import org.springframework.stereotype.Component;

import java.util.Scanner;

import static com.arrenaid.mmarketREST.controllermultiagent.Market.*;

//дописал component
@Component
public class MainController{
    private final int numberOfAgents = 0;
    private int countBuyer = 1;
    private int countSeller = 1;
    private Runtime rt;
    private Profile profile;
    private ContainerController cc;
    private AgentController agent;

    public void initAgents(){
        Market.initMarket();
       //---// rt = Runtime.instance();
        rt = Runtime.instance();
        ///create controller host
        profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        profile.setParameter(Profile.MAIN_PORT,"10098");
        profile.setParameter(Profile.GUI,"true");
        profile.setParameter(Profile.MAIN,"true");
        cc = rt.createMainContainer(profile);
        try{
            while( Market.getCountSeller() <= numberOfAgents && Market.getCountBuyer() <= numberOfAgents){
                initNewAgent(Role.SELLER);
                initNewAgent(Role.BUYER);
            }
            cycleConsoleControllerRun();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    void cycleConsoleControllerRun(){
        Scanner in = new Scanner(System.in);
        int index = 451;
        while (true) {
            switch (index) {
                case 451:
                    System.out.println("> - count BUYER = "+ getCountBuyer() + "\n> - count SELLER = " + getCountSeller());
                    System.out.println("MC> -- enter number -- \n> - 2 + buyer\n> - 3 + seller");
                    index = in.nextInt();
                    break;
                case 1:
                    System.out.println("MC> -- 1 -- ");
                    Market.printGrids();
                    index = 451;
                    break;
                case 2:
                    System.out.println("MC> -- 2 -- ");
                    initNewAgent(Role.BUYER);
                    index = 451;
                    break;
                case 3:
                    System.out.println("MC> -- 3 -- ");
                    this.initNewAgent(Role.SELLER);
                    index = 451;
                    break;
                case 0:
                    System.out.println("MC> -- 0 exit -- ");
                    shutDown();
                    return;
                default:
                    index = 451;
                    break;
            }
        }
    }
    private String initRandomAgentGridList(int index, Role role){
        Grid grid = new Grid();
        RandValue r = new RandValue();
        grid.setName(role +"-" + Integer.toString(index));
        if(role.equals(Role.SELLER)) {
            grid.setMaxVolume(r.RandValue(101, 250));
            grid.setCurrentVolume(r.RandValue(150, grid.getMaxVolume()));
            grid.setCost(r.RandValue(19, 35));
        }
        else {
            grid.setMaxVolume(r.RandValue(75,199));
            grid.setCurrentVolume(r.RandValue(grid.getMaxVolume()));
            grid.setCost(r.RandValue(19,99));
        }
        grid.setRole(role);
        //gridsList.add(grid);
        grids.put(grid.getName(),grid);
        return grid.getName();
    }
    private void initNewAgent(Role role){
        try{
            int count;
            if(role.equals(Role.BUYER)){
                count = countBuyer;
                countBuyer++;
                Market.setCountBuyer(Market.getCountBuyer() + 1);
            }
            else {
                count = countSeller;
                countSeller++;
                Market.setCountSeller(Market.getCountSeller() + 1);
            }
            this.agent = (AgentController) cc.createNewAgent(initRandomAgentGridList(count,role),
                    "com.arrenaid.mmarketREST.controllermultiagent.buyer.AgentBuyer", null);
            this.agent.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
//    public void initBuyer(){
//        try{
//            this.agent = (AgentController) cc.createNewAgent("AgentBuyer-" + Integer.toString(countBuyer), "multiagent.AgentBuyer", null);
//            this.agent.start();
//            countBuyer++;
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    public void initSeller(){
//        try{
//            this.agent = (AgentController) cc.createNewAgent("AgentSeller-" + Integer.toString(countSeller),"multiagent.AgentSeller",null);
//            this.agent.start();
//            countSeller++;
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    void shutDown(){
        rt.shutDown();
    }


}
