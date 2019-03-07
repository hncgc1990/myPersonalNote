
[TOC]
#### mysql安装
##### cenos7.5中安装mysql
```
wget http://repo.mysql.com/mysql-community-release-el7-5.noarch.rpm
rpm -ivh mysql-community-release-el7-5.noarch.rpm
yum update
yum install mysql-server
```

##### 权限设置
```
chown mysql:mysql -R /var/lib/mysql
```

##### 初始化MySQL
```
mysqld --initialize
```

##### 启动MySQL
```
systemctl start mysqld
```

##### 关闭MySQL
```

```

##### 查看MySQL的运行状态
```
systemctl status mysqld
```


#### 初始化
##### 设置root密码
Mysql安装成功后，默认的root用户密码为空，你可以使用以下命令来创建root用户的密码：
```
mysqladmin -u root password "new_password";
```

通过以下命令连接数据库：
```
mysql -u root -p

```

##### 新建用户




#### 解决问题
[因为防火墙的关系没办法连接上mysql](https://blog.csdn.net/u013257111/article/details/79063578)




参考：[安装](http://www.runoob.com/mysql/mysql-install.html)





















