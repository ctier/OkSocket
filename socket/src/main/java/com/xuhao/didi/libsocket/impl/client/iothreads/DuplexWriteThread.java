package com.xuhao.didi.libsocket.impl.client.iothreads;

import android.content.Context;

import com.xuhao.didi.common.basic.AbsLoopThread;
import com.xuhao.didi.common.common_interfacies.client.io.IWriter;
import com.xuhao.didi.common.common_interfacies.dispatcher.IStateSender;
import com.xuhao.didi.core.utils.SLog;
import com.xuhao.didi.libsocket.impl.exceptions.ManuallyDisconnectException;
import com.xuhao.didi.libsocket.sdk.client.action.IAction;

import java.io.IOException;

/**
 * Created by xuhao on 2017/5/17.
 */

public class DuplexWriteThread extends AbsLoopThread {
    private IStateSender mStateSender;

    private IWriter mWriter;

    public DuplexWriteThread(Context context, IWriter writer,
                             IStateSender stateSender) {
        super(context, "duplex_write_thread");
        this.mStateSender = stateSender;
        this.mWriter = writer;
    }

    @Override
    protected void beforeLoop() {
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        mWriter.write();
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mWriter.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex write error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_SHUTDOWN, e);
    }
}
