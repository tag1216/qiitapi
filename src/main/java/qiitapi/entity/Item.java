package qiitapi.entity;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Item {
    
    @Data
    public static class Tag {
        private String name;
        private List<String> versions;
    }

    private String id;
    private String url;

    private List<Item.Tag> tags;
    private User user;

    private Date createdAt;
    private Date updatedAt;
    private boolean coediting;

    private String title;
    private String body;
    private String renderedBody;
    
}
