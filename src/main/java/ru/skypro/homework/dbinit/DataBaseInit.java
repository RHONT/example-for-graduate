package ru.skypro.homework.dbinit;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.repository.UsersRepository;

@Component
public class DataBaseInit  implements ApplicationRunner {

    private final   UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public DataBaseInit(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(ApplicationArguments args) {

        if (usersRepository.existsByUsername("user@gmail.com")) {
            UserEntity user = usersRepository.findByUsername("user@gmail.com").get();
            user.setPassword(passwordEncoder.encode("123123123"));
            usersRepository.save(user);
        }

        if (usersRepository.existsByUsername("enemy@gmail.com")) {
            UserEntity enemy = usersRepository.findByUsername("enemy@gmail.com").get();
            enemy.setPassword(passwordEncoder.encode("123123123"));
            usersRepository.save(enemy);
        }

        if (usersRepository.existsByUsername("admin@gmail.com")) {
            UserEntity admin = usersRepository.findByUsername("admin@gmail.com").get();
            admin.setPassword(passwordEncoder.encode("123123123"));
            usersRepository.save(admin);
        }

    }
}
