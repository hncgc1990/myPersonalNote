部门表



| id| departId |departName|priority|status |created|updated|
|--------|--------|-------|-------|-------|-------|-------|
|  主键      |部门id |部门名称|？级别？|状态|创建时间|更新时间|



用户表



| id     | peerid |gender |main_name|pinyin_name |real_name|avatar|phone|email|department_id|status|created|updated|
|--------|--------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
|  主键   |?id?    |性别   |名字    |名字拼音|真实名称|头像地址|电话|邮箱地址|所属部门id|??|创建时间|更新时间|



群聊表



| id| peerid |groupType|mainName|avatar |creatorId|userCnt|userList|version|status|created|updated|
|--------|--------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
|  主键   |群的主键 |群聊类型|群聊名称|群聊头像地址|创建者的id|群里的人员数量|参与群聊用户列表|版本号|状态信息|创建时间|更新时间|





消息表



| id| msgId |fromId|toId|sessionKey |content|msgType|displayType|status|created|updated|
|--------|--------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
|  主键   |消息id |发送方的用户id|接收方的用户id??|会话的主键|发送内容|消息类型|显示类型|状态信息|创建时间|更新时间|
|  int   |int |int| int|登录用户id_toId|string|int|int|int|int|int|



session表



| id| sessionKey |peerId|peer_Type|latestMsgType |latesttMsgId|latestMsgData|talkId|created|updated|
|--------|--------|-------|-------|-------|-------|-------|-------|-------|-------|
|  主键   |会话的主键 |群的主键|会话的类型|最后消息的类型|最后消息的id|最后消息的内容|最后发言的用户id|创建时间|更新时间|
|  int   |String |int| int(分为单聊和群聊)|int|int|string|int|int|int|int|