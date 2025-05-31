package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Answer;
import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.service.letter.LetterWithAnswers;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {UserMapper.class})
public interface LetterMapper {
    LetterMapper INSTANCE = Mappers.getMapper(LetterMapper.class);

    @Mapping(target = "createdAt", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(letter.getCreatedAt()))")
    @Mapping(target = "deleteTime", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(letter.getDeleteTime()))")
    @Mapping(target = "answersList", ignore = true)
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Letter toLetterProto(org.parent.grpcserviceseeyaa.entity.Letter letter);

    @AfterMapping
    default Letter.Builder mapAnswers(@MappingTarget Letter.Builder builder, org.parent.grpcserviceseeyaa.entity.Letter letter) {
        if (letter.getAnswers() != null && !letter.getAnswers().isEmpty()) {
            List<Answer> protoAnswers = AnswerMapper.INSTANCE.toAnswerList(letter.getAnswers());
            builder.addAllAnswers(protoAnswers);
        }
        return builder;
    }

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    LetterWithAnswers toLetterWithAnswersProto(org.parent.grpcserviceseeyaa.entity.Letter letter);

    @AfterMapping
    default LetterWithAnswers.Builder mapAnswersForLetterWithAnswers(@MappingTarget LetterWithAnswers.Builder builder, org.parent.grpcserviceseeyaa.entity.Letter letter) {
        if (letter.getAnswers() != null && !letter.getAnswers().isEmpty()) {
            List<Answer> protoAnswers = AnswerMapper.INSTANCE.toAnswerList(letter.getAnswers());
            builder.addAllAnswers(protoAnswers);
        }
        return builder;
    }
}
