package swm.wbj.asyncrum.domain.record;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.service.MemberService;
import swm.wbj.asyncrum.domain.member.service.MemberServiceImpl;
import swm.wbj.asyncrum.domain.record.bookmark.dto.BookmarkCreateRequestDto;
import swm.wbj.asyncrum.domain.record.bookmark.dto.BookmarkReadAllResponseDto;
import swm.wbj.asyncrum.domain.record.bookmark.dto.BookmarkUpdateRequestDto;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.bookmark.entity.VideoBookmarkCoordinates;
import swm.wbj.asyncrum.domain.record.bookmark.exception.BookmarkNotExistsException;
import swm.wbj.asyncrum.domain.record.bookmark.repository.BookmarkRepository;
import swm.wbj.asyncrum.domain.record.bookmark.service.BookmarkServiceImpl;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.record.record.service.RecordService;
import swm.wbj.asyncrum.domain.record.record.service.RecordServiceImpl;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.util.Optional;


class BookmarkServiceTest {

    BookmarkRepository bookmarkRepository = Mockito.mock(BookmarkRepository.class);

    MemberService memberService = Mockito.mock(MemberServiceImpl.class);

    RecordService recordService = Mockito.mock(RecordServiceImpl.class);

    @InjectMocks
    BookmarkServiceImpl bookmarkService = new BookmarkServiceImpl(bookmarkRepository, memberService, recordService);


    static final Long MOCK_ID = 1L;

    Bookmark mockBookmark;

    Record mockRecord;

    Member mockMember;


    @BeforeEach
    void setUp() {

        mockBookmark = new Bookmark(){
            @Override
            public Long getId() { return MOCK_ID; }


            @Override
            public Member getMember() { return mockMember; }

            @Override
            public Record getRecord() { return mockRecord; }

        };

        mockMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_ID;
            }
        };

        mockRecord = new Record() {
            @Override
            public Long getId() {
                return MOCK_ID;
            }
        };
        
        Mockito.when(memberService.getCurrentMember()).thenReturn(mockMember);
        Mockito.when(recordService.getCurrentRecord(MOCK_ID)).thenReturn(mockRecord);
        Mockito.when(bookmarkRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockBookmark));
    }

    @DisplayName("북마크 생성")
    @Test
    void createBookmark() {

        String emoji = "U+1F600";
        String content = "test bookmark";
        Double time = 5.6;
        VideoBookmarkCoordinates position = new VideoBookmarkCoordinates(0.5, 0.5);
        String drawing = "{\"id\":\"doc\",\"name\":\"New Document\",\"version\":15.5,\"pages\":{\"page\":{\"id\":\"page\",\"name\":\"Page 1\",\"childIndex\":1,\"shapes\":{},\"bindings\":{}}},\"pageStates\":{\"page\":{\"id\":\"page\",\"selectedIds\":[],\"camera\":{\"point\":[0,0],\"zoom\":0.8342592592592591}}},\"assets\":{}}";
        Double scale = 0.8342592592592591;

        Mockito.when(bookmarkRepository.save(Mockito.any(Bookmark.class))).thenReturn(mockBookmark);

        BookmarkCreateRequestDto requestDto = new BookmarkCreateRequestDto();
        requestDto.setRecordId(MOCK_ID);
        requestDto.setEmoji(emoji);
        requestDto.setContent(content);
        requestDto.setTime(time);
        requestDto.setPosition(position);
        requestDto.setDrawing(drawing);
        requestDto.setScale(scale);

        bookmarkService.createBookmark(requestDto);

        Mockito.verify(bookmarkRepository).save(Mockito.isA(Bookmark.class));

        Mockito.when(bookmarkRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockBookmark));


    }

    @DisplayName("현재 북마크 가져 오기")
    @Test
    void readBookmark() {

        bookmarkService.readBookmark(MOCK_ID);

        Mockito.verify(bookmarkRepository).findById(MOCK_ID);
        Mockito.when(bookmarkRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BookmarkNotExistsException.class, () -> {
            bookmarkService.readBookmark(MOCK_ID + 1L);
        });

    }

    @DisplayName("녹화에 따른 모든 북마크 정보 가져오기")
    @Test
    void readAllBookmark() {

        bookmarkService.readAllBookmark(MOCK_ID);

        Mockito.verify(bookmarkRepository).findAllByRecord(mockRecord);
    }

    @DisplayName("북마크 정보 업데이트")
    @Test
    void updateBookmark() {

        String emoji = "U+1F601";
        String content = "update bookmark";
        Double time = 6.0;
        VideoBookmarkCoordinates position = new VideoBookmarkCoordinates(0.6, 0.7);
        String drawing = "{\"id\":\"doc\",\"name\":\"New Document\",\"version\":15.5,\"pages\":{\"page\":{\"id\":\"page\",\"name\":\"Page 1\",\"childIndex\":1,\"shapes\":{},\"bindings\":{}}},\"pageStates\":{\"page\":{\"id\":\"page\",\"selectedIds\":[],\"camera\":{\"point\":[0,0],\"zoom\":0.8342592592592591}}},\"assets\":{}}";
        Double scale = 0.8342592592592591;

        BookmarkUpdateRequestDto requestDto = new BookmarkUpdateRequestDto();
        requestDto.setEmoji(emoji);
        requestDto.setContent(content);
        requestDto.setTime(time);
        requestDto.setPosition(position);
        requestDto.setDrawing(drawing);
        requestDto.setScale(scale);

        Mockito.when(bookmarkRepository.save(Mockito.any(Bookmark.class))).thenReturn(mockBookmark);

        bookmarkService.updateBookmark(MOCK_ID, requestDto);
        Mockito.verify(bookmarkRepository).findById(MOCK_ID);

        Assertions.assertEquals(mockBookmark.getEmoji(), emoji);
        Assertions.assertEquals(mockBookmark.getContent(), content);
        Assertions.assertEquals(mockBookmark.getTime(), time);
        Assertions.assertEquals(mockBookmark.getPosition(), position);
        Assertions.assertEquals(mockBookmark.getDrawing(), drawing);
        Assertions.assertEquals(mockBookmark.getScale(), scale);


    }

    @DisplayName("북마크 삭제")
    @Test
    void deleteBookmark() {
        bookmarkService.deleteBookmark(MOCK_ID);

        Mockito.verify(bookmarkRepository).findById(MOCK_ID);
        Mockito.verify(bookmarkRepository).delete(mockBookmark);


        Mockito.when(bookmarkRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BookmarkNotExistsException.class, () -> {
            bookmarkService.deleteBookmark(MOCK_ID + 1L);
        });

    }
}