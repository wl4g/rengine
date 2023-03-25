/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
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
package com.wl4g.rengine.service.util;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToSet;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.Collectors2.toLinkedHashSet;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.endsWithAny;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.startsWithAny;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.graalvm.polyglot.HostAccess;
import org.junit.Test;

import com.wl4g.infra.common.reflect.ReflectionUtils3;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.sys.UploadObject;
import com.wl4g.rengine.service.util.RuleScriptParser.ArgInfo;
import com.wl4g.rengine.service.util.RuleScriptParser.MethodInfo;
import com.wl4g.rengine.service.util.RuleScriptParser.ScriptASTInfo;
import com.wl4g.rengine.service.util.RuleScriptParser.ScriptInfo;
import com.wl4g.rengine.service.util.RuleScriptParser.TypeInfo;

import lombok.AllArgsConstructor;

/**
 * {@link RuleScriptParserTests}
 * 
 * @author James Wong
 * @version 2023-02-14
 * @since v1.0.0
 */
public class RuleScriptParserTests {

    @Test
    public void testParse() {
        final List<ScriptInfo> scripts = new ArrayList<>();

        // @formatter:off
        final String script0 = "'use strict';\n"
                + "var CommonUtil = function() {\n"
                + "    this.printSafeWarn = function(title, msg){\n"
                + "        if(arguments.length <= 1) { msg = title; title = \"SECURITY WARNING!\"; }\n"
                + "        console.log(\"%c\" + title, \"font-size:50px;color:red;-webkit-text-fill-color:red;-webkit-text-stroke:1px black;\");\n"
                + "        if(!CommonUtil.isEmpty(msg)){\n"
                + "            console.log(\"%c\" + msg,\"font-size:14px;color:#303d65;\");\n"
                + "        }\n"
                + "    };\n"
                + "    this.getEleValue = function(name, obj){\n"
                + "        return getEleValue(name, obj, true);\n"
                + "    };\n"
                + "    this.getEleValue = function(name, obj, assertion){\n"
                + "        if(!assertion) {\n"
                + "            return $(obj).val();\n"
                + "        }\n"
                + "        return CommonUtil.checkEmpty(name, $(obj).val());\n"
                + "    }\n"
                + "};";
        // @formatter:on
        scripts.add(new ScriptInfo(UploadObject.builder().filename("test0.js").build(), script0.getBytes()));

        // @formatter:off
        final String script1 = "'use strict';\n"
                + "var printSafeWarn = function(title, msg){\n"
                + "    if(arguments.length <= 1) { msg = title; title = \"SECURITY WARNING!\"; }\n"
                + "    console.log(\"%c\" + title, \"font-size:50px;color:red;-webkit-text-fill-color:red;-webkit-text-stroke:1px black;\");\n"
                + "    if(!CommonUtil.isEmpty(msg)){\n"
                + "        console.log(\"%c\" + msg,\"font-size:14px;color:#303d65;\");\n"
                + "    }\n"
                + "};\n"
                + "var getEleValue = function(name, obj){\n"
                + "    return getEleValue(name, obj, true);\n"
                + "};\n"
                + "var getEleValue = function(name, obj, assertion){\n"
                + "    if(!assertion) {\n"
                + "        return $(obj).val();\n"
                + "    }\n"
                + "    return CommonUtil.checkEmpty(name, $(obj).val());\n"
                + "};";
        // @formatter:on
        scripts.add(new ScriptInfo(UploadObject.builder().filename("test1.js").build(), script1.getBytes()));

        // @formatter:off
        final String script2 = "class MyCollege {\n"
                + "    constructor() {\n"
                + "        this.name = \"defaultcollege\";\n"
                + "        this.students = [];\n"
                + "    }\n"
                + "    setName(name) {\n"
                + "        this.name = name;\n"
                + "    }\n"
                + "    getName() {\n"
                + "        return this.name;\n"
                + "    }\n"
                + "    addStudent(student) {\n"
                + "        this.students.push(student);\n"
                + "    }\n"
                + "    getStudents() {\n"
                + "        return this.students;\n"
                + "    }\n"
                + "    toString() {\n"
                + "        return this.name + this.students;\n"
                + "    }\n"
                + "}";
        // @formatter:on
        scripts.add(new ScriptInfo(UploadObject.builder().filename("test2.js").build(), script2.getBytes()));

        final ScriptASTInfo result = RuleScriptParser.parse(1010101L, 1L, RuleEngine.JS, scripts);
        System.out.println(toJSONString(result, true));
        assert nonNull(result);

    }

