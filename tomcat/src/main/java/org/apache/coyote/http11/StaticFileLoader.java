package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticFileLoader {
    private static final String PREFIX = "static";

    private File findFileWithUri(final String fileUri) {
        URL systemResource = ClassLoader.getSystemResource(PREFIX + fileUri);
        if (systemResource == null) {
            throw new IllegalArgumentException("정적 파일을 찾지 못했습니다.");
        }

        return new File(systemResource.getFile());
    }

    public byte[] readAllFileWithUri(final String fileUri) throws IOException {
        return Files.readAllBytes(findFileWithUri(fileUri).toPath());
    }
}
