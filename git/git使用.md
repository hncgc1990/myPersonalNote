[TOC]

##### 基本操作
1.配置用户名字和邮箱地址
```
 git config --global user.name "Your Name"
 git config --global user.email "email@example.com"
```

2.新建库
```
git init
```
3.添加文件或者文件夹到版本库中
```
git add <file>
```
4.提交到版本管理
```
git commit -m "当前提交操作的日志"
```
5.查看本地库中的文件修改状况
```
git status
```
6.查看修改的内容
```
git diff <file>
```
7.查看提交日志
```
git log 
```
8.查看包括回滚的日志
```
git reflog
```
9.版本的回滚或者前进
```
git reset --hard commitid
```
HEAD表示当前版本,HEAD^表示上一个版本,HEAD^^表示上上一个版本,往回n个版本,HEAD~n
eg:git reset --hard HEAD

10.删除文件
```
git rm <file>
```
11.从本地的版本库中恢复文件
```
git checkout <file>
```
12.推送到远程库
```
git remote add origin git@server-name:path/repo-name.git　首先关联远程库，然后用以下命令提交
git push -u origin master//-u 参数用于第一次提交时,把本地的master分支和远程的master分支关联起来

git push origin master //正常推送命令
```
13.拷贝远程库到本地
```
git clone git@github.com:hncgc1990/Android-NoteBook.git
```
##### 分支
1.查看所有的分支
```
git branch //*开头的分支表示当前的分支
```
2.创建一个分支
```
git branch <name>
```
3.切换到指定分支
```
git checkout <name>
```
4.创建＋切换分支
```
git checkout -b <name>
```
5.合并指定分支到当前分支
```
git merge <name>
```
6.删除指定分支
```
git branch -d <name>
```

##### 分支管理策略
```
git merge --no-ff -m "merge with no-ff" dev
合并分支时，加上--no-ff参数就可以用普通模式合并，合并后的历史有分支，能看出来曾经做过合并，而fast forward合并就看不出来曾经做过合并
```
```
git stash//用来保存当前分支没有提交的工作(没有完成的工作)

git stash pop //当完成bug修复或者其他工作之后回来修复工作分支
```
```
如果要丢弃一个没有被合并过的分支，可以通过git branch -D <name>强行删除。(估计这种事情很少会做，谁知道什么时候又说要了)
```

##### 多人协作
推送到远程库
```
git push origin dev  //dev 为本地分支名称
```
当push失败并提示有冲突时,先从远程库中更新代码到本地，再解决冲突后重新推送
```
git pull 
```
如果但前分支没有跟远程库建立链接，那么可以通过以下命令建立连接.
```
git branch --set-upstream branch-name origin/branch-name
```

##### 标签
1.创建一个标签
```
git tag <name>
git tag <name> commit_id //可以指定提交的id 
git tag -a  <name> -m "标签说明" commit_id　//-m添加标签说明文字（应该更多使用）
```
2.查看所有的标签(顺序并不是创建时间)
```
git tag
```
3.查看某一个标签的信息
```
git show <name>
```
4.删除本地指定标签
```
git tag -d <name>
要删除远程库上的tag
先删除本地的标签，然后再删除远程库的标签
git push origin:refs/tags/<tagname>
```
5.将标签提推送到远程
```
git push origin <tagname>
git push origin --tags//把所有没有推送的标签都推送到远程库
```

#### 问题
- git status 命令路径乱码
解决方案：
```
git config --global core.quotepath false
```


























