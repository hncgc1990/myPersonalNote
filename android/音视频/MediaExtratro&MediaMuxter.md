#### MediaExtractor
用于视频的解封装(API16添加)

方法:
- setDataSource() 设置视频源,可以是本地或者网络
- getTrackCount() 获取源文件的通道数
- getTrackFormat(index) 获取指定索引的通道信息
- getSampleTime() 返回当前的时间戳
- readSampleData(byteBuf,offset) 读取一帧数据到byteBuf中
- advance() 读取下一帧数据
- release() 释放资源

#### MediaMuxer
用于视频的封装(API18添加)

- MediaMuxer(String path,int format) path输出文件的路径,format输出文件的格式,只支持MP4格式
- addTrack(MediaFormat format) 添加通道
- start() 开始
- writeSampleData(int trackindex,ByteBuffer byteBuf,MediaCodec.BufferInfo info) 将数据添加到指定的通道中
- stop 停止
- release() 释放


参考:[Android中如何提取和生成mp4文件](http://blog.51cto.com/ticktick/1710743)

### TODO
- [ ]如何使用MediaMuxer添加
- [ ]如何添加wav文件中的音频到MP4文件