package com.projectx;

// Represents a single chat record entry.
class SingleChatRecord {

  public static final int SENT = 1;  // The chat message was sent.
  public static final int RECEIVED = 2;  // The chat message was received.

  private int type;  // Either sent or recieved.
  private String message;
  private int timestamp;  // Timestamp in local timezone of the phone.

  public SingleChatRecord(int type, String message, int timezone) {
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

  public int getTimestamp() {
    return timestamp;
  }
}

