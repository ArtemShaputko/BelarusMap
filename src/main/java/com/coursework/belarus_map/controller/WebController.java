package com.coursework.belarus_map.controller;

import com.coursework.belarus_map.model.Option;
import com.coursework.belarus_map.condition.WeatherConditionType;
import com.coursework.belarus_map.configuration.CityConfig;
import com.coursework.belarus_map.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebController {
    private final CityConfig cityConfig;
    private final CityService cityService;
    private final static List<Option> OPTIONS = List.of(
            new Option("temperature", "Температура"),
            new Option("humidity", "Влажность"),
            new Option("clouds", "Облачность"),
            new Option("wind", "Скорость ветра")
    );

    @GetMapping("/map")
    public String showMap(Model model, @RequestParam(required = false) String selected) {
        WeatherConditionType condition;
        try {
            condition = WeatherConditionType.valueOf(selected.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            condition = WeatherConditionType.TEMPERATURE;
        }
        cityService.setCitiesColor(condition);
        cityService.setStripColors(condition);

        model.addAttribute("cities", cityConfig.getList());
        model.addAttribute("gradientStops", condition.getGradientValues().stream()
                .map(cv -> cv.getColorHex() + " " + cv.getPositionCSS())
                .collect(Collectors.joining(", ")));

        model.addAttribute("gradientValues", condition.getGradientValues());
        model.addAttribute("title_gradient", condition.getLabel());
        model.addAttribute("title_form", "Выберете параметр:");

        model.addAttribute("options", OPTIONS);
        model.addAttribute("selectedOption", condition.name().toLowerCase());
        return "index";
    }
    @PostMapping("/submit")
    public String handleSubmit(@RequestParam String selectedOption) {
        // Обработка выбранной опции
        return "redirect:/map?selected=" + selectedOption;
    }
}