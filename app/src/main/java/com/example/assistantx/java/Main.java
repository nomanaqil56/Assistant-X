package com.example.assistantx.java;

import com.example.assistantx.java.mypackage.Student;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Student student = new Student();

        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        System.out.println("Hello, " + name);

        student.display();

        sc.close();
    }
}