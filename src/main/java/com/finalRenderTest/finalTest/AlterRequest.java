package com.databaseviewer.dbviewerapp;

public record AlterRequest(
        String pass,
        String tableName,
        String columnName,
        String newValue,
        int ID) {
}
