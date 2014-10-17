package org.drools.chance.kbase.fuzzy;

import java.util.List;

public class SpamResponse {
    List<SpamElement> spamElements;
    
    public boolean hasSpamElement(String name) {
        if (name.equals("username") || name.equals("email"))
            return true;
        else 
            return false;
    }
}
