package com.example.springboot.model;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    @Getter
    private long  objectId;

    @Getter
    private String  blob;
}
