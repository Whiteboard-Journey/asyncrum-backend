package swm.wbj.asyncrum.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.image.dto.*;
import swm.wbj.asyncrum.domain.image.entity.Image;
import swm.wbj.asyncrum.domain.image.repository.ImageRepository;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.io.IOException;

@RequiredArgsConstructor
@Transactional
@Service
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;
    private final MemberService memberService;
    private final AwsService awsService;

    private static final String IMAGE_BUCKET_NAME = "images";
    private static final String IMAGE_FILE_PREFIX ="image";

    @Override
    public ImageCreateResponseDto createImage(ImageCreateRequestDto requestDto) throws IOException {

        String title = requestDto.getTitle();

        if(imageRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("해당 제목은 이미 사용중입니다.");
        }

        Image image = requestDto.toEntity(memberService.getCurrentMember());
        Long imageId = imageRepository.save(image).getId();

        String imageFileKey = createImageFileKey(memberService.getCurrentMember().getId(), imageId);

        String preSignedURL = awsService.generatePresignedURL(imageFileKey, IMAGE_BUCKET_NAME, FileType.JPEG);

        image.update(null, null, imageFileKey, awsService.getObjectURL(imageFileKey, IMAGE_BUCKET_NAME), null);

        return new ImageCreateResponseDto(image.getId(), preSignedURL);
    }

    @Transactional(readOnly = true)
    @Override
    public ImageReadResponseDto readImage(Long id) {

        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 존재하지 않습니다."));

        return new ImageReadResponseDto(image);
    }

    @Override
    public ImageReadAllResponseDto readAllImage(ScopeType scope, Integer pageIndex, Long topId) {
        int SIZE_PER_PAGE = 12;
        Page<Image> imagePage;
        Pageable pageable = PageRequest.of(pageIndex, SIZE_PER_PAGE, Sort.Direction.DESC, "image_id");

        Member currentMember = memberService.getCurrentMember();
        RoleType memberRoleType = currentMember.getRoleType();

        // TODO: JPA 학습 후 JPA Specification 사용
        switch (memberRoleType) {
            case ADMIN:
                if(topId == 0) {
                    imagePage = imageRepository.findAll(pageable);
                }
                else {
                    imagePage = imageRepository.findAllByTopId(topId, pageable);
                }
                break;
            case USER:
                if (scope == ScopeType.TEAM && currentMember.getTeam() != null) {
                    if(topId == 0) {
                        imagePage = imageRepository.findAllByTeam(currentMember.getTeam().getId(), currentMember.getId(), pageable);
                    }
                    else {
                        imagePage = imageRepository.findAllByTeamAndTopId(currentMember.getTeam().getId(), currentMember.getId(), topId, pageable);
                    }
                }
                else if (scope == ScopeType.PRIVATE || currentMember.getTeam() == null) {
                    if(topId == 0) {
                        imagePage = imageRepository.findAllByAuthor(currentMember.getId(), pageable);
                    }
                    else {
                        imagePage = imageRepository.findAllByAuthorAndTopId(currentMember.getId(), topId, pageable);
                    }
                }
                else {
                    throw new IllegalArgumentException("허용되지 않은 범위입니다.");
                }
                break;
            case GUEST:
            default:
                throw new IllegalArgumentException("허용되지 않은 작업입니다.");
        }

        return new ImageReadAllResponseDto(imagePage.getContent(), imagePage.getPageable(), imagePage.isLast());
    }

    @Override
    public ImageUpdateResponseDto updateImage(Long id, ImageUpdateRequestDto requestDto) throws IOException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 존재하지 않습니다."));

        String preSignedURL = awsService.generatePresignedURL(image.getImageFileKey(), IMAGE_BUCKET_NAME, FileType.JPEG);
        return new ImageUpdateResponseDto(imageRepository.save(image).getId(), preSignedURL);
    }

    @Override
    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 존재하지 않습니다."));

        awsService.deleteFile(image.getImageFileKey(), IMAGE_BUCKET_NAME);
        imageRepository.delete(image);

    }

    public String createImageFileKey(Long memberId, Long imageId) {
        return IMAGE_FILE_PREFIX + "_" + memberId + "_" + imageId + "." + FileType.JPEG.getName();
    }
}
