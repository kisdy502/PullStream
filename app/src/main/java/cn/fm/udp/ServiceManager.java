/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.fm.udp;

import android.content.Context;
import android.util.Log;

/**
 * This class is to manage the notificatin service and to load the
 * configuration.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class ServiceManager {

    private static final String LOGTAG = "ServiceManager";

    public Context context;

    public static boolean isServiceStart = false;

    public ServiceManager(Context context) {
        this.context = context;
    }

    public synchronized void startService(final String serverHost, final String dir, final String files, final int code) {
        final Callback callback = new Callback();
        final UDPClient client = new UDPClient();
        new Thread() {
            @Override
            public void run() {
                client.startClient(serverHost, dir, code, files, callback);
            }
        }.start();

    }

}
