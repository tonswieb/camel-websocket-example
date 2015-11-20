package nl.finalist.test.ws;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.builder.RouteBuilder;

public class WebSocketRoute extends RouteBuilder {

	public final static String WEBSOCKET_BROADCAST_URI = "websocket://camel-tweet?sendToAll=true&staticResources=classpath:webapp";
	public final static String WEBSOCKET_URI = "websocket://camel-tweet?staticResources=classpath:webapp";

	private ConcurrentHashMap<String, String> activeClients = new ConcurrentHashMap<>();
	
	@Override
	public void configure() throws Exception {
		
		//Generate event every 6 seconds
		from("timer://foo?fixedRate=true&period=6000")
		.setBody(constant("Broadcast message"))
		.to(WEBSOCKET_BROADCAST_URI);
		
		from(WEBSOCKET_URI)
		.log("Received: ${body} from ${header[websocket.connectionKey]}")
//		.bean(activeClients,"put(${header[websocket.connectionKey]},'Test')")
		.setBody(simple("${body} from ${header[websocket.connectionKey]}"))
		.to(WEBSOCKET_URI);
		
//		from("timer://client?fixedRate=true&period=6000")
//		.setBody(constant(activeClients.keySet()))
//		.split(body())
//			.setHeader("websocket.connectionKey",body())
//			.log("Test message to: ${body}")
//			.setBody(simple("Test message to: ${body}"))
//			.to(WEBSOCKET_URI);
	}
}
