package swm.wbj.asyncrum.global.media;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType {

    WEBM("video/webm", "webm"),
    TLDR("application/octet-stream", "tldr");

    private final String contentType;
    private final String name;
}
