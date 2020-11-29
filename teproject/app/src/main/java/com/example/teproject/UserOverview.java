package com.example.teproject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserOverview {
    private String Branch;
    private HashMap<String, ArrayList<String>> Domains;
    private String Email;
    private String Github;
    private String GroupID;
    private String Linkedin;
    private String Name;
    private String Password;
    private String PhoneNo;
    private String RegistrationID;
    private String Resume;
    private boolean Role;
    private String RollNo;

    public UserOverview() {
    }

    public UserOverview(String branch, HashMap<String, ArrayList<String>> domains, String email, String github, String groupID, String linkedin, String name, String password, String phoneNo, String registrationID, String resume, boolean role, String rollNo) {
        Branch = branch;
        Domains = domains;
        Email = email;
        Github = github;
        GroupID = groupID;
        Linkedin = linkedin;
        Name = name;
        Password = password;
        PhoneNo = phoneNo;
        RegistrationID = registrationID;
        Resume = resume;
        Role = role;
        RollNo = rollNo;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public HashMap<String, ArrayList<String>> getDomains() {
        return Domains;
    }

    public void setDomains(HashMap<String, ArrayList<String>> domains) {
        Domains = domains;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGithub() {
        return Github;
    }

    public void setGithub(String github) {
        Github = github;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getLinkedin() {
        return Linkedin;
    }

    public void setLinkedin(String linkedin) {
        Linkedin = linkedin;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public String getRegistrationID() {
        return RegistrationID;
    }

    public void setRegistrationID(String registrationID) {
        RegistrationID = registrationID;
    }

    public String getResume() {
        return Resume;
    }

    public void setResume(String resume) {
        Resume = resume;
    }

    public boolean getRole() {
        return Role;
    }

    public void setRole(boolean role) {
        Role = role;
    }

    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String rollNo) {
        RollNo = rollNo;
    }
}
