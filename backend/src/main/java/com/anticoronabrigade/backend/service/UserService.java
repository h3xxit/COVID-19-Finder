package com.anticoronabrigade.backend.service;

import com.anticoronabrigade.backend.customClasses.Constants;
import com.anticoronabrigade.backend.customClasses.EmailUtil;
import com.anticoronabrigade.backend.dto.*;
import com.anticoronabrigade.backend.entity.RegisterCode;
import com.anticoronabrigade.backend.entity.User;
import com.anticoronabrigade.backend.exception.*;
import com.anticoronabrigade.backend.mapper.RegisterCodeMapper;
import com.anticoronabrigade.backend.mapper.UserMapper;
import com.anticoronabrigade.backend.repository.RegisterCodeRepository;
import com.anticoronabrigade.backend.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Configurable
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RegisterCodeMapper registerCodeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegisterCodeRepository registerCodeRepository;

    public void sendConfirmationEmail(EmailOrSmsDto emailOrSmsDto) {
        deleteOldRegisterCodes();

        if(emailOrSmsDto.getEmailOrPhone()==null)
            throw new NullValueInNotNullColumnException();

        if(userRepository.findUserByEmailOrPhoneNumber(emailOrSmsDto.getEmailOrPhone())!=null)
            throw new UserAlreadyRegisteredException();

        Integer code = saveRegisterCode();

        try {
            EmailUtil.sendmail(emailOrSmsDto.getEmailOrPhone(), code);
        } catch (IOException | MessagingException e) {
            checkCodeAndDeleteCode(code);
            throw new EmailException();
        }

    }

    public void sendConfirmationSms(EmailOrSmsDto emailOrSmsDto) {
        deleteOldRegisterCodes();

        if(emailOrSmsDto.getEmailOrPhone()==null)
            throw new NullValueInNotNullColumnException();

        if(userRepository.findUserByEmailOrPhoneNumber(emailOrSmsDto.getEmailOrPhone())!=null)
            throw new UserAlreadyRegisteredException();

        Integer code = saveRegisterCode();

        Twilio.init(Constants.ACCOUNT_SID, Constants.AUTH_TOKEN);
        try{
            Message message = Message.creator(
                    new PhoneNumber(emailOrSmsDto.getEmailOrPhone()),
                    new PhoneNumber(Constants.TWILIO_PHONE_NUMBER),
                    "Activation code is " + code.toString())
                    .create();
            if(message.getStatus() == Message.Status.FAILED)
                throw new SmsException();
        } catch (ApiException e) {
            throw new SmsException();
        }
    }

    public void sendForgotPasswordEmail(EmailOrSmsDto emailOrSmsDto) {
        deleteOldRegisterCodes();

        if(emailOrSmsDto.getEmailOrPhone()==null)
            throw new NullValueInNotNullColumnException();

        if(userRepository.findUserByEmailOrPhoneNumber(emailOrSmsDto.getEmailOrPhone())==null)
            throw new UserNotFoundException();

        Integer code = saveRegisterCode();

        try {
            EmailUtil.sendmail(emailOrSmsDto.getEmailOrPhone(), code);
        } catch (IOException | MessagingException e) {
            checkCodeAndDeleteCode(code);
            throw new EmailException();
        }
    }

    public void sendForgotPasswordSms(EmailOrSmsDto emailOrSmsDto) {
        deleteOldRegisterCodes();

        if(emailOrSmsDto.getEmailOrPhone()==null)
            throw new NullValueInNotNullColumnException();

        if(userRepository.findUserByEmailOrPhoneNumber(emailOrSmsDto.getEmailOrPhone())==null)
            throw new UserNotFoundException();

        Integer code = saveRegisterCode();

        Twilio.init(Constants.ACCOUNT_SID, Constants.AUTH_TOKEN);
        try{
            Message message = Message.creator(
                    new PhoneNumber(emailOrSmsDto.getEmailOrPhone()),
                    new PhoneNumber(Constants.TWILIO_PHONE_NUMBER),
                    "Activation code is " + code.toString())
                    .create();
            if(message.getStatus() == Message.Status.FAILED)
                throw new SmsException();
        } catch (ApiException e) {
            throw new SmsException();
        }
    }

    public void changePassword(ChangePasswordDto changePasswordDto) {
        deleteOldRegisterCodes();

        if(changePasswordDto.getEmail()==null || changePasswordDto.getPassword()==null)
            throw new NullValueInNotNullColumnException();

        User user = userRepository.findUserByEmailOrPhoneNumber(changePasswordDto.getEmail());
        if(user==null)
            throw new UserNotFoundException();

        checkCodeAndDeleteCode(changePasswordDto.getCode());

        user.setPassword(passwordEncoder.encode(changePasswordDto.getPassword()));

        save(user);
    }

    private void deleteOldRegisterCodes() {
        registerCodeRepository.deleteOldRegisterCodes(Calendar.getInstance().getTimeInMillis()-Constants.FIVE_MINUTES_IN_MILLISECONDS);
    }

    private Integer generateRegisterCode(){
        Random rand = new Random();
        return rand.nextInt(900000)+100000;
    }

    private void checkCodeAndDeleteCode(Integer code) {
        List<Long> codeIdToDelete = registerCodeRepository.selectByCode(code);

        if(codeIdToDelete.size()==0)
            throw new CodeUnregisteredException();

        deleteCodeById(codeIdToDelete.get(0));
    }

    public void createUser(RegisterDtoWithCode registerDto) {
        deleteOldRegisterCodes();
        if(registerDto.getEmail()==null || registerDto.getPassword()==null)
            throw new NullValueInNotNullColumnException();

        if(userRepository.findUserByEmailOrPhoneNumber(registerDto.getEmail())!=null)
            throw new UserAlreadyRegisteredException();

        checkCodeAndDeleteCode(registerDto.getCode());

        User user = userMapper.convertToUserRegister(new RegisterDto(registerDto.getEmail(), registerDto.getPassword()));
        user.setInfected(false);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        save(user);
    }

    public Boolean findUser(LoginDto loginDto) {
        if(loginDto.getEmail()==null || loginDto.getPassword()==null)
            throw new NullValueInNotNullColumnException();

        User user = findUserByEmail(loginDto.getEmail());
        if(user==null)
            throw new UserNotFoundException();
        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
            throw new UserNotFoundException();
        return user.getInfected();
    }

    public void changeInfected(InfectedDto infectedDto){
        if(infectedDto.getEmail()==null || infectedDto.getPassword()==null)
            throw new NullValueInNotNullColumnException();

        User user = userRepository.findUserByEmailOrPhoneNumber(infectedDto.getEmail());

        if(user==null)
            throw new UnauthorizedException();

        if(!passwordEncoder.matches(infectedDto.getPassword(), user.getPassword()))
            throw new UnauthorizedException();

        user.setInfected(infectedDto.isInfected());
        userRepository.save(user);
    }

    private User findUserByEmail(String email)
    {
        return userRepository.findUserByEmailOrPhoneNumber(email);
    }

    private Integer saveRegisterCode() {
        Integer code = generateRegisterCode();
        registerCodeRepository.save(registerCodeMapper.convertRegisterCodeDtoToRegisterCode(new RegisterCodeDto(code, Calendar.getInstance().getTimeInMillis())));
        return code;
    }

    private void deleteCodeById(Long codeIdToDelete){
        registerCodeRepository.deleteById(codeIdToDelete);
    }

    private void save(User user)
    {
        userRepository.save(user);
    }

}
