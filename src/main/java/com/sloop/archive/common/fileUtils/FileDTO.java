package com.sloop.archive.common.fileUtils;

import lombok.*;

@Getter
@Setter
@ToString
public class FileDTO {
    private Long id;
    private Long contentId;
    private String extension;
    private String originalName;
    private String savedName;
    private String thumbName;
    private String originalPath;
    private String savedPath;
    private String thumbPath;

    @Builder
    public FileDTO(Long contentId, String extension, String originalName, String savedName, String thumbName, String originalPath, String savedPath, String thumbPath) {
        this.contentId = contentId;
        this.extension = extension;
        this.originalName = originalName;
        this.savedName = savedName;
        this.thumbName = thumbName;
        this.originalPath = originalPath;
        this.savedPath = savedPath;
        this.thumbPath = thumbPath;
    }

}