    @SuppressWarnings("deprecation")
    @Test
    public void generateSystemWithJsSDKAst() {
        final String classDirs = USER_DIR + "/../executor/target/classes";
        final String classPackages = "com.wl4g.rengine.executor.execution.sdk";
        final boolean onlyPublicMethods = true;
        final boolean includeLombokTypeMethods = false;

        final Set<URL> findUrls = safeArrayToSet(split(classDirs, ",")).stream().map(baseDir -> {
            try {
                return new File(replace(baseDir, "/", File.separator)).toURL();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }).collect(toSet());
        out.println("finding of URLs: " + findUrls);

        final Set<String> findClassPackages = safeArrayToSet(split(classPackages, ",")).stream().collect(toSet());
        out.println("finding of classPackages: " + findClassPackages);

        final Collection<Class<?>> classes = ReflectionUtils3.findClassesAll(findClassPackages, findUrls);
        final String classesString = classes.stream().map(cls -> cls.getName()).collect(joining("\n"));
        out.println("\n----- Found All built SDK classes -----\n\n" + classesString);

        final List<TypeInfo> items = generateTypeInfos(onlyPublicMethods, includeLombokTypeMethods, classes);
        out.println("\n\n----- Generated determined script AST -----\n\n");
        out.println(toJSONString(items, true));
    }

    public static List<TypeInfo> generateTypeInfos(
            final boolean onlyPublicMethods,
            final boolean includeLombokTypeMethods,
            final Collection<Class<?>> classes) {
        final var ignoreClassPredicate = new IgnoreClassPredicate();
        final var publicConstructorPredicate = new PublicConstructorPredicate(onlyPublicMethods);
        final var publicMethodPredicate = new PublicMethodPredicate(onlyPublicMethods);
        final var lombokTypePredicate = new LombokTypePredicate(includeLombokTypeMethods);
        final var lombokConstructorPredicate = new LombokConstructorPredicate(includeLombokTypeMethods);
        final var lombokMethodPredicate = new LombokMethodPredicate(includeLombokTypeMethods);
        final var exportMethodPredicate = new ExportAnnonotedMethodPredicate();

        return safeList(classes).stream().filter(ignoreClassPredicate).filter(lombokTypePredicate).map(cls -> {
            if (cls.toString().toLowerCase().contains("hashing")) {
                System.out.println(cls);
            }

            final Set<MethodInfo> allItem = new LinkedHashSet<>(classes.size());
            try {
                final Set<MethodInfo> constructorMethods = safeArrayToList(cls.getDeclaredConstructors()).stream()
                        .filter(publicConstructorPredicate)
                        .filter(lombokConstructorPredicate)
                        .filter(exportMethodPredicate)
                        .map(m -> {
                            try {
                                return MethodInfo.builder()
                                        .modifier("")
                                        .name(cls.getSimpleName())
                                        .args(resolveArgs(m.getParameterTypes()))
                                        .returnType(transfromToJsType(cls))
                                        .build();
                            } catch (Throwable e) {
                                err.println(format("[WARNING] Unable to load constructor methods of %s#%s. reason: %s",
                                        cls.getName(), m.getName(), e.getMessage()));
                                return null;
                            }
                        })
                        .filter(m -> nonNull(m))
                        .collect(toLinkedHashSet());
                allItem.addAll(constructorMethods);
            } catch (Throwable e) {
                err.println(
                        format("[WARNING] Unable to load constructor methods of %s. reason: %s", cls.getName(), e.getMessage()));
            }
            try {
                final Set<MethodInfo> memberMethods = safeArrayToList(cls.getDeclaredMethods()).stream()
                        .filter(m -> !Modifier.isStatic(m.getModifiers()))
                        .filter(publicMethodPredicate)
                        .filter(lombokMethodPredicate)
                        .filter(exportMethodPredicate)
                        .map(m -> {
                            try {
                                return MethodInfo.builder()
                                        .modifier("")
                                        .name(m.getName())
                                        .args(resolveArgs(m.getParameterTypes()))
                                        .returnType(transfromToJsType(m.getReturnType()))
                                        .build();
                            } catch (Throwable e) {
                                err.println(format("[WARNING] Unable to load member methods of %s#%s. reason: %s", cls.getName(),
                                        m.getName(), e.getMessage()));
                                return null;
                            }
                        })
                        .filter(m -> nonNull(m))
                        .collect(toLinkedHashSet());
                allItem.addAll(memberMethods);
            } catch (Throwable e) {
                err.println(format("[WARNING] Unable to load member methods of %s. reason: %s", cls.getName(), e.getMessage()));
            }
            try {
                final Set<MethodInfo> staticMethods = safeArrayToList(cls.getDeclaredMethods()).stream()
                        .filter(m -> Modifier.isStatic(m.getModifiers()))
                        .filter(publicMethodPredicate)
                        .filter(lombokMethodPredicate)
                        .filter(exportMethodPredicate)
                        .map(m -> {
                            try {
                                return MethodInfo.builder()
                                        .modifier("")
                                        .name(m.getName())
                                        .args(resolveArgs(m.getParameterTypes()))
                                        .returnType(transfromToJsType(m.getReturnType()))
                                        .build();
                            } catch (Throwable e) {
                                err.println(format("[WARNING] Unable to load static methods of %s#%s. reason: %s", cls.getName(),
                                        m.getName(), e.getMessage()));
                                return null;
                            }
                        })
                        .filter(m -> nonNull(m))
                        .collect(toLinkedHashSet());
                allItem.addAll(staticMethods);
            } catch (Throwable e) {
                err.println(format("[WARNING] Unable to load static methods of %s. reason: %s", cls.getName(), e.getMessage()));
            }

            return TypeInfo.builder().name(cls.getSimpleName()).modifier("").extension(".js").methods(allItem).build();
        }).collect(toList());
    }

    private static List<ArgInfo> resolveArgs(final Class<?>[] parameterTypes) {
        return safeArrayToList(parameterTypes).stream()
                .map(p -> ArgInfo.builder().name("").type(transfromToJsType(p)).build())
                .collect(toList());
    }

    private static String transfromToJsType(final Class<?> javaType) {
        if (String.class.isAssignableFrom(javaType)) {
            return "string";
        } else if (Integer.class.isAssignableFrom(javaType)) {
            return "int";
        } else if (Long.class.isAssignableFrom(javaType)) {
            return "int";
        } else if (Float.class.isAssignableFrom(javaType)) {
            return "float";
        } else if (Double.class.isAssignableFrom(javaType)) {
            return "float";
        } else if (javaType.isArray()) {
            return "array";
        } else if (Map.class.isAssignableFrom(javaType)) {
            return "map";
        }
        final String simpleName = javaType.getSimpleName();
        return simpleName.substring(Math.max(simpleName.lastIndexOf("."), 0));
    }

    @AllArgsConstructor
    public static class IgnoreClassPredicate implements Predicate<Class<?>> {
        @Override
        public boolean test(Class<?> c) {
            return !equalsAnyIgnoreCase(c.getSimpleName(), "package-info", "module-info");
        }
    }

    @AllArgsConstructor
    public static class ExportAnnonotedMethodPredicate implements Predicate<AnnotatedElement> {
        @Override
        public boolean test(AnnotatedElement ae) {
            final var export = ae.getAnnotation(HostAccess.Export.class);
            return nonNull(export);
        }
    }

    @AllArgsConstructor
    public static class PublicConstructorPredicate implements Predicate<Constructor<?>> {
        final boolean onlyPublicMethods;

        @Override
        public boolean test(Constructor<?> c) {
            if (Modifier.isPublic(c.getModifiers())) {
                return onlyPublicMethods;
            }
            return false;
        }
    }

    @AllArgsConstructor
    public static class PublicMethodPredicate implements Predicate<Method> {
        final boolean onlyPublicMethods;

        @Override
        public boolean test(Method m) {
            if (Modifier.isPublic(m.getModifiers())) {
                return onlyPublicMethods;
            }
            return false;
        }
    }

    @AllArgsConstructor
    public static class LombokTypePredicate implements Predicate<Class<?>> {
        final boolean includeLombokTypeMethods;

        // for example:
        // MyService(MyServiceBuilder)
        // MyService.$default$myfield(),
        // MyService.builder(),
        // MyService$MyServiceBuilder.build(),
        // MyService$MyServiceBuilder.access$4000(MyServiceBuilder),
        // MyService$MyServiceBuilderImpl,
        @Override
        public boolean test(Class<?> cls) {
            final String className = cls.getSimpleName();
            // Check the inner class and specification suffix.
            if (cls.isMemberClass() && endsWithAny(className, "Builder", "BuilderImpl")) {
                // final List<String> parts = safeArrayToSet(split(className,
                // "$"));
                // if (parts.stream().filter(p -> "$".equals(p)).count() >= 1) {
                // return true;
                // }
                return includeLombokTypeMethods;
            }
            return true;
        }
    }

    @AllArgsConstructor
    public static class LombokConstructorPredicate implements Predicate<Constructor<?>> {
        final boolean includeLombokTypeMethods;

        // for example:
        // MyService(MyServiceBuilder)
        // MyService.$default$myfield(),
        // MyService.builder(),
        // MyService$MyServiceBuilder.build(),
        // MyService$MyServiceBuilder.access$4000(MyServiceBuilder),
        // MyService$MyServiceBuilderImpl,
        @Override
        public boolean test(Constructor<?> c) {
            final var cls = c.getDeclaringClass();
            final var className = cls.getSimpleName();
            final var parameterTypes = c.getParameterTypes();
            // Check the inner class and specification suffix.
            if (cls.isMemberClass() && endsWithAny(className, "Builder", "BuilderImpl")
                    || (parameterTypes.length == 1 && endsWithAny(parameterTypes[0].getTypeName(), "Builder"))) {
                return includeLombokTypeMethods;
            }
            return true;
        }
    }

    @AllArgsConstructor
    public static class LombokMethodPredicate implements Predicate<Method> {
        final boolean includeLombokTypeMethods;

        // for example:
        // MyService(MyServiceBuilder)
        // MyService.$default$myfield(),
        // MyService.builder(),
        // MyService$MyServiceBuilder.build(),
        // MyService$MyServiceBuilder.access$4000(MyServiceBuilder),
        // MyService$MyServiceBuilderImpl,
        @Override
        public boolean test(Method m) {
            final Class<?> cls = m.getDeclaringClass();
            final String className = cls.getSimpleName();
            final String methodName = m.getName();
            // Check the inner class and specification suffix.
            if (cls.isMemberClass() && endsWithAny(className, "Builder", "BuilderImpl")
                    || startsWithAny(methodName, "builder", "access$", "self", "$default$")) {
                return includeLombokTypeMethods;
            }
            return true;
        }
    }

}
