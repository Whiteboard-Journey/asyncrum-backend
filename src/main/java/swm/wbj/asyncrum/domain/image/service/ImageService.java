package swm.wbj.asyncrum.domain.image.service;

import swm.wbj.asyncrum.domain.image.dto.*;
import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.io.IOException;

public interface ImageService {
    ImageCreateResponseDto createImage(ImageCreateRequestDto requestDto) throws IOException;

    ImageReadResponseDto readImage(Long id);

    ImageReadAllResponseDto readAllImage(ScopeType scope, Integer pageIndex, Long topId);

    ImageUpdateResponseDto updateImage(Long id, ImageUpdateRequestDto requestDto) throws IOException;

    void deleteImage(Long id);
}
