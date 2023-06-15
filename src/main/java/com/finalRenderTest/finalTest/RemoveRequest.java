package com.databaseviewer.dbviewerapp;

public record RemoveRequest(
        String pass,
        String tableName,
        int ID) {
}
