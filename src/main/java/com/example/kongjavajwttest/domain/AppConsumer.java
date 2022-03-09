package com.example.kongjavajwttest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AppConsumer {

    @JsonIgnore
    private long id;
    @JsonProperty("username")
    private String kongUsername;
    @JsonIgnore
    private String kongUuid;
    @JsonIgnore
    private AppJwtCredential jwtCredential;

    public AppConsumer(long id, AppJwtCredential.AppJwtCredentialBuilder credentialBuilder) {
        this.id = id;
        this.kongUsername = String.format("appUser_%d", id);
        this.kongUuid = UUID.nameUUIDFromBytes(kongUsername.getBytes()).toString();
        this.jwtCredential = credentialBuilder.key(String.valueOf(id)).uuid(UUID.nameUUIDFromBytes(String.format("appUserCred_%d", id).getBytes()).toString()).build();
    }

}
