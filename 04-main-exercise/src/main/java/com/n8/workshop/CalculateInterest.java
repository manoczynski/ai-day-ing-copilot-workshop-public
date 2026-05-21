package com.n8.workshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateInterest {

    public double calc(double a, int t, String type) {
        double r;
        if (type.equals("R")) {
            r = 0.02;
        } else if (type.equals("S")) {
            r = 0.05;
        } else if (type.equals("P")) {
            r = 0.07;
        } else {
            r = 0.01;
        }

        double res = a;
        for (int i = 0; i < t; i++) {
            res = res + res * r;
        }

        double tax = (res - a) * 0.19;
        res = res - tax;

        if (a > 100000) {
            res = res - 50;
        } else if (a > 50000) {
            res = res - 20;
        }

        return res;
    }

    public Map<Integer, Double> getMonthlyBalances(double a, int t, String type) {
        Map<Integer, Double> m = new HashMap<>();
        double r;
        if (type.equals("R")) {
            r = 0.02;
        } else if (type.equals("S")) {
            r = 0.05;
        } else if (type.equals("P")) {
            r = 0.07;
        } else {
            r = 0.01;
        }

        double mr = r / 12;
        double res = a;
        int months = t * 12;
        for (int i = 1; i <= months; i++) {
            res = res + res * mr;
            m.put(i, res);
        }
        return m;
    }

    public List<String> generateReport(double a, int t, String type) {
        List<String> lines = new ArrayList<>();
        double result = calc(a, t, type);
        lines.add("Kwota poczatkowa: " + a);
        lines.add("Okres (lata): " + t);
        lines.add("Typ konta: " + type);
        lines.add("Kwota koncowa: " + result);
        lines.add("Zysk: " + (result - a));
        return lines;
    }

    public static void main(String[] args) {
        CalculateInterest c = new CalculateInterest();
        double r1 = c.calc(10000, 5, "S");
        System.out.println("Wynik: " + r1);
        for (String s : c.generateReport(60000, 3, "P")) {
            System.out.println(s);
        }
    }
}
