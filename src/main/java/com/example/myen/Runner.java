package com.example.myen;

import java.io.File;
import java.nio.file.Files;

public class Runner {
    public static void main(String[] args) throws Exception {

        String path = "F:\\myen\\src\\main\\resources\\en\\en.txt";
        Files.lines(new File(path).toPath())
                .map(s -> s.trim())
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.contains("["))
                .forEach(System.out::println);

        /*File file = new File(path);

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        int count = 1;
        String line;
        while((line = br.readLine()) != null)
        {
            *//*if(line.contains("password")){
                System.out.println(line);
            }*//*
            System.out.println(line);
            System.out.println(count);
            count++;
        }
        br.close();
        fr.close();*/
    }
}
