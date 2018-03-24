
[TOC]

### 自定义Task
1.添加描述和分组
2.自定义Type,实现task的重用

#### Task的依赖
1.一般的依赖
```
task hello {
    doLast {
        println 'Hello world!'
    }
}
task intro(dependsOn: hello) {
    doLast {
        println "I'm Gradle"
    }
}
```
intro依赖hello,hello会先运行,被依赖的Task可以在依赖的Task后定义,但是这时并不能通过Task名称调用(task名称.属性 eg:hello.name),

2.如果被依赖的Task在其他子项目中,通过:项目名称:task名称 eg:
```
project('projectA') {
    //这里依赖taskY
    task taskX(dependsOn: ':projectB:taskY') {
        doLast {
            println 'taskX'
        }
    }
}

project('projectB') {
    task taskY {
        doLast {
            println 'taskY'
        }
    }
}

```

3.通过对象进行依赖
```
task taskX{}
task taskY{}
taskX.dependsOn taskY //通过对象依赖
//通过闭抱,闭抱返回task或者task集合
taskX.dependsOn {
　　　//查找所有以lib开头的task
    tasks.findAll { task -> task.name.startsWith('lib') }
}


```


#### 动态Task
使用groovy动态创建Task,一旦任务被创建,则可以通过API进行操作,eg:动态添加依赖．

```
//动态创建了task0,task1,task2,task3四个Task
4.times { counter ->
    task "task$counter" {
        doLast {
            println "I'm task number $counter"
        }
    }
}
task0.dependsOn task2, task3

```

eg:动态添加Action,并不是覆盖原来的Action,而是在后续添加,执行的顺序就是添加的顺序
```
task hello {
    doLast {
        println 'Hello Earth'
    }
}
hello.doFirst {
    println 'Hello Venus'
}
hello.doLast {
    println 'Hello Mars'
}
hello {
    doLast {
        println 'Hello Jupiter'
    }
}
```

#### 添加Task属性
使用ext关键字添加属性，eg:
```
task myTask {
    ext.myProperty = "myValue"
}

task printTaskProperties {
    doLast {
        println myTask.myProperty
    }
}
```

#### 默认Task
没有指定任何Task时执行的task,称为默认Task,eg:
```
//定义默认的task
defaultTasks 'clean', 'run'

task clean {
    doLast {
        println 'Default Cleaning!'
    }
}

task run {
    doLast {
        println 'Default Running!'
    }
}

task other {
    doLast {
        println "I'm not a default task!"
    }
}

```
在多项目构建中,每个子项目都可以有各自的默认Task,如果没有,则会调用父项目的默认Task(如果有的话).


#### 给Task添加钩子
- [ ]适当说明

eg:
```
task distribution {
    doLast {
        println "We build the zip with version=$version"
    }
}

task release(dependsOn: 'distribution') {
    doLast {
        println 'We release now'
    }
}

gradle.taskGraph.whenReady {taskGraph ->
    if (taskGraph.hasTask(release)) {
        version = '1.0'
    } else {
        version = '1.0-SNAPSHOT'
    }
}
```
动态修改了version这个属性



#### 定位Task
在build.gradle文件中可以通过
- task名称直接访问
- 通过tasks.task名称 eg:tasks.hello.name
- 通过tasks [' task名称 '] 访问 eg: tasks['hello'].name
- 通过getByPath()方法 eg:

```
project(':projectA') {
    task hello
}

task hello

println tasks.getByPath('hello').path //结果  :hello
println tasks.getByPath(':hello').path //结果  :hello
println tasks.getByPath('projectA:hello').path//结果 :projectA:hello
println tasks.getByPath(':projectA:hello').path//结果  :projectA:hello
```

#### Task的配置
三种配置的方式:
方式一:

```
Copy myCopy = task(myCopy, type: Copy)
myCopy.from 'resources'
myCopy.into 'target'
myCopy.include('**/*.txt', '**/*.xml', '**/*.properties')

```

方式二:

```
task myCopy(type: Copy)
//这里说是闭包，搞不懂 tasks.getByName()
myCopy {
   from 'resources'
   into 'target'
   include('**/*.txt', '**/*.xml', '**/*.properties')
}

```

方式三:在定义Task的时候进行配置

```
task copy(type: Copy) {
   from 'resources'
   into 'target'
   include('**/*.txt', '**/*.xml', '**/*.properties')
}
```

#### 替换Task
重写或者替换插件中的Task eg:
```
task copy(type: Copy)

//这里设置overwirte: true即可实现task的替换
task copy(overwrite: true) {
    doLast {
        println('I am the new one.')
    }
}

```


