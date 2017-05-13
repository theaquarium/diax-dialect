package me.diax.dialect;

public class API {
    public String ask(String input) {
        DatabaseOperations databaseOps = DatabaseOperations.getInstance();
        int id = databaseOps.findBestMatch(input);
        return databaseOps.getOutputFromId(id);
    }

}