package me.diax.dialect;

public class API {
    public static String ask(String input) {
        int id = QuestionLogic.findBestMatch(input);
        String output = QuestionLogic.getOutputFromId(id);
        return output;
    }
}
