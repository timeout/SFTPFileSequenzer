package io.gainable.sftpfilesequenzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class SolaceQueueScheduler {

    @Autowired
    private SolaceQueueService solaceQueueService;

    @Autowired
    private SftpService sftpService;

    private static final String queueName = "your-queue-name";
    private static final String sftpRemoteDirectory = "your-sftp-remote-directory";

    @Scheduled(fixedRate = 60000) // 60,000 ms = 1 minute
    public void checkAndAddFilenamesToQueue() {
        sftpService.fetchFilenames(sftpRemoteDirectory)
                .flatMap(fileList -> solaceQueueService.checkAndAddFilenamesToQueue(queueName, fileList))
                .subscribe(
                        size -> log.info("Added [{}] files to the queue", size),
                        error -> log.error(
                                "Error while checking and adding filenames to the queue: {}",
                                error.getMessage()
                        )
                );
    }
}

