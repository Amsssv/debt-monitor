package com.amsssv.debt_monitor.bot;

import com.amsssv.debt_monitor.entity.Contact;
import com.amsssv.debt_monitor.entity.ContactType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSession {
    private BotState state = BotState.IDLE;
    private Contact selectedContact;
    private ContactType pendingType;
}
