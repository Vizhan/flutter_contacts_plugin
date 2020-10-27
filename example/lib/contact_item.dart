import 'dart:typed_data';

import 'package:flutter/material.dart';

class ContactItem extends StatelessWidget {
  final String name;
  final Uint8List imageBytes;

  ContactItem({
    @required this.name,
    @required this.imageBytes,
  });

  @override
  Widget build(BuildContext context) {
    final avatar = imageBytes.isNotEmpty ? Image.memory(imageBytes) : Container();
    return Row(
      mainAxisSize: MainAxisSize.max,
      children: [
        ClipRRect(
          borderRadius: BorderRadius.circular(20),
          child: CircleAvatar(
            child: avatar,
            backgroundImage: null,
          ),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: Text(
            name,
            overflow: TextOverflow.ellipsis,
          ),
        ),
      ],
    );
  }
}
