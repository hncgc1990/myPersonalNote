<!-- MarkdownTOC -->

- [mysql安装](#mysql安装)
    - [cenos7.5中安装mysql](#cenos75中安装mysql)
    - [权限设置](#权限设置)
    - [初始化MySQL](#初始化mysql)
    - [启动MySQL](#启动mysql)
    - [关闭MySQL](#关闭mysql)
    - [查看MySQL的运行状态](#查看mysql的运行状态)
- [初始化](#初始化)
    - [设置root密码](#设置root密码)
    - [新建用户并授权](#新建用户并授权)
- [登陆相关命令](#登陆相关命令)
- [数据库操作](#数据库操作)
- [数据表操作](#数据表操作)
    - [新建表](#新建表)
    - [创建后表的修改](#创建后表的修改)
        - [添加列](#添加列)
        - [修改列](#修改列)
        - [删除列](#删除列)
        - [重命名表](#重命名表)
    - [删除表](#删除表)
- [解决问题](#解决问题)
- [小技巧](#小技巧)

<!-- /MarkdownTOC -->

#### mysql安装
##### cenos7.5中安装mysql
```sql
wget http://repo.mysql.com/mysql-community-release-el7-5.noarch.rpm
rpm -ivh mysql-community-release-el7-5.noarch.rpm
yum update
yum install mysql-server
```

##### 权限设置
```sql
chown mysql:mysql -R /var/lib/mysql
```

##### 初始化MySQL
```sql
mysqld --initialize
```

##### 启动MySQL
```sql
systemctl start mysqld
```

##### 关闭MySQL
```sql
systemctl stop mysqld
```

##### 查看MySQL的运行状态
```sql
systemctl status mysqld
```


#### 初始化
##### 设置root密码
Mysql安装成功后，默认的root用户密码为空，你可以使用以下命令来创建root用户的密码：
```sql
mysqladmin -u root password "new_password";
```

通过以下命令连接数据库：
```sql
mysql -u root -p

```

##### 新建用户并授权
```sql
＃表示授予了192.168.0.111从远程用root用户名和123456密码对所有数据库所有表的所有权限
GRANT ALL PRIVILEGES ON *.* TO root@'192.168.0.111' IDENTIFIED BY '123456' WITH GRANT OPTION;

#表示授予了所有外来的IP从远程用myuser用户名和pass密码对demo数据库下的所有表的所有权限
GRANT ALL PRIVILEGES ON demo.* to 'myuser'@'%' IDENTIFIED BY 'pass' WITH GRANT OPTION;

＃表示授予了本机中的用户keke和haha密码对user数据库下的所有表的所有权限
GRANT ALL PRIVILEGES ON user.* to 'keke'@'localhost' IDENTIFIED BY 'haha' WITH GRANT OPTION; 

```
- ALL PRIVILEGES 表示所有的权限 全部权限[查看](https://dev.mysql.com/doc/refman/5.5/en/privileges-provided.html)
- ＊.＊ ．之前表示数据库，*表示全部，.之后表示数据表
- @'%' %表示所有的外来IP,localhost表示本地主机，％并不包括localhost

==注意:调用以下命令，方可即时刷新权限，或者重启数据库==
```sql
FLUSH PRIVILEGES;
```
新增用户之后可以使用以下命令（mysql数据库下的user表）
```sql
use mysql;
select user,host from user;
```
![](showuser.png)

#### 登陆相关命令
```sql
mysql -h 127.0.0.1 -u 用户名 -p #登陆命令
mysql> exit #退出登陆
mysql> status; #显示当前mysql的各种信息
mysql> select version(); #显示当前mysql的版本
mysql> show global variables like 'port'; #查看MySQL端口号
```

#### 数据库操作
```sql
show databases; #列出所有数据库
use db_name; #进入到制定的数据库中
create database db_name character set utf8; #创建一个名为db_name的数据库且字符编码指定为utf8
drop database db_name; #删除 库名为db_name的库
show tables; #列出所有的表名
describe table_name; #显示数据表的结构
```

#### 数据表操作

##### 新建表

```sql
CREATE TABLE 'user' (
'id' int(100) unsigned NOT NULL AUTO_INCREMENT primary key,
'password' varchar(32) NOT NULL DEFAULT '' COMMENT '用户密码',
'reset_password' tinyint(32) NOT NULL DEFAULT '' COMMENT '',
'mobile' varchar(20) NOT NULL DEFAULT '' COMMENT '',
'create_at' timestamp(6) NOT NULL DEFAULT 
)
```

##### 创建后表的修改
###### 添加列
```sql
alter table 表名 add 列名 列数据类型 [after 插入位置];
# 在students的最后追加列address
alter table students add address char(60);
# 在名为age的列后插入列birthday
alter table students add birthday date after age;
```

###### 修改列
```sql
语法：alter table 表名 change 列名称 新的列名称 新的列数据类型;
# 将表tel 列改名为telphone
alter table students change tel telphone char(13) default '-';
# 将name列的数据类型改为char(16)
alter table students change name name char(16) not null;
```

###### 删除列
```sql
语法：alter table tb_name drop 列名称;

#删除表students 中的birthday列
alter table students drop birthday;
```

###### 重命名表
```sql
alter table tb_name rename 新表名;
#重命名students为roommates;
alter table students rename roommates;
```

##### 删除表
```sql
语法：drop table 表名;
#删除roommates表
drop table roommates;
```



































#### 解决问题
可以尝试telnet来查看端口号是否正常.
- [因为防火墙的关系没办法连接上mysql](https://blog.csdn.net/u013257111/article/details/79063578)


#### 小技巧
存储ip地址
```sql
inet_aton 把ip转为无符号整型(4-8位) 
inet_ntoa 把整型的ip转为电地址
```
[ip地址存放](https://www.cnblogs.com/phpper/p/10220703.html)


参考：
- [安装](http://www.runoob.com/mysql/mysql-install.html)
- [21分钟MySQL基础入门](https://github.com/jaywcjlove/mysql-tutorial/blob/master/21-minutes-MySQL-basic-entry.md#%E5%A2%9E%E5%88%A0%E6%94%B9%E6%9F%A5)
- [官方文档](https://dev.mysql.com/doc/refman/5.5/en/)
- [Mysql合集](https://www.cnblogs.com/phpper/category/944100.html)
- [官方提供EMPLOYEE数据库](https://dev.mysql.com/doc/employee/en/employees-installation.html)
