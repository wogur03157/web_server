package com.aroasoft.core.tcpserver.websocket;

import com.aroasoft.core.tcpserver.dto.Response;
import com.aroasoft.core.tcpserver.entity.Client;
import com.aroasoft.core.tcpserver.service.RequestService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketEventListener webSocketEventListener;
    private boolean bBroadcast = false;

    public WebSocketServerHandler(WebSocketEventListener webSocketEventListener) {
        this.webSocketEventListener = webSocketEventListener;
    }

    // 웹 소켓 서비스의 uri
    private static final String WEBSOCKET_PATH = "/websocket";

    // ChannelGroup은 라이브 채널을 나타냅니다.
    private static Map<Integer, ChannelGroup> channelGroupMap = new ConcurrentHashMap<>();

    // 이 요청에 대한 코드
    private static final String HTTP_REQUEST_STRING = "request";

    private Client client;

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
            System.out.println("FullHttpRequest: " + msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            String i = new JSONObject(msg).toString();
            System.out.println("WebSocketFrame: " + i);
        }
    }
   /* @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
            System.out.println("FullHttpRequest: " + msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            String i = new JSONObject(msg).toString();
            System.out.println("WebSocketFrame: " + i);
        }
    }*/

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // 잘못된 요청을 처리합니다.
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // GET 메소드만 허용합니다.
        if (req.method() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        if ("/favicon.ico".equals(req.uri()) || ("/".equals(req.uri()))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
        Map<String, List<String>> parameters = queryStringDecoder.parameters();

        if (parameters.size() == 0 || !parameters.containsKey(HTTP_REQUEST_STRING)) {
            System.err.printf(HTTP_REQUEST_STRING + "매개변수를 기본값으로 설정할 수 없음");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        client = RequestService.clientRegister(parameters.get(HTTP_REQUEST_STRING).get(0));
        System.out.println("clent: " + client);
        if (client.getRoomId() == 0) {
            System.err.printf("방 번호 를 기본값으로 설정할 수 없습니다");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        // 방 목록에 채널이 없으면 새 채널을 추가합니다. ChannelGroup
        if (!channelGroupMap.containsKey(client.getRoomId())) {
            channelGroupMap.put(client.getRoomId(), new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
        // 클라이언트를 채널에 추가하기 전에 방 번호가 있는지 확인하십시오.
        channelGroupMap.get(client.getRoomId()).add(ctx.channel());

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), req);

            // 핸드셰이크가 성공한 후 비즈니스 로직
            if (channelFuture.isSuccess()) {
                if (client.getId() == 0) {
                    System.out.println(ctx.channel() + " 방문자");
                    return;
                }
            }
        }
    }

    private void broadcast(ChannelHandlerContext ctx, WebSocketFrame frame, String sendMsg) throws Exception {

        if (client.getId() == 0) {
            Response response = new Response(1001, "로그인하지 않고는 채팅할 수 없습니다");
            String msg = new JSONObject(response).toString();
            System.out.println("broadcast: " + msg);

            ctx.channel().write(new TextWebSocketFrame(msg));
            return;
        }

        String request = ((TextWebSocketFrame) frame).text();
        //if
        // opcode
        System.out.println("수신 " + ctx.channel() + "," + request + ", "+client.getRoomId());

        /*Response response = MessageService.sendMessage(client, request);
        String msg = new JSONObject(response).toString();
        System.out.println("broadcast: "+msg);*/
        if (channelGroupMap.containsKey(client.getRoomId())) {
            channelGroupMap.get(client.getRoomId()).writeAndFlush(new TextWebSocketFrame(sendMsg));
        }

    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println("rrrequest:" + request);

        String sendMsg = webSocketEventListener.onEvent1(request, ctx);
        if (sendMsg != null && sendMsg.length() > 4) {
            String strBraoadHeader = sendMsg.substring(0, 5);
            System.out.println(strBraoadHeader);
            if (strBraoadHeader.equals("@all:")) {
                System.out.println("broadcast");
                bBroadcast = true;
                sendMsg = sendMsg.substring(5);
            } else {
                bBroadcast = false;
            }
            System.out.println(sendMsg);
        }
        if (bBroadcast)
            broadcast(ctx, frame, sendMsg);
        else
            ctx.channel().write(new TextWebSocketFrame(sendMsg)); // 버퍼에 쓰기*/

    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) throws Exception {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            /*HttpHeaderUtil.setContentLength(res, res.content().readableBytes());*/
            System.out.println("sendHttpResponse: " + res.content());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        /*!HttpHeaderUtil.isKeepAlive(req) ||*/
        if ( res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("수신 " + incoming.remoteAddress() + " 핸드셰이크 요청");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (client != null && channelGroupMap.containsKey(client.getRoomId())) {
            channelGroupMap.get(client.getRoomId()).remove(ctx.channel());
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) throws Exception {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }


}
