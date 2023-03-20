package io.gainable.sftpfilesequenzer.service;

import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Service
public class SolaceQueueService {

    @Autowired
    private JCSMPFactory jcsmpFactory;

    @Autowired
    private JCSMPProperties jcsmpProperties;

    public Mono<Integer> checkAndAddFilenamesToQueue(String queueName, Collection<String> filenames) {
        return Mono.fromCallable(() -> {
            try {
                JCSMPSession session = jcsmpFactory.createSession(jcsmpProperties);
                Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
                BrowserProperties browserProps = new BrowserProperties();
                browserProps.setEndpoint(queue);
                Browser browser = session.createBrowser(browserProps);

                ConsumerFlowProperties flowProps = new ConsumerFlowProperties();
                flowProps.setEndpoint(queue);
                FlowReceiver receiver = session.createFlow(null, flowProps);
                receiver.start();

                boolean isEmpty = true;
                Message msg;
                while ((msg = (Message) browser.getNext()) != null) {
                    isEmpty = false;
                    break;
                }

                if (isEmpty) {
                    filenames.forEach(filename -> {
                        try {
                            TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
                            message.setText(filename);
                            XMLMessageProducer producer = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
                                @Override
                                public void handleError(String messageID, JCSMPException e, long timestamp) {
                                    System.err.printf("Producer received error for msg: %s@%s - %s%n", messageID, timestamp, e);
                                }

                                @Override
                                public void responseReceived(String messageID) {
                                    System.out.printf("Producer received response for msg: %s%n", messageID);
                                }
                            });
                            producer.send(message, queue);
                        } catch (JCSMPException e) {
                            throw new RuntimeException("Error sending message to Solace PubSub+ queue", e);
                        }
                    });
                }

                receiver.stop();
                session.closeSession();

                return filenames.size();
            } catch (JCSMPException e) {
                throw new RuntimeException("Error interacting with Solace PubSub+ queue", e);
            }
        });
    }
}
