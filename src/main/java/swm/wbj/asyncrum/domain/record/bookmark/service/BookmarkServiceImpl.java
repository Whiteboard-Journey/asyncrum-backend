package swm.wbj.asyncrum.domain.record.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.record.bookmark.dto.*;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.bookmark.exception.BookmarkNotExistsException;
import swm.wbj.asyncrum.domain.record.bookmark.repository.BookmarkRepository;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.record.record.service.RecordService;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberService memberService;
    private final RecordService recordService;

    @Override
    public BookmarkCreateResponseDto createBookmark(BookmarkCreateRequestDto requestDto) {
        Member currentMember = memberService.getCurrentMember();
        Record currentRecord = recordService.getCurrentRecord(requestDto.getRecordId());

        Bookmark bookmark = requestDto.toEntity(currentRecord, currentMember);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return new BookmarkCreateResponseDto(savedBookmark.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BookmarkReadResponseDto readBookmark(Long id) {
        Bookmark bookmark = bookmarkRepository.findById(id).orElseThrow(BookmarkNotExistsException::new);

        return new BookmarkReadResponseDto(bookmark);
    }

    @Override
    @Transactional(readOnly = true)
    public BookmarkReadAllResponseDto readAllBookmark(Long recordId) {
        Record record = recordService.getCurrentRecord(recordId);

        List<Bookmark> bookmarks = bookmarkRepository.findAllByRecord(record);

        return new BookmarkReadAllResponseDto(bookmarks);
    }

    @Override
    public BookmarkUpdateResponseDto updateBookmark(Long id, BookmarkUpdateRequestDto requestDto) {
        Bookmark bookmark = bookmarkRepository.findById(id).orElseThrow(BookmarkNotExistsException::new);

        bookmark.updateBookmark(
                requestDto.getEmoji(),
                requestDto.getContent(),
                requestDto.getTime(),
                requestDto.getPosition(),
                requestDto.getDrawing(),
                requestDto.getScale()
        );

        return new BookmarkUpdateResponseDto(bookmark.getId());
    }

    @Override
    public void deleteBookmark(Long id) {
        Bookmark bookmark = bookmarkRepository.findById(id).orElseThrow(BookmarkNotExistsException::new);

        bookmarkRepository.delete(bookmark);
    }
}
