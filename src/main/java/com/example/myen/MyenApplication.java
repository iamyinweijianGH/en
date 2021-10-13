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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    public final Map<String, List<String>> F_MAP = new HashMap<>();
    public final Set<String> SET_2 = new HashSet<>();
    public final Set<String> SET_3 = new HashSet<>();

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/index")
    public Object en(@RequestParam(name = "key", required = false, defaultValue = "a") String key, Model model) {
        List<String> strings = F_MAP.get(key);
        Collections.shuffle(strings);
        model.addAttribute("list", strings);
        return "index";
    }

    @GetMapping("/o/1")
    public Object o1(Model model) {
        Collection<List<String>> values = F_MAP.values();
        List<String> strings = values.stream().flatMap(List::stream).collect(Collectors.toList());
        Collections.shuffle(strings);
        strings.add(0, "数量：" + strings.size());
        model.addAttribute("list", strings);
        return "index";
    }

    @GetMapping("/o/2")
    public Object o2(Model model) {
        List<String> strings = new ArrayList<>(SET_2);
        Collections.shuffle(strings);
        strings.add(0, "数量：" + strings.size());
        model.addAttribute("list", strings);
        return "index";
    }

    @GetMapping("/o/3")
    public Object o3(Model model) {
        List<String> strings = new ArrayList<>(SET_3);
        Collections.shuffle(strings);
        strings.add(0, "数量：" + strings.size());
        model.addAttribute("list", strings);
        return "index";
    }

    @PostMapping("/ok")
    @ResponseBody
    public void r(@RequestBody JSONObject jsonObject) {
        String key = jsonObject.getString("key");
        if (SET_2.contains(key)) {
            SET_2.remove(key);
            SET_3.add(key);
        } else {
            SET_2.add(key);
        }
        String k = key.substring(0, 1);
        List<String> strings = F_MAP.get(k);
        strings.remove(key);
        F_MAP.put(k, strings);
    }

    @GetMapping("/l")
    @ResponseBody
    public void l() throws Exception {
        SET_2.clear();
        SET_3.clear();
        F_MAP.clear();
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
        Map<String, List<String>> listMap = strings
                .stream()
                .collect(Collectors.groupingBy(x -> x.substring(0, 1).toLowerCase(Locale.ROOT)));

        F_MAP.putAll(listMap);
    }
}
