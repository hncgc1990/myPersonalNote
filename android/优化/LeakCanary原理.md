TODO LeakCanary原理

[TOC]



## 使用

#### 1.导入库
```
dependencies {
  debugImplementation 'com.squareup.leakcanary:leakcanary-android:1.5.4'
  releaseImplementation 'com.squareup.leakcanary:leakcanary-android-no-op:1.5.4'
}
```



#### 2.监听Activity
```
public class ExampleApplication extends Application {

  public static RefWatcher getRefWatcher(Context context) {
    ExampleApplication application = (ExampleApplication) context.getApplicationContext();
    return application.refWatcher;
  }

  private RefWatcher refWatcher;

  @Override public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
  //默认监听所有的Activity在onDestroy之后是否泄漏
    refWatcher = LeakCanary.install(this);
  }
}

```

#### 3.监听Fragment
```
public abstract class BaseFragment extends Fragment {

  @Override public void onDestroy() {
    super.onDestroy();
    RefWatcher refWatcher = ExampleApplication.getRefWatcher(getActivity());
    refWatcher.watch(this);
  }
}
```

#### 4.自定义LeakCanary的行为

在release模式下，引用的```leakcanary-android-no-op```只包含LeakCanary和RefWatcher两个类,所以当你自定义LeakCanary的行为的时候,有






release和debug使用不同的清单文件的方式,参考[different manifest for debug build and release build](https://stackoverflow.com/questions/48885078/different-manifest-for-debug-build-and-release-build)




## 原理

### 1.简单过程描述

#### 1.1 监听
通过Application.registerActivityLifecycleCallbacks()方法注册Activity生命周期的监听,当Activity页面销毁时,获取这个Activity去检测是否被系统回收

#### 1.2 检测
通过WeakReference和ReferenceQueue来判断对象是否被系统GC回收,利用WeakReference引用的对象被回收时,虚拟机会把该对象添加到ReferenceQueue中的原理,在等待系统回收之后,从ReferenceQueue中尝试获取Activity,如果不为空,表示已经回收,为空,表示没有回收,手动触发GC,再次等待,然后重复以上的判断

#### 1.3 分析
如果没有被回收,首先使用 Debug.dumpHprofData生成堆信息的快照文件,然后使用haha开源库,对堆信息的快照进行分析,查找强引用关系.

### 2.各个过程细节








- [LeakCanary 内存泄露监测原理研究](https://www.jianshu.com/p/5ee6b471970e)
- [LeakCanary原理解析](http://blog.csdn.net/xiaohanluo/article/details/78196755)
- [官方文档](https://github.com/square/leakcanary/wiki/FAQ)