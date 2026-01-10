package model;

public class Note {

    private int id;
    private int problemId;
    private String content;
    private String dateAdded;
    private String lastUpdated;

    public Note() {
    }

    public Note(int id, int problemId, String content,
                String dateAdded, String lastUpdated) {
        this.id = id;
        this.problemId = problemId;
        this.content = content;
        this.dateAdded = dateAdded;
        this.lastUpdated = lastUpdated;
    }

    public Note(int problemId, String content) {
        this.id = 0;
        this.problemId = problemId;
        this.content = content;
        this.dateAdded = null;
        this.lastUpdated = null;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
