# ServerAPI Config File

This documentation describes the specification of the config file format.

The config format is binary and compressed, i.e. it can't be edited directly.
It can be generated/edited through the Config editor.

## General structure

A config file looks like this:

    SAC <Uncompressed size, 8>
     
    <Config name, str> <Config Type, 1> <Config Value, 1/4/8/str>
    ...

- The first 3 Bytes (```SAC```, the magic value) identify the format
- The next 8 Bytes mark the uncompressed size of the rest of the data

The rest of the data is compressed and stores the config entries and their
respective data. Every entry is stored like this:

- The entry starts with a Null-terminated string which holds the entry name
- The next Byte stores the entry type
    - Boolean (1 Byte)
    - Byte (1 Byte, duh)
    - Int (4 Bytes)
    - Float (4 Bytes)
    - Long (8 Bytes)
    - Double (8 Bytes)
    - String (Null-terminated string)
- The next Bytes store the entry value. The number of Bytes depends on the type