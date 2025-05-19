package com.example.springboot.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    @Getter
    @Schema(description = "object id", example = "1234567890", required = true)
    private long  objectId;

    @Getter
    @Schema(description = "payload", example = "anything i want here", required = true)
    private String  blob;
}
