package com.crysoft.me.pichat.models;

import java.util.List;

/**
 * Created by Maxx on 6/15/2016.
 */
public class GroupDetailsModel {
    private UserDetails groupDetails;
    private List<UserDetails> memberDetails;

    public List<UserDetails> getMemberDetails() {
        return memberDetails;
    }

    public void setMemberDetails(List<UserDetails> memberDetails) {
        this.memberDetails = memberDetails;
    }

    public UserDetails getGroupDetails() {
        return groupDetails;
    }

    public void setGroupDetails(UserDetails groupDetails) {
        this.groupDetails = groupDetails;
    }
}
