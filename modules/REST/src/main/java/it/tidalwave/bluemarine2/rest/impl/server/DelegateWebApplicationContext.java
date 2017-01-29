/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.rest.impl.server;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: Class.java,v 631568052e17 2013/02/19 15:45:02 fabrizio $
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class DelegateWebApplicationContext implements WebApplicationContext
  {
//    @Delegate FIXME doesn't work
    @Nonnull
    private final ApplicationContext delegate;

    @Getter @Nonnull
    private final ServletContext servletContext;

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getApplicationName() {
        return delegate.getApplicationName();
    }

    @Override
    public String getDisplayName() {
        return delegate.getDisplayName();
    }

    @Override
    public long getStartupDate() {
        return delegate.getStartupDate();
    }

    @Override
    public ApplicationContext getParent() {
        return delegate.getParent();
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return delegate.getAutowireCapableBeanFactory();
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return delegate.containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return delegate.getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return delegate.getBeanDefinitionNames();
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return delegate.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return delegate.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return delegate.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return delegate.getBeansOfType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        return delegate.getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return delegate.getBeanNamesForAnnotation(annotationType);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return delegate.getBeansWithAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return delegate.findAnnotationOnBean(beanName, annotationType);
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return delegate.getParentBeanFactory();
    }

    @Override
    public boolean containsLocalBean(String name) {
        return delegate.containsLocalBean(name);
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return delegate.getResources(locationPattern);
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        delegate.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {
        delegate.publishEvent(event);
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return delegate.getMessage(code, args, defaultMessage, locale);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return delegate.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return delegate.getMessage(resolvable, locale);
    }

    @Override
    public Environment getEnvironment() {
        return delegate.getEnvironment();
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return delegate.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return delegate.getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return delegate.getBean(requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return delegate.getBean(name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return delegate.getBean(requiredType, args);
    }

    @Override
    public boolean containsBean(String name) {
        return delegate.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return delegate.isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return delegate.isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return delegate.isTypeMatch(name, typeToMatch);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return delegate.isTypeMatch(name, typeToMatch);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return delegate.getType(name);
    }

    @Override
    public String[] getAliases(String name) {
        return delegate.getAliases(name);
    }

    @Override
    public Resource getResource(String location) {
        return delegate.getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }
  }

