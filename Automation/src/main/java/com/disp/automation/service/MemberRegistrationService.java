package com.disp.automation.service;

import com.disp.automation.entity.Member;
import com.disp.automation.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class MemberRegistrationService {

    private final MemberRepository memberRepository;

    public MemberRegistrationService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member registerMember(String firstName, String lastName, String dateOfBirth,
                                 String email, String phoneNumber) throws Exception {
        Member member = new Member();
        member.setFirstname(firstName);
        member.setSurname(lastName);
        member.setEmail(email);
        member.setPhoneNumber(phoneNumber);

        // dateOfBirth comes from Camunda form as a string e.g. "2000-01-25"
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = sdf.parse(dateOfBirth);
            member.setDob(dob);
        }

        return memberRepository.save(member);
    }
}