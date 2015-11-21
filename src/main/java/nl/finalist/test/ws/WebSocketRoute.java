package nl.finalist.test.ws;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.websocket.WebsocketConstants;

public class WebSocketRoute extends RouteBuilder {

	public final static String WEBSOCKET_URI = "websocket://camel-tweet?staticResources=classpath:webapp";

	private ConcurrentHashMap<String, String> activeClients = new ConcurrentHashMap<>();
	
	@Override
	public void configure() throws Exception {
		
		from(WEBSOCKET_URI)	
		.log("Received: ${body} from ${header[websocket.connectionKey]}")
		.setHeader(WebsocketConstants.SEND_TO_ALL,constant(""))
//		.bean(activeClients,"put(${header[websocket.connectionKey]},'Test')")
		.setBody(simple("${body} from ${header[websocket.connectionKey]}"))
		.to(WEBSOCKET_URI);

		//Generate event every 6 seconds
		from("timer://foo?fixedRate=true&period=6000")
		.setBody(constant("Broadcast message"))
		.setHeader(WebsocketConstants.SEND_TO_ALL,constant(true))
		.to(WEBSOCKET_URI);		
		
		from("timer://client?fixedRate=true&period=6000")
		.setBody(constant(activeClients.keySet()))
		.split(body())
			.setHeader("websocket.connectionKey",body())
			.log("Test message to: ${body}")
			.setBody(simple("Test message to: ${body}"))
			.to(WEBSOCKET_URI);
	}
}
