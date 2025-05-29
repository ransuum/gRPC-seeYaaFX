package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Answer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    @Mapping(target = "createdAt", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(answer.getCreatedAt()))")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Answer toAnswerProto(org.parent.grpcserviceseeyaa.entity.Answer answer);
}
