package it.openly.projects.million4ukraine.m4urest.utils;

import it.openly.projects.million4ukraine.m4urest.views.M4UMessage;
import org.owasp.esapi.ESAPI;

public class DataCleaner {

    public static void xssClean(M4UMessage msg) {
        msg.setName(ESAPI.encoder().encodeForHTML(msg.getName()));
        msg.setEmail(ESAPI.encoder().encodeForHTML(msg.getEmail()));
        msg.setMessage(ESAPI.encoder().encodeForHTML(msg.getMessage()));
        msg.setNationality(ESAPI.encoder().encodeForHTML(msg.getNationality()));
    }

    public static void constrainSize(M4UMessage msg) {
        msg.setName(msg.getName().substring(0, Math.min(msg.getName().length() , 255)));
        msg.setEmail(msg.getEmail().substring(0, Math.min(msg.getEmail().length() , 255)));
        msg.setMessage(msg.getMessage().substring(0, Math.min(msg.getMessage().length() , 500)));
        msg.setNationality(msg.getNationality().substring(0, Math.min(msg.getNationality().length() , 100)));
    }
}
