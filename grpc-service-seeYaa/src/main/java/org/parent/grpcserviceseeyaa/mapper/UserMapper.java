package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Users;
import com.seeYaa.proto.email.service.users.UserWithLetters;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    Users toUserProto(org.parent.grpcserviceseeyaa.entity.Users users);
    UserWithLetters toUserWithLettersProto(org.parent.grpcserviceseeyaa.entity.Users users);
}
