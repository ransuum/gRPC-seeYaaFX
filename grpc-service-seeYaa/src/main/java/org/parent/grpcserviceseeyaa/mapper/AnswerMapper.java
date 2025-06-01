package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Answer;
import com.seeYaa.proto.email.Letter;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {LetterMapper.class})
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    @Mapping(target = "createdAt", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(answer.getCreatedAt()))")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Answer toAnswerProto(org.parent.grpcserviceseeyaa.entity.Answer answer);

    @Named("answersMapping")
    default List<Answer> toAnswersProto(List<org.parent.grpcserviceseeyaa.entity.Answer> answers) {
        return answers.stream()
                .map(AnswerMapper.INSTANCE::toAnswerProto)
                .toList();
    }

    @AfterMapping
    default void addAnswers(@MappingTarget Letter.Builder letterBuilder, org.parent.grpcserviceseeyaa.entity.Letter letter) {
        if (letter.getAnswers() != null && !letter.getAnswers().isEmpty()) {
            List<Answer> answers = toAnswersProto(letter.getAnswers());
            letterBuilder.addAllAnswers(answers);
        }
    }
}
