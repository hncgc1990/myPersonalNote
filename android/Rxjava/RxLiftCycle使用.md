#### gradle配置
```java
compile 'com.trello.rxlifecycle2:rxlifecycle:2.0.1'

// If you want to bind to Android-specific lifecycles
compile 'com.trello.rxlifecycle2:rxlifecycle-android:2.0.1'

// If you want pre-written Activities and Fragments you can subclass as providers
compile 'com.trello.rxlifecycle2:rxlifecycle-components:2.0.1'
```


#### 基本使用
1.决定在哪个生命周期终止数据的发射
```java
myObservable
    .compose(this.bindUntilEvent(lifecycle, ActivityEvent.DESTROY))
    .subscribe();
```

2.让自身决定在哪个生命周期结束,eg:在onResume绑定,则RxLifeCycle会在onPause终止数据发射.
```java
myObservable
    .compose(this.bindUntilEvent())
    .subscribe();
```





### 注意:
RxLifecycle并没有取消订阅,仅仅是终止了流.不同的数据流调用不一样的结果
- Observable,Flowable跟Maybe 直接调用onCompleted()
- Single和Completable则调用onError(CancellationException)

从这里看出,并不适合使用RxLifeCycle来取消Retrofit的请求.