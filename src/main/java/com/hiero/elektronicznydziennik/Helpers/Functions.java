package com.hiero.elektronicznydziennik.Helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Functions {
    static FileWriter fw;
    static PrintWriter pw;

    public static void SaveLog(Exception e) {
        try {
            fw = new FileWriter("exception.txt", true);
            pw = new PrintWriter(fw);
            e.printStackTrace(pw);
            pw.close();
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void SaveLog(String content) {
        try {
            fw = new FileWriter("exception.txt", true);
            pw = new PrintWriter(fw);
            pw.write(content);
            pw.close();
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
