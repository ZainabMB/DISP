package com.disp.automation.service;

import com.disp.automation.entity.Member;
import com.disp.automation.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberValidationService {

    private final MemberRepository memberRepository;

    public MemberValidationService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean validateMember(Long membershipNumber) {
        if (membershipNumber == null) {
            return false;
        }
        Optional<Member> member = memberRepository.findByMemberId(membershipNumber);
        return member.isPresent();
}
    }
