package com.example.springboot.controller;

import com.example.springboot.config.Secrets;
import com.example.springboot.controller.exception.BarException;
import com.example.springboot.controller.exception.FooException;
import com.example.springboot.model.Meta;
import com.example.springboot.service.HelloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.function.Supplier;

@Log4j2
@Validated
@RestController
@RequestMapping(value = "/api/v1")
@ConditionalOnProperty(name = "app.controller.enable", havingValue = "true", matchIfMissing = true) // feature toggle
@Tag(name = "HelloController", description = "Hello world controller")
public class HelloController {
    private long roll = 0;
    private long pingCount = 0;

    private final Secrets secrets;
    private HelloService service;

    private int poolsize;
    private int maxpoolsize;
    ExecutorService executor;

    public HelloController(HelloService service, Secrets secrets, @Value("${app.controller.poolsize:2}") int poolsize,
                           @Value("${app.controller.maxpoolsize:1000}") int maxpoolsize) {
        this.secrets = secrets;
        this.service = service;
        this.poolsize = poolsize;
        this.maxpoolsize = maxpoolsize;

        log.info("HelloController initialized with poolsize: {} (max {})", poolsize, maxpoolsize);
        executor = new ThreadPoolExecutor(poolsize, maxpoolsize, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    @Operation(summary = "dummy endpoint", description = "returns pong every 3rd time otherwise throws exception")
    @ApiResponse(responseCode = "200", description = "no content")
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        log.info("ping");
        if (++pingCount % 3 == 0) {
            throw new ArithmeticException("bad pong");
        }
        return "{ \"status\": 200, \"message\": \"pong\" }";
    }

    @Operation(summary = "tests error response from server", description = "throws FooException or BarException depending on the roll")
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "even invocations"),
            @ApiResponse(responseCode = "418", description = "odd invocations")})
    @RequestMapping(path = "/roll/dice", method = RequestMethod.GET)
    public void rollDice() throws FooException, BarException {
        log.info("GET  rolling dice...");
        if (roll++ % 2 == 0)
            throw new FooException(roll);
        throw new BarException(roll);
    }

    // curl -X POST -i localhost:8080/api/v1/objects/1234 --data '{ "what":
    // "onetwofreetfour" }' -H
    // "Content-Type: application/json"
    @Operation(summary = "add new object", description = "adds a new object to internal ephemeral store")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "add blob"),
            @ApiResponse(responseCode = "400", description = "duplicate"),
            @ApiResponse(responseCode = "500", description = "internal error"),
            @ApiResponse(responseCode = "501", description = "when objectId == 666")})
    @RequestMapping(path = "/objects/{objectId}", method = RequestMethod.POST, consumes = {
            MediaType.APPLICATION_JSON_VALUE})
    public Meta postObject(@PathVariable("objectId") Long objectId,
                           @Valid @RequestBody String blob,
                           @Valid @RequestHeader(name = "Content-Type", required = false) String content_type) {
        log.info("POST /api/objects -> {}  content-type {}", objectId, content_type);
        final var m = new Meta(objectId, blob);
        final int ret = service.addObject(m.getObjectId(), m.getBlob());
        if (ret < 0) {
            log.warn("not impl -> " + objectId);
            throw new UnsupportedOperationException("not implemented");
        }
        if (ret > 0) {
            log.info("bad request -> " + objectId);
            throw new IllegalStateException("bad request");
        }

        log.info("ok -> " + objectId);
        return m;
    }

    @RequestMapping(value = "/objects", method = RequestMethod.GET)
    public ResponseEntity<Object> getObjects() {
        log.info("GET  /objects");
        try {
            final List<Meta>  all = service.getAll();
            log.debug("-> items: {}", () -> all.size());

            return new ResponseEntity<Object>(all, HttpStatus.OK);
        } catch (Exception ex) {
            // can throw or return - the ResponseExceptionHandler can map
            return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/objects/{objectId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getObject(
            @RequestParam(name = "fail_404", defaultValue = "no", required = false) String fail_404,
            @PathVariable Long objectId) {
        log.info("GET  /api/objects -> " + objectId);

        try {
            final Meta m = service.getById(objectId);
            if (m != null) {
                log.debug("Meta found with objectId={} blob={}", +objectId, (Supplier<String>)() -> m.getBlob());
                return new ResponseEntity<Object>(m, HttpStatus.OK);
            }
        } catch (Exception ex) {
            log.error("Error search for " + objectId, ex, ex.getMessage());
            return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String err404 = "{ \"response\": \"Meta + " + objectId + " not found\" }";
        if (fail_404.equals("no")) {
            return new ResponseEntity<Object>(err404, HttpStatus.NOT_FOUND);
        }

        // use the responseException handler that has annotations afor this class to do
        // specific
        // error
        // response
        // the exception handler can be used to consolidate all handling based on thrown
        // exception
        // types
        throw new IndexOutOfBoundsException("Meta not found");
    }

    @RequestMapping(value = "/exception/notimplemented", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    /* no exception is thrown, its a straight not impl back to client */
    @Operation(summary = "not implemented", description = "not implemented, always returns 501")
    public void exceptionNotImpl() {
    }

    static String doWork(String name) {
        try {
            // simulate variable delay
            Thread.sleep(name.equals("Task1") ? 1000 : 200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Completed " + name;
    }

    @RequestMapping(value = "/async", method = RequestMethod.GET)
    public List<String> async(@RequestParam(name = "tasks", defaultValue = "3", required = false)
                              @Min(value = 1, message = "tasks must be 1 or more")
                              @Max(value = 100, message = "tasks can not exceed 100") Integer tasks) {
        log.info("requested {} async tasks", () -> tasks);

        final var taskNames = IntStream.range(0, tasks)
                .mapToObj(i -> "Task" + i)
                .toList();

        List<CompletableFuture<String>> futures = taskNames
                .stream()
                .map(taskName -> {
                    while (true) {
                        try {
                            return CompletableFuture
                                    .supplyAsync(() -> doWork(taskName), executor)
                                    .completeOnTimeout("Timeout from " + taskName, 400, TimeUnit.MILLISECONDS)
                                    .exceptionally(ex -> {
                                        log.error("Error in " + taskName + ": " + ex.getMessage());
                                        return "Fallback from " + taskName;
                                    });
                        } catch (OutOfMemoryError oom) {
                            if (oom.getMessage() != null && oom.getMessage().contains("unable to create new native thread")) {
                                log.warn("Thread creation failed for " + taskName + ", backing off and retrying...");
                                try {
                                    Thread.sleep(500); // backoff before retrying
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    throw new RuntimeException("Interrupted during backoff", ie);
                                }
                            } else {
                                throw oom; // rethrow other OOM errors
                            }
                        }
                    }
                }).toList();

        log.info("Waiting for tasks to complete...");
        List<String> results = futures.stream().map(CompletableFuture::join).toList();

        results.forEach(log::info);

        executor.shutdown();
        return results;
    }

    @GetMapping(value = "/secrets")
    public Object secrets() {
        log.info("secrets -> {}", secrets.getLastUpdated());
        // cheap out messing with the proxing of the Secrets class
        return Map.ofEntries(
                Map.entry("dbUsername", secrets.getDbUsername()),
                Map.entry("dbPassword", secrets.getDbPassword()),
                Map.entry("lastUpdated",
                        DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(secrets.getLastUpdated())))
        );
    }
}
