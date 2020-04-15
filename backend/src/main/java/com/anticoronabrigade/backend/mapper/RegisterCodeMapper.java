package com.anticoronabrigade.backend.mapper;

import com.anticoronabrigade.backend.dto.RegisterCodeDto;
import com.anticoronabrigade.backend.entity.RegisterCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.persistence.Column;

@Component
public class RegisterCodeMapper {
    @Autowired
    private ModelMapper modelMapper;

    public RegisterCode convertRegisterCodeDtoToRegisterCode(RegisterCodeDto registerCodeDto) {
        return modelMapper.map(registerCodeDto, RegisterCode.class);
    }
}
