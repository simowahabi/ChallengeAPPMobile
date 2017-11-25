package com.hidden.mohamedwahabi.hiddenfbpictures.classes;

/**
 * Created by wahabiPro on 11/21/2017.
 */

public class AlbumClass {
    String id, source, name, countpic;

    public AlbumClass(String id, String source, String name, String countpic) {
        this.id = id;
        this.source = source;
        this.name = name;
        this.countpic = countpic;
    }

    public String getCountpic() {
        return countpic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
