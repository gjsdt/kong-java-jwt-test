package com.example.kongjavajwttest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppJwtCredential {
    @JsonIgnore
    private String uuid;
    private String key;
    private String algorithm;
    private String secret;

}
