package com.company.project.controllers;

import com.company.project.models.Person;
import com.company.project.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("get/all")
    @Operation
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("get/{id}")
    @Operation
    public Person getPersonById(@PathVariable Long id) {
        return personService.getPersonsById(id);
    }

    @PostMapping("addPerson")
    @Operation
    public Person addPerson(@RequestBody Person person) {
        return personService.addPerson(person);
    }

    @PutMapping("update/{id}")
    @Operation
    public Person updatePerson(@PathVariable Long id, @RequestBody Person updatedPerson) {
        return personService.updatePerson(id, updatedPerson);
    }

    @DeleteMapping("delete/{id}")
    @Operation
    public void deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
    }

    @GetMapping("get/{Username}")
    @Operation
    public Person getPersonByUsername(String Username) {
        return personService.getPersonByUsername(Username);
    }
}
