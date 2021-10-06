package com.example.myen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@SpringBootApplication
public class MyenApplication implements CommandLineRunner {

    public final Map<String, List<String>> F_MAP = new HashMap<>();

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/index")
    public Object en(@RequestParam(name = "key", required = false, defaultValue = "a") String key, Model model) {
        List<String> strings = F_MAP.get(key);
        Collections.shuffle(strings);
        model.addAttribute("list", strings);
        return "index";
    }

    public static void main(String[] args) {
        SpringApplication.run(MyenApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //Resource resource = new ClassPathResource("classpath:en/en.txt");
        Resource resource = resourceLoader.getResource("classpath:en/en.txt");

        List<String> strings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            for (String line; (line = br.readLine()) != null; ) {
                strings.add(line.trim());
            }
        }
        Map<String, List<String>> listMap = strings
                .stream()
                .collect(Collectors.groupingBy(x -> x.substring(0, 1).toLowerCase(Locale.ROOT)));

        F_MAP.putAll(listMap);
    }
}