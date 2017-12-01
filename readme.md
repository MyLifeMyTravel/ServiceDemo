# 第10章 后台默默的劳动者-探究服务
这章我们将介绍 Android 四大组件之一的 Service(服务)，Service 是 Android 程序实现后台操作的解决方案，它主要用于那些不需要用户交互(Foreground Service 除外)且长时间运行的任务，比如：下载、播放音乐等。即使程序被切换到后台，Service 也会一直运行，除非 Service 附属的进程被系统杀掉。当然，大家也别被服务这个概念给迷惑了，Service 并不会主动开启线程，Service 中所有代码默认都是运行在主线程中的。

## 10.1 Service 的基本用法
Service 作为 Android 四大组件之一，涉及到的内容非常多，这里我们先讲述一下 Service 的基础知识。

### 10.1.1 Service 的基础

创建一个 Service，必须继承 Service 或者使用已有 Service 子类。Service 类似于 Activity，有自己的生命周期，我们可以在相应的回调中实现我们的处理逻辑。以下是几个比较重要的回调方法：

**onStartCommand()**:当应用组件(如 Activity)调用 `startService()` 方法时，系统即会回调该方法，此时 Service 已处于启动状态。一旦 Service 启动，即可在后台无限期运行，即使启动 Service 的组件已经被销毁也不受影响，除非手动调用 `stopSelf()` 或者 `stopService()`。

**onBind()**:当应用组件调用 `bindService()` 方法时，系统会回调该方法。通过 `bindService()` 可以实现 RPC 服务(Remote Procedure Calls)。

**onUnbind()**:当应用组件调用 `unbindService()` 方法时，系统会回调该方法。

**onCreate()**: Service 第一次创建时回调。该方法在 `onStartCommand()` 和 `onBind()` 之前回调。

**onDestroy()**: Service 销毁时调用。我们应该在这里回收那些不再需要的资源，如：线程、注册广播已经监听器。

学以致用，下面我们通过一个小例子来学习一下 Service 的基本用法。

### 10.1.2 Service-小试牛刀
新建一个项目 **ServiceDemo**，在 `service` 包下创建 `FirstService.java` 类(mac:Command + N)。

![WX20171009-135151](http://odsdowehg.bkt.clouddn.com/WX20171009-135151.png)

在弹出的对话框中，我们可以看到有两个选项，其中 [Exported](https://developer.android.com/guide/topics/manifest/service-element.html#exported) 属性表示是否允许其它程序启动该 Service。[Enabled](https://developer.android.com/guide/topics/manifest/service-element.html#enabled) 属性表示服务是否被启用。

`FirstService.java` 代码如下：

```java
public class FirstService extends Service {

    public FirstService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
```

现在 `FirstService.java` 中只有一个构造函数和一个 `onBind()` 方法。通过查看 `Service` 的源码，我们可以发现 `onBind()` 方法是 `Service` 中唯一的一个抽象方法，该方法用于 Bound Service。关于 Bound Service 会在后面小结会讲到。这里我们复写 `onStartCommand`、`onCreate()`、`onDestroy()` 方法，并在这些方法中添加 Log 来验证服务是否开启。

```java
public class FirstService extends Service {

    // 如果对 TAG 没有特殊要求，可以使用该方法获取类名当 TAG
    private static final String TAG = FirstService.class.getSimpleName();

    public FirstService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + " onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + " onDestroy");
    }
}
```

Service 创建完成后，我们就来学习如何启动和关闭这个服务。启动 Service 和打开 Activity 类似，也是通过 Intent 来实现，下面就让我们动手来实现吧~

首先，我们创建一个名为 `activity_first_service` 的布局文件，布局中的两个按钮分别用于启动和关闭服务。代码如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:id="@+id/btn_start_service"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="启动 Service"
        android:textAllCaps="false" />

    <Button
        android:id="@+id/btn_stop_service"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="停止 Service"
        android:textAllCaps="false" />
</LinearLayout>
```

接着，我们创建对应的 Activity，代码如下：

```java
public class FirstServiceActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_service);

        //绑定点击事件监听
        findViewById(R.id.btn_start_service).setOnClickListener(this);
        findViewById(R.id.btn_stop_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_service:
                //启动服务
                startService(new Intent(this, FirstService.class));
                break;
            case R.id.btn_stop_service:
                //关闭服务
                stopService(new Intent(this, FirstService.class));
                break;
        }
    }
}
```

可以看到，我们通过 `startService()` 和 `stopService()` 方法来启动、关闭服务。这两个方法是定义在 `Context` 类中的抽象方法，因此，在任意 Context 的子类中都能开启、关闭服务。

在正式运行程序之前，我们需要把 `FirstService` 在 `AndroidManifest.xml` 中进行注册：

```xml
<service
    android:name=".service.FirstService"
    android:enabled="true"
    android:exported="true" />
