package com.example.obietalab2todoapp.controllers;

import com.example.obietalab2todoapp.models.Person;
import com.example.obietalab2todoapp.models.TodoItem;
import com.example.obietalab2todoapp.services.PersonService;
import com.example.obietalab2todoapp.services.TodoItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class TodoFormController {

    @Autowired
    private TodoItemService todoItemService;

    @Autowired
    private PersonService personService;

    @GetMapping("/create-todo")
    public String showCreateForm(TodoItem todoItem, Model model) {
        List<Person> persons = personService.findAll();
        model.addAttribute("persons", persons);

        List<TodoItem> todoItems = StreamSupport.stream(todoItemService.getAll().spliterator(), false)
                                            .collect(Collectors.toList());
        model.addAttribute("todoItems", todoItems);

        return "new-todo-item";
    }

    @PostMapping("/todo")
    public String createTodoItem(@Valid TodoItem todoItem, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "new-todo-item"; 
        }
        
        todoItem.setIsComplete(false);
        todoItemService.save(todoItem); 
        return "redirect:/"; 
    }

    @GetMapping("/delete/{id}")
    public String deleteTodoItem(@PathVariable("id") Long id, Model model) {
        todoItemService.delete(id); 
        return "redirect:/"; 
    }

    @PutMapping("/api/todos/{id}")
    @ResponseBody
    public TodoItem updateTodoItem(@PathVariable Long id, @RequestBody TodoItem updatedItem) {
        TodoItem todoItem = todoItemService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("TodoItem id: " + id + " not found"));

        todoItem.setIsComplete(updatedItem.getIsComplete());
        todoItemService.save(todoItem); 

        return todoItem;
    }

    // New method for filtering TodoItems
    @GetMapping("/filter")
    public String filterTodoItems(@RequestParam(required = false) String taskDescription,
                                @RequestParam(required = false) Long personId,
                                @RequestParam(required = false) Boolean isComplete,
                                @RequestParam(required = false) String completionDate,
                                Model model) {
        LocalDate parsedCompletionDate = null;
        
        // Parse the completionDate string to LocalDate
        if (completionDate != null && !completionDate.isEmpty()) {
            try {
                parsedCompletionDate = LocalDate.parse(completionDate);
            } catch (DateTimeParseException e) {
                // Handle the exception, maybe log it or set to null
                parsedCompletionDate = null; // Or you can throw an exception or show an error message
            }
        }
        
        // Call the service layer to filter TodoItems based on the parameters
        List<TodoItem> filteredItems = todoItemService.filterTodoItems(taskDescription, personId, isComplete, parsedCompletionDate);
        
        // Add the filtered items and persons to the model
        model.addAttribute("todoItems", filteredItems);
        model.addAttribute("persons", personService.findAll());
        
        return "index"; // Return the view name for your index page
    }
}