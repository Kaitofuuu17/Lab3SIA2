package com.example.obietalab2todoapp.controllers;


import com.example.obietalab2todoapp.models.Person;
import com.example.obietalab2todoapp.models.TodoItem;
import com.example.obietalab2todoapp.services.PersonService; // Ensure this service exists
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;
    
    @GetMapping("/add-person")
    public String showAddPersonForm(@RequestParam(value = "description", required = false) String description, 
                                    @RequestParam(value = "selectedPersonId", required = false) Long selectedPersonId,
                                    Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("description", description);
        model.addAttribute("selectedPersonId", selectedPersonId); 
        return "add-person"; 
    }

    @PostMapping("/person")
    public String addPerson(@Valid Person person, BindingResult result,
                            @RequestParam("description") String description, Model model) {
        if (result.hasErrors()) {
            return "add-person";
        }
        personService.save(person);

        model.addAttribute("persons", personService.findAll());
        model.addAttribute("todoItem", new TodoItem());
        model.addAttribute("description", description);
        model.addAttribute("selectedPersonId", person.getId());

        return "new-todo-item";
    }
}