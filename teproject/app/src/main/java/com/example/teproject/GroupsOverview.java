package com.example.teproject;

import java.util.List;

public class GroupsOverview {
    private String ProblemStatement;
    private String GroupID;
    private String MentorID;
    private List<String> Members;
    private List<String> TechStack;

    public GroupsOverview() {
    }

    public GroupsOverview(String groupID) {
        GroupID = groupID;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getProblemStatement() {
        return ProblemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        ProblemStatement = problemStatement;
    }

    public String getMentorID() {
        return MentorID;
    }

    public void setMentorID(String mentorID) {
        MentorID = mentorID;
    }

    public List<String> getMembers() {
        return Members;
    }

    public void setMembers(List<String> members) {
        Members = members;
    }

    public List<String> getTechStack() {
        return TechStack;
    }

    public void setTechStack(List<String> techStack) {
        TechStack = techStack;
    }
}
