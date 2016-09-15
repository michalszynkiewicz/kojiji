/**
 * Copyright (C) 2015 Red Hat, Inc. (jcasey@redhat.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.red.build.koji.model.xmlrpc;

import org.commonjava.rwx.binding.anno.DataKey;
import org.commonjava.rwx.binding.anno.KeyRefs;
import org.commonjava.rwx.binding.anno.StructPart;

/**
 * Created by jdcasey on 12/3/15.
 */
@StructPart
public class KojiSessionInfo
{
    @DataKey( "session-id" )
    private int sessionId;

    @DataKey( "session-key" )
    private String sessionKey;

    private transient KojiUserInfo userInfo;

    @KeyRefs( {"session-id", "session-key"} )
    public KojiSessionInfo( int sessionId, String sessionKey )
    {
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
    }

    public int getSessionId()
    {
        return sessionId;
    }

    public String getSessionKey()
    {
        return sessionKey;
    }

    @Override
    public String toString()
    {
        return "KojiSessionInfo{" +
                "sessionId=" + sessionId +
                ", sessionKey='" + sessionKey + '\'' +
                '}';
    }

    public void setUserInfo( KojiUserInfo userInfo )
    {
        this.userInfo = userInfo;
    }

    public KojiUserInfo getUserInfo()
    {
        return userInfo;
    }
}
