package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.MovedLetter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MovedLetterMapper {
    MovedLetterMapper INSTANCE = Mappers.getMapper(MovedLetterMapper.class);

    MovedLetter toMovedLetterProto(org.parent.grpcserviceseeyaa.entity.MovedLetter movedLetter);
}
