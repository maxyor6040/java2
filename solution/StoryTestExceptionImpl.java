package solution;

import provided.StoryTestException;
public class StoryTestExceptionImpl extends StoryTestException {
    /**
     * Returns a string representing the sentence
     * of the first Then sentence that failed
     */
    @Override
    public String getSentance() {
        return null;
        //TODO implement
    }

    /**
     * Returns a string representing the expected value from the story
     * of the first Then sentence that failed.
     */
    @Override
    public String getStoryExpected() {
        return null;
        //TODO implement
    }

    /**
     * Returns a string representing the actual value.
     * of the first Then sentence that failed.
     */
    @Override
    public String getTestResult() {
        return null;
        //TODO implement
    }

    /**
     * Returns an int representing the number of Then sentences that failed.
     */
    @Override
    public int getNumFail() {
        return 0;
        //TODO implement
    }
}
