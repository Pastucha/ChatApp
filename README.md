# Chat application (client and server)
* The application uses a local database.
* IDE: Apache NetBeans 17.0
* Java with Ants

## Details about server App:
* Server listen for the connections on specific port (might be configured or hardcoded)
* Server keeps list of connected clients (list of connection with their id - nick, name etc)
* Server handle commands sent by GUI App
* Commands which server understand is more or less: send message, get users list, get
statistics, find message by text, get message history with specific user)
* Server keeps some kind in-memory DB (structure of messages and users)