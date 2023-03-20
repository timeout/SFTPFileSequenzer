package io.gainable.sftpfilesequenzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;

@Service
public class FacadeService {
    @Autowired
    private SolaceQueueService solaceQueueService;

    public void processFilenames(String queueName, Collection<String> filenames) {
        solaceQueueService.checkAndAddFilenamesToQueue(queueName, filenames)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
