/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.rengine.common.util.spring.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.wl4g.infra.common.lang.ClassUtils2;

/**
 * @author Joe Grandja
 * @since 5.2
 */
final class ObjectToListStringConverter implements ConditionalGenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> convertibleTypes = new LinkedHashSet<>();
        convertibleTypes.add(new ConvertiblePair(Object.class, List.class));
        convertibleTypes.add(new ConvertiblePair(Object.class, Collection.class));
        return convertibleTypes;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.getElementTypeDescriptor() == null || targetType.getElementTypeDescriptor().getType().equals(String.class)
                || sourceType == null
                || ClassUtils2.isAssignable(sourceType.getType(), targetType.getElementTypeDescriptor().getType())) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        if (source instanceof Collection) {
            Collection<String> results = new ArrayList<>();
            for (Object object : ((Collection<?>) source)) {
                if (object != null) {
                    results.add(object.toString());
                }
            }
            return results;
        }
        return Collections.singletonList(source.toString());
    }

}
