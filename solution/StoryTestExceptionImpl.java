package solution;

import provided.StoryTestException;

public class StoryTestExceptionImpl extends StoryTestException {
    public String firstFailedThenSentence;
    public String expectedValueOfFirstFailedThen;
    public String actualValueOfFirstFailedThen;
    public int countFailedThen;

    StoryTestExceptionImpl() {
        firstFailedThenSentence = null;
        expectedValueOfFirstFailedThen = null;
        actualValueOfFirstFailedThen = null;
        countFailedThen = 0;
    }

    /**
     * Returns a string representing the sentence
     * of the first Then sentence that failed
     */
    @Override
    public String getSentance() {
        return firstFailedThenSentence;
    }

    /**
     * Returns a string representing the expected value from the story
     * of the first Then sentence that failed.
     */
    @Override
    public String getStoryExpected() {
        return expectedValueOfFirstFailedThen;
    }

    /**
     * Returns a string representing the actual value.
     * of the first Then sentence that failed.
     */
    @Override
    public String getTestResult() {
        return actualValueOfFirstFailedThen;
    }

    /**
     * Returns an int representing the number of Then sentences that failed.
     */
    @Override
    public int getNumFail() {
        return countFailedThen;
    }
}
