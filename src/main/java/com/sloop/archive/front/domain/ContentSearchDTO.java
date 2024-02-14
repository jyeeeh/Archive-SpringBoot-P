package com.sloop.archive.front.domain;


import com.sloop.archive.content.domain.ContentDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class ContentSearchDTO extends ContentDTO {
    private String presignedURL;

}
