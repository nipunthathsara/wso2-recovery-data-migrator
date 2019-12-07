/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.recovery.data.migrator.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.recovery.data.migrator.Constants;
import org.wso2.recovery.data.migrator.jdbc.migrator.JDBCMigrator;

@Component(
        name = "org.wso2.recovery.data.migrator.client",
        immediate = true
)
public class MigratorServiceComponent {
    private static RegistryService registryService;
    private static MigratorServiceComponentDataHolder dataHolder = MigratorServiceComponentDataHolder.getInstance();

    private static Log log = LogFactory.getLog(MigratorServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {
        log.info("Activating recovery data migrating client...");
        try {
            if (System.getProperty(Constants.JDBC_TO_REGISTRY_MIGRATION_PROPERTY) != null) {
                log.info("Migrating recovery data from JDBC to Registry.");
                JDBCMigrator.migrateRegistryToJDBC();
            } else if (System.getProperty(Constants.REGISTRY_TO_JDBC_MIGRATOR) != null) {
                log.info("MIgrating recovery data from Registry to JDBC");
                JDBCMigrator.migrateRegistryToJDBC();
            } else {
                log.info("Recovery data migration is not enabled.");
            }

        } catch (Throwable e) {
            log.error("Error while activating recovery data migrating client.", e);
        }
    }

    @Reference(
            name = "registry.service",
            service = RegistryService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRegistryService"
    )
    protected void setRegistryService(RegistryService registryService) {
        log.debug("Setting the Registry Service");
        dataHolder.setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        log.debug("UnSetting the Registry Service");
        dataHolder.setRegistryService(null);
    }
}
