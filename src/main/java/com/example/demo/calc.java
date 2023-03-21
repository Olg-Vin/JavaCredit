package com.example.demo;

import java.util.Scanner;

public class calc {
    public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);
       System.out.println("Введите тип кредита:\n1-Потребительский кредит\n2-Автокредит\n3-Ипотека\n4-Кредит для иных нужд");
       Integer type = Integer.parseInt(scanner.nextLine());
       switch (type) {
           case 1:
               System.out.println("Введите первоначальную сумму кредита");
               Integer S = Integer.parseInt(scanner.nextLine());
               System.out.println("Введите годовую ставку (в процентах)");
               Double Ps = Double.parseDouble(scanner.nextLine());
               System.out.println("Укажите длительность кредита в месяцах");
               Integer N = Integer.parseInt(scanner.nextLine());
               Double P = (Ps/12)/100;
               Double X = (S*(P+(P/(Math.pow((1+P),N)-1))));
               System.out.printf("%.2f",X);
               break;
           case 2:
               System.out.println("Введите стоимость автомобиля");
               S = Integer.parseInt(scanner.nextLine());
               System.out.println("Введите годовую ставку (в процентах)");
               Ps = Double.parseDouble(scanner.nextLine());
               System.out.println("Укажите длительность кредита в месяцах");
               N = Integer.parseInt(scanner.nextLine());
               System.out.println("Укажите первоночальный взнос");
               Integer V = Integer.parseInt(scanner.nextLine());
               P = (Ps/12)/100;
               X = ((S-V)*(P+(P/(Math.pow((1+P),N)-1))));
               System.out.printf("%.2f",X);
               break;
           case 3:
               System.out.println("Введите первоначальную сумму кредита");
               S = Integer.parseInt(scanner.nextLine());
               System.out.println("Введите годовую ставку (в процентах)");
               Ps = Double.parseDouble(scanner.nextLine());
               System.out.println("Укажите длительность кредита в месяцах");
               N = Integer.parseInt(scanner.nextLine());
               P = (Ps/12)/100;
               X = (S*(P+(P/(Math.pow((1+P),N)-1))));
               System.out.printf("%.2f",X);
               break;
           case 4:
               System.out.println("Введите стоимость квартиры");
               S = Integer.parseInt(scanner.nextLine());
               System.out.println("Введите годовую ставку (в процентах)");
               Ps = Double.parseDouble(scanner.nextLine());
               System.out.println("Укажите длительность кредита в месяцах");
               N = Integer.parseInt(scanner.nextLine());
               System.out.println("Укажите первоночальный взнос");
               V = Integer.parseInt(scanner.nextLine());
               P = (Ps/12)/100;
               X = ((S-V)*(P+(P/(Math.pow((1+P),N)-1))));
               System.out.printf("%.2f",X);
               break;
       }
    }
}
