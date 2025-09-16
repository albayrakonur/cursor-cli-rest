package com.albayrak.agent_cli.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller (basit, örnek)
@RestController
@RequestMapping("/cursor-cli")
public class CursorCliController {
    private final ThreadPoolTaskExecutor executor;

    public CursorCliController(@Qualifier("commandExecutor") ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @PostMapping("/ask")
    public CompletableFuture<ResponseEntity<String>> askCursorCli(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        String cmd = "cursor-agent -p" + "\"" + prompt + "\"";
        if (prompt == null) {
            return CompletableFuture
                    .completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prompt is required"));
        }

        // asenkron çalıştır, hemen future dön
        return CompletableFuture.supplyAsync(() -> {
            try {
                return runCommandWithTimeout(cmd, 30); // example 30 seconds timeout
            } catch (Exception e) {
                return "Hata: " + e.getMessage();
            }
        }, executor).thenApply(output -> ResponseEntity.ok(output));
    }

    private String runCommandWithTimeout(String command, long timeoutSeconds) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        // okuma için ayrı thread kullan (blocking IO'yu engellemek için)
        StringWriter sw = new StringWriter();
        Thread reader = new Thread(() -> {
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) {
                    sw.append(line).append("\n");
                }
            } catch (IOException ignored) {
            }
        }, "process-reader");
        reader.start();

        boolean finished = p.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            p.destroyForcibly();
            reader.join(2000);
            throw new TimeoutException("Process zaman aşımına uğradı ve sonlandırıldı.");
        }

        reader.join();
        int exit = p.exitValue();
        return "exit=" + exit + "\n" + sw.toString();
    }
}
