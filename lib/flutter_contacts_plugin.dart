import 'dart:async';

import 'package:flutter/services.dart';

import 'contact_model.dart';

class FlutterContactsPlugin {
  static const MethodChannel _channel = const MethodChannel('vizhan/flutter_contacts_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Iterable<Contact>> get contacts async {
    final Iterable contacts = await _channel.invokeMethod('getContacts');
    return contacts.map((m) => Contact.fromMap(m));
  }
}
