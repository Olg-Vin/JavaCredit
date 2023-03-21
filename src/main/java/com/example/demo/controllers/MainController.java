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
@Controller
public class MainController {
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
        return "avtocredit";
    }
//____________________________________________________________________________________________________________

    /**
     * Класс обрабатывает POST запрос по адресу "/potrebitel"
     * @param user_name - имя пользователя значение по молчанию 0
     * @param user_email - e-mail пользователя значение по молчанию 0
     * @param summa_credit - сумма кредита значение по молчанию 0
     * @param time_credit - срок кредитования значение по молчанию 0
     * @param dohod_credit - доход значение по молчанию 0
     * @param dependent_credit - количество иждевенцев значение по молчанию 0
     * @param model - модель html страници "true" или "false"
     * @param user_message - сообщение пользователя значение по молчанию 0
     * @return - модель html страници "true" или "false" значение по молчанию 0
     */
    @PostMapping("/potrebitel")
    public String PotrebitelCredit(@RequestParam(defaultValue = "0") String user_name,
                                   @RequestParam(defaultValue = "0") String user_email,
                                   @RequestParam(defaultValue = "0") long summa_credit,
                                   @RequestParam(defaultValue = "0") double time_credit,
                                   @RequestParam(defaultValue = "0") long dohod_credit,
                                   @RequestParam(defaultValue = "0") int dependent_credit,
                                   @RequestParam(defaultValue = "0") String user_message, Model model) {
        long maxSum = 1000000;
        long minSum = 10000;
        double maxTime = 24;
        if (summa_credit>maxSum || summa_credit<minSum || time_credit>maxTime || user_email.equals("0")
            || user_name.equals("0") || time_credit == 0 || dohod_credit == 0){
            return "error";
        }
        double Ps;
        String time_credit1 = String.valueOf(time_credit).replace(".", ",");
        Ps = GetPercent() + 4.4;
        model.addAttribute("percent", Ps);
//___________расчёты для полученых данных____________________________________________________________________
        long x = CalculateFor.CountPayment(Ps, summa_credit, (int) time_credit); /*ожидаемая выплата в месяц*/
        long op = CalculateFor.Overpayment(Ps, summa_credit, (int) time_credit); /*переплата по процентам*/
        long gt = CalculateFor.GrandTotal(Ps, summa_credit, (int) time_credit); /*итоговая сумма кредита с процентами*/
//__________перерасчёт_______________________________________________________________________________________
        long pay = CalculateFor.NecessaryIncome(dohod_credit, dependent_credit); /*то, сколько человек реально может выплатить*/
        long s = CalculateFor.CountOtherPayment(Ps, (int) time_credit, pay); /*перерасчёт суммы кредита на тот же срок*/
        System.out.println("s = " + s);
        System.out.println("summa_credit = " + summa_credit);
        int maxN = 24;
        double time = CalculateFor.CountOtherTime(Ps, summa_credit, (int) time_credit, pay, maxN); /*перерасчёт срока на ту же сумму*/
//____________________________________________________________________________________________________________
        model.addAttribute("payment", x);
        if (time != 0 && time != time_credit && time <= maxN && !time_credit1.equals(String.valueOf(time))) {
            model.addAttribute("text", "Вы можете взять ту же сумму на срок:");
            model.addAttribute("time", time);
            model.addAttribute("month", "месяцев");
        }
        else {model.addAttribute("text",
                    "Мы не можем уменьшить ваш срок на другой при такой сумме");}
        if (s >= maxSum){s = maxSum;}
        if (summa_credit != s) {
            model.addAttribute("textForSumm", "Вы можете взять сумму в размере");
            model.addAttribute("summa", s);
            model.addAttribute("textForSumm2", "р. на тот же срок.");
        }
        else {model.addAttribute("textForSumm",
                    "Мы не можем изменить вашу сумму, она и так оптимальна");}
        if (x > pay){
            return "false";
        }
        return "true";
    }
    /**
     * Класс обрабатывает POST запрос по адресу "/ipoteka"
     * @param user_name - имя пользователя значение по умолчанию 0
     * @param user_email - e-mail пользователя значение по умолчанию 0
     * @param summa_credit - сумма кредита значение по умолчанию 0
     * @param time_credit - срок кредитования значение по умолчанию 0
     * @param dohod_credit - доход значение по умолчанию 0
     * @param dependent_credit - количество иждевенцев значение по умолчанию 0
     * @param contribution_credit - первоначальный взнос значение по умолчанию 0
     * @param model - модель html страници "true" или "false"
     * @param user_message - сообщение пользователя значение по умолчанию 0
     * @param type_pledge1 - залог квартиры значение по умолчанию v1 - обязатольно в залоге
     * @param type_pledge2 - залог автомобиль значение по умолчанию 0
     * @param type_pledge3 - залог ценные бумаги значение по умолчанию 0
     * @param type_pledge4 - залог акции значение по умолчанию 0
     * @param type_pledge5 - залог драгоценные металлы значение по умолчанию 0
     * @return - модель html страници "true" или "false"
     */
    @PostMapping("/ipoteka")
    public String IpotekaCredit(@RequestParam(defaultValue = "0") String user_name,
                                @RequestParam(defaultValue = "0") String user_email,
                                @RequestParam(defaultValue = "0") long summa_credit,
                                @RequestParam(defaultValue = "0") double time_credit,
                                @RequestParam(defaultValue = "0") long dohod_credit,
                                @RequestParam(defaultValue = "0") int dependent_credit,
                                @RequestParam(defaultValue = "0") long contribution_credit,
                                @RequestParam(defaultValue = "v1") String type_pledge1,
                                @RequestParam(defaultValue = "0") String type_pledge2,
                                @RequestParam(defaultValue = "0") String type_pledge3,
                                @RequestParam(defaultValue = "0") String type_pledge4,
                                @RequestParam(defaultValue = "0") String type_pledge5,
                                @RequestParam(defaultValue = "0") String user_message,
                                Model model){

        long maxSum = 15000000;
        long minSum = 100000;
        double maxTime = 30;
        if (summa_credit>maxSum || summa_credit<minSum || time_credit>maxTime || user_email.equals("0")
                || user_name.equals("0") || time_credit == 0 || dohod_credit == 0){
            return "error";
        }
        double Ps;
        double time_credit1 = time_credit;
//        переход от годов к месяцам
        String b = String.valueOf(time_credit).replace(".", ",");
        String[] a = b.split(",");
        time_credit = Integer.parseInt(a[0])*12 + Integer.parseInt(a[1]);
        long summa_credit1 = summa_credit;
        summa_credit -= contribution_credit;

/*___________расчёт ставки_________________________________________________если первоначальный взнос больше 20%*/
        if (contribution_credit>=summa_credit*0.2){Ps = GetPercent() + 2;}
        else {Ps = GetPercent() + 3;}
        model.addAttribute("percent", Ps);
//___________расчёты для полученых данных____________________________________________________________________
        long x = CalculateFor.CountPayment(Ps, summa_credit, (int) time_credit); /*ожидаемая выплата в месяц*/
        long op = CalculateFor.Overpayment(Ps, summa_credit, (int) time_credit); /*переплата по процентам*/
        long gt = CalculateFor.GrandTotal(Ps, summa_credit, (int) time_credit); /*итоговая сумма кредита с процентами*/
//__________перерасчёт_______________________________________________________________________________________
        long pay = CalculateFor.NecessaryIncome(dohod_credit, dependent_credit); /*то, сколько человек реально может выплатить*/
        long s = CalculateFor.CountOtherPayment(Ps, (int) time_credit, pay); /*перерасчёт суммы кредита на тот же срок*/
        int maxN = 360;
        double time = CalculateFor.CountOtherTime(Ps, summa_credit, (int) time_credit, pay, maxN); /*перерасчёт срока на ту же сумму*/
        double timeNew = CalculateFor.CountOtherTime(Ps, s, (int) time_credit, pay, maxN); /*перерасчёт срока на новую сумму*/
//____________________________________________________________________________________________________________
        model.addAttribute("payment", x);
//        обратный переход от месяцев к годам с округлением
        BigDecimal resultTime = new BigDecimal(time/12);
        resultTime = resultTime.setScale(1, RoundingMode.CEILING);

        if ((time != 0) && (time <= maxN) && !resultTime.equals(BigDecimal.valueOf(time_credit1)) && summa_credit!=s
                && resultTime.min(BigDecimal.valueOf(time_credit1)).equals(resultTime)) {
            model.addAttribute("text", "Вы можете взять ту же сумму на срок:");
            model.addAttribute("time", resultTime);
            model.addAttribute("month", "лет");
        }
        else {model.addAttribute("text",
                    "Мы не можем уменьшить ваш срок на другой при такой сумме");}
        System.out.println(summa_credit1);
        System.out.println(s);
        if (s >= maxSum){s=maxSum;}
        if (summa_credit1 != s) {
            model.addAttribute("textForSumm", "Вы можете взять сумму в размере");
            model.addAttribute("summa", s);
            model.addAttribute("textForSumm2", "р. на тот же срок.");
        }
        else {model.addAttribute("textForSumm",
                    "Мы не можем изменить вашу сумму, она и так оптимальна");}

        if (x > pay){
            return "false";
        }
        return "true";
    }
    /**
     * Класс обрабатывает POST запрос по адресу "/avtocredit"
     * @param user_name - имя пользователя
     * @param user_email - e-mail пользователя
     * @param summa_credit - сумма кредита
     * @param time_credit - срок кредитования
     * @param dohod_credit - доход
     * @param dependent_credit - количество иждевенцев
     * @param contribution_credit - первоначальный взнос
     * @param model - модель html страници "true" или "false"
     * @param user_message - сообщение пользователя значение по умолчанию 0
     * @param type_pledge1 - залог автомобиль значение по умолчанию v1 - обязатольно в залоге
     * @param type_pledge2 - залог квартира значение по умолчанию 0
     * @param type_pledge3 - залог ценные бумаги значение по умолчанию 0
     * @param type_pledge4 - залог акции значение по умолчанию 0
     * @param type_pledge5 - залог драгоценные металлы значение по умолчанию 0
     * @return - модель html страници "true" или "false"
     */
    @PostMapping("/avtocredit")
    public String AvtoCredit(@RequestParam(defaultValue = "0") String user_name,
                             @RequestParam(defaultValue = "0") String user_email,
                             @RequestParam(defaultValue = "0") long summa_credit,
                             @RequestParam(defaultValue = "0") double time_credit,
                             @RequestParam(defaultValue = "0") long dohod_credit,
                             @RequestParam(defaultValue = "0") int dependent_credit,
                             @RequestParam(defaultValue = "0") long contribution_credit,
                             @RequestParam(defaultValue = "0") String user_message,
                             @RequestParam(defaultValue = "v1") String type_pledge1,
                             @RequestParam(defaultValue = "0") String type_pledge2,
                             @RequestParam(defaultValue = "0") String type_pledge3,
                             @RequestParam(defaultValue = "0") String type_pledge4,
                             @RequestParam(defaultValue = "0") String type_pledge5,
                                Model model){
        long maxSum = 5000000;
        long minSum = 100000;
        double maxTime = 5;
        if (summa_credit>maxSum || summa_credit<minSum || time_credit>maxTime || user_email.equals("0")
                || user_name.equals("0") || time_credit == 0 || dohod_credit == 0){
            return "error";
        }
        double Ps;
        double time_credit1 = time_credit;
//        переход от годов к месяцам
        String b = String.valueOf(time_credit).replace(".", ",");
        String[] a = b.split(",");
        time_credit = Integer.parseInt(a[0])*12 + Integer.parseInt(a[1]);
        long summa_credit1 = summa_credit;
        summa_credit -= contribution_credit;

        /*___________расчёт ставки_________________________________________________если первоначальный взнос больше 20%*/
        if (contribution_credit>=summa_credit*0.2){Ps = GetPercent() + 2;}
        else {Ps = GetPercent() + 3;}
        model.addAttribute("percent", Ps);
//___________расчёты для полученых данных____________________________________________________________________
        long x = CalculateFor.CountPayment(Ps, summa_credit, (int) time_credit); /*ожидаемая выплата в месяц*/
        long op = CalculateFor.Overpayment(Ps, summa_credit, (int) time_credit); /*переплата по процентам*/
        long gt = CalculateFor.GrandTotal(Ps, summa_credit, (int) time_credit); /*итоговая сумма кредита с процентами*/
//__________перерасчёт_______________________________________________________________________________________
        long pay = CalculateFor.NecessaryIncome(dohod_credit, dependent_credit); /*то, сколько человек реально может выплатить*/
        long s = CalculateFor.CountOtherPayment(Ps, (int) time_credit, pay); /*перерасчёт суммы кредита*/
        int maxN = 60;
        double time = CalculateFor.CountOtherTime(Ps, summa_credit, (int) time_credit, pay, maxN); /*перерасчёт срока на ту же сумму*/
//____________________________________________________________________________________________________________
        model.addAttribute("payment", x);
//        обратный переход от месяцев к годам с округлением
        BigDecimal resultTime = new BigDecimal(time/12);
        resultTime = resultTime.setScale(1, RoundingMode.CEILING);

        if ((time != 0) && (time <= maxN) && !resultTime.equals(BigDecimal.valueOf(time_credit1)) && summa_credit!=s
                && resultTime.min(BigDecimal.valueOf(time_credit1)).equals(resultTime)) {
            model.addAttribute("text", "Вы можете взять ту же сумму на срок:");
            model.addAttribute("time", resultTime);
            model.addAttribute("month", "лет");
        }
        else {model.addAttribute("text",
                    "Мы не можем уменьшить ваш срок на другой при такой сумме");}
        if (s >= maxSum){s = maxSum;}
        if (summa_credit1 != s) {
            model.addAttribute("textForSumm", "Вы можете взять сумму в размере");
            model.addAttribute("summa", s);
            model.addAttribute("textForSumm2", "р. на тот же срок.");
        }
        else {model.addAttribute("textForSumm",
                    "Мы не можем изменить вашу сумму, она и так оптимальна");}
        if (x > pay){
            return "false";
        }
        return "true";
    }
}