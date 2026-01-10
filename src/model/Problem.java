package model;

public class Problem {

    private int id;
    private int siteId;
    private String code;
    private String title;
    private String difficulty;
    private String link;
    private int tries;

    public Problem() {
    }

    public Problem(int id, int siteId, String code, String title,
                   String difficulty, String link, int tries) {
        this.id = id;
        this.siteId = siteId;
        this.code = code;
        this.title = title;
        this.difficulty = difficulty;
        this.link = link;
        this.tries = tries;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    @Override
    public String toString() {
        return code + " - " + title; // Problem
    }


}
