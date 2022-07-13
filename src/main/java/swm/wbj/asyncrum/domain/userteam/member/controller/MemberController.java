package swm.wbj.asyncrum.domain.userteam.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public void createMember(@RequestBody MemberDto memberDto){
        memberService.createMember(memberDto);

    }

    @GetMapping("/api/v1/members/{id}")
    public Member readMember(@PathVariable("id") Long id){
        return memberService.readMember(id);
    }

    @GetMapping("/api/v1/members")
    public List<Member> readAllMember(){
        return memberService.readAllMember();
    }



    @PatchMapping("/api/v1/members/{id}")
    public Long updateMember(@PathVariable("id") Long id, @RequestBody MemberDto memberDto){
        return memberService.updateMember(id, memberDto);
    }

    @DeleteMapping("/api/v1/members/{id}")
    public void deleteMember(@PathVariable("id") Long id){
        memberService.deleteMember(id);
    }
}
