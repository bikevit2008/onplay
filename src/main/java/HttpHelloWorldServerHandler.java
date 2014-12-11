/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import com.sun.net.httpserver.Headers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String CONTENT = "Hello world!" ;


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {

        StringBuilder buf = new StringBuilder()
        .append("<!DOCTYPE html>\r\n")
                .append("<html><link rel=stylesheet href=http://getbootstrap.com/dist/css/bootstrap.min.css /><head><title>onPlay")
                .append("</title></head><body>\r\n")
                .append("<div class=\"jumbotron\">\n" +
                        "      <div class=\"container\">\n" +
                        "        <h1>Hello, world!</h1>\n" +
                        "        <p>This is a template for a simple marketing or informational website. It includes a large callout called a jumbotron and three supporting pieces of content. Use it as a starting point to create something more unique.</p>\n" +
                        "        <p><a class=\"btn btn-primary btn-lg\" href=\"#\" role=\"button\">Learn more »</a></p>\n" +
                        "      </div>\n" +
                        "    </div>");




        buf.append("</body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            response.content().writeBytes(buffer);


                ctx.write(response);
            sendHttpResponse(ctx, req, response);

    }
    protected static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse response) {
        setContentLength(response);

        boolean keepAlive = isKeepAlive(req);

        if (!keepAlive) {
            ctx.channel().write(response).addListener(CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.channel().write(response);
        }
    }
    private static void setContentLength(FullHttpResponse response) {
        HttpHeaders.setContentLength(response, response.content().readableBytes());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
