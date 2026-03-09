package com.jaswanth.accessmapper.DTO;

import lombok.Data;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Data
public class AccessReportResponseDTO {

    private String organization;
    private List<UserAccessDTO> users;

}