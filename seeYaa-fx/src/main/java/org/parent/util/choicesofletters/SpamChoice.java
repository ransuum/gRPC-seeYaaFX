package org.parent.util.choicesofletters;

import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.TypeOfLetter;
import com.seeYaa.proto.email.service.movedletter.EmailRequest;
import com.seeYaa.proto.email.service.movedletter.MovedLetterServiceGrpc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class SpamChoice implements Choice {
    private final MovedLetterServiceGrpc.MovedLetterServiceBlockingStub movedLetterService;

    public SpamChoice(@Qualifier("movedLetter") MovedLetterServiceGrpc.MovedLetterServiceBlockingStub movedLetterService) {
        this.movedLetterService = movedLetterService;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public List<Letter> addToBox(int index, String email) {
        return movedLetterService.getSpamLetters(
                EmailRequest.newBuilder()
                        .setEmail(email).build()).getLettersList();
    }

    @Override
    public TypeOfLetter getChoice() {
        return TypeOfLetter.SPAM;
    }
}
