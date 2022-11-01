package swm.wbj.asyncrum.domain.record.comment.service;

import swm.wbj.asyncrum.domain.record.comment.dto.*;

public interface CommentService {

    CommentCreateResponseDto createComment(CommentCreateRequestDto requestDto);

    CommentReadResponseDto readComment(Long id);

    CommentReadAllResponseDto readAllComment(Long bookmarkId);

    CommentUpdateResponseDto updateComment(Long id, CommentUpdateRequestDto requestDto);

    void deleteComment(Long id);
}
