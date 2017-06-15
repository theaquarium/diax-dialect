package com.aquariumpain.dialect.chatbot;

import javax.servlet.ServletContext;
import com.aquariumpain.dialect.database.DatabaseOperations;

public class Chatbot {
	
    public static String ask(String input, ServletContext context) {
    	DatabaseOperations databaseOps = (DatabaseOperations) context.getAttribute("databaseOps");
        long id = databaseOps.findBestMatch(input);
        String response = databaseOps.getOutputFromId(id);
        return response;
    }

    public static void createDependenciesAndIncrement(String originalInput, String response, ServletContext context) {
    	DatabaseOperations databaseOps = (DatabaseOperations) context.getAttribute("databaseOps");
    	long originalInputId = databaseOps.databaseContains(originalInput);
        if (originalInputId == -1) {
            originalInputId = databaseOps.createMessageRow(originalInput);
        }
        long responseId = databaseOps.databaseContains(response);
        if (responseId == -1) {
            responseId = databaseOps.createMessageRow(response);
        }
        long linkId = databaseOps.isLinked(originalInputId, responseId);
        if (linkId >= 0) {
            databaseOps.incrementWeight(linkId);
        }
        else {
            databaseOps.createLink(originalInputId, responseId);
        }
    }
}