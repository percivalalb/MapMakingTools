package mapmakingtools.api.util;

public enum State {

    DEVELOPMENT("Dev"), // Feature only available in dev mode, not visible otherwise
    ALPHA("WIP"), // Visible to all, not accessible
    BETA("Beta"), // Visible to all, only accessible to those with permission
    RELEASE("R"); // Visible and accessible to all

    public final String letter;

    State(String letterIn) {
        this.letter = letterIn;
    }
}
