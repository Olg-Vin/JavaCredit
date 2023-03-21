package com.example.demo.controllers;

public class CalculateFor {
    /**
     * Класс для подсчёта ежемесячной выплаты
     * @param Ps - процентная ставка
     * @param S - сумма кредита
     * @param N - срок кредитования
     * @return возвращает x
     */
    public static long CountPayment(double Ps, long S, int N){
        Ps = Ps/(100*12);
        long x = (long) (S * (Ps + (Ps/(Math.pow(1+Ps, N)-1))));
        return x;
    }
    /**
     * Класс для подсчёта переплаты по процентам
     * @param Ps - процентная ставка
     * @param S - сумма кредита
     * @param N - срок кредитования
     * @return возвращает op
     */
    public static long Overpayment(double Ps, long S, int N){
        Ps = Ps/(100*12);
        long op = (long) (Ps * (N+1)/2*S);
        return op;
    }
    /**
     * Класс для подсчёта итоговой суммы кредита
     * @param Ps - процентная ставка
     * @param S - сумма кредита
     * @param N - срок кредитования
     * @return возвращает gt
     */
    public static long GrandTotal(double Ps, long S, int N){
        long gt = S+Overpayment(Ps, S, N);
        return gt;
    }

    /**
     * Класс для подсчёта необходимого дохода для ежемесячной выплаты
     * @param dohod_credit - доход потребителя
     * @param dependent_credit - количество иждевенцев
     * @return возвращает pay
     */
    public static long NecessaryIncome (long dohod_credit, int dependent_credit){
        long pay = (long) (dohod_credit * (0.8 - 0.03*dependent_credit));
        return pay;
    }
    /**
     * Класс для перерасчёта суммы кредита
     * @param Ps - процентная ставка
     * @param pay - то, сколько потребитель может выплачивать в месяц
     * @param N - срок кредитования
     * @return возвращает x
     */
    public static long CountOtherPayment(double Ps, int N, long pay){
        Ps = Ps/(100*12)+1;
        long s;
        s = (long) (((Math.pow(Ps, N)-1)/((Math.pow(Ps, N+1)-Math.pow(Ps, N))))*pay);
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
    public static int CountOtherTime(double Ps, long S, int N, long pay, int maxN){ /*возвращает значение в месяцах*/
        int time = 1;
        long x = CountPayment(Ps, S, time);
        while ((pay <= x) && time <= maxN){
            time ++;
//            System.out.println(time + ": x = " + x + " pay = " + pay);
            x = CountPayment(Ps, S, time);
        }
        time++;
//        System.out.println(time + ": x = " + x + " pay = " + pay);
//        x = CountPayment(Ps, S, time);
        if (pay < x){
            return 0;
        }
        return time;
    }
}
