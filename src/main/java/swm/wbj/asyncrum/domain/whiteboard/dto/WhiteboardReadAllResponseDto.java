package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class WhiteboardReadAllResponseDto {

    private List<WhiteboardReadResponseDto> whiteboards;
    private Pageable pageable;
    private Boolean isList;

    public WhiteboardReadAllResponseDto(List<Whiteboard> whiteboards, Pageable pageable, Boolean isList) {
        this.whiteboards = whiteboards.stream()
                .map(WhiteboardReadResponseDto::new)
                .collect(Collectors.toList());
        this.pageable = pageable;
        this.isList = isList;
    }
}
