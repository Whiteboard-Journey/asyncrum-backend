package swm.wbj.asyncrum.domain.auth.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.auth.dto.LoginRequestDto;
import swm.wbj.asyncrum.domain.auth.dto.TokenResponseDto;
import swm.wbj.asyncrum.domain.auth.AuthService;
import swm.wbj.asyncrum.global.exception.ErrorResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Authentication")
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    /**
     * userId 로그인
     */
    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> localLogin(HttpServletRequest request,
                                   HttpServletResponse response, @RequestBody LoginRequestDto requestDto) {
        try {
            TokenResponseDto tokenResponseDto = authService.localLogin(request, response, requestDto);
            return ResponseEntity.ok(tokenResponseDto);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    /**
     * Access Token refresh
     * Access Token 만료 시 Refresh Token을 통해 refresh
     */
    @GetMapping(value = "/refresh", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            TokenResponseDto tokenResponseDto = authService.refreshToken(request, response);
            return ResponseEntity.ok(tokenResponseDto);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    /**
     * AWS Target Group Health Check
     * 해당 경로로 요청받으면 항상 HTTP 200 OK 반환
     */
    @GetMapping(value = "/health", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> refreshToken() {
        return ResponseEntity.ok("OK");
    }
}
