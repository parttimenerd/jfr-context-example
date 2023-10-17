JFR Context Example
===================

Example code for my blog post on Jaroslav Bachorik's prototypical
implementation of JFR context.

You can find his blog posts, from which I adapted my example, here: 
https://jbachorik.github.io/posts/seeing-in-context_2 

And his trial implementation in an OpenJDK fork here:
https://github.com/DataDog/openjdk-jdk/tree/jb/jfr_context (commit 6670884)

My main idea is here to show an example of the API and how to use it for
a somewhat realistic example.

JFR Context proposal
--------------------
The proposal would allow us to create custom context
classes, set a specific context instance for the current thread
and let this be attached as meta data to certain events
(configurable via the JFR configuration).

The context then allows us to relate events to each other,
making it simpler to analyse performance issues.

The currently supported events are:

- jdk.ExecutionSample
- jdk.NativeMethodSample
- jdk.ObjectAllocationSample
- jdk.ThreadPark
- jdk.JavaMonitorEnter
- jdk.JavaMonitorWait
- jdk.JavaMonitorInflate
- jdk.SystemGC
- jdk.ObjectAllocationInNewTLAB
- jdk.ObjectAllocationOutsideTLAB
- jdk.ZAllocationStall
- jdk.FileRead
- jdk.FileWrite
- jdk.SocketRead
- jdk.SocketWrite
- jdk.ThreadSleep

(see https://github.com/DataDog/dd-trace-java/pull/6013#issue-1935670789)
              
We use the following basic server as a test application:   

Example Program
---------------
We create a simple file server, which can
- register a user (url schema `register/{user}`)
- store data as a file (`store/{user}/{file}/{content}`)
- retrieve file content (`load/{user}/{file}`)
- delete files (`delete/{user}/{file}`)

The URLs are simple to use and we don't bother about
error handling, user authentication, or large files,
as this would complicate our example. I leave as an
exercise to the inclined reader.

We build this web server using [Javalin](https://javalin.io), as it
allows to create all this without any Spring magic.

Be aware that this example requires the custom
JDK to build and run.

You can build the example using `mvn package` and 
run it via:

```sh
# where 1000 is the port to listen on
java -jar target/jfr-context-example.jar 1000
```

You can use it via curl:

```sh
# start the server
java -XX:StartFlightRecording=filename=flight.jfr,settings=config.jfc \
     -jar target/jfr-context-example.jar 1000 &
pid=$!

# register a user
curl http://localhost:1000/register/moe

# store a file
curl http://localhost:1000/store/moe/hello_file/Hello

# load the file
curl http://localhost:1000/load/moe/hello_file
-> Hello

# delete the file
curl http://localhost:1000/delete/moe/hello_file

kill $pid

# this results in the flight.jfr file
```

For test purposes, you can also use the `test.sh` script,
which starts the server, registers a few users and
stores, loads and deletes a few files. This also
results in a `flight.jfr` file.

We use a custom JFR configuration, to enable the IO events
without any threshold. This is not recommended for production,
but is required in our toy example to get any events.

Our implementation creates a new trace context for each request
and tracks the user, the file name and the operation.


License
-------
MIT