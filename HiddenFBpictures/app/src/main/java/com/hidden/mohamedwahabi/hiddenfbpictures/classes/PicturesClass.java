package com.hidden.mohamedwahabi.hiddenfbpictures.classes;

/**
 * Created by wahabiPro on 11/22/2017.
 */

public class PicturesClass {
    String id, urlpic;

    public PicturesClass(String id, String urlpic) {
        this.id = id;
        this.urlpic = urlpic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {this.id = id;}

    public String getUrlpic() {
        return urlpic;
    }

}
