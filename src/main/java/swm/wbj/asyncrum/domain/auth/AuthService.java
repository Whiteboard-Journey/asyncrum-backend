package swm.wbj.asyncrum.domain.auth;

import swm.wbj.asyncrum.domain.auth.dto.LoginRequestDto;
import swm.wbj.asyncrum.domain.auth.dto.TokenResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    TokenResponseDto localLogin(HttpServletRequest request,
                                HttpServletResponse response, LoginRequestDto requestDto);
    TokenResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response);
}
