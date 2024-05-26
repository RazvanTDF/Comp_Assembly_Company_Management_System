package com.company.project.services;

import com.company.project.models.Person;
import com.company.project.models.PurchaseOrder;
import com.company.project.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person getPersonsById(Long id) {
        return (Person) personRepository.findById(id).orElse(null);
    }

    public Person addPerson(Person person) {
        String userName = person.getName();
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Invalid person data");
        }
        return personRepository.save(person);
    }

    public Person updatePerson(Long id, Person updatedPerson) {
        Person existingPerson = (Person) personRepository.findById(id).orElse(null);
        if (existingPerson != null) {
            String updatedName = updatedPerson.getName();
            if (updatedName != null && !updatedName.isEmpty()) {
                existingPerson.setName(updatedName);
            }
            // Alte actualizări de câmpuri după nevoie
            return personRepository.save(existingPerson);
        }
        return null;
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public void addOrderToPerson(Long personId, PurchaseOrder purchaseOrder) {
        Person person = (Person) personRepository.findById(personId).orElse(null);
        if (person != null) {
            person.addOrder(purchaseOrder); // Adăugăm comanda la lista de comenzi a personului
            personRepository.save(person); // Salvăm personul actualizat în baza de date
        }
    }

    public void updatePersonBalance(Long personId, double amount) {
        Person person = (Person) personRepository.findById(personId).orElse(null);
        if (person != null) {
            double currentBalance = person.getBalance();
            person.setBalance(currentBalance + amount); // Actualizăm soldul personului cu suma specificată
            personRepository.save(person); // Salvăm personul actualizat în baza de date
        }
    }

    public Person getPersonByUsername(String Username) {
        return personRepository.findByUsername(Username);
    }

    // Alte metode specifice pentru gestionarea clienților pot fi adăugate aici, dacă este nevoie
}
