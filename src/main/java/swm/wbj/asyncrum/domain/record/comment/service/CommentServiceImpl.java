package swm.wbj.asyncrum.domain.record.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.service.MemberService;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.bookmark.service.BookmarkService;
import swm.wbj.asyncrum.domain.record.comment.dto.*;
import swm.wbj.asyncrum.domain.record.comment.entity.Comment;
import swm.wbj.asyncrum.domain.record.comment.exception.CommentNotExistsException;
import swm.wbj.asyncrum.domain.record.comment.repository.CommentRepository;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;

import java.util.List;

import static swm.wbj.asyncrum.global.media.AwsService.*;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final BookmarkService bookmarkService;

    private final AwsService awsService;

    @Override
    public CommentCreateResponseDto createComment(CommentCreateRequestDto requestDto){
        Member currentMember = memberService.getCurrentMember();
        Bookmark currentBookmark = bookmarkService.getCurrentBookmark(requestDto.getBookmarkId());

        Comment comment = requestDto.toEntity(currentBookmark, currentMember);
        Comment savedComment = commentRepository.save(comment);
        Long commentId = commentRepository.save(comment).getId();

        String commentFileKey = createCommentFileKey(currentMember.getId(), commentId);
        String preSignedURL = awsService.generatePresignedURL(commentFileKey, COMMENT_BUCKET_NAME, FileType.MP4);
        comment.updateCommentFileMetadata(commentFileKey, awsService.getObjectURL(commentFileKey, COMMENT_BUCKET_NAME));
        comment.updateProfileImage(currentMember.getProfileImageUrl());
        return new CommentCreateResponseDto(savedComment.getId(), preSignedURL);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentReadResponseDto readComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotExistsException::new);
        return new CommentReadResponseDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentReadAllResponseDto readAllComment(Long bookmarkId) {
        Bookmark bookmark = bookmarkService.getCurrentBookmark(bookmarkId);
        List<Comment> commentList = commentRepository.findAllByBookmark(bookmark);
        return new CommentReadAllResponseDto(commentList);
    }

    @Override
    public CommentUpdateResponseDto updateComment(Long id, CommentUpdateRequestDto requestDto) {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotExistsException::new);

        comment.updateComment(
                requestDto.getDescription()

        );

        return new CommentUpdateResponseDto(comment.getId());
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotExistsException::new);
        commentRepository.delete(comment);
    }

    private String createCommentFileKey(Long memberId, Long commentId) {
        return COMMENT_FILE_PREFIX  + "_" + memberId + "_" + commentId + "." + FileType.MP4.getName();
    }



}
