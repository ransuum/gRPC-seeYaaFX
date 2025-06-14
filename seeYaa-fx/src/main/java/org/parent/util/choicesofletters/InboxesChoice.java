package org.parent.util.choicesofletters;

import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.TypeOfLetter;
import com.seeYaa.proto.email.service.movedletter.EmailRequest;
import com.seeYaa.proto.email.service.movedletter.MovedLetterServiceGrpc;
import org.parent.grpcserviceseeyaa.security.rolechecker.Authorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InboxesChoice implements Choice {
    private final MovedLetterServiceGrpc.MovedLetterServiceBlockingStub movedLetterService;

    public InboxesChoice(MovedLetterServiceGrpc.MovedLetterServiceBlockingStub movedLetterService) {
        this.movedLetterService = movedLetterService;
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public List<Letter> addToBox(int index, String email) {
        return movedLetterService.getInboxLetters(EmailRequest.newBuilder().setEmail(email).build()).getLettersList();
    }

    @Override
    public TypeOfLetter getChoice() {
        return TypeOfLetter.INBOXES;
    }
}
