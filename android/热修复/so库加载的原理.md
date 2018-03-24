Java提供两个接口来加载so库:
- System.loadLibrary(String libName) libs目录下
- System.load(String pathName) pathName表示磁盘中的完整路径
这两个方法都调用Runtime.doLoad方法