```

关于 `<service>` 更多内容可查看 [service-element](https://developer.android.com/guide/topics/manifest/service-element.html)。

点击 `Start Service` 按钮，logcat 中输出日志如下：

```log
10-11 15:39:48.950 com.apkbus.servicedemo D/FirstService: FirstService onCreate
10-11 15:39:48.950 com.apkbus.servicedemo D/FirstService: FirstService onStartCommand
```

`FirstService` 中的 `onCreate()` 和 `onStartCommand()` 方法都执行了，说明服务的确启动成功了，并且你可以在系统的 设置->开发者选项->正在运行的服务 查看服务是否成功启动。
![device-2017-10-11-162838-w360](http://odsdowehg.bkt.clouddn.com/device-2017-10-11-162838.png)

当再次点击 `开启 Service` 按钮，观察 logcat 输出：

```log
10-11 15:39:55.950 com.apkbus.servicedemo D/FirstService: FirstService onStartCommand
```
你会发现，`onCreate()` 方法没有再次调用。这是因为 `onCreate()` 只在服务第一次创建时才会调用，而 `onStartCommand()` 则是每次通过 `startService()` 启动服务时都会调用。

点击 `关闭 Service` 按钮，logcat 中输出日志如下：

```log
10-11 16:34:01.037 com.apkbus.servicedemo D/FirstService: FirstService onDestroy
```

可以看到服务的确被关闭了。值得注意的是，通过 `startService()` 方法启动的服务的生命周期与启动它的组件的生命周期是互相独立的。也就是说，如果你通过 `startService()` 启动了一个服务，你必须调用 `stopService()` 或者当服务在后台处理完事件后调用 Service 的内部方法 `stopSelf()` 来主动结束。当然，如果系统内存不足时，系统也会关闭掉一些服务。

### 10.1.3 让 Service 成为真正的 Service
还记得，开头说的服务默认是运行在主线程中的嘛？也就是说，我们需要在服务中创建子线程去执行真正的耗时操作，否则就会出现阻塞主线程导致 ANR(Application Not Response)。下面我们就来验证一下。

修改 `FirstServiceActivity`，给 `启动 Service` 按钮添加 Log：

```java
@Override
public void onClick(View view) {
    switch (view.getId()) {
        case R.id.btn_start_service:
            //输出当前线程的ID
            Log.d(TAG, "当前线程ID：" + Thread.currentThread().getId());
            //启动服务
            startService(new Intent(this, FirstService.class));
            break;
        case R.id.btn_stop_service:
            //关闭服务
            stopService(new Intent(this, FirstService.class));
            break;
    }
}
```

同时，我们修改 `FirstService` 中的 `onStartCommand()` 方法：

```java
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, TAG + " onStartCommand");
    Log.d(TAG, "当前线程ID：" + Thread.currentThread().getId());
    //让 Service 所在的线程 sleep 10秒
    //如果为主线程，则会出现 ANR
    try {
        Thread.sleep(10000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return super.onStartCommand(intent, flags, startId);
}
```

运行程序，点击 `启动 Service` 按钮，观察 logcat 输出：

```log
10-12 11:17:14.909 com.apkbus.servicedemo D/FirstServiceActivity: 当前线程ID：1
10-12 11:17:14.918 com.apkbus.servicedemo D/FirstService: FirstService onCreate
10-12 11:17:14.919 com.apkbus.servicedemo D/FirstService: FirstService onStartCommand
10-12 11:17:14.919 com.apkbus.servicedemo D/FirstService: 当前线程ID：1
10-12 11:17:21.305 com.apkbus.servicedemo I/art: Thread[3,tid=7023,WaitingInMainSignalCatcherLoop,Thread*=0x7e4e496400,peer=0x12c93f70,"Signal Catcher"]: reacting to signal 3
10-12 11:17:21.364 com.apkbus.servicedemo I/art: Wrote stack traces to '/data/anr/traces.txt'
```

可以看到，`FirstServiceActivity` 和 `FirstService` 所在线程ID都为1，说明服务的确默认在主线程中运行。点击 `启动 Service` 后发现程序不再有任何相应而且 log 中有 `/data/anr/traces.txt` 字样，说明程序的确出现了 ANR。

最简单的解决办法，就是创建子线程去执行耗时操作，执行完成后在主线程中更新UI。这里我们选择 Thread + Handler 来实现。

```java
public class FirstService extends Service {

    private static final String TAG = FirstService.class.getSimpleName();
    private static final int MESSAGE_SLEEP = 1;
    private static final int MESSAGE_GET_UP = 2;

    //用于处理耗时操作的Handler
    private Handler mServiceHandler;
    private HandlerThread mServiceThread;
    //主线程上的Handler，用于更新UI
    private final Handler mMainHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SLEEP:
                    Log.d(TAG, "当前线程ID：" + Thread.currentThread().getId());
                    Toast.makeText(getApplicationContext(), "让我先睡20s", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_GET_UP:
                    Toast.makeText(getApplicationContext(), "我睡醒了~服务关闭", Toast.LENGTH_SHORT).show();
                    //后台操作结束，主动关闭
                    //之后会回调onDestroy()
                    stopSelf();
                    break;
            }
        }
    };

    public FirstService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAG + " onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, TAG + " onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + " onCreate");
        mServiceThread = new HandlerThread("FirstService", Process.THREAD_PRIORITY_BACKGROUND);
        mServiceThread.start();

        Looper serviceLooper = mServiceThread.getLooper();
        mServiceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + " onStartCommand");
        mServiceHandler.sendEmptyMessage(MESSAGE_SLEEP);
        return super.onStartCommand(intent, flags, startId);
    }

    private class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "当前线程ID：" + Thread.currentThread().getId() + "；即将开始耗时操作");
            //发送消息提示主线程弹出Toast
            mMainHandler.sendEmptyMessage(MESSAGE_SLEEP);
            long startTime = System.currentTimeMillis();
            //模拟耗时操作
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //发送消息提示主线程耗时操作已结束，弹出Toast
            mMainHandler.sendEmptyMessage(MESSAGE_GET_UP);
            Log.d(TAG, "耗时操作已结束，用时：" + (System.currentTimeMillis() - startTime) / 1000 + "s，该服务即将关闭");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + " onDestroy");
    }

}
```

再次运行程序，点击 `启动 Service`，查看 logcat 输出：

```
10-12 14:39:24.626 com.apkbus.servicedemo D/FirstServiceActivity: 当前线程ID：1
10-12 14:39:24.632 com.apkbus.servicedemo D/FirstService: FirstService onCreate
10-12 14:39:24.640 com.apkbus.servicedemo D/FirstService: FirstService onStartCommand
10-12 14:39:24.641 com.apkbus.servicedemo D/FirstService: 当前线程ID：4618；即将开始耗时操作
10-12 14:39:44.641 com.apkbus.servicedemo D/FirstService: 耗时操作已结束，用时：20s，该服务即将关闭
10-12 14:39:44.652 com.apkbus.servicedemo D/FirstService: FirstService onDestroy
```

可以观察到，Service 启动后先打印了子线程的 ID 为 4618，不同于主线程，等待20秒后，耗时操作完成，主动调用 `stopSelf()` 关闭当前服务并弹出 Toast 提示。

通过上述代码，我们真正实现了如何让服务在后台执行耗时操作。但是，上述代码虽然能使服务变成真正的服务，但是代码量有点多。那么，有么有什么更简洁的方法呢？答案当然是有咯，它就是 [IntentService](https://developer.android.com/reference/android/app/IntentService.html)。`IntentService` 是 `Service` 的一个子类，用来处理异步的请求，内部也是通过 Thread + Handler 这种方式实现。你可以通过 `startService()` 来启动服务，该 Service 会在需要的时候创建，当完成所有的任务以后自己关闭。使用 `IntentService` 有两个好处，一是不用自己创建子线程，二是后台处理完成后会自己关闭 Service。而且代码相当简洁，只需要实现构造方法和 `onHandlerIntent()`。

下面，我们就用 `IntentService` 来改造 `FirstService` 的实现。

```java
public class FirstService extends IntentService {

