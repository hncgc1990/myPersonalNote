#### 
App启动的时候也会创建自己的ClassLoader实例,PathClassLoader,而它的父加载器BootClassLoader,则是由系统启动的时候创建的

#### 类加载器的双亲代理模型
如果一个类被父的ClassLoader加载过,那么在子的ClassLoader就不会再进行加载.

- DexClassLoader可以加载jar/apk/dex,可以从SD卡中加载未安装的apk
- PathClassLoader只能加载系统中已经安装过的apk

