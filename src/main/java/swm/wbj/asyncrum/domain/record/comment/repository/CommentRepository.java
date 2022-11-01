package swm.wbj.asyncrum.domain.record.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.comment.entity.Comment;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBookmark(Bookmark bookmark);
}
