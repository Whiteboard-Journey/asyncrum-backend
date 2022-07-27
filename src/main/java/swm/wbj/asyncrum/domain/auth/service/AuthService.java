package swm.wbj.asyncrum.domain.auth.service;

import swm.wbj.asyncrum.domain.auth.dto.LoginRequestDto;
import swm.wbj.asyncrum.domain.auth.dto.TokenResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    TokenResponseDto loginService(HttpServletRequest request,
                                  HttpServletResponse response, LoginRequestDto requestDto);
    TokenResponseDto refreshService(HttpServletRequest request, HttpServletResponse response);
}
