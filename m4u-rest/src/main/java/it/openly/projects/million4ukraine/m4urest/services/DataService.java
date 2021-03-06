package it.openly.projects.million4ukraine.m4urest.services;

import it.openly.projects.million4ukraine.m4urest.repositories.M4UMessages;
import it.openly.projects.million4ukraine.m4urest.utils.XY;
import it.openly.projects.million4ukraine.m4urest.views.M4UMessage;
import it.openly.projects.million4ukraine.m4urest.views.NameAndMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DataService {

    @Autowired
    M4UMessages repository;

    public void savePreDonationMessage(M4UMessage request, XY spot) {
        request.setId(UUID.randomUUID().toString());
        request.setTimestamp(new Date());
        request.setX(spot.getX());
        request.setY(spot.getY());
        request.setProcessed(false);
        request.setAmountDonated(null);
        repository.save(request);
    }

    public void savePostDonationMessage(M4UMessage request, BigDecimal amountDonated) {
        request.setProcessed(true);
        request.setAmountDonated(amountDonated);
        repository.save(request);
    }


    public M4UMessage loadMessage(String uuid) {
        return repository.getById(uuid);
    }

    public List<NameAndMessage> getLatestMessages() {
        Page<M4UMessage> results = repository.findAll(PageRequest.of(0, 5, Sort.Direction.DESC, "timestamp"));
        return results.get().map(msg -> new NameAndMessage(msg.getName(), msg.getMessage(), msg.getTimestamp())) .collect(Collectors.toList());
    }
}
