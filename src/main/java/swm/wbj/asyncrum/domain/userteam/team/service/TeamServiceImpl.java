package swm.wbj.asyncrum.domain.userteam.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.userteam.team.dto.*;
import swm.wbj.asyncrum.domain.userteam.team.repository.TeamRepository;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    // 팀 생성
    @Override
    @Transactional
    public TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto) {
        String name = requestDto.getCode();

        // 팀만의 고유한 코드가 이미 존재한다면 예외처리
        if(teamRepository.existsByCode(name)) {
            throw new IllegalArgumentException("해당 코드는 이미 사용중입니다.");
        }

        Team team = requestDto.toEntity();

        return new TeamCreateResponseDto(teamRepository.save(team).getId());
    }

    // 단일 팀 조회
    @Override
    @Transactional(readOnly = true)
    public TeamReadResponseDto readTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        return new TeamReadResponseDto(team);
    }

    // 팀 전체 조회
    @Override
    @Transactional(readOnly = true)
    public TeamReadAllResponseDto readAllTeam(Integer pageIndex, Long topId) {
        int SIZE_PER_PAGE = 10;

        Page<Team> teamPage;
        Pageable pageable = PageRequest.of(pageIndex, SIZE_PER_PAGE, Sort.Direction.DESC, "id");

        if(topId == 0) {
            teamPage = teamRepository.findAll(pageable);
        }
        else {
            teamPage = teamRepository.findAllByTopId(topId, pageable);
        }

        return new TeamReadAllResponseDto(teamPage.getContent(), teamPage.getPageable(), teamPage.isLast());
    }

    // 팀 정보 업데이트
    @Override
    @Transactional
    public TeamUpdateResponseDto updateTeam(Long id, TeamUpdateRequestDto requestDto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        team.update(requestDto.getName(), requestDto.getPictureUrl());

        return new TeamUpdateResponseDto(teamRepository.save(team).getId());
    }

    // 팀 삭제
    @Override
    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        teamRepository.delete(team);
    }
}
