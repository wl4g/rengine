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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeSet;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.sys.UploadObject;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link RuleScriptParser}
 * 
 * @author James Wong
 * @date 2023-02-20
 * @since v1.0.0
 */
@CustomLog
public abstract class RuleScriptParser {

    public static ScriptASTInfo parse(
            final @NotNull Long scriptId,
            final @NotNull Long revision,
            final @NotNull RuleEngine engine,
            final List<ScriptInfo> depends) {
        notNullOf(scriptId, "scriptId");
        notNullOf(revision, "revision");
        notNullOf(engine, "engine");
        switch (engine) {
        case JS: // Resolve depends AST for JS.
            return parseForJS(scriptId, revision, depends);
        default:
            throw new UnsupportedOperationException(format("No supported parse rule script AST for %s -> %s", scriptId, engine));
        }
    }

    private static ScriptASTInfo parseForJS(
            final @NotNull Long scriptId,
            final @NotNull Long revision,
            final @NotNull List<ScriptInfo> depends) {
        return ScriptASTInfo.builder()
                .metadata(MetadataInfo.builder()
                        .lang(RuleEngine.JS.name())
                        .langFamily(RuleEngine.JS.name())
                        .revision(revision)
                        .build())
                .types(safeList(depends).stream().map(depend -> {
                    final UploadObject upload = depend.getMetadata();
                    final byte[] buf = depend.getScriptData();

                    // resolve members with graal.js.
                    try (Context graalContext = Context.newBuilder("js").allowAllAccess(false).build();) {
                        graalContext.eval(Source.newBuilder("js", new String(buf), upload.getFilename()).build());
                        final Value bindings = graalContext.getBindings("js");

                        final List<Value> members = safeSet(bindings.getMemberKeys()).stream()
                                .map(m -> bindings.getMember(m))
                                .collect(toList());

                        final List<FieldInfo> fields = members.stream()
                                .filter(m -> m.isMetaObject() && !m.canExecute())
                                .map(m -> FieldInfo.builder()
                                        .name(m.getMetaSimpleName())
                                        .modifier("default")
                                        .type("object")
                                        .build())
                                .collect(toList());

                        final List<FieldInfo> staticFields = members.stream()
                                .filter(m -> !m.isMetaObject() && !m.canExecute())
                                .map(m -> FieldInfo.builder()
                                        .name(m.getMetaSimpleName())
                                        .modifier("default")
                                        .type("object")
                                        .build())
                                .collect(toList());

                        final List<MethodInfo> methods = members.stream()
                                .filter(m -> m.isMetaObject() && m.canExecute())
                                .map(m -> MethodInfo.builder()
                                        .name(m.getMetaSimpleName())
                                        .modifier("default")
                                        // TODO The js method param name is
                                        // anonymous and can only be represented
                                        // by ellipsis for the time being.
                                        .args(singletonList(ArgInfo.builder().name("...").type("object").build()))
                                        .returnType("object")
                                        .build())
                                .collect(toList());

                        final List<MethodInfo> staticMethods = members.stream()
                                .filter(m -> !m.isMetaObject() && m.canExecute())
                                .map(m -> MethodInfo.builder()
                                        .name(m.getMetaSimpleName())
                                        .modifier("default")
                                        // TODO The js method param name is
                                        // anonymous and can only be represented
                                        // by ellipsis for the time being.
                                        .args(singletonList(ArgInfo.builder().name("...").type("object").build()))
                                        .returnType("object")
                                        .build())
                                .collect(toList());

                        return TypeInfo.builder()
                                .name(getFilenameWithoutExt(upload.getFilename()))
                                .modifier("default")
                                .extension(StringUtils2.getFilenameExtension(upload.getFilename()))
                                .fields(fields)
                                .staticFields(staticFields)
                                .methods(methods)
                                .staticMethods(staticMethods)
                                .build();
                    } catch (Throwable ex) {
                        final String errmsg = format("Unable to parse depend for %s. reason: %s", depend, ex.getMessage());
                        log.warn(errmsg);
                        // throw new IllegalStateException(errmsg);
                        return null;
                    }
                }).filter(t -> nonNull(t)).collect(toList()))
                .build();
    }

    private static String getFilenameWithoutExt(String filepath) {
        final var filename = StringUtils2.getFilename(filepath);
        return filename.substring(0, filename.lastIndexOf("."));
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScriptInfo {
        private UploadObject metadata;
        private byte[] scriptData;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class ScriptASTInfo {
        private MetadataInfo metadata;
        private Collection<TypeInfo> types;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class MetadataInfo {
        private String lang;
        private String langFamily;
        private Long revision;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class TypeInfo {
        private String name;
        private String modifier;
        private String extension;
        private Collection<FieldInfo> fields;
        private Collection<FieldInfo> staticFields;
        private Collection<MethodInfo> methods;
        private Collection<MethodInfo> staticMethods;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class FieldInfo {
        private String name;
        private String modifier;
        private String type;

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FieldInfo other = (FieldInfo) obj;
            return Objects.equals(name, other.name) && Objects.equals(type, other.type);
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class MethodInfo {
        private String name;
        private String modifier;
        private String returnType;
        private Collection<ArgInfo> args;

        @Override
        public int hashCode() {
            return Objects.hash(name, args.toString());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MethodInfo other = (MethodInfo) obj;
            return Objects.equals(name, other.name) && Objects.equals(args.toString(), other.args.toString());
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class ArgInfo {
        private String name;
        private String type;

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ArgInfo other = (ArgInfo) obj;
            return Objects.equals(name, other.name) && Objects.equals(type, other.type);
        }
    }

}
