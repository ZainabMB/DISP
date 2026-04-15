package com.disp.automation.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name= "member", schema= "probuilds")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "member_id")
    private Long memberId;

    @Column(name = "firstname" )
    private String firstname;

    @Column(name = "surname" )
    private String surname;

    @Column(name = "dob" )
    private Date dob;

    @Column(name = "email" )
    private String email;

    @Column(name = "phone_number" )
    private String phoneNumber;

    //getters and setters


    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
