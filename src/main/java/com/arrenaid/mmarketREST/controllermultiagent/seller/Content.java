package com.arrenaid.mmarketREST.controllermultiagent.seller;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private AID agent;
    private double volume;
    private double cost;
    private ACLMessage message;
    private double calculatedPrice;
    private boolean isParticipant;
}