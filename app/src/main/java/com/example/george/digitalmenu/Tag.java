package com.example.george.digitalmenu;

import android.graphics.Bitmap;

enum Tag {
    GLUTEN_FREE("Gluten Free", "https://firebasestorage.googleapis.com/v0/b/digitalmenu-6df05.appspot.com/o/tag_icons%2Fgluten_free.png?alt=media&token=13ab19ba-d1dd-42a8-8324-4f1fb746ea27"),
    HALAL("Halal", "https://firebasestorage.googleapis.com/v0/b/digitalmenu-6df05.appspot.com/o/tag_icons%2Fhalal.png?alt=media&token=ce6b9054-bd73-445e-b1bd-919216d007a8"),
    KOSHER("Kosher", "https://firebasestorage.googleapis.com/v0/b/digitalmenu-6df05.appspot.com/o/tag_icons%2FKosher.png?alt=media&token=fbd0f605-207f-4f8a-9947-4ed3321f3bac"),
    VEGAN("Vegan", "https://firebasestorage.googleapis.com/v0/b/digitalmenu-6df05.appspot.com/o/tag_icons%2Fvegen.png?alt=media&token=e1ea2361-4e70-4cfe-9535-6740680afc7e"),
    VEGETARIAN("Vegetarian", "https://firebasestorage.googleapis.com/v0/b/digitalmenu-6df05.appspot.com/o/tag_icons%2Fvegetarian.svg?alt=media&token=e96bc5a4-b8a4-4234-b7ae-f81872e9d169"),
    HOT("Hot", "https://firebasestorage.googleapis.com/v0/b/digitalmenu-6df05.appspot.com/o/tag_icons%2Fhot.svg?alt=media&token=785f38c2-77b0-4d6b-a9c7-11851e9ebd8a"),
    Atomic("Atomic","https://firebasestorage.googleapis.com/v0/b/digitalmenu-6df05.appspot.com/o/tag_icons%2Fk-hot.png?alt=media&token=945160cd-c06d-474c-a5ba-ad5b38891697");

    private String text;
    private String pic_url;
    private byte[] picture;
    private boolean iconDownloaded = false;

    Tag(String text, String pic_url) {
        this.text = text;
        this.pic_url = pic_url;
    }

    public String getPic_url() {
        return this.pic_url;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
        this.iconDownloaded = true;
    }

    public byte[] getPicture() {
        return this.picture;
    }

    public boolean iconDownload() {
        return this.iconDownloaded;
    }
}
