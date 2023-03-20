package io.gainable.sftpfilesequenzer.service;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Vector;

@Service
public class SftpService {

    @Autowired
    private SftpRemoteFileTemplate sftpRemoteFileTemplate;

    public Mono<List<String>> fetchFilenames(String remoteDirectory) {
        return Mono.fromCallable(() -> sftpRemoteFileTemplate.execute(session -> {
            final var fileList = session.list(remoteDirectory);

            return Arrays.stream(fileList)
                    .filter(file -> !file.getAttrs().isDir())
                    .map(ChannelSftp.LsEntry::getFilename)
                    .toList();
        }));
    }
}


