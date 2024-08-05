package org.koishi.launcher.h2co3.core.utils.task;

import static org.koishi.launcher.h2co3.core.utils.DigestUtils.getDigest;
import static java.util.Objects.requireNonNull;

import org.koishi.launcher.h2co3.core.utils.Hex;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.io.ChecksumMismatchException;
import org.koishi.launcher.h2co3.core.utils.io.CompressingUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public class FileDownloadTask extends FetchTask<Void> {

    public static final IntegrityCheckHandler ZIP_INTEGRITY_CHECK_HANDLER = (filePath, destinationPath) -> {
        String ext = FileTools.getExtension(destinationPath).toLowerCase(Locale.ROOT);
        if (ext.equals("zip") || ext.equals("jar")) {
            try (FileSystem ignored = CompressingUtils.createReadOnlyZipFileSystem(filePath)) {
                // test for zip format
            }
        }
    };
    private final File file;
    private final IntegrityCheck integrityCheck;
    private final ArrayList<IntegrityCheckHandler> integrityCheckHandlers = new ArrayList<>();
    private Path candidate;

    public FileDownloadTask(URL url, File file) {
        this(url, file, null);
    }

    public FileDownloadTask(URL url, File file, IntegrityCheck integrityCheck) {
        this(Collections.singletonList(url), file, integrityCheck);
    }

    public FileDownloadTask(URL url, File file, IntegrityCheck integrityCheck, int retry) {
        this(Collections.singletonList(url), file, integrityCheck, retry);
    }

    public FileDownloadTask(List<URL> urls, File file) {
        this(urls, file, null);
    }

    public FileDownloadTask(List<URL> urls, File file, IntegrityCheck integrityCheck) {
        this(urls, file, integrityCheck, 3);
    }

    public FileDownloadTask(List<URL> urls, File file, IntegrityCheck integrityCheck, int retry) {
        super(urls, retry);
        this.file = file;
        this.integrityCheck = integrityCheck;

        setName(file.getName());
    }

    public File getFile() {
        return file;
    }

    public FileDownloadTask setCandidate(Path candidate) {
        this.candidate = candidate;
        return this;
    }

    public void addIntegrityCheckHandler(IntegrityCheckHandler handler) {
        integrityCheckHandlers.add(Objects.requireNonNull(handler));
    }

    @Override
    protected EnumCheckETag shouldCheckETag() {
        if (integrityCheck != null && caching) {
            Optional<Path> cache = repository.checkExistentFile(candidate, integrityCheck.algorithm(), integrityCheck.checksum());
            if (cache.isPresent()) {
                try {
                    FileTools.copyFile(cache.get().toFile(), file);
                    Logging.LOG.log(Level.FINER, "Successfully verified file " + file + " from " + urls.get(0));
                    return EnumCheckETag.CACHED;
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Failed to copy cache files", e);
                }
            }
            return EnumCheckETag.NOT_CHECK_E_TAG;
        } else {
            return EnumCheckETag.CHECK_E_TAG;
        }
    }

    @Override
    protected void beforeDownload(URL url) {
        Logging.LOG.log(Level.FINER, "Downloading " + url + " to " + file);
    }

    @Override
    protected void useCachedResult(Path cache) throws IOException {
        FileTools.copyFile(cache.toFile(), file);
    }

    @Override
    protected Context getContext(URLConnection conn, boolean checkETag) throws IOException {
        Path temp = Files.createTempFile(null, null);
        RandomAccessFile rFile = new RandomAccessFile(temp.toFile(), "rw");
        MessageDigest digest = integrityCheck == null ? null : integrityCheck.createDigest();

        return new Context() {
            @Override
            public void write(byte[] buffer, int offset, int len) throws IOException {
                if (digest != null) {
                    digest.update(buffer, offset, len);
                }

                rFile.write(buffer, offset, len);
            }

            @Override
            public void close() throws IOException {
                try {
                    rFile.close();
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Failed to close file: " + rFile, e);
                }

                if (!isSuccess()) {
                    try {
                        Files.delete(temp);
                    } catch (IOException e) {
                        Logging.LOG.log(Level.WARNING, "Failed to delete file: " + rFile, e);
                    }
                    return;
                }

                for (IntegrityCheckHandler handler : integrityCheckHandlers) {
                    handler.checkIntegrity(temp, file.toPath());
                }

                Files.deleteIfExists(file.toPath());
                if (!FileTools.makeDirectory(requireNonNull(file.getAbsoluteFile().getParentFile())))
                    throw new IOException("Unable to make parent directory " + file);

                try {
                    FileTools.moveFile(temp.toFile(), file);
                } catch (Exception e) {
                    throw new IOException("Unable to move temp file from " + temp + " to " + file, e);
                }

                if (integrityCheck != null) {
                    integrityCheck.performCheck(digest);
                }

                if (caching && integrityCheck != null) {
                    try {
                        repository.cacheFile(file.toPath(), integrityCheck.algorithm(), integrityCheck.checksum());
                    } catch (IOException e) {
                        Logging.LOG.log(Level.WARNING, "Failed to cache file", e);
                    }
                }

                if (checkETag) {
                    repository.cacheRemoteFile(file.toPath(), conn);
                }
            }
        };
    }

    public interface IntegrityCheckHandler {
        void checkIntegrity(Path filePath, Path destinationPath) throws IOException;
    }

    public record IntegrityCheck(String algorithm, String checksum) {
        public IntegrityCheck(String algorithm, String checksum) {
            this.algorithm = requireNonNull(algorithm);
            this.checksum = requireNonNull(checksum);
        }

        public static IntegrityCheck of(String algorithm, String checksum) {
            if (checksum == null) return null;
            else return new IntegrityCheck(algorithm, checksum);
        }

        public MessageDigest createDigest() {
            return getDigest(algorithm);
        }

        public void performCheck(MessageDigest digest) throws ChecksumMismatchException {
            String actualChecksum = Hex.encodeHex(digest.digest());
            if (!checksum.equalsIgnoreCase(actualChecksum)) {
                throw new ChecksumMismatchException(algorithm, checksum, actualChecksum);
            }
        }
    }
}