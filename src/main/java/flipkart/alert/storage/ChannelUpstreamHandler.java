package flipkart.alert.storage;

import com.yammer.dropwizard.logging.Log;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 10/5/13
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChannelUpstreamHandler extends SimpleChannelHandler
{
    private static final Log logger = Log.forClass(ChannelUpstreamHandler.class);

    private static final AtomicLong connections_established = new AtomicLong();
    private static final AtomicLong exceptions_caught = new AtomicLong();

    private static final DefaultChannelGroup channels =  new DefaultChannelGroup("all-channels");

    static void closeAllConnections()
    {
        channels.close().awaitUninterruptibly();
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx,
                            final ChannelStateEvent e)
    {
        channels.add(e.getChannel());
        connections_established.incrementAndGet();
    }

    @Override
    public void handleUpstream(final ChannelHandlerContext ctx,
                               final ChannelEvent e) throws Exception {

        super.handleUpstream(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final ExceptionEvent e)
    {
        final Throwable cause = e.getCause();
        final Channel chan = ctx.getChannel();
        if (cause instanceof ClosedChannelException)
        {
            logger.info("Attempt to write to closed channel : " + chan.toString());
        }
        else if (cause instanceof IOException
                && "Connection reset by peer".equals(cause.getMessage()))
        {
            logger.info("Connection reset by peer for channel :" + chan.toString());
        }
        else
        {
            logger.info("Unexpected exception from downstream for " + chan.toString(), cause);
            e.getChannel().close();
        }
        exceptions_caught.incrementAndGet();
    }
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception
    {
        logger.info("Written amount : " + e.getWrittenAmount());
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        try{
            BigEndianHeapChannelBuffer buffer =(BigEndianHeapChannelBuffer) e.getMessage();
             int size=buffer.readableBytes();
        byte[] bytes=new byte[size];
        buffer.readBytes(bytes);
        buffer.clear();

        logger.info("Message written at: " + e.getRemoteAddress() +" Received message: " + new String(bytes));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }


}
