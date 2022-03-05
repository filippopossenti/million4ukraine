package it.openly.projects.million4ukraine.m4urest.views;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String name;
    private String nationality;
    private String message;

    private String imageDataurl;

}
