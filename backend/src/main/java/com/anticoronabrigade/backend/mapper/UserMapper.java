package com.anticoronabrigade.backend.mapper;

import com.anticoronabrigade.backend.dto.RegisterDto;
import com.anticoronabrigade.backend.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    @Autowired
    private ModelMapper modelMapper;

    public User convertToUserRegister(RegisterDto registerDto)
    {
        return modelMapper.map(registerDto, User.class);
    }
}
