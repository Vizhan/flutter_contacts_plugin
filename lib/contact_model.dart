import 'dart:typed_data';

class Contact {
  Contact({
    this.id,
    this.displayName,
    this.avatar,
  });

  String id, displayName;
  Uint8List avatar;

  Contact.fromMap(Map m) {
    id = m["id"];
    displayName = m["displayName"];
    avatar = m["avatar"];
  }
}
