#! /bin/sh

# port
port=8081

# start server
java -XX:StartFlightRecording=filename=flight.jfr,settings=config.jfc \
     -jar target/jfr-context-example.jar $port &
pid=$!

sleep 2

# list of 10 realistic people users stored in a variable
people="joe sue bob sally frank mary john larry moe curly"

# function that returns shuffled user list
shuffle() {
  echo "$people" | tr ' ' '\n' | shuf | tr '\n' ' '
}

# register all users
for person in $people; do
  curl http://localhost:$port/register/$person
done

# create file test for every user in random order
for i in {1..10}; do
  for person in $(shuffle); do
    curl http://localhost:$port/store/$person/test_$i/test_$i_content_for_$person
  done
done

# load file test for every user in random order
for i in {1..10}; do
  for person in $(shuffle); do
    curl http://localhost:$port/load/$person/test_$i
  done
done

# create file test for every user in random order
for i in {1..10}; do
  for person in $(shuffle); do
    curl http://localhost:$port/delete/$person/test_$i
  done
done

kill $pid