    private static final String TAG = FirstService.class.getSimpleName();
    private static final int MESSAGE_SLEEP = 1;
    private static final int MESSAGE_GET_UP = 2;

    //主线程上的Handler，用于更新UI
    private final Handler mMainHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SLEEP:
                    Toast.makeText(getApplicationContext(), "让我先睡20s", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_GET_UP:
                    Toast.makeText(getApplicationContext(), "我睡醒了~服务关闭", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public FirstService() {
        //IntentService构造方法需要有name参数，用于设置子线程的名称，方便调试
        //建议传类名
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent, 当前线程ID：" + Thread.currentThread().getId());
        //注意，onHandleIntent 是在子线程中执行，所以请不要在此更新UI
        mMainHandler.sendEmptyMessage(MESSAGE_SLEEP);
        long startTime = System.currentTimeMillis();
        //模拟耗时操作
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "耗时操作已结束，用时：" + (System.currentTimeMillis() - startTime) / 1000 + "s，该服务即将关闭");
        mMainHandler.sendEmptyMessage(MESSAGE_GET_UP);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + " onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + " onDestroy");
    }
}
```

使用 `IntentService` 改造过的代码相对于直接继承 `Service` 代码量已经少了很多，不需要自己去繁琐的创建子线程。因为，`onHandleIntent` 是在子线程中执行，所以不能直接更新 UI。在例子中创建了一个 `mMainHandler` 对象用于更新 UI。

```
10-12 20:07:42.975 com.apkbus.servicedemo D/FirstServiceActivity: 当前线程ID：1
10-12 20:07:42.984 com.apkbus.servicedemo D/FirstService: FirstService onCreate
10-12 20:07:42.988 com.apkbus.servicedemo D/FirstService: FirstService onStartCommand
10-12 20:07:42.988 com.apkbus.servicedemo D/FirstService: onHandleIntent, 当前线程ID：5072
10-12 20:08:02.990 com.apkbus.servicedemo D/FirstService: 耗时操作已结束，用时：20s，该服务即将关闭
10-12 20:08:02.996 com.apkbus.servicedemo D/FirstService: FirstService onDestroy
```

## 10.2 Service 的分类
在上一节我们讲了 Service 的基本用法以及 `IntentService`，想必大家现在对服务有了比较深刻的了解。细心的同学会发现，我们前面所讲的 Service 都是通过 `startService()` 方法启动，Android 官方把这种 Service 称为 `Started Service`。那么，Service 还有哪些类型呢？

参考 [Service Guide](https://developer.android.com/guide/components/services.html)，Android 大致把 Service 分为以下三类：

1. **Started Service**：即通过 `startService()` 启动的服务。`FirstService` 即是该种类型。
2. **Bound Service**：通过 `bindService()` 启动的服务。Bound Service 相较于 Started Service 的区别在于，一是Bound Service 的生命周期依附于启动它的组件，二是 Bound Service 可以通过 IBinder 接口控制后台处理。此类型 Service 会回调 `onBind()` 方法。
3. **Foreground Service**：顾名思义，即前台服务。前台服务与前两种服务的区别在于，前台服务会一直在状态栏显示应用图标，下拉后可以在通知栏看见更详细的信息。一般与 Notification 结合使用。相较于前两种服务，前台服务具有较高的优先级，在系统内存不够的情况下不容易被系统回收掉。常见的如天气应用、音乐播放器。

### 10.2.1 Bound Service
我们假定加减运算是耗时操作，用户可以控制何时通过 Service 进行加法和减法操作。

我们来创建 `BoundService.java`，代码如下：

```java
public class BoundService extends Service {

