package com.consol.citrus.schema;

import java.util.Map;

import org.apache.xerces.util.DOMUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.consol.citrus.group.RepeatOnErrorUntilTrue;

public class RepeatOnErrorUntilTrueParser implements BeanDefinitionParser {

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(RepeatOnErrorUntilTrue.class);

        DescriptionElementParser.doParse(element, beanDefinition);

        String indexName = element.getAttribute("indexName");
        beanDefinition.addPropertyValue("indexName", indexName);

        String condition = element.getAttribute("condition");
        beanDefinition.addPropertyValue("condition", condition);

        String autoSleep = element.getAttribute("autoSleep");
        if (StringUtils.hasText(autoSleep)) {
            beanDefinition.addPropertyValue("autoSleep", autoSleep);
        }

        Map actionRegistry = TestActionRegistry.getRegisteredActionParser();
        ManagedList actions = new ManagedList();

        Element action = DOMUtil.getFirstChildElement(element);

        if (action != null && action.getTagName().equals("description")) {
            beanDefinition.addPropertyValue("description", action.getNodeValue());
            action = DOMUtil.getNextSiblingElement(action);
        }

        if (action != null) {
            do {
                BeanDefinitionParser parser = (BeanDefinitionParser)actionRegistry.get(action.getTagName());

                actions.add(parser.parse(action, parserContext));
            } while ((action = DOMUtil.getNextSiblingElement(action)) != null);
        }

        if (actions.size() > 0) {
            beanDefinition.addPropertyValue("actions", actions);
        }

        beanDefinition.addPropertyValue("name", element.getLocalName());

        return beanDefinition.getBeanDefinition();
    }
}
