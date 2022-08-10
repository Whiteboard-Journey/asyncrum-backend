package swm.wbj.asyncrum.domain.image.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.image.dto.*;
import swm.wbj.asyncrum.domain.image.service.ImageService;
import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.domain.whiteboard.service.WhiteboardService;
import swm.wbj.asyncrum.global.error.ErrorResponseDto;
import swm.wbj.asyncrum.global.type.ScopeType;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<?> createImage(@RequestBody ImageCreateRequestDto requestDto) {
        try {
            ImageCreateResponseDto responseDto = imageService.createImage(requestDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readImage(@PathVariable Long id) {
        try {
            ImageReadResponseDto responseDto = imageService.readImage(id);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    @GetMapping
    public ResponseEntity<?> readAllImage(
            @RequestParam(value = "scope") ScopeType scope,
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId)
    {
        try {
            ImageReadAllResponseDto responseDto = imageService.readAllImage(scope, pageIndex, topId);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateImage(@PathVariable Long id,  ImageUpdateRequestDto requestDto) {
        try {
            ImageUpdateResponseDto responseDto = imageService.updateImage(id, requestDto);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        try {
            imageService.deleteImage(id);

            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }







}
