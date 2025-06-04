package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Users;
import com.seeYaa.proto.email.service.users.SignUpRequest;
import com.seeYaa.proto.email.service.users.UserWithLetters;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.parent.grpcserviceseeyaa.dto.SignUpRequestDto;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Users toUserProto(org.parent.grpcserviceseeyaa.entity.Users users);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    UserWithLetters toUserWithLettersProto(org.parent.grpcserviceseeyaa.entity.Users users);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    SignUpRequestDto toSignUpRequestDto(SignUpRequest sign);
}
