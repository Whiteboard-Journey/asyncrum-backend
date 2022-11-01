package swm.wbj.asyncrum.domain.record.bookmark.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.record.bookmark.dto.*;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.bookmark.exception.BookmarkNotExistsException;
import swm.wbj.asyncrum.domain.record.bookmark.repository.BookmarkRepository;
import swm.wbj.asyncrum.domain.record.comment.exception.CommentNotExistsException;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.record.record.service.RecordService;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.service.MemberService;
import swm.wbj.asyncrum.global.firebase.NotificationPushService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberService memberService;
    private final RecordService recordService;
    private final NotificationPushService notificationPushService;

    @Override
    public BookmarkCreateResponseDto createBookmark(BookmarkCreateRequestDto requestDto) throws Exception {
        Member currentMember = memberService.getCurrentMember();
        Record currentRecord = recordService.getCurrentRecord(requestDto.getRecordId());

        Bookmark bookmark = requestDto.toEntity(currentRecord, currentMember);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        List<Member> mentionedMember = checkMentionsFromContent(bookmark.getContent());
        for (Member member : mentionedMember) {
            sendMentionMessageToMentioned(member, currentRecord);
        }

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
    public Bookmark getCurrentBookmark(Long id) {
        return bookmarkRepository.findById(id)
                .orElseThrow(CommentNotExistsException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public BookmarkReadAllResponseDto readAllBookmark(Long recordId) {
        Record record = recordService.getCurrentRecord(recordId);

        List<Bookmark> bookmarks = bookmarkRepository.findAllByRecord(record);

        return new BookmarkReadAllResponseDto(bookmarks);
    }

    @Override
    public BookmarkUpdateResponseDto updateBookmark(Long id, BookmarkUpdateRequestDto requestDto) throws Exception {
        Bookmark bookmark = bookmarkRepository.findById(id).orElseThrow(BookmarkNotExistsException::new);

        bookmark.updateBookmark(
                requestDto.getEmoji(),
                requestDto.getContent(),
                requestDto.getTime(),
                requestDto.getPosition(),
                requestDto.getDrawing(),
                requestDto.getScale()
        );

        List<Member> mentionedMember = checkMentionsFromContent(bookmark.getContent());
        for (Member member : mentionedMember) {
            sendMentionMessageToMentioned(member, bookmark.getRecord());
        }

        return new BookmarkUpdateResponseDto(bookmark.getId());
    }

    @Override
    public void deleteBookmark(Long id) {
        Bookmark bookmark = bookmarkRepository.findById(id).orElseThrow(BookmarkNotExistsException::new);

        bookmarkRepository.delete(bookmark);
    }

    private List<Member> checkMentionsFromContent(String content) {
        Set<String> memberFullnames = new HashSet<>();

        Pattern p = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_\\\\.]))@([A-Za-z][A-Za-z0-9_]+)");
        Matcher m = p.matcher(content);

        if (m.find()){
            memberFullnames.add(m.group(1));
        }

        return memberFullnames.stream()
                .map(memberService::getMemberByFullname).collect(Collectors.toList());
    }

    private void sendMentionMessageToMentioned(Member member, Record record) throws FirebaseMessagingException {
        String title = "Daily Scrum Mention";
        String body = "You have been mentioned by [" + record.getMember().getFullname() + "] in ["
                + record.getTeam().getName() + "] team! " +
                "Please check your team's daily scrum for more information!";

        notificationPushService.sendToSingleClient(title, body, member);
    }
}
