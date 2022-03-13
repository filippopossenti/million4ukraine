package it.openly.projects.million4ukraine.m4urest.views;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class NameAndMessage {
    private String name;
    private String message;
    private Date timestamp;
}
