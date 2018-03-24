
**Lru(Least Recently Used)**


LruCache利用**LinkedHashMap**开启访问排序的数据结构(最少访问在最前,最多访问在最后),通过put图片时,put操作后,如果当前总大小超出定义的最大值,就将LinkedHashMap中最老的元素删除.来达到使用最少的先删除的目的.


LruCache源码解析:

相关的变量
```
//当前所有元素的大小总和
private int size;
//最大缓存大小
private int maxSize;

```


首先是初始化

```
 public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
      	//这里传入true,表示开启访问排序
        this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
    }
```

然后是添加

```
//返回被替换的相同key的Value
public final V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }

        V previous;
        synchronized (this) {
            putCount++;
          	//将添加的图片大小加到size
            size += safeSizeOf(key, value);
          	//把图片添加到LinkedHashMap
            previous = map.put(key, value);
            //如果当前key的value被替换,则删除原来的缓存大小
            if (previous != null) {
                size -= safeSizeOf(key, previous);
            }
        }

        if (previous != null) {
            entryRemoved(false, key, previous, value);
        }

        trimToSize(maxSize);
        return previous;
    }

```
```trimToSize(maxSize)``` 方法的作用就是让缓存大小保持小于设定的最大缓存大小
```
private void trimToSize(int maxSize) {
        while (true) {//无循环
            K key;
            V value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName()
                            + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize) {//没有触及临界点,退出循环
                    break;
                }

                // BEGIN LAYOUTLIB CHANGE
                // get the last item in the linked list.
                // This is not efficient, the goal here is to minimize the changes
                // compared to the platform version.
              	//获取最少访问的元素
                Map.Entry<K, V> toEvict = null;
                for (Map.Entry<K, V> entry : map.entrySet()) {
                    toEvict = entry;
                }
                // END LAYOUTLIB CHANGE

                if (toEvict == null) {
                    break;
                }
				//
                key = toEvict.getKey();
                value = toEvict.getValue();
              	//删除这个最少访问的元素
                map.remove(key);
              	//从size中减掉这个最少访问元素的大小
                size -= safeSizeOf(key, value);
                evictionCount++;
            }

            entryRemoved(true, key, value, null);
        }
    }

```



```sizeOf()``` 方法返回每个value的大小,使用时需要覆盖这个方法

```
private int safeSizeOf(K key, V value) {
        int result = sizeOf(key, value);
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + key + "=" + value);
        }
        return result;
    }
 protected int sizeOf(K key, V value) {
        return 1;
    }
```





参考:
[LruCache原理和用法与LinkedHashMap](http://blog.csdn.net/qq_25806863/article/details/77548468)
