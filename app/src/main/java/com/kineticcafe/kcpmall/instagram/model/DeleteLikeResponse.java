package com.kineticcafe.kcpmall.instagram.model;

public class DeleteLikeResponse {

    private Meta meta;

    public boolean isSuccessfull() {
        return meta.code.equals(200);
    }

    private static class Meta {

        private Integer code;

    }

}
