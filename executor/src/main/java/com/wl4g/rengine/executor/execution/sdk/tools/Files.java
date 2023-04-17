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
package com.wl4g.rengine.executor.execution.sdk.tools;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_SCRIPT_ROOTFS_DIR;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.infra.common.io.FileIOUtils.ReadTailFrame;
import com.wl4g.rengine.common.exception.RengineException;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * {@link Files}
 * 
 * @author James Wong
 * @date 2022-12-25
 * @since v1.0.0
 */
public class Files {
    public static final Files DEFAULT = new Files();

    private Files() {
    }

    public @HostAccess.Export List<FileInfo> listFiles(@NotBlank String path) {
        return listFiles(path, name -> true);
    }

    public @HostAccess.Export List<FileInfo> listFiles(@NotBlank String path, @NotNull Function<String, Boolean> filter) {
        hasTextOf(path, "path");
        notNullOf(filter, "filter");
        try {
            return safeArrayToList(new File(wrapChrootDir(path)).listFiles(f -> nonNull(f) && filter.apply(f.getName()))).stream()
                    .map(f -> FileInfo.builder()
                            .path(unwrapChrootDir(f))
                            .isDir(f.isDirectory())
                            .isFile(f.isFile())
                            .isHidden(f.isHidden())
                            .name(f.getName())
                            .build())
                    .collect(toList());
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to list files of '%s'", path), ex);
        }
    }

    public @HostAccess.Export void forceDelete(@NotBlank String path) {
        hasTextOf(path, "path");
        try {
            FileIOUtils.forceDelete(new File(wrapChrootDir(path)));
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to delete directory '%s'", path), ex);
        }
    }

    public @HostAccess.Export void createFile(@NotBlank String path) {
        hasTextOf(path, "path");
        try {
            FileIOUtils.ensureFile(new File(wrapChrootDir(path)));
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to create file '%s'", path), ex);
        }
    }

    public @HostAccess.Export void mkdirs(@NotBlank String path) {
        mkdirs(path, null);
    }

    public @HostAccess.Export void mkdirs(@NotBlank String path, @Nullable String childDir) {
        hasTextOf(path, "path");
        try {
            FileIOUtils.ensureDir(wrapChrootDir(path), childDir);
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to mkdir directory '%s'", path), ex);
        }
    }

    public @HostAccess.Export String readToString(@NotBlank String path) {
        hasTextOf(path, "path");
        try {
            return FileIOUtils.readFileToString(new File(wrapChrootDir(path)), UTF_8);
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to read file to string of '%s'", path), ex);
        }
    }

    public @HostAccess.Export byte[] readToByteArray(@NotBlank String path) {
        hasTextOf(path, "path");
        try {
            return FileIOUtils.readFileToByteArray(new File(wrapChrootDir(path)));
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to read file to byte array of '%s'", path), ex);
        }
    }

    public @HostAccess.Export List<String> readLines(@NotBlank String path) {
        hasTextOf(path, "path");
        try {
            return FileIOUtils.readLines(new File(wrapChrootDir(path)), UTF_8);
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to read file to string lines of '%s'", path), ex);
        }
    }

    public @HostAccess.Export ReadTailFrame seekReadLines(
            @NotBlank String path,
            @Min(-1) long startPos,
            @Min(0) int aboutLimit,
            @NotNull Function<String, Boolean> stopper) {
        hasTextOf(path, "path");
        isTrueOf(startPos >= -1, "startPos >= -1");
        isTrueOf(aboutLimit >= -1, "aboutLimit >= 0");
        notNullOf(stopper, "stopper");
        try {
            return FileIOUtils.seekReadLines(wrapChrootDir(path), startPos, aboutLimit, stopper);
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to seek read file to string lines of '%s'", path), ex);
        }
    }

    public @HostAccess.Export void writeFromString(@NotBlank String path, @Nullable String data) {
        writeFromString(path, data, false);
    }

    public @HostAccess.Export void writeFromString(@NotBlank String path, @Nullable String data, boolean append) {
        hasTextOf(path, "path");
        try {
            FileIOUtils.writeFile(new File(wrapChrootDir(path)), data, append);
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to write byte array to file '%s'", path), ex);
        }
    }

    public @HostAccess.Export void writeFromByteArray(@NotBlank String path, @Nullable byte[] data) {
        writeFromByteArray(path, data, false);
    }

    public @HostAccess.Export void writeFromByteArray(@NotBlank String path, @Nullable byte[] data, boolean append) {
        hasTextOf(path, "path");
        try {
            FileIOUtils.writeFile(new File(wrapChrootDir(path)), data, append);
        } catch (Throwable ex) {
            throw new RengineException(format("Failed to write byte array to file '%s'", path), ex);
        }
    }

    /**
     * Wrap sandbox file path with chroot dir.
     * 
     * @param path
     * @return
     */
    public static final String wrapChrootDir(@NotBlank String path) {
        hasTextOf(path, "path");
        return DEFAULT_EXECUTOR_SCRIPT_ROOTFS_DIR.concat("/").concat(path);
    }

    /**
     * Unwap sandbox file path with chroot dir.
     * 
     * @param file
     * @return
     */
    public static final String unwrapChrootDir(@NotNull File file) {
        notNullOf(file, "file");
        String filepath = file.getAbsolutePath();
        final int index = filepath.indexOf(DEFAULT_EXECUTOR_SCRIPT_ROOTFS_DIR);
        if (index >= 0) {
            filepath = filepath.substring(index + DEFAULT_EXECUTOR_SCRIPT_ROOTFS_DIR.length());
        }
        return filepath;
    }

    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class FileInfo {
        private String path;
        private String name;
        private boolean isDir;
        private boolean isFile;
        private boolean isHidden;

        public @HostAccess.Export String getPath() {
            return path;
        }

        public @HostAccess.Export String getName() {
            return name;
        }

        public @HostAccess.Export boolean isDir() {
            return isDir;
        }

        public @HostAccess.Export boolean isFile() {
            return isFile;
        }

        public @HostAccess.Export boolean isHidden() {
            return isHidden;
        }

        @Override
        public @HostAccess.Export String toString() {
            return "FileInfo [path=" + path + ", name=" + name + ", isDir=" + isDir + ", isFile=" + isFile + ", isHidden="
                    + isHidden + "]";
        }
    }

}
