启动需要配置url,端口号,解码器,编码器,接收信息处理的Handler,TCP_NODELAY等


几个类:

- ```ChannelOutboundHandlerAdapter ``` 处理发送的信息,子类:StringDecoder
-  ```ChannelInboundHandlerAdapter ``` 处理收到的信息,子类:StringEncoder
-  ```SimpleChannelInboundHandler ``` 过滤指定类型的Handler
-  ```ChannelHandler``` 处理发送和收到信息的基类
-  ```ChannelPipeline``` 包含多个```ChannelHandler``` 的容器，在这些```ChannelHandler``` 中,前一个```ChannelHandler``` 中需要调用```ChannelHandlerContext ```中的fire方法来调用下一个```ChannelHandler``` 的方法,例如调用```fireChannelRead```方法,后一个```ChannelHandler```的```ChannelRead```方法才会调用.(责任链模式)



相关的参数:
- TCP_NODELAY
  TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，如果需要发送一些较小的报文，则需要禁用该算法。Netty默认禁用该算法，从而最小化报文传输延时。
- SO_KEEPALIVE
  Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
SO_REUSEADDR





Bootstrap 

```
 // Configure the client.  
        EventLoopGroup group = new NioEventLoopGroup();  
        try {  
            Bootstrap b = new Bootstrap();  
            b.group(group)  
             .channel(NioSocketChannel.class)//配置底层为NIO
             .option(ChannelOption.TCP_NODELAY, true) //不延迟，直接发送
           　 .option(ChannelOption.SO_KEEPALIVE, true) // 保持长连接状态
             .handler(new ChannelInitializer<SocketChannel>() {  
                 @Override  
                 public void initChannel(SocketChannel ch) throws Exception {  
                     ChannelPipeline p = ch.pipeline();  
                     p.addLast("decoder", new StringDecoder());//配置解码器
                     p.addLast("encoder", new StringEncoder());//配置编码器
                     p.addLast(new HelloWorldClientHandler()); 
                 }  
             });  
			//连接到客户端  
            ChannelFuture future = b.connect(HOST, PORT).sync();//指定连接的ip和端口号
          //发送信息到客户端
            future.channel().writeAndFlush("Hello Netty Server ,I am a common client");  
            future.channel().closeFuture().sync();//断开连接
        } finally {  
            group.shutdownGracefully();  
        }  

```
添加收到信息处理的Handler代码
```
public class HelloWorldClientHandler extends ChannelInboundHandlerAdapter{  
      
    
      @Override  
      public void channelActive(ChannelHandlerContext ctx) {  
          System.out.println("HelloWorldClientHandler Active");  
      }  
    
      @Override  
      public void channelRead(ChannelHandlerContext ctx, Object msg) {  
         System.out.println("HelloWorldClientHandler read Message:"+msg);  
      }  
    
    
     @Override  
     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {  
         cause.printStackTrace();  
         ctx.close();  
      }  
  
}
```

#### ByteBuf
ByteBuf在NIO中的ByteBuffer的基础上进行封装,维护了两个指针,一个是读指针(readIndex),一个是写指针(writeIndex),读的时候,可读的区域是下标区间[readIndex,writeIndex],可写区间[writeIndex,capacity-1],[0,readIndex]区间是不能读,也不可以写.
**0<=readIndex<=writeIndex<=capacity**


clear方法将所有的数据清空,且将readIndex和writeIndex归0.


#### TCP粘包和拆包场景和解决方案
- 粘包 当客户端发送的消息比较少的时候,TCP会等多次发送之后才发送
- 拆包 当客户端发送的消息比较多的时候,TCP会拆分成多次发送.

解决方案:
- ```LineBasedFrameDecoder``` 使用\n或者\r\n来标记行结束 初始化会设置每行最大字节数(max),但是如果发送的每行字节数超过这个max就会报异常(```TooLongFrameException```)
- [ ] 查看源码,了解原理

- ```FixedLengthFrameDecoder```初始化设置定长的字节数,定长字节切分一次


#### LengthFieldBasedFrameDecoder
构造参数:
- maxFrameLength:解码帧的最大长度
- lengthFieldOffset:正文数据长度值这个属性的起始位(偏移位)
- lengthFieldLength:正文数据长度值所用的数据类型的长度(eg:int类型就是4)
- lengthAdjustmen:



#### 心跳的实现
利用```IdleStateHandler```,传入读,写,读和写的超时时间,每次读或者写的时候,记录当前读，写的时间,在channelActive的时候,定时(分别是传入的三个时间)三个检查时间的任务,以写为例，如果检查的时候,距离上一次写的操作的时间大于设置的超时时间,那么就会触发```UserEventTriggered```方法,在这个方法中进行心跳包的发送.一般关注的是ALL_IDLE(读或者写时间超时)



#### 重连
- 连接失败进行重连
  在连接的方法中添加回调```ChannelFutureListener```,如果连接失败就进行重连
- 心跳包发送失败进行重连
- 安卓中网络发生变化的时候也要进行重连







参考:

- [TCP粘包，拆包及解决方法](https://blog.insanecoder.top/tcp-packet-splice-and-split-issue/) 发生丢包的可能是因为太多频繁发送数据,导致发送的数据超出发送缓冲区,然后数据根本没有发送出去.
- [解读Netty之接收缓冲区](http://blog.csdn.net/zs_scofield/article/details/56486629)
Netty默认使用动态扩容的缓冲区
- [Netty：option和childOption参数设置说明](https://www.jianshu.com/p/0bff7c020af2)
- [netty实战之ChannelOption配置](http://blog.csdn.net/linsongbin1/article/details/77685242) 一个客户端的经过校验的配置,只需要配置nodelay和keepalive
- [ Netty 之 ChannelOption的TCP_NODELAY属性设置](http://blog.csdn.net/z69183787/article/details/52636658)
- [Netty Client重连实现](http://ju.outofmemory.cn/entry/198494) 关于重连的两个场景
- [一起学Netty](http://blog.csdn.net/column/details/enjoynetty.html)















