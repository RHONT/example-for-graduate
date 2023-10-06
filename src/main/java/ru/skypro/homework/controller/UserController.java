package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.SetPasswordDto;
import ru.skypro.homework.dto.UserDto;

@Slf4j
@RestController
@RequestMapping("users/")
public class UserController {

    @PostMapping("set_password")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordDto setPasswordDto) {
        log.info("User {} change password","SomeUser");
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> infoAboutAuthUser() {
        return ResponseEntity.ok(new UserDto());
    }


}
