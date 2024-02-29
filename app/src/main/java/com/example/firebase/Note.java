package com.example.firebase;

public class Note {
    private String id;
    public String getId() {return id;}
    public void setId(String id) { this.id = id; }
    private String name;

    public String getName() {
        return name;
    }

    public Note(){

    }
    public String getDescription() {
        return description;
    }

    private String description;

    public Note(int id, String name, String description) {
        this.id = String.valueOf(id);
        this.name = name;
        this.description = description;
    }
    public Note(String name, String description) {
        this.name = name;
        this.description = description;
    }

//    public Note setID(String id) {
//        this.id = id;
//        return this;
//    }
}