    private static final String TAG = BoundService.class.getSimpleName();

    private CalcBinder mBinder = new CalcBinder();

    public BoundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + " onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + " onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAG + " onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, TAG + " onUnbind");
        return super.onUnbind(intent);
    }

    public class CalcBinder extends Binder {

        public int plus(int a, int b) {
            Log.d(TAG, "a + b = " + (a + b));
            return a + b;
        }

        public int sub(int a, int b) {
            Log.d(TAG, "a - b = " + (a - b));
            return a - b;
        }
    }
}
```

这里，我们新建了一个继承自 `Binder` 的 `CalcBinder` 类，在其内部提供了用于计算的加法、减法方法。我们在这两个方法中分别打印了执行的结果并将结果返回。

而 `CalcBinder` 的实例我们在 `onBind()` 方法中进行返回。同时，我们在 Service 的各个回调函数里打印了一些日志。

创建 `activity_bound_service.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context="com.apkbus.servicedemo.BoundServiceActivity">

    <Button
        android:id="@+id/btn_bind_service"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Bind Service"
        android:textAllCaps="false" />

    <Button
        android:id="@+id/btn_unbind_service"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="UnBind Service"
        android:textAllCaps="false" />

    <Button
        android:id="@+id/btn_plus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="1 + 2 = ?"
        android:textAllCaps="false" />

    <Button
        android:id="@+id/btn_sub"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="10 - 1 = ?"
        android:textAllCaps="false" />
