package com.jaswanth.accessmapper.DTO;
import lombok.Data;

@Data
public class PermissionsDTO {

    private boolean admin;
    private boolean push;
    private boolean pull;
    private boolean triage;
    private boolean maintain;
}