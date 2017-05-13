package me.diax.dialect;


class Trainer {
    private DatabaseOperations databaseOps;

    Trainer(DatabaseOperations _databaseOps) {
        databaseOps = _databaseOps;
    }

    void createDependenciesAndIncrement(String response, String originalInput) {
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
        System.out.println("Recorded!");
    }


    String train(String input, String originalInput, String response) {
        String result = "";
        if (input.equals("yes")) {
            createDependenciesAndIncrement(response, originalInput);
            result = "recorded";
        }
        else if (input.equals("no")) {
            result = "askResponse";
        }
        return result;
    }
}
