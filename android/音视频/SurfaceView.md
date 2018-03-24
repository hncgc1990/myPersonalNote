### Sufraceview GLSurfaceView TextureView

区别：
- SurfaceView不能进行平移,缩放等动画的变换,在一个单独的线程中绘制,不会影响主线程,使用双缓冲机制,播放更流畅
- TextureView 消耗内存更多,支持移动，旋转,缩放等动画，支持截图,必须在硬件加速的窗口中使用,5.0在主线程中渲染,而5.0后有单独的线程,会有1-3帧的延迟



### Surface SurfaceView SurfaceHolder

#### 1.Surface
Surface是原始图像缓冲区的一个句柄,实现了Parcelable接口

#### 2.SurfaceView
提供两个线程.UI线程和渲染线程,SurfaceView和SurfaceHolder.Callback的方法都应该在UI线程里调用

#### 3.SurfaceHolder 
用来控制Surface的控制器,处理Canvas上的效果和动画.

- lockCanvas(Rect) 获取Canvas对象,并锁定画布,只更新指定区域的画布
- unlockCanvasAndPost(canvase) 结束锁定


#### 4.SurfaceTexture
SurfaceView从图像流(Camera,视频解码,GL绘制场景)中获取帧数据,调用updateTextImage()时,更新SurfaceTexture中的GL纹理对象,可能会造成跳帧.


- getTransformMatrix方法　
返回当前纹理的矩阵,旋转屏幕的时候,会返回不一样的矩阵.可用于[旋转,缩放等操作](https://stackoverflow.com/questions/30595493/what-does-the-return-value-of-surfacetexture-gettransformmatrix-mean-who-can-ex)

旋转90度
```
Matrix.rotateM(mTmpMatrix, 0, 90, 0, 0, 1);
Matrix.translateM(mTmpMatrix, 0, 0, -1, 0);
```
[参考](https://stackoverflow.com/questions/33773770/use-rotatem-of-matrix-to-rotate-matrix-from-surfacetexture-but-corrupt-the-vid)





 [0.0, -1.0, 0.0, 0.0, 
 1.0, 0.0, 0.0, 0.0, 
 0.0, 0.0, 1.0, 0.0,
 0.0, 1.0, 0.0, 1.0]
 
 [-1.0, 0.0, 0.0, 0.0,
 0.0, -1.0, 0.0, 0.0,
 0.0, 0.0, 1.0, 0.0,
 1.0, 1.0, 0.0, 1.0]














参考：
- [视频画面帧的展示控件 SurfaceView 及 TextureView 对比](https://juejin.im/entry/58b4ccd944d904006a1ce446)
- [Android 5.0(Lollipop)中的SurfaceTexture，TextureView, SurfaceView和GLSurfaceView](http://blog.csdn.net/jinzhuojun/article/details/44062175)
- [Android图形系统之Surface、SurfaceView、SurfaceHolder及SurfaceHolder.Callback之间的联系](http://blog.csdn.net/conowen/article/details/7821409#t0)
- [什么是surfaceview？和surface有区别吗？SurfaceHolder和他们的关系是怎样?](https://www.zhihu.com/question/30922650)


