package com.studentsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards non-API requests to index.html so Vue Router can handle them.
 */
@Controller
public class SpaController {

    @GetMapping(value = {
        "/",
        "/login",
        "/dashboard",
        "/students",
        "/courses",
        "/course-offerings",
        "/enrollments",
        "/scores",
        "/my-scores",
        "/attendance",
        "/my-attendance",
        "/users"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
