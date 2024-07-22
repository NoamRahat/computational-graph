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
