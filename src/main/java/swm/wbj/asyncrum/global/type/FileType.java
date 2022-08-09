package swm.wbj.asyncrum.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType {

    MP4("video/mp4", "mp4"),
    TLDR("application/octet-stream", "tldr");

    private final String contentType;
    private final String name;
}
