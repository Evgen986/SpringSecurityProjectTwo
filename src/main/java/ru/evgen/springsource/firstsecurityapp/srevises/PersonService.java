package ru.evgen.springsource.firstsecurityapp.srevises;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.evgen.springsource.firstsecurityapp.models.Person;
import ru.evgen.springsource.firstsecurityapp.repositories.PeopleRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public Optional<Person> findByUserName(String name){
        return peopleRepository.findByName(name);
    }
}
