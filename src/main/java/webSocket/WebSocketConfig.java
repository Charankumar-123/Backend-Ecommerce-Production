package webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Destination prefix for broadcasting messages from backend to frontend
        // e.g., /topic/order-tracking/{orderId}
        config.enableSimpleBroker("/topic");

        // Destination prefix for messages received from client to server (if any)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register /ws endpoint for clients, allowing React frontend origin
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:3000")
                .withSockJS();
    }
}
