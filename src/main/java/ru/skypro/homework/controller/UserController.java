package ru.skypro.homework.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.SetPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

@Slf4j
@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("users/")
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path ="set_password")
    public SetPasswordDto setPassword(@RequestBody SetPasswordDto setPasswordDto) {
        log.info("User {} change password","Id user");
        return new SetPasswordDto();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "me")
    public UserDto infoAboutAuthUser(Authentication authentication) {

        return userService.getInfoAboutUser(authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "me")
    public UpdateUserDto updateUserDto(@RequestBody UpdateUserDto updateUserDto, Authentication authentication){

        log.info("User {} updated data","Id user");

        return userService.updateInfoUser(updateUserDto,authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateUserDto(@RequestParam MultipartFile avatarUser){
        log.info("User {} update avatar","Id user");
        //add avatar
    }
}
