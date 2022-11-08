package com.chanx.rpn;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        RPNCalculator rpnCalculator = new RPNCalculator();
        System.out.println("input pls ...");
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            System.out.println(rpnCalculator.compute(sc.nextLine()));
        }
    }
}
