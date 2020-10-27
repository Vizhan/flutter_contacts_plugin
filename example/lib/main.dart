import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_contacts_plugin/contact_model.dart';
import 'package:flutter_contacts_plugin/flutter_contacts_plugin.dart';

import 'contact_item.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<Contact> _contacts = [];

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    Iterable<Contact> contacts = [];
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      contacts = await FlutterContactsPlugin.contacts;
    } on PlatformException {}

    if (!mounted) return;

    setState(() {
      _contacts = contacts.toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Expanded(
              child: ListView.separated(
                itemCount: _contacts.length,
                padding: EdgeInsets.all(16),
                separatorBuilder: (context, index) => const SizedBox(height: 8),
                itemBuilder: (context, index) {
                  return ContactItem(
                    name: _contacts[index].displayName ?? 'no name',
                    imageBytes: _contacts[index].avatar,
                  );
                },
              ),
            )
          ],
        ),
      ),
    );
  }
}
