# Exercise1 tests:

## Test Cases for Message

### Test Constructors

- Test constructor with `byte[] data`.
- Test constructor with `String data`.
- Test constructor with `double data`.
- Ensure each constructor correctly initializes the `date` field to the current time.

### Test Conversions

- Verify `asText` returns the correct string representation.
- Verify `asDouble` returns the correct double representation.
- Check that invalid conversions (e.g., non-numeric strings to double) result in `Double.NaN`.

## Test Cases for Agent

### Test Agent Interface Methods

- Implement a mock Agent and verify:
  - `getName()` returns the correct name.
  - `reset()` correctly resets the agent state.
  - `callback(String topic, Message msg)` handles messages appropriately.
  - `close()` releases any resources or performs necessary cleanup.

## Test Cases for Topic and TopicManagerSingleton

### Test Singleton Property

- Ensure that `TopicManagerSingleton.get()` always returns the same instance.
- Verify that multiple calls to `TopicManagerSingleton.get()` do not create multiple instances.

### Test Topic Management

- Verify `getTopic(String name)` creates a new Topic if one does not exist.
- Ensure `getTopic(String name)` returns the existing Topic if it already exists.
- Test `getTopics()` returns a collection of all Topics.
- Verify `clear()` removes all topics from the manager.

### Test Subscribe and Unsubscribe

- Verify that `subscribe(Agent agent)` adds an agent to the subscribers list.
- Ensure that `publish(Message msg)` sends the message to all subscribed agents via callback to verify the subscription.
- Verify that `unsubscribe(Agent agent)` removes an agent from the subscribers list.
- Ensure that after unsubscribing, the agent does not receive messages.

### Test Publish

- Ensure `publish(Message msg)` sends the message to all subscribed agents via callback.
- Verify that agents not subscribed to the topic do not receive the message.

### Test Add and Remove Publisher

- Verify that `addPublisher(Agent agent)` adds an agent to the publishers list.
- Verify that `removePublisher(Agent agent)` removes an agent from the publishers list.




# Exercise2 tests:

## Test Cases for ParallelAgent

### Test ParallelAgent Initialization

- Verify that `ParallelAgent` can be initialized with an `Agent` and a specified queue capacity.

### Test Callback Method

- Ensure `callback(String topic, Message msg)` enqueues the message into the `BlockingQueue`.
- Verify that the message is correctly passed to the `callback` method of the encapsulated `Agent` after being dequeued.

### Test Message Processing

- Verify that the `ParallelAgent` processes messages in the correct order.
- Ensure that long-running `callback` methods in one agent do not block the execution of `callback` methods in other agents.

### Test Thread Management

- Verify that `ParallelAgent` starts a separate thread for processing the queue.
- Ensure the processing thread sleeps when the queue is empty and wakes up when new messages are added.

### Test Close Method

- Ensure `close()` method properly stops the message processing thread.
- Verify that no threads remain open after calling the `close()` method.



# Exercise3 tests:

# Test Cases for Node

## Test Node Initialization

- Verify that a `Node` can be initialized with a name.

## Test Getters and Setters

- Ensure getters and setters work correctly for the `name` and `message` fields.

## Test Add Edge

- Verify that `addEdge(Node node)` correctly adds an edge to the `edges` list.

# Test Cases for Config

## Test Config Interface Methods

- Implement a mock `Config` and verify:
  - `create()` correctly initializes agents and topics.
  - `getName()` returns the correct name.
  - `getVersion()` returns the correct version.

# Test Cases for BinOpAgent

## Test BinOpAgent Initialization

- Verify that a `BinOpAgent` can be initialized with a name, input topics, output topic, and operation.

## Test Callback Method

- Ensure `callback(String topic, Message msg)` correctly processes messages and publishes results.

## Test Reset Method

- Verify that `reset()` correctly resets the state of the agent.
