
[TOC]

#### 代码修复
- 底层替换方案限制颇多,时效性好,**无法进行方法和字段的增减**
- 类加载方案时效性差,需要冷启动才能见效,但限制少.

##### 底层替换方案
AndFix的实现:
- 通过反射获取被替换方法和新方法的Method对象
- 通过evn->FromReflectedMethod获取到对应的ArtMethod的起始地址,
- 然后通过这个ArtMethod指针替换其所有的成员.

###### 1.存在的问题,由于ArtMethod结构体是写死的(使用官方的结构),如果Rom对这个结构进行修改,那么就没办法做到替换了.

Sophix的实现:
整个ArtMethod进行替换,重点就是获取ArtMethod方法的大小(根据虚拟机中分配类方法空间的代码,得到ArtMethod是按照线性排列的),

测量ArtMethod方法的大小的方法:
由于都是static方法且都存在一个类中,所以他们ArtMethod肯定是相邻的.
```
public class NativeStructsModel{
  final public static void f1(){}
  final public static void f2(){}
}
```
获取ArtMethod的方法:
```
size_t firMid=(size_t)env->GetStaticMethodId(nativeStructsModelClazz,"f1","{}V");
size_t secMid=(size_t)env->GetStaticMethodId(nativeStructsModelClazz,"f2","{}V");

size_t methSize=secMid-firMid;
```

###### 2.相同包名下的权限问题.
补丁中的类访问同包名下的类(原程序中)时,会报出访问权限异常:
```
Caused by:java.lang.IllegalAccessError:
Method 'void com.patch.demo.BaseBug.test()' is inaccessible to class 'com.patch.demo.MyClass'
```
原因是补丁中的类加载的Classloader跟原包中的类加载的Classloader不一致.解决方案:替换补丁的类加载器.
```
Field classLoaderField=Class.class.getDeclaredField("classLoader");
classLoaderField.setAccessible(true);
classLoaderField.set(newClass,oldClass.getClassLoader());

```

- [ ] 为啥这里两个类加载器不一致.
- [ ]　测试ArtMetho的size的获取.

###### 3.热替换之后的非静态方法,通过反射调用时会有问题.
在原程序中通过反射调用被热替换的非静态方法,会报异常,具体的代码调用:
```
//这个对象是原程序的对象
Basebug bb=new BaseBug();
//这个方法是热替换之后的方法,需要传入的对象是补丁包中的同名类对象
Method testMeth=BaseBug.class.getDeclaredMethod("test");
//最后传入的是原程序中的对象,所以报错.
testMeth.invoke(bb);

```
```
Caused by:java.lang.IllegalArgumentException:
```
因为被替换的方法，需要的是补丁中的那个类的对象,而传入的是原来的对象


###### 4. 存在的限制
1. 引起原有类发生结构变化的修改(属性或者方法的新增或者减少)
2. 修复了的非静态方法的反射调用.



#### java编译时引发的问题

##### 1.内部类编译
静态内部类不持有外部类的引用,非静态内部类持有外部类的引用.**内部类在编译期会被编译为跟外部类一样的顶级类.**,那么外部类访问内部类的私有方法,是通过编译期内给内部类的私有方法生成一个```access&**```的方法,同样,内部类访问外部类也一样.

问题:.修改外部类某个方法为访问内部类的某个**私有**方法,会让补丁包新增一个方法
解决方式:
- 如果一个外部类有内部类,外部类的所有method/field的private 改成protected或者默认访问权限或者public
- 同时将内部类的所有method/field的private 改成protected或者默认访问权限或者public

##### 2.匿名内部类编译
匿名内部类的名称格式一般是**外部类&number**,number就是外部类中出现的个数.
 
问题:新增/减少一个匿名内部类,同时规避了以上的情况,还是会新增/减少方法.
解决方式:
**无解**

##### 3.field编译
静态field,静态代码块实际上会被编译器编译在<clinit>方法中,**两者在clinit中出现的顺序跟在源码中一样**,而非静态field跟非静态代码块则会被编译器编译在<init>默认无参构造函数中,**两者在init中出现的顺序跟源码中顺序一样**
问题:静态field初始化和静态代码块的变更会翻译到clinit方法中.
- [ ]静态field/代码块跟非静态field/代码块有啥区别,不是都拿不到对应的方法的？

##### 4.final static field编译
静态的field会被编译器编译到<clinit>方法，但final static 修饰的基本类型/String常量类型则没有.final static 修饰的引用型常量仍然在clinit方法中初始化
问题:final static 修饰的引用型常量不可以替换,而final static 修饰的基本类型或者String类型则可以替换.

##### 5.混淆导致方法内联编译

方法的内联:
- 方法没有被其他任何地方引用,该方法会被内联掉.
- 方法足够简单,那么任何调用该方法的地方都会被该方法的实现替换掉.
- 方法只被一个地方引用到,那么这个地方会被方法的实现替换掉.

方法的裁剪:
方法的参数在方法内部没有使用到,那么这个参数就有可能被裁剪掉,

问题:**项目应用了混淆,可能会导致方法的内联和裁剪,最后导致方法的减少**
解决方式:关闭方法的内联和裁剪,在混淆文件中加上```-dontoptimize```


