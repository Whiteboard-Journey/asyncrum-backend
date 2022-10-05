package swm.wbj.asyncrum.domain.record.bookmark.entity;

import lombok.Getter;

import javax.persistence.Embeddable;

@Getter
@Embeddable
public class VideoBookmarkCoordinates {

    private Double x;
    private Double y;

    protected VideoBookmarkCoordinates() { }

    public VideoBookmarkCoordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
}
