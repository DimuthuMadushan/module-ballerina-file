/*
 * Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.stdlib.file.service;

import io.ballerina.runtime.api.Runtime;
import io.ballerina.runtime.api.types.MethodType;
import io.ballerina.runtime.api.values.BObject;
import org.wso2.transport.localfilesystem.server.connector.contract.LocalFileSystemEvent;
import org.wso2.transport.localfilesystem.server.connector.contract.LocalFileSystemListener;

import java.util.HashMap;
import java.util.Map;

/**
 * File System connector listener for Ballerina.
 */
public class FSListener implements LocalFileSystemListener {

    private Runtime runtime;
    private Map<BObject, Map<String, MethodType>> serviceRegistry = new HashMap<>();

    public FSListener(Runtime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onMessage(LocalFileSystemEvent fileEvent) {
        for (Map.Entry<BObject, Map<String, MethodType>> serviceEntry: serviceRegistry.entrySet()) {
            MethodType serviceFunction = serviceEntry.getValue().get(fileEvent.getEvent());
            if (serviceFunction != null) {
                String functionName = serviceFunction.getName();
                BObject service  = serviceEntry.getKey();
                runtime.callMethod(service, functionName, null);
            }
        }
    }

    public void addService(BObject service, Map<String, MethodType> attachedFunctions) {
        this.serviceRegistry.put(service, attachedFunctions);
    }

    public void removeService(BObject service) {
        this.serviceRegistry.remove(service);
    }
}