##### 6.泛型的编译
**重写的意思是子类中的方法跟父类的某一方法具有相同的方法名,返回类型和参数表**,但下面的泛型返回的类型就不一致,虚拟机是通过一个特殊的方法来完成这项重写功能，bridge方法,bridge方法来重写父类的方法,并在内部调用父类重写的方法.

```
class A<T>{
private T t;
//此处返回的是Object
public T get(){
	return t;
}

class B extends A<Number>{
 private Number n;
 
 @Override //此处跟父类返回值不一样,为什么允许重写父类方法
 public Number get(){
 	return n;
 }
}
}

```

问题:虚拟机为了解决泛型跟多态的冲突,新增的这个bridge方法，导致了方法的新增
解决方式:无解

- [ ] 就算新增了方法,本来就新增方法了啊？为什么不可以使用热部署？





#### 类加载方案
app重新启动之后让Classloader去加载新的类,


##### 美团的实现(Robust)
- 打基础包时插桩,在每一个方法前插入一段类型为ChangeQuickRedirect静态变量的逻辑
- 加载补丁时,从补丁包中读取要替换的类以及具体替换的方法实现,加载补丁dex,通过反射替换这个静态变量

插桩过程使用Transform API修改字节码文件





##### QQ空间的实现
在Dalvik下采用插桩的方式,阻止类打上预校验的标志,最后加载补丁dex到dexFile对象,构建一个Element对象插入到dexElements数组的最前面.

- 由于原dex中的类没有打上预校验的标志,所有的类校验和类优化都在加载类时进行,影响加载性能．


###### 插桩实现(Dalvik虚拟机)
加载dex文件到本地内存的时候,如果不存在odex文件,就会先执行dexopt,存在两个优化的过程:
- dvmVerifyClass: 类校验，为了防止类被篡改校验类的合法性.如果类的所有方法直接引用的类和当前类都在同一个dex的话,dvmVerifyClass就返回true
- dvmOptimizeClass:类优化,就是把部分指令优化成虚拟机内部指令,加快方法的执行效率.

插桩的实现:
将一个单独无关帮助类放到一个单独的dex中,原dex中所有类的构造函数都引用这个类.




##### Tinker的实现
提供dex差量包,整体替换dex的方式．通过将补丁dex跟应用的classes.dex合并成一个完整的dex，然后替换dexElements数组中旧的Element
- 由于涉及到补丁dex跟原dex的merge,容易OOM


##### 阿里的实现
- Art下,基于Art虚拟机默认支持加载压缩文件中包含多个dex,将补丁dex命名为classes.dex,原apk中的dex依次命名为classes2,3,4然后一起打包成一个压缩文件.然后解析这个压缩文件,得到DexFile对象,替换dexElements数据即可
- Davlik下,使用全量dex方案,在原dex包中去掉补丁包中包含的所有类.仅仅是让类加载找不到Class的定义










#### 资源修复
##### Instant Run的实现:
- 通过反射构造一个新的AssetManager,并调用addAssetPath,将完整的新资源包加入到AssetManager中
- 找到所有之前引用原有AssetManager的地方(包括所有的Activity中的),通过反射,将引用处替换为新的AssetManager.

新资源包或者是全量资源包或者差量资源包,在本地进行merge成全量.







##### sophix的实现:
- 直接加入一个只包含改变的资源项的资源包到已有的AssetManager,通过在原有的AssetManager对象上进行析构和重构.

##### 资源文件的格式
resources.arsc文件实际由一个个ResChunk拼接起来.每个chunk的头部由ResChunk_header和资源信息组成,header包含当前chunk的数据类型,header的大小,chunk的大小.

资源信息主要指的是资源的名称以及对应的编号,编号是一个32位数字,0xPPTTEEEE,PP是package id, TT是type id ,EEEE 是entry id.

- package id 
- type id 表示资源的类型 attr(1),drawable(2),mipmap(3),layout(4)对应的id为括号中的数字
- entry id 表示一个资源项,资源项是按照排列的先后顺序自动被标记编号的









#### so库修复

##### JNI方法的注册方式
- 静态注册
- 动态注册

##### 接口调用替换方案
使用SOPatchManager.loadLibrary接口加载so库的时候,优先尝试加载指定目录下的补丁so,
- 如果存在则加载补丁so库,而不会去加载安装apk安装目录下的so库
- 如果不存在补丁so,那么调用System.loadLibrary去加载安装apk目录下的so库.

缺点：不能修复三方库的so




##### Sophix的实现:
###### 如何正确选择对应的so文件
sdk>=21时,直接反射拿到ApplicationInfo对象的primaryCpuAbi
sdk<21时,直接把Build.CPU_ABI,Build.CPU_ABI2作为primaryCpuAbi即可


利用反射把补丁so库的路径插入到nativeLibraryDirectories数组的最前面.

具体的代码可以参考:[Tinker的实现](https://github.com/Tencent/tinker/blob/master/tinker-android/tinker-android-lib/src/main/java/com/tencent/tinker/lib/library/TinkerLoadLibrary.java)

缺点:需要进行版本的适配



































