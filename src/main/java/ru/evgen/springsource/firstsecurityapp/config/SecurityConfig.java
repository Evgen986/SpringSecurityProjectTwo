package ru.evgen.springsource.firstsecurityapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.evgen.springsource.firstsecurityapp.srevises.PersonDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Сразу используем класс имплементирующий UserDetailsService и реализующий loadUserByUsername
    private final PersonDetailsService detailsService;

    @Autowired
    public SecurityConfig(PersonDetailsService detailsService) {
        this.detailsService = detailsService;
    }

    /*   1 вариант:
         Создаем бин DaoAuthenticationProvider данный класс настроен на работу с UserDetailsService
         и производит запрос в БД сравнивая логин и пароль пользователя.
         Создаем бин PasswordEncoder данный класс определяем как буде шифроваться пароль пользователя.
         Данный бин следует передать провайдеру, что бы он понимал, как работать с паролями.
         Создаем бин AuthenticationManager который отправляет запрос на аутентификацию доступным провайдерам.
     */
    // Настройка аутентификации
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(detailsService);
        daoAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /*  2 вариант:
        Создаем бин PasswordEncoder данный класс определяем как буде шифроваться пароль пользователя.
        Данный бин следует передать провайдеру, что бы он понимал, как работать с паролями.
        Сразу создаем бин AuthenticationManager который отправляет запрос на аутентификацию доступным провайдерам.
        В данном бине создаем DaoAuthenticationProvider и передаем ему UserDetailsService, PasswordEncoder.
        И сразу передаем провайдер менеджеру.
        Позволяет сократить код конфигурации.
    */
    //    @Bean
//    public AuthenticationManager authManager(){
//        var authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(detailsService);
//        authProvider.setPasswordEncoder(getPasswordEncoder());
//        return new ProviderManager(authProvider);
//    }


    //          УРОК НАИЛЯ СОЗДАНИЕ СОБСТВЕННОЙ СТРАНИЦЫ АУТЕНТИФИКАЦИИ
//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        http
////                .csrf(csrf -> csrf.disable())
//                .securityMatcher("/hello").authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//                .formLogin(formLogin ->  // Задаем собственную страницу аутентификации для Spring Security
//                    formLogin
//                        .loginPage("/auth/login")  // Задаем адрес страницы аутентификации
//                        .loginProcessingUrl("/process_login") // URL для обработки входа
//                        .defaultSuccessUrl("/hello", true) // URL после успешного входа (второй аргумент указывает, что необходимо всегда перенаправлять пользователя на данную страницу после аутентификации)
//                        .failureUrl("/auth/login?error")); // URL в случае неудачной аутентификации
////        http.formLogin().loginPage("/auth/login").loginProcessingUrl("/process_login");
//        return http.build();
//    }

    //              УРОК СЕЛЬСКОГО ПРОГРАММИСТА

    // Фильтр защиты доступа к страницам приложения с перенаправлением на страницу аутентификации
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                // Правило собственной страницы аутентификации
                .formLogin(login -> login
                        .loginPage("/auth/login")  // Перенаправление на собственную страницу аутентификации (обрати внимание вначале ставиться "/")
                        .permitAll()  // обязательно вызвать метод permitAll() для доступа к странице не аутентифицированных пользователей
                        .loginProcessingUrl("/process_login") // // URL для обработки входа
                        .defaultSuccessUrl("/hello", true)  // URL после успешного входа (второй аргумент указывает, что необходимо всегда перенаправлять пользователя на данную страницу после аутентификации)
                        .failureUrl("/auth/login?error"))  // URL в случае неудачной аутентификации
                // Правило аутентификации при входе на сайт
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/admin").hasRole("ADMIN") // Предоставляет доступ по запросу /admin (т.е. к странице admin.html в корне приложения) пользователям только с ролью ADMIN
                        .requestMatchers("/public/**", "/auth/**").permitAll()  // Предоставляет доступ по запросу public/403 (если указать public/** то будет предоставлено разрешение для всех запросов начинающихся на public/ и далее на любой уровень вложенности) любым пользователям, в том числе без аутентификации
                        .anyRequest().hasAnyRole("USER", "ADMIN")) // Для любого другого запроса требуется одна из ролей или ROLE_USER или ROLE_ADMIN
//                        .anyRequest()  // любой запрос
//                        .authenticated())  // аутентифицировать
                // Обработка исключений (например при попытке обращения не аутентифицированного пользователя)
                .exceptionHandling(except -> except
                        .authenticationEntryPoint((request, response, authException) -> {  // Точка входа
//                                    authException.printStackTrace(); // Вывод полученного исключения в консоль (логирование)
//                                    response.sendError(HttpStatus.UNAUTHORIZED.value()); // Отправка пользователю кода ошибки 401
//                                    response.sendRedirect("http://localhost:8080/public/test/403"); // Перенаправление пользователя при возникновении исключения на необходимую страницу (НЕ ЗАБУДЬ РАЗРЕШИТЬ ДОСТУП К ЭТОЙ СТРАНИЦЕ)
                            response.sendRedirect("http://localhost:8080/auth/login"); // Перенаправление на страницу аутентификации
                        }))
                // Выход пользователя из аутентификации (при переходе по данному адресу пользователь разлогинится)
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL запрос приводящий к выходу пользователя из аутентификации
                        .logoutSuccessUrl("/auth/login")); // URL адрес на который будет перенаправлен пользователь после выхода из аутентификации
        return http.build();
    }
}
