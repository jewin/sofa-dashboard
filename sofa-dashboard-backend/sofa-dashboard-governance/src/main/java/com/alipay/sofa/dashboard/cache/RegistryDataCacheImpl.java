/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.dashboard.cache;

import com.alipay.sofa.dashboard.domain.RpcConsumer;
import com.alipay.sofa.dashboard.domain.RpcProvider;
import com.alipay.sofa.dashboard.domain.RpcService;
import com.alipay.sofa.rpc.common.utils.CommonUtils;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bystander
 * @version $Id: RegistryDataCache.java, v 0.1 2018年12月10日 23:57 bystander Exp $
 */
@Service
public class RegistryDataCacheImpl implements RegistryDataCache {

    private static final Logger                LOGGER    = LoggerFactory
                                                             .getLogger(RegistryDataCacheImpl.class);

    private Map<String, RpcService>            services  = new ConcurrentHashMap<>();

    private Map<RpcService, List<RpcProvider>> providers = new ConcurrentHashMap<>();

    private Map<RpcService, List<RpcConsumer>> consumers = new ConcurrentHashMap<>();

    @Override
    public void addProviders(String serviceName, List<RpcProvider> providerList) {

        RpcService rpcService = services.get(serviceName);

        if (rpcService == null) {
            LOGGER.warn("receive provider registry data add, but service name is not exist now,{}",
                serviceName);
            return;
        }

        List<RpcProvider> currentProviderList = providers.get(rpcService);
        if (currentProviderList == null) {
            providers.put(rpcService, providerList);
        } else {
            currentProviderList.addAll(providerList);
        }

        LOGGER.info("receive provider registry data add, data is {}", providerList);
    }

    @Override
    public void addConsumers(String serviceName, List<RpcConsumer> consumersList) {

        RpcService rpcService = services.get(serviceName);

        if (rpcService == null) {
            LOGGER.warn("receive consumer registry data add, but service name is not exist now,{}",
                serviceName);
            return;
        }

        List<RpcConsumer> currentConsumerList = consumers.get(rpcService);
        if (currentConsumerList == null) {
            consumers.put(rpcService, consumersList);
        } else {
            currentConsumerList.addAll(consumersList);
        }

        LOGGER.info("receive consumer registry data add, data is {}", consumers);

    }

    @Override
    public void removeProviders(String serviceName, List<RpcProvider> providerList) {
        RpcService rpcService = services.get(serviceName);
        List<RpcProvider> currentProviderList = providers.get(rpcService);
        for (RpcProvider deleteProvider : providerList) {
            currentProviderList.remove(deleteProvider);
        }
        LOGGER.info("receive provider registry data remove, data is {}", providerList);
    }

    @Override
    public void removeConsumers(String serviceName, List<RpcConsumer> consumersList) {
        RpcService rpcService = services.get(serviceName);
        List<RpcConsumer> currentConsumerList = consumers.get(rpcService);
        for (RpcConsumer deleteProvider : consumersList) {
            currentConsumerList.remove(deleteProvider);
        }
        LOGGER.info("receive consumer registry data remove, data is {}", consumersList);
    }

    @Override
    public void updateProviders(String serviceName, List<RpcProvider> providerList) {
        LOGGER.warn("receive provider registry data update, but not support,data is {}",
            providerList);
    }

    @Override
    public void updateConsumers(String serviceName, List<RpcConsumer> consumersList) {
        LOGGER.warn("receive consumer registry data update, but not support,data is {}",
            consumersList);

    }

    @Override
    public void addService(List<RpcService> rpcServiceList) {
        for (RpcService rpcService : rpcServiceList) {
            LOGGER.info("receive service registry data add, data is {}", rpcService);
            services.put(rpcService.getServiceName(), rpcService);
        }
    }

    @Override
    public void removeService(List<RpcService> rpcServices) {
        for (RpcService rpcService : rpcServices) {
            LOGGER.info("receive service registry data remove, data is {}", rpcService);
            services.remove(rpcService.getServiceName());
        }
    }

    @Override
    public void updateService(RpcService rpcService) {
        services.put(rpcService.getServiceName(), rpcService);
    }

    @Override
    public Map<String, RpcService> fetchService() {
        return this.services;
    }

    @Override
    public List<RpcProvider> fetchProvidersByService(String serviceName) {
        List<RpcProvider> result = new ArrayList<>();
        if (StringUtils.isEmpty(serviceName)) {
            return new ArrayList<>();
        }
        RpcService rpcService = services.get(serviceName);

        if (rpcService != null) {
            result = providers.get(rpcService);
            if (CommonUtils.isNotEmpty(result)) {
                return result;
            }
        }
        return result;
    }

    @Override
    public List<RpcConsumer> fetchConsumersByService(String serviceName) {

        List<RpcConsumer> result = new ArrayList<>();
        if (StringUtils.isEmpty(serviceName)) {
            return new ArrayList<>();
        }
        RpcService rpcService = services.get(serviceName);
        if (rpcService != null) {
            result = consumers.get(rpcService);
            if (CommonUtils.isNotEmpty(result)) {
                return result;
            }
        }
        return result;
    }
}