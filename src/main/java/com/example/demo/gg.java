package com.example.demo;


import com.example.demo.controllers.CalculateFor;

import java.math.BigDecimal;

public class gg {
    public static void main(String[] args) {
//        double time = 5.3333;
        int time = 1;
        double Ps= 10.5;
        long S = 16179473;
        long pay = 74820;
        int maxN = 360;
        long x = CalculateFor.CountPayment(Ps, S, time);
        while ((pay < x) && time <= maxN){
            time ++;
            x = CalculateFor.CountPayment(Ps, S, time);
            System.out.println(x);
        }
        if (pay < x){
            System.out.println("huy");
        }
        System.out.println(time);
        System.out.println(time/12);

    }
}
