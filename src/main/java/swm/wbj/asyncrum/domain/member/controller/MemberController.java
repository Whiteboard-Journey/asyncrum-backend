package swm.wbj.asyncrum.domain.member.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.member.dto.*;
import swm.wbj.asyncrum.domain.member.service.MemberService;
import swm.wbj.asyncrum.global.exception.ErrorResponseDto;

import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Member")
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMember(@RequestBody MemberCreateRequestDto requestDto){
        try{
            MemberCreateResponseDto responseDto= memberService.createMember(requestDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readMember(@PathVariable("id") Long id){
        try{
            MemberReadResponseDto responseDto = memberService.readMember(id);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllMember(
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId,
            @RequestParam(value = "sizePerPage", required = false, defaultValue = "12") Integer sizePerPage)
    {
        try{
            MemberReadAllResponseDto responseDto = memberService.readAllMember(pageIndex, topId, sizePerPage);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }


    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody MemberUpdateRequestDto requestDto){
        try {
            MemberUpdateResponseDto responseDto = memberService.updateMember(id, requestDto);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMember(@PathVariable("id") Long id){
        try {
            memberService.deleteMember(id);

            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    /**
     * 메일 인증 링크 전송
     * 해당 엔드포인트는 JWT 토큰이 있어야 사용 가능
     *
     */
    @PostMapping(value = "/email/verification", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendEmailVerificationLinkByEmail() {
        try {
            memberService.sendEmailVerificationLinkByEmail();

            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    /**
     * 메일 인증 링크 검증 및 처리
     * TODO: 인증 성공 Thymeleaf HTML 만들기 OR 프론트엔드 특정 페이지로 REDIRECT
     */
    @GetMapping(value = "/email/verification", produces = TEXT_PLAIN_VALUE)
    public String verifyEmailVerificationLink(@RequestParam("memberId") Long memberId) {
        try {
            memberService.verifyEmailVerificationLink(memberId);

            return "인증 성공";
        } catch (Exception e){
            return "인증 성공";
        }
    }

    /**
     * 사용자 프로필 이미지 처리
     */
    @PostMapping(value = "/{id}/image", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMemberImage(@PathVariable("id") Long id) {
        try {
            ImageCreateResponseDto responseDto = memberService.createImage(id);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }
}
