package swm.wbj.asyncrum.domain.userteam.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.userteam.member.dto.ImageCreateResponseDto;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.domain.whiteboard.dto.WhiteboardCreateRequestDto;
import swm.wbj.asyncrum.domain.whiteboard.dto.WhiteboardCreateResponseDto;
import swm.wbj.asyncrum.domain.whiteboard.service.WhiteboardService;
import swm.wbj.asyncrum.global.error.ErrorResponseDto;

@RestController
@RequestMapping("/api/v1/images")
@Log4j2
@RequiredArgsConstructor
public class ImageController {

    private final MemberService memberService;

    @PostMapping("/{id}")
    public ResponseEntity<?> createImage(@PathVariable("id") Long id) {
        try {
            ImageCreateResponseDto responseDto = memberService.createImage(id);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }
}
