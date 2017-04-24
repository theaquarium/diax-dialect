package me.diax.dialect;

public class API {
    public static String ask(String input) {
        int id = mainQuestionLogic.findBestMatch(input);
        String output = mainQuestionLogic.getOutputFromId(id);
        return output;
    }
}
