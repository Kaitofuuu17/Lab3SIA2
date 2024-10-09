package com.example.obietalab2todoapp.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.obietalab2todoapp.models.Person;
import com.example.obietalab2todoapp.models.TodoItem;
import com.example.obietalab2todoapp.repositories.PersonRepository;
import com.example.obietalab2todoapp.repositories.TodoItemRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TodoItemService {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private PersonRepository personRepository;

    // Get a TodoItem by its ID
    public Optional<TodoItem> getById(Long id) {
        return todoItemRepository.findById(id);
    }

    // Retrieve all TodoItems
    public Iterable<TodoItem> getAll() {
        return todoItemRepository.findAll();
    }

    // Save a new or updated TodoItem
    public TodoItem save(TodoItem todoItem) {
        return todoItemRepository.save(todoItem);
    }

    // Delete a TodoItem by its ID without renumbering
    public void delete(Long id) {
        Optional<TodoItem> todoItemOptional = todoItemRepository.findById(id);
        if (todoItemOptional.isPresent()) {
            todoItemRepository.delete(todoItemOptional.get());
        }
    }

    // Create a TodoItem for a specific person
    public TodoItem createTodoForPerson(Long personId, TodoItem todoItem) {
        Optional<Person> person = personRepository.findById(personId);
        if (person.isPresent()) {
            todoItem.setPerson(person.get());
            return todoItemRepository.save(todoItem);
        } else {
            throw new IllegalArgumentException("Person not found");
        }
    }

    // Find all tasks assigned to a specific person by personId
    public List<TodoItem> findTasksByPerson(Long personId) {
        return todoItemRepository.findByPerson_Id(personId);
    }

    // Filter TodoItems based on description, person, completion status, and completion date
    public List<TodoItem> filterTodoItems(String taskDescription, Long personId, Boolean isComplete, LocalDate completionDate) {
        List<TodoItem> todoItems = (List<TodoItem>) todoItemRepository.findAll();
        return todoItems.stream()
                .filter(item -> (taskDescription == null || item.getDescription().toLowerCase().contains(taskDescription.toLowerCase()))
                        && (personId == null || (item.getPerson() != null && item.getPerson().getId().equals(personId)))
                        && (isComplete == null || item.getIsComplete().equals(isComplete))
                        && (completionDate == null || (item.getCompletionDate() != null && item.getCompletionDate().isEqual(completionDate)))
                ).toList();
    }
}
