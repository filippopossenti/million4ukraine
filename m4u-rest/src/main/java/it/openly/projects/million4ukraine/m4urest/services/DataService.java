package it.openly.projects.million4ukraine.m4urest.services;

import it.openly.projects.million4ukraine.m4urest.repositories.M4UMessages;
import it.openly.projects.million4ukraine.m4urest.utils.XY;
import it.openly.projects.million4ukraine.m4urest.views.M4UMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class DataService {

    @Autowired
    M4UMessages repository;

    public void saveData(M4UMessage request, XY spot) {
        request.setId(UUID.randomUUID().toString());
        request.setTimestamp(new Date());
        request.setX(spot.getX());
        request.setY(spot.getY());
        repository.save(request);
    }
}
