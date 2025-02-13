package com.server.community.domain.board;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageConverter {

    List<Image> convertImageFilesToImages(final List<MultipartFile> imageFiles);

    List<Image> convertImageIdsToImages(final List<Long> imageIds, final List<Image> images);
}
