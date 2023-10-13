package ru.evgen.springsource.firstsecurityapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.evgen.springsource.firstsecurityapp.models.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PersonDetails implements UserDetails {
    private final Person person;

    public PersonDetails(Person person) {
        this.person = person;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Метод авторизации пользователя (список его прав)
        // Пример для множества действий, для более тонкой настройки доступа
//        List<GrantedAuthority> authorityList = new ArrayList<>();
//        authorityList.add(new SimpleGrantedAuthority(person.getRole()));
//        authorityList.add(new SimpleGrantedAuthority(person.getRole()));
//        authorityList.add(new SimpleGrantedAuthority(person.getRole()));
//        authorityList.add(new SimpleGrantedAuthority(person.getRole()));
//        return authorityList;
        return Collections.singletonList(new SimpleGrantedAuthority(person.getRole())); // - если у пользователя только одна роль
    }

    @Override
    public String getPassword() { // Получение пароля Person
        return this.person.getPassword();
    }

    @Override
    public String getUsername() { // Получение логина Person
        return this.person.getName();
    }

    @Override
    public boolean isAccountNonExpired() { // Проверяет не просрочен ли аккаунт
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {  // Проверяет не заблокирован ли аккаунт
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {  // Проверяет не просрочен ли пароль
        return true;
    }

    @Override
    public boolean isEnabled() { // Проверяет работает ли аккаунт
        return true;
    }
    // Нужен чтобы получать данные идентифицированного пользователя
    public Person getPerson(){
        return this.person;
    }
}
