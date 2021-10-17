package com.example.myen;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@SpringBootApplication
public class MyenApplication implements CommandLineRunner {

    public final Map<String, List<String>> FIRST_WOLD = new HashMap<>();
    public final Set<String> SECOND_WOLD = new HashSet<>();
    public final Set<String> THIRD_WOLD = new HashSet<>();

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/index/{id}")
    public Object o1(@PathVariable("id") Long id, Model model) {
        List<String> strings = null;
        if (id == 1L) {
            Collection<List<String>> values = FIRST_WOLD.values();
            strings = values.stream().flatMap(List::stream).collect(Collectors.toList());
        } else if (id == 2L) {
            strings = new ArrayList<>(SECOND_WOLD);
        } else if (id == 3L) {
            strings = new ArrayList<>(THIRD_WOLD);
        }
        Collections.shuffle(strings);
        strings.add(0, "数量：" + strings.size());
        model.addAttribute("list", strings);
        return "index" + id;
    }

    @PostMapping("/ok/{id}")
    @ResponseBody
    public void ok(@PathVariable("id") Long id, @RequestBody JSONObject jsonObject) {
        String key = jsonObject.getString("key");
        if (id == 2L) {
            SECOND_WOLD.add(key);
            String k = key.substring(0, 1);
            List<String> strings = FIRST_WOLD.get(k);
            strings.remove(key);
            FIRST_WOLD.put(k, strings);
        } else if (id == 3L) {
            SECOND_WOLD.remove(key);
            THIRD_WOLD.add(key);
        }
    }

    @PostMapping("/bad/{id}")
    @ResponseBody
    public void bad(@PathVariable("id") Long id, @RequestBody JSONObject jsonObject) {
        String key = jsonObject.getString("key");
        if (id == 2L) {
            SECOND_WOLD.remove(key);
            String k = key.substring(0, 1);
            List<String> strings = FIRST_WOLD.get(k);
            strings.add(key);
            FIRST_WOLD.put(k, strings);
        }
    }

    @GetMapping("/refresh")
    @ResponseBody
    public void l() throws Exception {
        SECOND_WOLD.clear();
        THIRD_WOLD.clear();
        FIRST_WOLD.clear();
        load();
    }

    public static void main(String[] args) {
        SpringApplication.run(MyenApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        load();
    }

    private void load() throws Exception{
        Resource resource = resourceLoader.getResource("classpath:en/en.txt");

        List<String> strings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            for (String line; (line = br.readLine()) != null; ) {
                strings.add(line.trim());
            }
        }
        /*Map<String, List<String>> listMap = strings
                .stream()
                .collect(Collectors.groupingBy(x -> x.substring(0, 1).toLowerCase(Locale.ROOT)));*/

        Map<String, List<String>> listMap = strings
                .stream()
                .collect(Collectors.groupingBy(x -> x.substring(0, 1).toLowerCase(Locale.ROOT),
                        Collectors.mapping(x -> {
                            int i = x.indexOf("[");
                            return x.substring(0, i) + "======================" + x.substring(i);
                        }, Collectors.toList())));

        FIRST_WOLD.putAll(listMap);
    }
}