</LinearLayout>
```

布局文件中的四个按钮分别用于绑定服务、取消绑定服务、加法运算和减法运算。

创建 `BoundServiceActivity.java`，代码如下:

```java
public class BoundServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = BoundServiceActivity.class.getSimpleName();

    private BoundService.CalcBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_service);

        findViewById(R.id.btn_bind_service).setOnClickListener(this);
        findViewById(R.id.btn_unbind_service).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);
        findViewById(R.id.btn_sub).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServiceConnection = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bind_service:
                bindService(new Intent(this, BoundService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                //unbindService之后依然能调用plus和sub方法
                //因为Binder对象依然存在
                unbindService(mServiceConnection);
                break;
            case R.id.btn_plus:
                Toast.makeText(this, "1 + 2 = " + mBinder.plus(1, 2), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_sub:
                Toast.makeText(this, "10 - 1 = " + mBinder.plus(10, 1), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, TAG + " onServiceConnected");
            mBinder = (BoundService.CalcBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, TAG + " onServiceDisconnected");
        }
    };
}
```

这里我们创建了一个 `ServiceConnection` 匿名类，重写了 `onServiceConnected()` 和 `onServiceDisconnected()`，这个两个方法分别会在 Service 绑定和解绑的时候调用。`onServiceConnected()` 方法有个 IBinder 对象，我们通过向下转型获取到 `CalcBinder` 的实例。通过 `CalcBinder` 实例我们可以执行 `plus()` 和 `sub()` 方法，让 Service 变得可控和可交互。

运行程序，点击 `Bind Service`。我们可以看到 `bindService()` 方法接受三个参数。第一个参数是一个 Intent，跟 `startService()` 方法参数一样；第二个参数是 `ServiceConnection` 对象；第三个参数是一个标志位，一般传入 `Service.BIND_AUTO_CREATE` 表示在绑定时自动创建服务。

服务绑定之后，一次点击加法、减法和取消绑定服务按钮，logcat 输出如下：

```
10-12 23:03:43.279 com.apkbus.servicedemo D/BoundService: BoundService onCreate
10-12 23:03:43.280 com.apkbus.servicedemo D/BoundService: BoundService onBind
10-12 23:03:43.281 com.apkbus.servicedemo D/BoundServiceActivity: BoundServiceActivity onServiceConnected
10-12 23:03:45.177 com.apkbus.servicedemo D/BoundService: a + b = 3
10-12 23:03:46.958 com.apkbus.servicedemo D/BoundService: a + b = 11
10-12 23:03:48.948 com.apkbus.servicedemo D/BoundService: BoundService onUnbind
10-12 23:03:48.949 com.apkbus.servicedemo D/BoundService: BoundService onDestroy
```

log 大致跟我们预期的一致，但是好像少了点什么，对，`onServiceDisconnected()` 没有被调用。这是为什么呢？查看官方文档发现：

> Called when a connection to the Service has been lost. This typically happens when the process hosting the service has crashed or been killed. This does not remove the ServiceConnection itself -- this binding to the service will remain active, and you will receive a call to onServiceConnected(ComponentName, IBinder) when the Service is next running.

大意即当 Service 所在进程 crash 或者被 kill 的时候，onServiceDisconnected 才会被调用。

### 10.2.2 Foreground Service
这一节我们来学习下如何创建前台服务吧。关于这里用到的 Nofication 知识可以查看 **8.2 通知的使用**。这里我们创建一个时钟的前台Service，实时更新通知栏显示的时间。

创建 `ForegroundService`:

```java
public class ForegroundService extends Service {

