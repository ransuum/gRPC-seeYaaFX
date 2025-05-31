package org.parent.util.choicesofletters;


import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.TypeOfLetter;

import java.util.List;

public interface Choice {
    List<Letter> addToBox(int index, String email);

    TypeOfLetter getChoice();
}
