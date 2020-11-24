package com.github.xingbo.java8;

import java.util.ArrayList;
import java.util.Iterator;

class CourseGrade {
     public String title;
     public char grade;
}

public class ReportCard {
    public String studentName;
    public ArrayList<CourseGrade> cliens;

     public void printReport() {
         System.out.println("Report card for " + studentName);
         System.out.println("------------------------");
         System.out.println("Course Title       Grade");
         Iterator<CourseGrade> grades = cliens.iterator();
         CourseGrade grade;
         double avg = 0.0d;
         while (grades.hasNext()) {
            grade = grades.next();
            System.out.println(grade.title + "    " + grade.grade);
            if (!(grade.grade == 'F')) {
                    avg = avg + grade.grade - 64;
                }
            }
        avg = avg / cliens.size();
        System.out.println("------------------------");
        System.out.println("Grade Point Average = " + avg);
     }

    public void printReport2() {
        System.out.println("Report card for " + studentName);
        System.out.println("------------------------");

        System.out.println("Course Title       Grade");
        cliens.forEach(it -> System.out.println(it.title + "    " + it.grade));

        double total = cliens.stream().filter(it -> it.grade != 'F')
                .mapToDouble(it -> it.grade - 64).sum();
        System.out.println("------------------------");
        System.out.println("Grade Point Average = "  + total / cliens.size());
    }

    private void printHeader() {
        System.out.println("Report card for " + studentName);
        System.out.println("------------------------");
    }

    private void printGrade() {
        System.out.println("Course Title       Grade");
        cliens.forEach(it -> System.out.println(it.title + "    " + it.grade));
    }

    private void printAverage() {
        double total = cliens.stream().filter(it -> it.grade != 'F')
                .mapToDouble(it -> it.grade - 64).sum();
        System.out.println("------------------------");
        System.out.println("Grade Point Average = "  + total / cliens.size());
        double avg = cliens.stream().filter(it -> it.grade != 'F').mapToDouble(it -> it.grade - 64).average().orElse(0.0d);
    }

    public void printReport3() {
        printHeader();
        printGrade();
        printAverage();
    }

}