package com.anticoronabrigade.backend.controller;

import com.anticoronabrigade.backend.dto.*;
import com.anticoronabrigade.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/postUser")
    @ResponseStatus(code = HttpStatus.CREATED)
    private @ResponseBody void saveUserToDatabase(@RequestBody RegisterDtoWithCode registerDto) {
        userService.createUser(registerDto);
    }

    @PostMapping(value = "/findUser")
    @ResponseStatus(code = HttpStatus.OK)
    private @ResponseBody Boolean getUserFromDatabase(@RequestBody LoginDto loginDto) {
        return userService.findUser(loginDto);
    }

    @PatchMapping(value = "/changeIsInfected")
    @ResponseStatus(code = HttpStatus.OK)
    private @ResponseBody void changeInfected(@RequestBody InfectedDto infectedDto) {
        userService.changeInfected(infectedDto);
    }

    @PostMapping(value = "/confirmEmail")
    @ResponseStatus(code = HttpStatus.OK)
    private @ResponseBody void sendConfirmationEmail(@RequestBody EmailOrSmsDto emailOrSmsDto) {
        userService.sendConfirmationEmail(emailOrSmsDto);
    }

    @PostMapping(value = "/confirmPhoneNumber")
    @ResponseStatus(code = HttpStatus.OK)
    private @ResponseBody void sendConfirmationSms(@RequestBody EmailOrSmsDto emailOrSmsDto) {
        userService.sendConfirmationSms(emailOrSmsDto);
    }

    @PostMapping(value = "/forgotPasswordEmail")
    @ResponseStatus(code = HttpStatus.OK)
    private @ResponseBody void sendForgotPasswordEmail(@RequestBody EmailOrSmsDto emailOrSmsDto) {
        userService.sendForgotPasswordEmail(emailOrSmsDto);
    }

    @PostMapping(value = "/forgotPasswordSms")
    @ResponseStatus(code = HttpStatus.OK)
    private @ResponseBody void sendForgotPasswordSms(@RequestBody EmailOrSmsDto emailOrSmsDto) {
        userService.sendForgotPasswordSms(emailOrSmsDto);
    }

    @PatchMapping(value = "/changePassword")
    @ResponseStatus(code = HttpStatus.CREATED)
    private @ResponseBody void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto);
    }

}
