package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.MovedLetter;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {LetterMapper.class})
public interface MovedLetterMapper {
    MovedLetterMapper INSTANCE = Mappers.getMapper(MovedLetterMapper.class);

    @Mapping(target = "willDeleteAt", ignore = true)
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    MovedLetter toMovedLetterProto(org.parent.grpcserviceseeyaa.entity.MovedLetter movedLetter);
}
