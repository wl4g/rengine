/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.wl4g.rengine.executor.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;

/**
 * {@link RootedPath}
 * 
 * @author James Wong
 * @date 2023-04-01
 * @since v1.0.0
 * @see {@link org.apache.sshd.common.file.root.RootedPath}
 */
public class RootedPath extends BasePath<RootedPath, RootedFileSystem> {

    public RootedPath(RootedFileSystem fileSystem, String root, List<String> names) {
        super(fileSystem, root, names);
    }

    @Override
    public File toFile() {
        RootedPath absolute = toAbsolutePath();
        RootedFileSystem fs = getFileSystem();
        Path path = fs.getRoot();
        for (String n : absolute.names) {
            path = path.resolve(n);
        }
        return path.toFile();
    }

    @Override
    public RootedPath toRealPath(LinkOption... options) throws IOException {
        RootedPath absolute = toAbsolutePath();
        FileSystem fs = getFileSystem();
        FileSystemProvider provider = fs.provider();
        provider.checkAccess(absolute);
        return absolute;
    }

}
