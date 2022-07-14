package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

import java.util.List;

@Data
@AllArgsConstructor
public class WhiteboardReadAllResponseDto {

    private List<Whiteboard> whiteboards;
    private Pageable pageable;
    private Boolean isList;

}
