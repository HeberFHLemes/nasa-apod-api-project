package com.nasa.apis.apod.controller;

import com.nasa.apis.apod.dto.ApodResponse;
import com.nasa.apis.apod.service.ApodService;
import com.nasa.apis.apod.service.CallLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApodController {

    @Autowired
    private ApodService apodService;

    @Autowired
    private CallLimiterService callLimiter;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/apod")
    public String apod(Model model){

        if (!callLimiter.canCall()){
            model.addAttribute("error", "Call limit reached. Try again later.");
            return "error";
        }

        ApodResponse apodData = apodService.fetchApodData();

        // Add to the model
        model.addAttribute("image", apodData);

        return "apod";
    }
}
