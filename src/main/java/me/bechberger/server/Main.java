package me.bechberger.server;

import io.javalin.Javalin;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    // returns the only argument which is the port number
    public static int parseOptions(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ... <port>");
            System.exit(1);
        }
        return Integer.parseInt(args[0]);
    }

    public static void main(String[] args) throws IOException {
        int port = parseOptions(args);
        FileStorage storage = new FileStorage();
        try (Javalin lin = Javalin.create(conf -> {
                    conf.jetty.server(() ->
                            new Server(new QueuedThreadPool(4))
                    );
                })
                .exception(Exception.class, (e, ctx) -> {
                    ctx.status(500);
                    ctx.result("Error: " + e.getMessage());
                    e.printStackTrace();
                })
                .get("/register/{user}", ctx -> {
                    String user = ctx.pathParam("user");
                    storage.register(user);
                    ctx.result("registered");
                })
                .get("/store/{user}/{file}/{content}", ctx -> {
                    String user = ctx.pathParam("user");
                    String file = ctx.pathParam("file");
                    storage.store(user, file, ctx.pathParam("content"));
                    ctx.result("stored");
                })
                .get("/load/{user}/{file}", ctx -> {
                    String user = ctx.pathParam("user");
                    String file = ctx.pathParam("file");
                    ctx.result(storage.load(user, file));
                })
                .get("/delete/{user}/{file}", ctx -> {
                    String user = ctx.pathParam("user");
                    String file = ctx.pathParam("file");
                    storage.delete(user, file);
                    ctx.result("deleted");
                })) {
            lin.start(port);
            Thread.sleep(100000000);
        } catch (InterruptedException ignored) {
        }
    }
}

/**
 * File storage that stores all files
 * in a temporary directory with subdirectories
 * per user
 */
class FileStorage {

    private final Path tmpDir;

    public FileStorage() throws IOException {
        tmpDir = Files.createTempDirectory("tmp");
        tmpDir.toFile().deleteOnExit();
    }

    private Path userPath(String user) {
        return tmpDir.resolve(user);
    }

    private Path filePath(String user, String file) {
        return userPath(user).resolve(file);
    }

    public void register(String user) throws IOException {
        if (Files.exists(userPath(user))) {
            throw new IllegalArgumentException("User already exists");
        }
        Files.createDirectories(userPath(user));
    }

    public void store(String user, String file, String content) throws IOException {
        Files.writeString(filePath(user, file), content);
    }

    public String load(String user, String file) throws IOException {
        return Files.readString(filePath(user, file));
    }

    public void delete(String user, String file) throws IOException {
        Files.delete(filePath(user, file));
    }
}