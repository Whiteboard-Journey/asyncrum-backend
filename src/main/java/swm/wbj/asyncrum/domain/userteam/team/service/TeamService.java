package swm.wbj.asyncrum.domain.userteam.team.service;

import swm.wbj.asyncrum.domain.userteam.team.dto.*;

public interface TeamService {

    // 팀 생성
    TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto);

    // 단일 팀 조회
    TeamReadResponseDto readTeam(Long id);

    // 팀 전체 조회
    TeamReadAllResponseDto readAllTeam(Integer pageIndex, Long topId);

    // 팀 정보 업데이트
    TeamUpdateResponseDto updateTeam(Long id, TeamUpdateRequestDto requestDto);

    // 팀 삭제
    void deleteTeam(Long id);

}
