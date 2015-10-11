package qiitapi.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    
    private int permanentId;
    private String id;
    private String name;

    private String facebookId;
    private String linkedinId;
    private String githubLoginName;
    private String twitterScreenName;

    private String location;
    private String organization;
    private String description;
    private String profileImageUrl;
    private String websiteUrl;

    private int followeesCount;
    private int followersCount;
    private int itemsCount;

}
