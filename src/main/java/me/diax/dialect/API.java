package me.diax.dialect;

public class API {
    public static String ask(String input) {
    	DatabaseOperations databaseOps = DatabaseOperations.getInstance();
        int id = databaseOps.findBestMatch(input);
        String response = databaseOps.getOutputFromId(id);
        return response;
    }

    public static void createDependenciesAndIncrement(String originalInput, String response) {
    	DatabaseOperations databaseOps = DatabaseOperations.getInstance();
    	int originalInputId = databaseOps.databaseContains(originalInput);
        if (originalInputId == -1) {
            originalInputId = databaseOps.createMessageRow(originalInput);
        }
        int responseId = databaseOps.databaseContains(response);
        if (responseId == -1) {
            responseId = databaseOps.createMessageRow(response);
        }
        int linkId = databaseOps.isLinked(originalInputId, responseId);
        if (linkId >= 0) {
            databaseOps.incrementWeight(linkId);
        }
        else {
            databaseOps.createLink(originalInputId, responseId);
        }
        System.out.println("success, response recorded");
    }
}