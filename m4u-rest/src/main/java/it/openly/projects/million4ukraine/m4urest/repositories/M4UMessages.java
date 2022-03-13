package it.openly.projects.million4ukraine.m4urest.repositories;

import it.openly.projects.million4ukraine.m4urest.views.M4UMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface M4UMessages extends JpaRepository<M4UMessage, String> {
}
