package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.SetPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;

@Slf4j
@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("users/")
public class UserController {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path ="set_password")
    public SetPasswordDto setPassword(@RequestBody SetPasswordDto setPasswordDto) {
        log.info("User {} change password","Id user");
        return new SetPasswordDto();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "me")
    public UserDto infoAboutAuthUser() {
        return new UserDto();
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "me")
    public UpdateUserDto updateUserDto(@RequestBody UpdateUserDto updateUserDto){
        log.info("User {} updated data","Id user");
        return new UpdateUserDto();
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateUserDto(@RequestParam MultipartFile avatarUser){
        log.info("User {} update avatar","Id user");
        //add avatar
    }
}
