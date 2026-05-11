package io.github.junhyeong9812.streamixexample;

import io.github.junhyeong9812.streamix.starter.annotation.EnableStreamix;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Streamix Example - Reference Spring Boot consumer for Streamix v3.
 *
 * <p>Activates the entire Streamix media-streaming stack with a single annotation:
 * REST API, dashboard UI (Cinema/Editorial Brutalist), file storage,
 * thumbnail generation, streaming session monitoring.</p>
 *
 * <h2>지원 파일 타입</h2>
 * <ul>
 *   <li>IMAGE - jpg, png, gif, webp</li>
 *   <li>VIDEO - mp4, webm, avi</li>
 *   <li>AUDIO - mp3, wav, flac</li>
 *   <li>DOCUMENT - pdf, doc, xlsx</li>
 *   <li>ARCHIVE - zip, rar, 7z</li>
 *   <li>OTHER - 기타 파일</li>
 * </ul>
 */
@SpringBootApplication
@EnableStreamix
public class StreamixExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamixExampleApplication.class, args);
    }
}
