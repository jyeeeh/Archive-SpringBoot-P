package com.sloop.archive.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

@Slf4j
@Data
@NoArgsConstructor
public class UserSearchRequest {

  @NotBlank
  @Length(min = 0, max = 20)
  private String searchWord;

  @NotBlank
  @Length(min = 1, max = 20)
  private String searchType;

  @NotBlank
  @Length(min = 1, max = 20)
  private String searchRole;
}
