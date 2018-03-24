
[TOC]


### 1.camear1
#### 1.1解本使用
- 摄像头的选择
- 参数的设置,尺寸，方向,数据格式
- 预览的方向和角度，默认是横屏，０度.
 ```Camera.setDisplayOrientation``` 设置的是相机的预览方向
 ```parameters.setRotation```　设置的是拍照的方向
 即使设置不同的方向，在最后获取的数据时，始终都是按横屏获取的数据

- 尺寸　```parameters.setPreviewSize```　设置的是预览的尺寸，``` parameters.setPictureSize```  设置的是拍照的尺寸,而相机支持的尺寸需要,可以通过``` parameters.getSupportedPreviewSizes```获取支持的所有尺寸
- 对焦 对焦成功之后再进行拍照

#### 1.2相机数据的回调
- setPreviewCallback方法，设置回调接口：PreviewCallback
- setPreviewCallbackWithBuffer方法，同样设置回调接口：PreviewCallback


两者的区别是:方式一是一帧数据准备好就马上回调onPreviewFrame,而方式二是由用户进行控制,在OnPreviewFrame方法中调用addCallbackBuffer　下一帧的数据才会被回调.并且在开始预览之前就需要调用addCallbackBuffer方法添加一个缓存的byte数组．





#### 1.3预览相机
- SurfaceView预览
- TextureView预览




###  2.camear2
camera2在5.0添加,但是在国内Rom支持的并不好
#### 2.1基本使用
- 创建 CameraManager 对象，相机操作始于“相机管家”：```context.getSystemService(Context.CAMERA_SERVICE)；```
- 创建 CameraDevice 对象：cameraManager.openCamera；和 Camera1 不同，Camera2 的操作都是异步的，调用 openCamera 时我们会传入一个回调，在其中接收相机操作状态的事件；
- 创建成功：CameraDevice.StateCallback#onOpened；
- 创建相机对象后，开启预览 session，设置数据回调：camera.createCaptureSession，同样，这个操作也会传入一个回调；
session 开启成功：```CameraCaptureSession.StateCallback#onConfigured；```
- 开启 session 后，设置数据格式（尺寸、帧率、对焦），发出数据请求：CaptureRequest.Builder 和 session.setRepeatingRequest；
- 停止预览：```cameraCaptureSession.stop 和 cameraDevice.close；```



#### 2.2预览
返回的数据是YUV420_888,最后的数据尺寸跟ImageReader初始化时的大小一致．






camera支持的yuv帧的格式
- NV21
- YV12


- [ ] 格式的选择
- [ ] 颜色模式的选择
- [ ] 为什么要先转换成rgb或者yuv420的格式?


一般需要将数据转化成ARGB格式或者是视频编码的YUV420格式。

异常：
```imageReader_JNI: Unable to acquire a lockedBuffer, very likely client tries to lock more than maxImages buffers ```

返回的Image 没有close掉


**兼容的采集方案**:[CameraCompat](https://github.com/Piasy/CameraCompat)

**camera的调用必须在同一线程中.**

坑：
- 低版本（5.0 以前）的系统上，Camera1 停止预览时，不要手贱地调用下列接口设置 null 值：setPreviewDisplay/setPreviewCallback/setPreviewTexture（文档中确实也说过不要调用…），否则可能导致系统服务全线崩溃，最终导致手机重启

参考:
- [Example of Camera preview using SurfaceTexture in Android](https://stackoverflow.com/questions/11539139/example-of-camera-preview-using-surfacetexture-in-android)　camera1预览的demo
- [摄像头Camera视频源数据采集解析](http://www.wjdiankong.cn/android%E4%B8%AD%E7%9B%B4%E6%92%AD%E8%A7%86%E9%A2%91%E6%8A%80%E6%9C%AF%E6%8E%A2%E7%A9%B6%E4%B9%8B-%E6%91%84%E5%83%8F%E5%A4%B4camera%E8%A7%86%E9%A2%91%E6%BA%90%E6%95%B0%E6%8D%AE%E9%87%87%E9%9B%86/)　camera1的相关总结
- [WebRTC-Android 源码导读（一）：相机采集实现分析](https://blog.piasy.com/2017/07/24/WebRTC-Android-Camera-Capture/#section-1)  大概流程以及遇到的坑
- [android camera2 拿到的yuv420数据到底是什么样的](http://blog.csdn.net/j12345678901/article/details/78110640) camera2中数据的转换用java方式
- [android-Camera2Basic](https://github.com/googlesamples/android-Camera2Basic)　camera2的基本使用
