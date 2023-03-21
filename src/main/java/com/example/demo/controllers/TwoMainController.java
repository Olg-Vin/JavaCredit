package com.example.demo.controllers;

import com.example.demo.Calculate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Класс-контроллер для обработки GET и POST
 */
//@Controller /*отвечает за обработку всех переходов на сайте*/
public class TwoMainController {
    public static int var;
    /**
     * Класс парсит ключевую ставку с главного сайта ЦБ
     * @return возвращает P в случает удачного парсинга и 0 в случае ошибки
     */
    public static double GetPercent(){ // получить процент ключевая ставка от цб
        try {
            Document document = Jsoup.connect("https://cbr.ru").get();
            Elements element = document.select("#content > div > div > div > div.home-main > div.home-main_aside > div > div:nth-child(3) > div.main-indicator_value");
//            System.out.println(element);
            String P = element.text();
            P = P.substring(0, P.length()-1).replace(',', '.'); /*ключевая ставка от цб*/
//            #content > div > div > div > div.home-main > div.home-main_aside > div > div:nth-child(3) > div.main-indicator_value
            return Double.parseDouble(P);
        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
//        return 0;
    }
//___________________________________________________________GET______________________________________________

    /**
     * Класс обрабатывает GET запрос по адресу "/potrebitel"
     * @param model принимает в себя
     * @return возвращает текст - название html модели, которая будет открыта в браузере
     */
    @GetMapping("/potrebitel")
    public String form1(Model model) throws MalformedURLException {/*возвращаем строку - название шаблона*/
        double P = GetPercent() + 4.4; /*процент от ...*/
        model.addAttribute("percent", P); /*передаём данные внутрь шаблона*/
        var = 1;
        System.out.println(var);
        return "potrebitel";
    }
    /**
     * Класс обрабатывает GET запрос по адресу "/ipoteka"
     * @param model принимает в себя
     * @return возвращает текст - название html модели, которая будет открыта в браузере
     */
    @GetMapping("/ipoteka")
    public String form2(Model model) {
        double P = 5.5; /*процент от ...*/
        model.addAttribute("percent", P);
        var = 2;
        System.out.println(var);
        return "ipoteka";
    }
    /**
     * Класс обрабатывает GET запрос по адресу "/avtocredit"
     * @param model принимает в себя
     * @return возвращает текст - название html модели, которая будет открыта в браузере
     */
    @GetMapping("/avtocredit")
    public String form3(Model model) {
        double P = GetPercent() + 2.5; /*процент от ...*/
        model.addAttribute("percent", P);
        var = 3;
        System.out.println(var);
        return "avtocredit";
    }
//____________________________________________________________________________________________________________



    /**
     * Класс обрабатывает POST запрос по адресу "/form"
     * @param user_name - имя пользователя
     * @param user_email - e-mail пользователя
     * @param summa_credit - сумма кредита
     * @param time_credit - срок кредитования
     * @param dohod_credit - доход
     * @param dependent_credit - количество иждевенцев
     * @param contribution_credit - первоначальный взнос
     * @param model - модель html страници "true" или "false"
     * @return - модель html страници "true" или "false"
     */
    @PostMapping("/form")
    public String Credit(@RequestParam String user_name, @RequestParam String user_email,
                                @RequestParam long summa_credit, @RequestParam double time_credit,
                                @RequestParam long dohod_credit, @RequestParam int dependent_credit,
                                @RequestParam(defaultValue = "0") long contribution_credit,
//                                @RequestParam String type_pledge1,
                                Model model){

        long summa = summa_credit;
        double N = time_credit;
        /*проверка введёных данных*/
        switch (var){
            case 1:
                if (summa>1000000 || summa<10000 || N>24.0){
                    System.out.println(var);
                    System.out.println(summa_credit);
                    System.out.println(time_credit);
                    return "error";
                }
            case 2:
                if (summa>1500000 || summa<100000 || N>360){
                    return "error";
                }
            case 3:
                if (summa>500000 || summa<100000 || N>60){
                    return "error";
                }
        }
        String b; String[] a; long summa_credit1 = 0;
        double Ps = 0;
        double time_credit1 = time_credit;
        switch (var) {
            case 1 -> {
                Ps = GetPercent() + 4.4;
                model.addAttribute("percent", Ps);
            }
            case 2 -> {
                /*переход от годов к месяца*/
                b = String.valueOf(time_credit).replace(".", ",");
                a = b.split(",");
                time_credit = Integer.parseInt(a[0]) * 12 + Integer.parseInt(a[1]);
                summa_credit1 = summa_credit;
                summa_credit -= contribution_credit;
                /*___________расчёт ставки_________________________________________________если первоначальный взнос больше 20%*/
                if (contribution_credit>=summa_credit*0.2){Ps = GetPercent() + 2;}
                else {Ps = GetPercent() + 3;}
                model.addAttribute("percent", Ps);
            }
            case 3 -> {
                /*переход от годов к месяцам*/
                b = String.valueOf(time_credit).replace(".", ",");
                a = b.split(",");
                time_credit = Integer.parseInt(a[0]) * 12 + Integer.parseInt(a[1]);
                summa_credit1 = summa_credit;
                summa_credit -= contribution_credit;
                /*___________расчёт ставки_________________________________________________если первоначальный взнос больше 20%*/
                if (contribution_credit>=summa_credit*0.2){Ps = GetPercent() + 2;}
                else {Ps = GetPercent() + 3;}
                model.addAttribute("percent", Ps);
            }

        }

//___________расчёты для полученых данных____________________________________________________________________
        long x = CalculateFor.CountPayment(Ps, summa_credit, (int) time_credit); /*ожидаемая выплата в месяц*/
        long op = CalculateFor.Overpayment(Ps, summa_credit, (int) time_credit); /*переплата по процентам*/
        long gt = CalculateFor.GrandTotal(Ps, summa_credit, (int) time_credit); /*итоговая сумма кредита с процентами*/
//__________перерасчёт_______________________________________________________________________________________
        long pay = CalculateFor.NecessaryIncome(dohod_credit, dependent_credit); /*то, сколько человек реально может выплатить*/
        long s = CalculateFor.CountOtherPayment(Ps, (int) time_credit, pay); /*перерасчёт суммы кредита на тот же срок*/
        System.out.println("s = " + s);
        System.out.println("summa_credit = " + summa_credit);
        int maxN = 0;
        switch (var){
            case 1: maxN = 24;
            case 2: maxN = 360;
            case 3: maxN = 60;
        }
        System.out.println("maxN = " + maxN);
        double time = CalculateFor.CountOtherTime(Ps, summa_credit, (int) time_credit, pay, maxN); /*перерасчёт срока на ту же сумму*/
        double timeNew = CalculateFor.CountOtherTime(Ps, s, (int) time_credit, pay, maxN); /*перерасчёт срока на новую сумму*/
//____________________________________________________________________________________________________________
        model.addAttribute("payment", x);
//        обратный переход от месяцев к годам с округлением
        BigDecimal resultTime = new BigDecimal(time/12);
        resultTime = resultTime.setScale(1, RoundingMode.CEILING);
//        String[] c = result.split(",");
//        result = c[0] + "," +String.valueOf(Integer.parseInt(c[1])+1);
        System.out.println("time_credit1 = " + time_credit1);
        System.out.println("result = " + resultTime);
        System.out.println("time = " + time);
        System.out.println("time_credit = " + time_credit);
        if ((time != 0) && (time <= maxN) && !resultTime.equals(BigDecimal.valueOf(time_credit1)) && summa_credit!=s) {
            model.addAttribute("text", "Вы можете взять ту же сумму на срок:");
            model.addAttribute("time", resultTime);
            model.addAttribute("month", "лет");
//            model.addAttribute("timeNew", );
        }
        else {
            model.addAttribute("text", "Мы не можем уменьшить ваш срок на другой при такой сумме");
        }
        if (summa_credit1 < s) {
            model.addAttribute("textForSumm", "Вы можете взять сумму в размере");
            model.addAttribute("summa", s);
            model.addAttribute("textForSumm2", "р. на тот же срок.");
        }
        else {
            model.addAttribute("textForSumm", "Мы не можем изменить вашу сумму, она и так оптимальна");
        }

        if (x > pay){
            return "false";
        }
        return "true";
    }

}