    private static final String TAG = ForegroundService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    private Handler mHandler = new Handler();

    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + " onCreate");
        mHandler.post(mUpdateRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + " onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + " onDestroy");
        //移除前台通知
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this)
                .setContentTitle("Foreground Service Notification")
                .setContentText("当前时间 : " + String.format("%tc", Calendar.getInstance()))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
            //将Service变成前台服务
            startForeground(NOTIFICATION_ID, buildNotification());
        }
    };
}
```

可以看到，这里我们通过 `startForeground()` 方法将 Service 变成前台并展示在通知栏中。该方法接收两个参数，第一个是通知的ID，第二个是 Notification 对象。

现在运行程序，点击 `开启 Foreground Service`，你会看到通知栏多了我们的应用图标，下拉查看通知，会发现通知的时间在实时更新。

当 Service 被关闭时，调用 `stopForeground(true)` 将通知消除掉。

## 10.3 Service 的生命周期
通过前面的学习，想必大家对 Service 的生命周期有了一定的了解。

![service_lifecycle](http://odsdowehg.bkt.clouddn.com/service_lifecycle.png)

上图是 Android 官方给出的 Service 生命周期示意图，可以帮助大家更加直观的了解 Service 的生命周期。

值得注意的是，图中给出的是 `Started Service` 和 `Bound Service` 独立的生命周期。那么，如果当 `Started Service` 遇见 `Bound Service` ，Service 的生命周期会怎么变化呢？

创建 `LifeCycleService` ：

```java
public class LifeCycleService extends Service {

    private static final String TAG = LifeCycleService.class.getSimpleName();

    public LifeCycleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand,startId = " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
```

接下来是，对应 Activity 的布局文件 `activity_service_lifecycle.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:id="@+id/btn_start_service"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="startService()"
        android:textAllCaps="false" />

