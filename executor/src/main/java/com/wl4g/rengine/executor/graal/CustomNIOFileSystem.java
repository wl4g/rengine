package com.wl4g.rengine.executor.graal;

import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.graalvm.polyglot.io.FileSystem;

/**
 * {@link CustomNIOFileSystem}
 * 
 * @author James Wong
 * @version 2023-04-01
 * @since v1.0.0
 * @see {@link com.oracle.truffle.polyglot.FileSystems.NIOFileSystem}
 */
public class CustomNIOFileSystem implements FileSystem {

    private final FileSystemProvider hostfs;
    private final boolean explicitUserDir;
    private volatile Path userDir;
    private volatile Path tmpDir;

    public CustomNIOFileSystem(final FileSystemProvider fileSystemProvider) {
        this(fileSystemProvider, false, null);
    }

    public CustomNIOFileSystem(final FileSystemProvider fileSystemProvider, final Path userDir) {
        this(fileSystemProvider, true, userDir);
    }

    private CustomNIOFileSystem(final FileSystemProvider fileSystemProvider, final boolean explicitUserDir,
            final Path userDir) {
        Objects.requireNonNull(fileSystemProvider, "FileSystemProvider must be non null.");
        this.hostfs = fileSystemProvider;
        this.explicitUserDir = explicitUserDir;
        this.userDir = userDir;
    }

    @Override
    public Path parsePath(URI uri) {
        try {
            return hostfs.getPath(uri);
        } catch (IllegalArgumentException | FileSystemNotFoundException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public Path parsePath(String path) {
        if (!"file".equals(hostfs.getScheme())) {
            throw new IllegalStateException("The ParsePath(String path) should be called only for file scheme.");
        }
        return Paths.get(path);
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
        if (isFollowLinks(linkOptions)) {
            hostfs.checkAccess(resolveRelative(path), modes.toArray(new AccessMode[modes.size()]));
        } else if (modes.isEmpty()) {
            hostfs.readAttributes(path, "isRegularFile", LinkOption.NOFOLLOW_LINKS);
        } else {
            throw new UnsupportedOperationException(
                    "CheckAccess for NIO Provider is unsupported with non empty AccessMode and NOFOLLOW_LINKS.");
        }
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        hostfs.createDirectory(resolveRelative(dir), attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        hostfs.delete(resolveRelative(path));
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        hostfs.copy(resolveRelative(source), resolveRelative(target), options);
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        hostfs.move(resolveRelative(source), resolveRelative(target), options);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
            throws IOException {
        final Path resolved = resolveRelative(path);
        try {
            return hostfs.newFileChannel(resolved, options, attrs);
        } catch (UnsupportedOperationException uoe) {
            return hostfs.newByteChannel(resolved, options, attrs);
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        Path cwd = userDir;
        Path resolvedPath;
        boolean relativize;
        if (!dir.isAbsolute() && cwd != null) {
            resolvedPath = cwd.resolve(dir);
            relativize = true;
        } else {
            resolvedPath = dir;
            relativize = false;
        }
        DirectoryStream<Path> result = hostfs.newDirectoryStream(resolvedPath, filter);
        if (relativize) {
            result = new RelativizeDirectoryStream(cwd, result);
        }
        return result;
    }

    @Override
    public void createLink(Path link, Path existing) throws IOException {
        hostfs.createLink(resolveRelative(link), resolveRelative(existing));
    }

    @Override
    public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
        hostfs.createSymbolicLink(resolveRelative(link), target, attrs);
    }

    @Override
    public Path readSymbolicLink(Path link) throws IOException {
        return hostfs.readSymbolicLink(resolveRelative(link));
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return hostfs.readAttributes(resolveRelative(path), attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        hostfs.setAttribute(resolveRelative(path), attribute, value, options);
    }

    @Override
    public Path toAbsolutePath(Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        Path cwd = userDir;
        if (cwd == null) {
            if (explicitUserDir) { // Forbidden read of current working
                                   // directory
                throw new SecurityException("Access to user.dir is not allowed.");
            }
            return path.toAbsolutePath();
        } else {
            return cwd.resolve(path);
        }
    }

    @Override
    public void setCurrentWorkingDirectory(Path currentWorkingDirectory) {
        Objects.requireNonNull(currentWorkingDirectory, "Current working directory must be non null.");
        if (!currentWorkingDirectory.isAbsolute()) {
            throw new IllegalArgumentException("Current working directory must be absolute.");
        }
        boolean isDirectory;
        try {
            isDirectory = Boolean.TRUE.equals(hostfs.readAttributes(currentWorkingDirectory, "isDirectory").get("isDirectory"));
        } catch (IOException ioe) {
            isDirectory = false;
        }
        if (!isDirectory) {
            throw new IllegalArgumentException("Current working directory must be directory.");
        }
        if (explicitUserDir && userDir == null) { // Forbidden set of current
                                                  // working directory
            throw new SecurityException("Modification of current working directory is not allowed.");
        }
        userDir = currentWorkingDirectory;
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        final Path resolvedPath = resolveRelative(path);
        return resolvedPath.toRealPath(linkOptions);
    }

    @Override
    public Path getTempDirectory() {
        Path result = tmpDir;
        if (result == null) {
            if (JAVA_IO_TMPDIR == null) {
                throw new IllegalStateException("The java.io.tmpdir is not set.");
            }
            result = parsePath(JAVA_IO_TMPDIR);
            tmpDir = result;
        }
        return result;
    }

    @Override
    public boolean isSameFile(Path path1, Path path2, LinkOption... options) throws IOException {
        if (isFollowLinks(options)) {
            Path absolutePath1 = resolveRelative(path1);
            Path absolutePath2 = resolveRelative(path2);
            return hostfs.isSameFile(absolutePath1, absolutePath2);
        } else {
            // The FileSystemProvider.isSameFile always resolves symlinks
            // we need to use the default implementation comparing the canonical
            // paths
            if (toAbsolutePath(path1).equals(toAbsolutePath(path2))) {
                return true;
            }
            return toRealPath(path1, options).equals(toRealPath(path2, options));
        }
    }

    private Path resolveRelative(Path path) {
        return !path.isAbsolute() && userDir != null ? toAbsolutePath(path) : path;
    }

    private static boolean isFollowLinks(final LinkOption... linkOptions) {
        for (LinkOption lo : linkOptions) {
            if (Objects.requireNonNull(lo) == LinkOption.NOFOLLOW_LINKS) {
                return false;
            }
        }
        return true;
    }

    private static final class RelativizeDirectoryStream implements DirectoryStream<Path> {

        private final Path folder;
        private final DirectoryStream<? extends Path> delegateDirectoryStream;

        RelativizeDirectoryStream(Path folder, DirectoryStream<? extends Path> delegateDirectoryStream) {
            this.folder = folder;
            this.delegateDirectoryStream = delegateDirectoryStream;
        }

        @Override
        public Iterator<Path> iterator() {
            return new RelativizeIterator(folder, delegateDirectoryStream.iterator());
        }

        @Override
        public void close() throws IOException {
            delegateDirectoryStream.close();
        }

        private static final class RelativizeIterator implements Iterator<Path> {

            private final Path folder;
            private final Iterator<? extends Path> delegateIterator;

            RelativizeIterator(Path folder, Iterator<? extends Path> delegateIterator) {
                this.folder = folder;
                this.delegateIterator = delegateIterator;
            }

            @Override
            public boolean hasNext() {
                return delegateIterator.hasNext();
            }

            @Override
            public Path next() {
                return folder.relativize(delegateIterator.next());
            }
        }
    }
}