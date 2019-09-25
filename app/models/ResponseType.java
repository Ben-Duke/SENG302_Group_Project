package models;

public enum ResponseType {
    Going("going"),
    Interested("interested"),
    NotGoing("not going"),
    Went("went");

    private String displayText;

    ResponseType(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