    <Button
        android:id="@+id/btn_bind_service"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="bindService()"
        android:textAllCaps="false" />

    <Button
        android:id="@+id/btn_unbind_service"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="unbindService()"
        android:textAllCaps="false" />

    <Button
        android:id="@+id/btn_stop_service"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="stopService()"
        android:textAllCaps="false" />

</LinearLayout>
```

UI效果如下：

![WechatIMG185 w360](http://odsdowehg.bkt.clouddn.com/WechatIMG185.jpeg)

接下来是 `ServiceLifeCycleActivity`，代码如下：

```java
public class ServiceLifeCycleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ServiceLifeCycleActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_lifecycle);

        findViewById(R.id.btn_start_service).setOnClickListener(this);
        findViewById(R.id.btn_bind_service).setOnClickListener(this);
        findViewById(R.id.btn_unbind_service).setOnClickListener(this);
        findViewById(R.id.btn_stop_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_service:
                Log.d(TAG, "Click to startService");
                startService(new Intent(this, LifeCycleService.class));
                break;
            case R.id.btn_bind_service:
                Log.d(TAG, "Click to bindService");
                bindService(new Intent(this, LifeCycleService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                Log.d(TAG, "Click to unbindService");
                unbindService(mServiceConnection);
                break;
            case R.id.btn_stop_service:
                Log.d(TAG, "Click to stopService");
                stopService(new Intent(this, LifeCycleService.class));
                break;
            default:
                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}
```

运行程序，依次点击 `startService()`、`bindService()`、`unbindService()`、`stopService()`或者`startService()`、`bindService()`、`stopService()`、`unbindService()`，log输出如下：

```
10-13 10:55:26.795 com.apkbus.servicedemo D/ServiceLifeCycleActivity: Click to startService
10-13 10:55:26.803 com.apkbus.servicedemo D/LifeCycleService: onCreate
10-13 10:55:26.803 com.apkbus.servicedemo D/LifeCycleService: onStartCommand,startId = 1
10-13 10:55:29.606 com.apkbus.servicedemo D/ServiceLifeCycleActivity: Click to bindService
10-13 10:55:29.610 com.apkbus.servicedemo D/LifeCycleService: onBind
10-13 10:55:31.892 com.apkbus.servicedemo D/ServiceLifeCycleActivity: Click to unbindService
10-13 10:55:31.894 com.apkbus.servicedemo D/LifeCycleService: onUnbind
10-13 10:55:33.344 com.apkbus.servicedemo D/ServiceLifeCycleActivity: Click to stopService
10-13 10:55:33.347 com.apkbus.servicedemo D/LifeCycleService: onDestroy
```

观察日志你会发现，不管你单独点击 `stopService()` 或 `unbindService()`，都不会调用 Service 的 `onDestroy()` 方法。只有两个按钮都点击一次 Service 才会被销毁。也就是说，`stopService()` 只会让 Service 停止，`unbindService()` 只会解除 Service 和 Activity 的绑定。Service 只有在既没有绑定又停止的时候才会调用 `onDestroy()` 销毁。

## 10.4 总结
在本章中，我们学习了很多关于 Service 的知识，包括 Service 的基本用法、Service 的生命周期、前台服务以及 IntentService。通过本章的学习，相信大家能解决开发中碰到的问题。本章作为 Service 入门，有很多知识点并未涉及到，如：AIDL、Service 保活等，希望大家再接再厉，早日成为 Android 大牛。

本章代码已全部上传至Github:[ServiceDemo](https://github.com/MyLifeMyTravel/ServiceDemo)。文中的每个例子都是一次commit，直接 checkout 就可以看到完整可运行的 Demo 了。

## 参考文档：
1. [Service](https://developer.android.com/guide/components/services.html)
2. [Service Reference](https://developer.android.com/reference/android/app/Service.html)
3. [Running in a Background Service](https://developer.android.com/training/run-background-service/index.html)
4. [IntentService](https://developer.android.com/reference/android/app/IntentService.html)