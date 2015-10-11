package qiitapi.entity;

import lombok.Data;

@Data
public class Tag {
    private String id;
    private String iconUrl;
    private long itemsCount;
    private long followersCount;
}
