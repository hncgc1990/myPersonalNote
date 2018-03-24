- [ ]基本的配置问题
- [ ]基本的使用问题
- [ ]弹窗的封装问题
- [ ]公用数据结构的封装问题
- [ ]请求的取消


#### 1.基本的配置问题
gradle文件的依赖配置:
```java
    compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
    compile 'io.reactivex.rxjava2:rxjava:2.0.1'
    compile 'com.jakewharton.rxbinding2:rxbinding:2.0.0'
    compile 'com.jakewharton:butterknife:8.5.1'
    compile 'com.squareup.retrofit2:retrofit:2.2.0'
    compile 'com.squareup.retrofit2:converter-gson:2.2.0'
    compile 'com.squareup.retrofit2:adapter-rxjava2:2.2.0'
    compile 'com.squareup.okhttp3:logging-interceptor:3.6.0'
```
Retrofit的基本配置:
```java
 HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
 logging.setLevel(HttpLoggingInterceptor.Level.BODY);//设置查看日志的等级
 OkHttpClient client=new OkHttpClient.Builder().addInterceptor(logging ).build();

 retrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl("http://gank.io/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用rxjava2
                        .build();

```


#### 2.基本使用问题
先构建请求的接口,**这里的接口可以使用Single跟Completable来更加简洁[理解 RxJava 中的 Single 和 Completable](http://johnnyshieh.github.io/android/2017/03/15/rxjava-single-completable/)**
```java
public interface PostListInter {


    @GET("data/Android/13/1")
    public Observable<PostData<List<Result>>> getPostList();
}
```
然后调用Retrofit单例调用这个接口即可,更多请参考[官方文档](http://square.github.io/retrofit/)
```java
 Retrofit instance = RetrofitSingle.getInstance();
 PostListInter postListInter = instance.create(PostListInter.class);
 postListInter
     .getPostList()
     .subscribe();
```


#### 3.弹窗封装问题
利用compose操作符进行
```java
public class DialogHelper {

   private  ProgressDialog mDialog;

   private  Context mContext;

    public DialogHelper(Context context){
        this.mContext=context;
    }
    <T> ObservableTransformer<T,T> applyDialog(){
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {

               return  upstream.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        //在这里进行弹窗
                        Logger.d("doOnSubscribe");
                        mDialog=new ProgressDialog(mContext);
                        mDialog.show();
                    }
                }).doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //在这里进行取消弹窗
                        Logger.d("doOnComplete");
                        if(mDialog!=null &&mDialog.isShowing()){
                            mDialog.dismiss();
                        }
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //在这里进行取消弹窗
                        Logger.d("doOnError");
                        if(mDialog!=null &&mDialog.isShowing()){
                            mDialog.dismiss();
                        }
                    }
                });

            }
        };

    }
}

```


#### 4.公用数据结构的封装问题

```json
{
  "error": false, 
  "results": [
    {
      "_id": "58e4a70b421aa90d6bc75abe", 
      "createdAt": "2017-04-05T16:12:59.265Z", 
      "desc": "Android\u5c0f\u7968\u6548\u679c", 
      "images": [
        "http://img.gank.io/8be2fb0e-deec-4bf7-9a03-a50ec61aed2d"
      ], 
      "publishedAt": "2017-04-06T12:06:10.17Z", 
      "source": "web", 
      "type": "Android", 
      "url": "https://github.com/vivian8725118/CardView", 
      "used": true, 
      "who": "Vivian"
    }
  ]
}
```
此处的接口来源于[干货集中营](http://gank.io/api)


使用泛型即可
```java
public class PostData <T> {

    @SerializedName("error")
    private Boolean mError;
    @SerializedName("results")
    private  T mResults;

    public Boolean getError() {
        return mError;
    }

    public void setError(Boolean error) {
        mError = error;
    }

    public T getResults() {
        return mResults;
    }

    public void setResults(T results) {
        mResults = results;
    }
}
```



#### 5.请求的取消

在订阅者的onSubscribe取到Disposable对象,在适当的时机进行取消订阅即可.





参考文章:
[RxJava 与 Retrofit 结合的最佳实践](https://gank.io/post/56e80c2c677659311bed9841)
[【译】避免打断链式结构：使用.compose()操作符](http://www.jianshu.com/p/e9e03194199e)










































