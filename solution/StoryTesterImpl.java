package solution;

import provided.GivenNotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class StoryTesterImpl implements provided.StoryTester {
    /**
     * Runs a given story on an instance of a given class, or an instances of its
     * ancestors. before running the story use must create an instance of the given
     * class.
     *
     * @param story     contains the text of the story to test, the string is
     *                  divided to line using '\n'. each word in a line is separated by space
     *                  (' ').
     * @param testClass the test class which the story should be run on.
     */
    @Override
    public void testOnInheritanceTree(String story, Class<?> testClass) throws Exception {
        //TODO finish implementation
        if (story == null || testClass == null)
            throw new IllegalArgumentException();

        Object testObject = testClass.newInstance();

        String[] storyLines = story.split("\n");
        if (!storyLines[0].substring(0, 5).equals("Given"))
            throw new GivenNotFoundException();//should not happen because it wont be tested

        String givenValue = storyLines[0].substring(6, storyLines[0].lastIndexOf(" "));//6 is the length of "Given "
        Method methodWithGivenAnnotation = getMethodOnInheritanceTree(testClass, Given.class, givenValue);
        if (methodWithGivenAnnotation == null)
            throw new GivenNotFoundException();
        String storyValue = storyLines[0].substring(storyLines[0].lastIndexOf(" ") + 1, storyLines[0].length());
        try {
            methodWithGivenAnnotation.invoke(testObject, storyValue);
        } catch (Exception IllegalArgumentException) {
            methodWithGivenAnnotation.invoke(testObject, Integer.parseInt(storyValue));
        }


    }

    private Method getMethodOnInheritanceTree(Class<?> currentClass, Class<?> annotationClass, String givenValue) {
        if (currentClass == null)
            return null;
        for (Method method : currentClass.getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (correctAnnotation(annotationClass, givenValue, annotation)) return method;
            }
        }
        return getMethodOnInheritanceTree(currentClass.getSuperclass(), annotationClass, givenValue);
    }

    private boolean correctAnnotation(Class<?> annotationClass, String givenValue, Annotation annotation) {
        if (annotation.annotationType().equals(annotationClass)) {
            try {
                String annotationValue = (String) (annotationClass.getMethod("value").invoke(annotation));
                String trimmedAnnotationValue = annotationValue.substring(0, annotationValue.lastIndexOf(" "));
                if (trimmedAnnotationValue.equals(givenValue))
                    return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    /**
     * Runs a given story on an instance of a given class, or an instances of its
     * ancestors, or its nested class (and their ancestors) as described by the
     * the assignment document. before running the story use must create an instance
     * of the given correct class to run story on.
     *
     * @param story     contains the text of the story to test, the string is
     *                  divided to line using '\n'. each word in a line is separated by space
     *                  (' ').
     * @param testClass the test class which the story should be run on.
     */
    @Override
    public void testOnNestedClasses(String story, Class<?> testClass) throws Exception {
        //TODO implement
    }
}