package solution;

import provided.GivenNotFoundException;
import provided.ThenNotFoundException;
import provided.WhenNotFoundException;
import provided.WordNotFoundException;
import sun.print.BackgroundLookupListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

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
        if (story == null || testClass == null)
            throw new IllegalArgumentException();

        Object testInstance = testClass.newInstance();
        testOnInheritanceTree_aux(story, testClass, testInstance);
    }

    /**
     * Runs a given story on an instance of a given class, or an instances of its
     * ancestors. instance of the given class provided.
     *
     * @param story        contains the text of the story to test, the string is
     *                     divided to line using '\n'. each word in a line is separated by space
     *                     (' ').
     * @param testClass    the test class which the story should be run on.
     * @param testInstance an instance of the given class.
     * @throws Exception
     */
    public void testOnInheritanceTree_aux(String story, Class<?> testClass, Object testInstance) throws Exception {

        String[] storyLines = story.split("\n");

        //region Given
        if (!storyLines[0].substring(0, 5).equals("Given"))
            throw new GivenNotFoundException();//should not happen because it wont be tested

        String annotationValue = storyLines[0].substring(6, storyLines[0].lastIndexOf(" "));//6 is the length of "Given "
        Method methodWithGivenAnnotation = getMethodOnInheritanceTree(testClass, Given.class, annotationValue);
        if (methodWithGivenAnnotation == null)
            throw new GivenNotFoundException();
        String parameterInStoryLine = storyLines[0].substring(storyLines[0].lastIndexOf(" ") + 1, storyLines[0].length());
        invokeMethod(methodWithGivenAnnotation, testInstance, parameterInStoryLine);
        //endregion

        //region When\Then
        BackupTool backupTool = new BackupTool(testClass);
        boolean lastLineWasThen = true;
        for (int i = 1; i < storyLines.length; ++i) {
            Method methodToInvoke;
            annotationValue = storyLines[i].substring(5, storyLines[i].lastIndexOf(" "));//5 is the length of "When "/"Then "
            parameterInStoryLine = storyLines[i].substring(storyLines[i].lastIndexOf(" ") + 1, storyLines[i].length());
            if (storyLines[i].substring(0, 4).equals("When")) {
                if (lastLineWasThen)
                    backupTool.backup(testInstance);
                lastLineWasThen = false;
                methodToInvoke = getMethodOnInheritanceTree(testClass, When.class, annotationValue);
                if (methodToInvoke == null)
                    throw new WhenNotFoundException();
                invokeMethod(methodToInvoke, testInstance, parameterInStoryLine);
            } else if (storyLines[i].substring(0, 4).equals("Then")) {
                lastLineWasThen = true;
                methodToInvoke = getMethodOnInheritanceTree(testClass, Then.class, annotationValue);
                if (methodToInvoke == null)
                    throw new ThenNotFoundException();
                invokeMethod(methodToInvoke, testInstance, parameterInStoryLine);//TODO should handle ComparisonFailure here & restore if necessary
            } else {
                throw new WordNotFoundException();//should not happen because it wont be tested
            }
            //Nachum's ComparisonFailure handler:
            //try {
            //    invokeMethod(methodToInvoke, testInstance, parameterInStoryLine);
            //} catch (InvocationTargetException e) {
            //    try {
            //        Throwable ea = e.getCause();
            //        throw ea;
            //    } catch (ComparisonFailure ce) {
            //        storyException = new StoryTestExceptionImpl();
            //        storyException.setFailedSentence(LineBackup, ce.getActual());
            //        storyException.incNumFailed();
            //    } catch (Throuable e1) {
            //        //How to handle here?
            //    }
            //}

        }
        //endregion

        //TODO: 1)if number of failes != 0 then should throw StoryTestException with all it's values.
        //TODO: 2)figure out how to catch that exception
        //TODO: 3)handle the backup/restore thing
    }

    private class BackupTool {
        private Object backupOfObject;
        private Class<?> classOfBackup;

        public BackupTool(Class<?> cls) {
            backupOfObject = null;
            classOfBackup = cls;
        }

        public void backup(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
            if (Arrays.asList(classOfBackup.getInterfaces()).contains(Cloneable.class)) {
                backupOfObject = classOfBackup.getMethod("clone").invoke(obj);
                return;
            }
            Constructor<?> copyConstructor = classOfBackup.getConstructor(classOfBackup);
            if (copyConstructor != null) {
                backupOfObject = copyConstructor.newInstance(obj);
                return;
            }
            //think how to get a backup otherwise
            //TODO implement the rest
        }

        public Object restore() {
            return backupOfObject;
            //TODO implement
        }
    }

    private void invokeMethod(Method method, Object Object, String parameter) throws Exception {
        if (isNumeric(parameter))
            method.invoke(Object, Integer.parseInt(parameter));
        else
            method.invoke(Object, parameter);
    }

    private Method getMethodOnInheritanceTree(Class<?> currentClass, Class<?> annotationClass, String annotationValue) {
        if (currentClass == null)
            return null;
        for (Method method : currentClass.getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (correctAnnotation(annotationClass, annotationValue, annotation)) return method;
            }
        }
        return getMethodOnInheritanceTree(currentClass.getSuperclass(), annotationClass, annotationValue);
    }

    /**
     * @param annotationClass Given/Then/When class.
     * @param annotationValue the string to lookup in the Annotation value (part of the 'current' line)
     * @param annotation      the current annotation that is being checked
     * @return true only if it's the correct annotation
     */
    private boolean correctAnnotation(Class<?> annotationClass, String annotationValue, Annotation annotation) {
        if (annotation.annotationType().equals(annotationClass)) {
            try {
                String annotationRealValue = (String) (annotationClass.getMethod("value").invoke(annotation));
                String trimmedAnnotationValue = annotationRealValue.substring(0, annotationRealValue.lastIndexOf(" "));
                if (trimmedAnnotationValue.equals(annotationValue))
                    return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
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
        if (story == null || testClass == null)
            throw new IllegalArgumentException();


        //region local Given
        String firstStoryLine = story.substring(0, story.indexOf('\n'));
        if (!firstStoryLine.substring(0, 5).equals("Given"))
            throw new GivenNotFoundException();//should not happen because it wont be tested

        String annotationValue = firstStoryLine.substring(6, firstStoryLine.lastIndexOf(" "));//6 is the length of "Given "
        Method methodWithGivenAnnotation = getMethodOnInheritanceTree(testClass, Given.class, annotationValue);
        if (methodWithGivenAnnotation != null) {
            testOnInheritanceTree(story, testClass);
            return;
        }
        //endregion
        //region inner Given
        Object testObject = testClass.newInstance();
        if (!testOnNestedClasses_aux(story, testClass, testObject, annotationValue))
            throw new GivenNotFoundException();
        //endregion


    }

    /**
     * Recursively lookup the correct method with the Given annotation in inner classes.
     *
     * @param story             contains the text of the story to test, the string is
     *                          divided to line using '\n'. each word in a line is separated by space
     *                          (' ').
     * @param enclosingClass    the test class enclosing the class which the story should be run on.
     * @param enclosingInstance instance of the enclosing class
     * @param annotationValue   the string to lookup in the Given value (part of the first line.
     *                          could get it from 'story' but no reason to do it over and over)
     * @return True only if we found a method with the correct Given annotation
     * @throws Exception
     */
    private boolean testOnNestedClasses_aux(String story, Class<?> enclosingClass, Object enclosingInstance, String annotationValue) throws Exception {
        Class<?>[] innerClasses = enclosingClass.getClasses();
        for (Class<?> innerClass : innerClasses) {
            Constructor<?> ctor = innerClass.getDeclaredConstructor(enclosingClass);
            Object innerInstance = ctor.newInstance(enclosingInstance);
            //region local Given
            Method methodWithGivenAnnotation = getMethodOnInheritanceTree(innerClass, Given.class, annotationValue);
            if (methodWithGivenAnnotation != null) {
                testOnInheritanceTree_aux(story, innerClass, innerInstance);
                return true;
            }
            //endregion
            //region inner Given
            if (testOnNestedClasses_aux(story, innerClass, innerInstance, annotationValue))
                return true;
            //endregion
        }
        return false;
    }
}