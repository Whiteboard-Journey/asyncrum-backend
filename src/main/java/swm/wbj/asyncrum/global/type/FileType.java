package swm.wbj.asyncrum.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 파일 MIME 타입 Enum
 */
@Getter
@AllArgsConstructor
public enum FileType {

    MP4("video/mp4", "mp4"),
    PNG("image/png", "png"),
    TLDR("application/octet-stream", "tldr"),
    ANY("application/octet-stream", "any");

    private final String mimeType;
    private final String name;

    public static FileType of(String code) {
        if (code == null) {
            throw new IllegalArgumentException("MIME 타입 설정이 되어있지 않습니다.");
        }

        return Arrays.stream(FileType.values())
                .filter(r -> r.getMimeType().equals(code))
                .findAny()
                .orElse(ANY);
    }
}
