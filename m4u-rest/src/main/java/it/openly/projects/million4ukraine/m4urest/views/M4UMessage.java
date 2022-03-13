package it.openly.projects.million4ukraine.m4urest.views;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "m4u_messages")
@Data
public class M4UMessage {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="timestamp")
    private Date timestamp;

    @Column(name="name")
    private String name;
    @Column(name="email")
    private String email;
    @Column(name="nationality")
    private String nationality;
    @Column(name="message")
    private String message;

    @Column(name="image_dataurl", columnDefinition = "CLOB")
    @Lob
    private String imageDataurl;
    @Column(name="x")
    private int x;
    @Column(name="y")
    private int y;
    @Column(name="size_x")
    private int sizeX;
    @Column(name="size_y")
    private int sizeY;
}
