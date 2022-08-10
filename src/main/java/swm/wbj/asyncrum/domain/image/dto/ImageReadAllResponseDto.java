package swm.wbj.asyncrum.domain.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.image.entity.Image;

import java.util.List;

@Data
@AllArgsConstructor
public class ImageReadAllResponseDto {

    private List<Image> images;
    private Pageable pageable;
    private Boolean isList;
}
