/**
 * Program can be in three states:
 *  - Intro: the user is on the screen displaying "click to start"
 *  - RUNNING_RED: the user is waiting for the screen to turn green
 *  - RUNNING_GREEN: the user should click the screen now, it has gone green
 *  - RESULTS: the results page is currently showing
 */
public enum ReactionGameStates {
    INTRO, RUNNING_RED, RUNNING_GREEN, RESULTS
}