#### 跳过Task的执行
1. 使用onlyIf()方法  eg ``` hello.onlyIf { !project.hasProperty('skipHello') } ```
2. 通过抛出``` StopExecutionException``` 这个异常,某个task抛出这个异常,则会跳过这个Task,执行下一个task
3. 通过设置Task的enalbed属性 eg: ``` hello.enabled=false ```

#### incremental build
增量编译，更多详情参考[Up-to-date checks](https://docs.gradle.org/4.4/userguide/more_about_tasks.html#sec:up_to_date_checks)


























### 自定义Gradle 插件

#### 1.引用插件的方式
- 




1.基础的插件实现
2.为插件添加一个ID




#### Wrapper
gradle包的缓存位置:```$USER_HOME/.gradle/wrapper/dists```
运行wrapper Task的gradle版本会默认成为wrapper中的gradle版本.
添加wrapper之后,直接使用gradlew命令代替gradle即可,命令使用是一样的

#### 后台进程
gradle Daemon 后台进程,用于提高build的效率,默认是开启的,
- 1.如何关闭这个Daemon
- 2.为什么会出现多个daemon 一个是没有空闲的进程，另外就是没有合适的进程 
>The basic rule is that Gradle will start a new Daemon if there are no existing idle or compatible Daemons available.

具体参考[gradle_daemon](https://docs.gradle.org/4.4/userguide/gradle_daemon.html#sec:disabling_the_daemon)


#### 多项目构建
- 使用```gradle projects``` 命令获取关于项目的具体构成
- 多项目构建项目组成:主目录中有build.gradle和settings.gradle,子项目中有或者没有build.gradle(这个文件可以修改名称,可在settings.gradle中修改)
- 执行子项目中的Task,一是可以切换到具体的子项目中执行,二是使用:,eg:```gradle :services:webservice:tasks```


### Build的生命周期
分为三个阶段:

1. 初始化 在这个阶段,为每个参与构建的项目创建Project实例.
2. 配置 在这个阶段,Project实例进行配置
3. 执行　执行被gradle命令指定的task子集


#### Settings file
在多项目构建中需要这个settings.gradle文件,是在初始化这个阶段执行的．
在一个没有settings.gradle文件的文件夹中,查找这个文件的步骤:

1. 在同级目录查找master目录下是否有,
2. 如果没有,就查找父级目录
3. 如果还没有,就当作单项目进行构建
4. 如果找到,gradle会检查当前项目是否在这个settings.gradle文件中定义的一个子项目,如果不是,就进行单项目构建，否则就是多项目构建.

#### 生命周期钩子
##### Project evaluation(项目评估？)
在项目评估后,使用Project.afterEvaluate()方法为全局有hasTests为true的项目添加一个task,这种用于定制日志或者简介
```
allprojects {
    afterEvaluate { project ->
        if (project.hasTests) {
            println "Adding test task to $project"
            project.task('test') {
                doLast {
                    println "Running tests for $project"
                }
            }
        }
    }
}

```

##### Task creation
用于在添加Task,设置默认值或者添加一些行为.使用whenTaskAdded方法,eg:
```
tasks.whenTaskAdded { task ->
    task.ext.srcDir = 'src/main/java' //这里为每一个新增的Task添加了一个属性,并设置默认值
}

task a

println "source dir is $a.srcDir"
```

##### Task execution
在Task执行前或者执行后，eg:在Task开始和结束时打印语句
```
task ok

task broken(dependsOn: ok) {
    doLast {
        throw new RuntimeException('broken')
    }
}
//在Task前打印
gradle.taskGraph.beforeTask { Task task ->
    println "executing $task ..."
}
//在Task执行后,根据执行状态进行打印
gradle.taskGraph.afterTask { Task task, TaskState state ->
    if (state.failure) {
        println "FAILED"
    }
    else {
        println "done"
    }
}

```
也可以使用[TaskExecutionGraph](https://docs.gradle.org/4.4/javadoc/org/gradle/api/execution/TaskExecutionGraph.html)来添加[TaskExecutionListener](https://docs.gradle.org/4.4/javadoc/org/gradle/api/execution/TaskExecutionListener.html)接口


#### Project对象
属性:

| 名称 | 类型 |默认值 |
|--------|--------|--------|
|  project|Project     | Project对象       |
|  name      | String       |  项目文件夹名称      |
|   path     |  String      | 项目文件夹的绝对路径       |
|   description     | String       |   项目描述     |
|   projectDir     |  File      |   包含build文件的目录对象     |
|   buildDir     |  File      |    projectDir/build     |
|   group     |  Object      |   没有指定 为 unspecified     |
|   version     |  Object      |   没有指定 为 unspecified     |

#### 变量声明
有两种变量:1是本地变量　2是扩展变量(extra properties)
##### 本地变量
用def声明的变量就是本地变量,变量的有效范围为声明的文件之内.eg:
```
def dest = "dest"

task copy(type: Copy) {
    from "source"
    into dest
}
```



##### Extra properties
在任意的project,task中都可以使用ext添加和查看，编辑属性,更多参考[ExtraPropertiesExtension](https://docs.gradle.org/4.4/dsl/org.gradle.api.plugins.ExtraPropertiesExtension.html)
eg:
```
apply plugin: "java"

//通过这种方式添加多个属性.
ext {
    springVersion = "3.1.0.RELEASE"
    emailNotification = "build@master.org"
}

sourceSets.all { ext.purpose = null }

sourceSets {
    main {
        purpose = "production"
    }
    test {
        purpose = "test"
    }
    plugin {
        purpose = "production"
    }
}

task printProperties {
    doLast {
        println springVersion
        println emailNotification
        sourceSets.matching { it.purpose == "production" }.each { println it.name }
    }
}
```



### 文件相关

#### 1.File生成的方法.
使用Project.file()方法,eg:
```
// Using a relative path
File configFile = file('src/config.xml')

// Using an absolute path
configFile = file(configFile.absolutePath)

// Using a File object with a relative path
configFile = file(new File('src/config.xml'))

// Using a java.nio.file.Path object with a relative path
configFile = file(Paths.get('src', 'config.xml'))

// Using an absolute java.nio.file.Path object
configFile = file(Paths.get(System.getProperty('user.home')).resolve('global-config.xml'))

```


#### 2.File collections
FileCollection 文件集合类，获取这个对象的方法:Project.files(Object[])
提供的方法,具体参考[FileCollection](https://docs.gradle.org/4.4/javadoc/org/gradle/api/file/FileCollection.html) eg:
```
// 迭代所有文件
collection.each { File file ->
    println file.name
}

// 转换成各种类型
Set set = collection.files
Set set2 = collection as Set
List list = collection as List
String path = collection.asPath
File file = collection.singleFile
File file2 = collection as File

// 直接通过加号和减号操作集合
def union = collection + files('src/file3.txt')
def different = collection - files('src/file3.txt')
```

#### 3.FileTree
一个FileTree代表着一个文件夹以及文件夹内所有文件或者一个压缩文件.

- 生成方法 eg:

```
//创建一个文件夹的FileTree
FileTree tree = fileTree(dir: 'src/main')

//添加include和exclude的规则
tree.include '**/*.java'
tree.exclude '**/Abstract*'

//通过路径创建一个FileTree
tree = fileTree('src').include('**/*.java')

//使用闭包的方式创建
tree = fileTree('src') {
    include '**/*.java'
}

//使用map方式创建
tree = fileTree(dir: 'src', include: '**/*.java')
tree = fileTree(dir: 'src', includes: ['**/*.java', '**/*.xml'])
tree = fileTree(dir: 'src', include: '**/*.java', exclude: '**/*test*/**')

//通过一个zip压缩文件创建
FileTree zip = zipTree('someFile.zip')

//通过一个tar压缩文件创建
FileTree tar = tarTree('someFile.tar')

//tarTree会根据文件后缀名猜测对应的压缩方式,自定义压缩方式的方法
//however if you must specify the compression explicitly you can:
FileTree someTar = tarTree(resources.gzip('someTar.ext'))



```




- 拥有的方法:如过滤,迭代,添加，访问等.具体参考[FileTree](https://docs.gradle.org/4.4/javadoc/org/gradle/api/file/FileTree.html)

```
// Iterate over the contents of a tree
tree.each {File file ->
    println file
}

// Filter a tree
FileTree filtered = tree.matching {
    include 'org/gradle/api/**'
}

// Add trees together
FileTree sum = tree + fileTree(dir: 'src/test')

// Visit the elements of the tree
tree.visit {element ->
    println "$element.relativePath => $element.file"
}
```


#### 4.文件拷贝
- 使用Copy类型的Task来实现拷贝,具体参考[CopySpec](https://docs.gradle.org/4.4/javadoc/org/gradle/api/file/CopySpec.html),eg:

```
task copyTask(type: Copy) {
    from 'src/main/webapp' //源目录,目录下所有但不包括目录本身
    into 'build/explodedWar' //目标目录
}

//可以指定多个from
task anotherCopyTask(type: Copy) {
    from 'src/main/webapp'
    from 'src/staging/index.html'
    //把copyTask的输出添加到源
    from copyTask
    // Copy the output of a task using Task outputs explicitly.
    from copyTaskWithPatterns.outputs
    // Copy the contents of a Zip file
    from zipTree('src/main/assets.zip')
    // Determine the destination directory later
    into { getDestDir() }
}

//使用include或者exclude规则,或者闭包

task copyTaskWithPatterns(type: Copy) {
    from 'src/main/webapp'
    into 'build/explodedWar'
    include '**/*.html'
    include '**/*.jsp'
    exclude { details -> details.file.name.endsWith('.html') &&
                         details.file.text.contains('staging') }
}

```

- 使用Project.copy()方法来实现拷贝，eg:

```
//在Task中使用copy方法,你必须明确指定from和into的值
task copyMethod {
    doLast {
        copy {
            from 'src/main/webapp'
            into 'build/explodedWar'
            include '**/*.html'
            include '**/*.jsp'
        }
    }
}
```

##### 4.1文件重命名
对拷贝的文件进行重命名,eg:

```
task rename(type: Copy) {
    from 'src/main/webapp'
    into 'build/explodedWar'
    // Use a closure to map the file name
    rename { String fileName ->
        fileName.replace('-staging-', '')
    }
    // Use a regular expression to map the file name
    rename '(.+)-staging-(.+)', '$1$2'
    rename(/(.+)-staging-(.+)/, '$1$2')
}
```

##### 4.2文件的过滤

```
import org.apache.tools.ant.filters.FixCrLfFilter
import org.apache.tools.ant.filters.ReplaceTokens

task filter(type: Copy) {
    from 'src/main/webapp'
    into 'build/explodedWar'
    // Substitute property tokens in files
    expand(copyright: '2009', version: '2.3.1')
    expand(project.properties)
    // Use some of the filters provided by Ant
    filter(FixCrLfFilter)
    filter(ReplaceTokens, tokens: [copyright: '2009', version: '2.3.1'])
    // Use a closure to filter each line
    filter { String line ->
        "[$line]"
    }
    // Use a closure to remove lines
    filter { String line ->
        line.startsWith('-') ? null : line
    }
    filteringCharset = 'UTF-8'
}
```

#### 5.Sync Task
Sync这个Task继承自Copy,运行这Task会把from的文件拷贝到into,然后删除from中没有拷贝的文件．可用于安装应用.eg:

```
task libs(type: Sync) {
    from configurations.runtime
    into "$buildDir/libs"
}
```

#### 6.生成压缩文件
提供了Zip,Tar,Jar,War,Ear等类型的Task用于生成压缩文件,由于同样实现了CopySpec,所以使用方法跟copy一样，eg:

```
apply plugin: 'java'

task zip(type: Zip) {
    from 'src/dist'
    into('libs') {
        from configurations.runtime
    }
}
```
#####  6.1生成的压缩文件的默认命名规则
```projectName-version.type```,可以通过配置多个属性进行自定义,具体参考[]()


##### 6.2 可重写的压缩包
为了让压缩包无论在任何时候，任何环境生成，都保持一致,需要将Zip.isReproducibleFileOrder()设置为true和将Zip.isPreserveFileTimestamps()设置为false,配置方式:

```
tasks.withType(AbstractArchiveTask) {
    preserveFileTimestamps = false
    reproducibleFileOrder = true
}
```


#### 7.Properties files
使用WriteProperties这个Task来创建properties文件



### 日志相关
#### 1.选择日志打印的level
有六种level,如下:
| levele | 用途 |
|--------|--------|
|   ERROR     | 错误信息       |
|   QUIET     | 重要信息       |
|   WARNING     | 警告信息       |
|   LIFECYCLE     | 构建进程信息(默认)      |
|   INFO     | information       |
|   DEBUG     | 调试信息       |

选择日志level的方式：

- 通过命令选项 -q/-w/-i/-d 分别是quiet/warn/info/debug
- 通过配置文件　具体参考[配置文件](https://docs.gradle.org/4.4/userguide/build_environment.html#sec:gradle_configuration_properties)

#### 2.打印自己的日志.

- 使用标准输出打印 println方法
- 使用logger属性来打印,eg:

```
logger.quiet('An info log message which is always logged.')
logger.error('An error log message.')
logger.warn('A warning log message.')
logger.lifecycle('A lifecycle info log message.')
logger.info('An info log message.')
logger.debug('A debug log message.')
logger.trace('A trace log message.')

```




































具体可以参考[Project](https://docs.gradle.org/4.4/dsl/org.gradle.api.Project.html)



































- [gradle官方文档](https://docs.gradle.org/4.4/userguide/userguide.html)
- [安卓gradle的配置文档](https://google.github.io/android-gradle-dsl/current/)
- [com.android.application插件源码的下载地址](https://android.googlesource.com/platform/tools/build/+/android-7.0.0_r35)







