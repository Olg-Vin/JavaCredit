package com.example.demo;

import org.springframework.stereotype.Controller;

//@Controller
public class Credit {

    private String user_name;
    private String user_email;
    private double credit_sum;
    private double first_money;
    private double all_money;
    private String credit_type;
    private String letter;


    public Credit(){}
    public Credit(String user_name, String user_email, double summa_credit, double contribution_credit, double contribution_credit_m, String type_credit, String user_message) {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public double getCredit_sum() {
        return credit_sum;
    }

    public void setCredit_sum(double credit_sum) {
        this.credit_sum = credit_sum;
    }

    public double getFirst_money() {
        return first_money;
    }

    public void setFirst_money(double first_money) {
        this.first_money = first_money;
    }

    public double getAll_money() {
        return all_money;
    }

    public void setAll_money(double all_money) {
        this.all_money = all_money;
    }

    public String getCredit_type() {
        return credit_type;
    }

    public void setCredit_type(String credit_type) {
        this.credit_type = credit_type;
    }



    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
}









