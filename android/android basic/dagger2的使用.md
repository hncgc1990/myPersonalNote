


| 注解 | 描述 |
|--------|--------|
| @Inject       |    1.标记需要依赖的变量,2.标记构造函数,3.标记函数(在构造之后调用的函数[做一些初始化的工作])    |
| @Module      |  用来标注提供依赖的类(通常是第三方的类或者带参数的构造函数)      |
| @Provides       |   在Module标注的类中标注提供依赖的方法     |
| @Component      |   自动注入的入口.可指定依赖的Component和提供依赖的module     |
| @Qulifier      |   用于自定义注解,这个自定义的注解用来区分相同类型如Context这个类新在Application和Activity提供的就不样,需要区分     |
| @Scope       |    用于自定义注解,下面的Singleton就是一个实现,用于限定注解的作用域,实现局部的单例.    |
| @Singleton     |   就是Scope的一个实现，是否可以实现单例,取决于对应的Component是否唯一     |



可参考的文章:
[Dagger2 最清晰的使用教程](http://www.jianshu.com/p/24af4c102f62)
[神兵利器Dagger2](https://segmentfault.com/a/1190000008016507)

