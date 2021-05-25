package com.arrenaid.mmarketREST.controllermultiagent;

import java.util.Random;

public class RandValue {
    protected Random r = new Random();

    public double RandValue(){
        return r.nextDouble();
    }

    public double RandValue(double min, double max){
        return  min +( r.nextDouble() * (max - min));
    }
    public double RandValue( double max){
        return   r.nextDouble() * max;
    }

    public int RandValue(int min, int max){
        return r.nextInt(max -min +1) +min;
    }

}
