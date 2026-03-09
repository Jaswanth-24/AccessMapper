package com.jaswanth.accessmapper.DTO;

import lombok.Data;
import java.util.List;

@Data
public class UserAccessDTO {

    private String username;
    private List<RepositoryAccessDTO> repositories;

}