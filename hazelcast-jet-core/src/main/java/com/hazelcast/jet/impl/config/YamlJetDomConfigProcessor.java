/*
 * Copyright (c) 2008-2021, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.impl.config;

import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.jet.config.JetConfig;
import org.w3c.dom.Node;

import java.util.Properties;

import static com.hazelcast.internal.config.yaml.W3cDomUtil.getWrappedYamlMapping;
import static com.hazelcast.internal.yaml.YamlUtil.asScalar;

public class YamlJetDomConfigProcessor extends JetDomConfigProcessor {

    YamlJetDomConfigProcessor(boolean domLevel3, JetConfig config) {
        super(domLevel3, config);
    }

    @Override
    protected void fillProperties(Node node, Properties properties) {
        YamlMapping propertiesMapping = getWrappedYamlMapping(node);
        for (YamlNode propNode : propertiesMapping.children()) {
            YamlScalar propScalar = asScalar(propNode);
            String key = propScalar.nodeName();
            String value = propScalar.nodeValue().toString();
            properties.put(key, value);
        }
    }
}
