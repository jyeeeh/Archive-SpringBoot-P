package com.sloop.archive.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

@Slf4j
@Data
@NoArgsConstructor
public class CheckIdRequest {
  @NotBlank
  @Length(min = 4, max = 20)
  private String id;
}
