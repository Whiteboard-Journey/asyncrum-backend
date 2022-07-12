package swm.wbj.asyncrum.domain.userteam.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;

import java.util.List;
import java.util.Optional;

@RestController
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public void saveMember(@RequestBody MemberDto memberDto){
        memberService.register(memberDto);

    }

    @GetMapping("/api/v1/members/{id}")
    public Optional<Member> readMember(@PathVariable("id") Long id){
        Optional<Member> member = memberService.read(id);
        return member;
    }

    @GetMapping("/api/v1/members")
    public List<MemberDto> readAllMember(){
        List<MemberDto> members= memberService.getListAll();
        return members;
    }



    @PatchMapping("/api/v1/members/{id}")
    public Long patchMember(@PathVariable("id") Long id, @RequestBody MemberDto memberDto){
        Long result = memberService.patch(id, memberDto);
        return result;
    }

    @DeleteMapping("/api/v1/members/{id}")
    public void deleteMember(@PathVariable("id") Long id, MemberDto memberDto){
        memberService.delete(memberDto);
    }
}
