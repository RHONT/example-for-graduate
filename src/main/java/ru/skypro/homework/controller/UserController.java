package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.SetPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

@Slf4j
@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("users/")
public class UserController {
    private final UserService userService;
    private final ImageRepository imageRepository;


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "set_password")
    public SetPasswordDto setPassword(@RequestBody SetPasswordDto setPasswordDto, @AuthenticationPrincipal UserDetails userDetails) {

        return userService.setPassword(setPasswordDto, userDetails);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "me")
    public UserDto infoAboutAuthUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getInfoAboutUser(userDetails);
    }


    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "me")
    public UpdateUserDto updateUserDto(@RequestBody UpdateUserDto updateUserDto, @AuthenticationPrincipal UserDetails userDetails) {
        return userService.updateInfoUser(updateUserDto, userDetails);
    }


    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateUserDto(@RequestParam MultipartFile avatarUser,
                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        log.info("User {} update avatar", "Id user");
        userService.uploadAvatar(avatarUser, userDetails);
    }

    @GetMapping(path = "id-image/{idImage}")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Integer idImage) {
        ImageEntity image = imageRepository.findById(idImage).get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        headers.setContentLength(image.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(image.getData());
    }
}
