package avengers.nexus.chat;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketIOLifeCycle {
    private final SocketIOServer socketIOServer;

    @PostConstruct
    public void start() {
        socketIOServer.start();
    }
    @PreDestroy
    public void stop() {
        socketIOServer.stop();
    }
}
