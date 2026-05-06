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

    public Optional<Member> validateMember(Long membershipNumber) {
        if (membershipNumber == null) return Optional.empty();
        return memberRepository.findByMemberId(membershipNumber);
    }
}