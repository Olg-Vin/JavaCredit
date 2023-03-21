package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Calculate {

    /**
     * Класс для подсчёта ежемесячной выплаты
     * @param Ps - процентная ставка
     * @param S - сумма кредита
     * @param N - срок кредитования
     * @return возвращает x
     */
    /*ЕЖЕМЕСЯЧНАЯ ВЫПЛАТА*/
    public static double CountPayment(double Ps, double S, int N){
        /* | Ps - процентная ставка | S - сумма кредита |N - срок | */
//        N = N/12;
        Ps = Ps/(100*12);
        double x = S * (Ps + (Ps/(Math.pow(1+Ps, N)-1)));
        return x;
    }
    /**
     * Класс для подсчёта переплаты по процентам
     * @param Ps - процентная ставка
     * @param S - сумма кредита
     * @param N - срок кредитования
     * @return возвращает op
     */
    /*ПЕРЕПЛАТА ПО ПРОЦЕНТАМ*/
    public static double Overpayment(double Ps, double S, int N){
        Ps = Ps/(100*12);
        double op = Ps * (N+1)/2*S;
        return op;
    }
    /**
     * Класс для подсчёта итоговой суммы кредита
     * @param Ps - процентная ставка
     * @param S - сумма кредита
     * @param N - срок кредитования
     * @return возвращает gt
     */
    /*ИТОГОВАЯ СУММА КРЕДИТА*/
    public static double GrandTotal(double Ps, double S, int N){
        double gt = S+Overpayment(Ps, S, N);
        return gt;
    }

    /**
     * Класс для подсчёта необходимого дохода для ежемесячной выплаты
     * @param dohod_credit - доход потребителя
     * @param dependent_credit - количество иждевенцев
     * @return возвращает pay
     */
    /*НЕОБХОДИМЫЙ ДОХОД*/
    public static double NecessaryIncome (double dohod_credit, int dependent_credit){
        double pay = dohod_credit * (0.8 - 0.03*dependent_credit);
        return pay;
    }
    /**
     * Класс для перерасчёта суммы кредита
     * @param Ps - процентная ставка
     * @param pay - то, сколько потребитель может выплачивать в месяц
     * @param N - срок кредитования
     * @return возвращает x
     */
    /*перерасчёт суммы*/
    public static double CountOtherPayment(double Ps, int N, double pay){
        /* | Ps - процентная ставка | N - срок | pay - то, сколько человек реально может заплатить */
        Ps = Ps/(100*12)+1;
        System.out.println("ps = " + Ps);
        double s;
        s = ((Math.pow(Ps, N)-1)/((Math.pow(Ps, N+1)-Math.pow(Ps, N))))*pay;
        System.out.println(s);
        return s;
    }

    /**
     * Класс для перерасчёта срока кредитования
     * @param Ps - процентная ставка
     * @param S - сумма кредита
     * @param N - изначальный срок кредитования
     * @param pay - то, сколько потребитель может выплачивать в месяц
     * @param maxN - максимальный срок кредитования
     * @return time - срок кредитования в месяцах
     */
    /*перерасчёт срока*/
    public static int CountOtherTime(double Ps, double S, int N, double pay, int maxN){ /*возвращает значение в месяцах*/
        int time = 1;
        double x = CountPayment(Ps, S, time);
        while ((pay < x) && time <= maxN){
            time ++;
            x = CountPayment(Ps, S, time);
        }
        if (pay < x){
            return 0;
        }

        return time;
    }
}
