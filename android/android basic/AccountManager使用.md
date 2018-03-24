

我所知道的用途
- 保存账号信息(用户名、密码、token等)
- 多个应用之间共享账号信息(需要使用同一个签名)



AccountManager常用方法速查表

| 方法 | 描述 |
|--------|--------|
|   addAccount     |     新增用户   |
|  getAccounts ()      |   获取当前设备中所有的账号信息(名称跟类型)     |
|   getAccountsByType     |    获取当前设备指定账号类型的所有账号信息(名称跟账号类型)  |
|   getAuthToken     |     根据指定账号和指定token类型获取对应的token   |
|   getAuthTokenByFeatures     |  根据账号类型和token类型获取对应的用户列表,如果只有一个账号,则直接返回这个账号的token,如果有多个账号则会弹窗让用户选择，返回用户选择的账号的信息,如果没有账号,则让用户登录或者注册之后返回对应的账号信息   |
|   setAuthToken     |    添加token到指定的用户到AccountManager   |
|   addAccountExplicitly     |   直接添加用户和对应的密码和用户信息到AccountManager,不会刷新授权的时间戳,需要主动调用notifyAccountAuthenticated方法来刷新时间戳,然而,由addAccount()和addAccountAsUser()方法触发这个方法时,会自动刷新授权时间戳.   |
|   peekAuthToken     |    获取指定账号和指定token类型的authToken   |
|  clearPassword    |   清空指定账号的密码,跟setPassword(accout)方法的效果一致,但需要的权限比较少    |
|  getPassword     |  获取指定账号的密码   |
|  getUserData     |  获取保存在AccountManager的指定账户信息  |


####1.一些概念的说明:
- authTokenType: 授权token的类型,在某些情况下,同一个账号会有不同的token类型,例如官方文档中的google就会利用不同类型的授权token分别登陆Gmail和GoogleCalendar


具体流程图:


#####void addOnAccountsUpdatedListener (OnAccountsUpdateListener listener,  Handler handler, boolean updateImmediately)
- 添加账号改动的监听,会返回当前所有的账号信息.
- **注意：在onDestroy里调用removeOnAccountsUpdatedListener方法删除监听**
- handler 还不知道怎么使用TODO

![](AccountManager.png)


####相关权限:
```
 <!-- client -->
    <uses-permission android:name="android.permission.USE_CREDENTIALS"/>
    <uses-permission android:name="android.permission.GET_ACCOUNTS"/>
    <uses-permission android:name="android.permission.MANAGE_ACCOUNTS"/>

    <!-- Authenticator -->
    <uses-permission android:name="android.permission.AUTHENTICATE_ACCOUNTS"/>
```



































