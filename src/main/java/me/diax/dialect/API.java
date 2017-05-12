package me.diax.dialect;

public class API {
    public static String ask(String input) {
        int id = QuestionLogic.findBestMatch(input);
        return QuestionLogic.getOutputFromId(id);
    }
}
