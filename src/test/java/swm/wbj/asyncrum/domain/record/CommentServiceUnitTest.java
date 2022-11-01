package swm.wbj.asyncrum.domain.record;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.service.MemberService;
import swm.wbj.asyncrum.domain.member.service.MemberServiceImpl;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.bookmark.service.BookmarkService;
import swm.wbj.asyncrum.domain.record.bookmark.service.BookmarkServiceImpl;
import swm.wbj.asyncrum.domain.record.comment.dto.CommentCreateRequestDto;
import swm.wbj.asyncrum.domain.record.comment.dto.CommentUpdateRequestDto;
import swm.wbj.asyncrum.domain.record.comment.entity.Comment;
import swm.wbj.asyncrum.domain.record.comment.exception.CommentNotExistsException;
import swm.wbj.asyncrum.domain.record.comment.repository.CommentRepository;
import swm.wbj.asyncrum.domain.record.comment.service.CommentServiceImpl;
import swm.wbj.asyncrum.global.media.AwsService;

import java.util.Optional;
import static org.mockito.Mockito.times;

class CommentServiceUnitTest {

    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    MemberService memberService = Mockito.mock(MemberServiceImpl.class);

    BookmarkService bookmarkService = Mockito.mock(BookmarkServiceImpl.class);

    AwsService awsService = Mockito.mock(AwsService.class);

    @InjectMocks
    CommentServiceImpl commentService = new CommentServiceImpl(commentRepository, memberService, bookmarkService, awsService);

    static final Long MOCK_ID = 1L;

    static final String MOCK_FILE_KEY = "FILE KEY";
    Comment mockComment;
    Member mockMember;
    Bookmark mockBookmark;


    @BeforeEach
    void setUp() {
        mockComment = new Comment(){
            @Override
            public Long getId() {return MOCK_ID;}

            @Override
            public Member getMember() { return mockMember;}

            @Override
            public Bookmark getBookmark() { return mockBookmark;}

        };

        mockMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_ID;
            }
        };

        mockBookmark = new Bookmark(){
            @Override
            public Long getId(){
                return MOCK_ID;
            }
        };

        Mockito.when(memberService.getCurrentMember()).thenReturn(mockMember);
        Mockito.when(bookmarkService.getCurrentBookmark(MOCK_ID)).thenReturn(mockBookmark);
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockComment));


    }

    @DisplayName("답글 생성")
    @Test
    void createComment() {
        String author = "test";
        String description = "description";

        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(mockComment);

        CommentCreateRequestDto requestDto = new CommentCreateRequestDto();
        requestDto.setBookmarkId(MOCK_ID);
        requestDto.setAuthor(author);
        requestDto.setDescription(description);

        commentService.createComment(requestDto);

        Mockito.verify(commentRepository,  times(2)).save(Mockito.isA(Comment.class));

        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockComment));


    }

    @DisplayName("현재 답글 가져오기")
    @Test
    void readComment() {
        commentService.readComment(MOCK_ID);

        Mockito.verify(commentRepository).findById(MOCK_ID);
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CommentNotExistsException.class, () -> {
            commentService.readComment(MOCK_ID + 1L);
        });
    }

    @DisplayName("범위에 따라 답글 리스트 가져오기")
    @Test
    void readAllComment() {
        commentService.readAllComment(MOCK_ID);

        Mockito.verify(commentRepository).findAllByBookmark(mockBookmark);
    }

    @DisplayName("답글 정보 업데이트")
    @Test
    void updateComment() {

        String description = "description";


        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto();
        requestDto.setDescription(description);

        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(mockComment);

        commentService.updateComment(MOCK_ID, requestDto);
        Mockito.verify(commentRepository).findById(MOCK_ID);

        Assertions.assertEquals(mockComment.getDescription(), description);

    }

    @DisplayName("답글 삭제")
    @Test
    void deleteComment() {

        commentService.deleteComment(MOCK_ID);

        Mockito.verify(commentRepository).findById(MOCK_ID);
        Mockito.verify(commentRepository).delete(mockComment);


        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CommentNotExistsException.class, () -> {
            commentService.deleteComment(MOCK_ID + 1L);
        });
    }
}