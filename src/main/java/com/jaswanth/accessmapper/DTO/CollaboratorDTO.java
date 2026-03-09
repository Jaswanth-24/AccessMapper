package com.jaswanth.accessmapper.DTO;

import lombok.Data;

@Data
public class CollaboratorDTO {

    private String login;
    private PermissionsDTO permissions;

}