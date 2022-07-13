package swm.wbj.asyncrum.domain.userteam.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamUpdateRequestDto;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public ResponseEntity<?> createMember(@RequestBody MemberCreateRequestDto requestDto){
        try{
            Long createdId = memberService.createMember(requestDto);
            return ResponseEntity.ok(createdId);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/api/v1/members/{id}")
    public ResponseEntity<?>  readMember(@PathVariable("id") Long id){
        try{
            MemberReadResponseDto responseDto = memberService.readMember(id);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/api/v1/members")
    public ResponseEntity<?> readAllMember(
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId)
    {
        try{
            MemberReadAllResponseDto responseDto = memberService.readAllMember(pageIndex, topId);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PatchMapping("/api/v1/members/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody MemberUpdateRequestDto requestDto){
        try {
            Long updateId = memberService.updateMember(id, requestDto);

            return ResponseEntity.ok(updateId);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/api/v1/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable("id") Long id){
        try {
            memberService.deleteMember(id);

            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
