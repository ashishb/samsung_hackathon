package com.projectx;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;

// Represents a single chat record entry.
class SingleChatRecord {

  public static final int SENT = 1;  // The chat message was sent.
  public static final int RECEIVED = 2;  // The chat message was received.

  private int type;  // Either sent or recieved.
  private String message;
  private long timestamp;  // Timestamp in local timezone of the phone.

  public SingleChatRecord(int type, String message, long timestamp) {
    this.type = type;
    this.message = message;
    this.timestamp = timestamp;
  }

  public String getMessage() {
    return message;
  }

  public int getType() {
    return type;
  }

  public long getTimestamp() {
    return timestamp;
  }
}

// Represents a collection of chat records with everyone else.
final class ChatRecords {
  // Maps unique id of the person (we are talking to)
  // to list of chat records.
  private static HashMap< String, ArrayList<SingleChatRecord> > chats =
    new HashMap< String, ArrayList<SingleChatRecord> >();

  static {
    // This block is only for debugging.
    ChatRecords.addOutgoingMessage(Constants.TEST, "outgoing foo");
    ChatRecords.addIncomingMessage(Constants.TEST, "incoming foo");
    ChatRecords.addOutgoingMessage(Constants.TEST, "outgoing foo");
    ChatRecords.addIncomingMessage(Constants.TEST, "incoming foo");
    ChatRecords.addOutgoingMessage(Constants.TEST, "outgoing foo");
    ChatRecords.addIncomingMessage(Constants.TEST, "incoming foo");
    ChatRecords.addOutgoingMessage(Constants.TEST, "outgoing foo");
    ChatRecords.addIncomingMessage(Constants.TEST, "incoming foo");
    ChatRecords.addOutgoingMessage(Constants.TEST, "outgoing foo");
    ChatRecords.addIncomingMessage(Constants.TEST, "incoming foo");
    ChatRecords.addOutgoingMessage(Constants.TEST, "outgoing foo");
    ChatRecords.addIncomingMessage(Constants.TEST, "incoming foo");
    ChatRecords.addOutgoingMessage(Constants.TEST, "outgoing foo");
    ChatRecords.addIncomingMessage(Constants.TEST, "incoming foo");
    ChatRecords.addOutgoingMessage(Constants.TEST, "outgoing foo");
    ChatRecords.addIncomingMessage(Constants.TEST, "incoming foo");
  };

  private ChatRecords() {}

  private static void addRecord(String personId, String message, int type) {
    // type is either SENT or RECEIVED.
    long timestamp = System.currentTimeMillis();
    SingleChatRecord record = new SingleChatRecord(type, message, timestamp);
    if (!chats.containsKey(personId)) {
      chats.put(personId, new ArrayList<SingleChatRecord>());
    }
    chats.get(personId).add(record);
  }

  public static void addOutgoingMessage(String personId, String message) {
    addRecord(personId, message, SingleChatRecord.SENT);
  }

  public static void addIncomingMessage(String personId, String message) {
    addRecord(personId, message, SingleChatRecord.RECEIVED);
  }

  public static ArrayList<SingleChatRecord> getAllRecordsForThePerson(String personId) {
    if (!chats.containsKey(personId)) {
      Log.e(Constants.TAG, "Key " + personId + "not found.");
      return null;
    } else {
      return chats.get(personId);
    }
  }
}
