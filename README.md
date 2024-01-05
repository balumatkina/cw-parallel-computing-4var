# cw-parallel-computing-4var

Hi. This is a guide for launching this Course Work for Parallel Computing on your local device.

### Step 0: Check your Java and IDE
Before running the program on your device, make sure Java is installed.
You can install it from official site: [Download Java](https://www.oracle.com/java/technologies/downloads/)

We recommend you installing Visual Studio Code as IDE to run this project successfully.
You can download it from official site: [Download Visual Studio Code](https://code.visualstudio.com/download)

### Step 1: Clone the project on your machine
Open it via installed GitHub Desktop and save to a location of your choise:
![image](https://github.com/balumatkina/cw-parallel-computing-4var/assets/90897866/b082c0a8-dfe6-4f18-b42d-5b11672aac1c)

Or clone the repository by using the following link:
https://github.com/balumatkina/cw-parallel-computing-4var.git

Or use the following prompt in your console:
`git clone https://github.com/balumatkina/cw-parallel-computing-4var.git`

### Step 2: Open and run the project
Open the code from your location using the IDE that we mentioned previously - Visual Studio Code.

Run the Server from a file named "Main.java" in "code/src" folder.

Run the Client from a file named "Client.java" of folder "code/src/client" and search for needed word(s).

Do not forget to disconnect your Clients and shut down the Server.


### Step 2.1: For more convenient users

`cd cw-parallel-computing-4var\code\src\`

`javac -d ./build code\src\Main.java code\src\server\*.java code\src\client\*java`

`cd build`

`jar cfe ../server.jar code.src.Main code\src\Main.class code\src\server\*`

`jar cfe ../client.jar code.src.client.Client code\src\client\Client.class`

`cd ..`

Server run:
`java -jar server.jar`

Client run:
`java -jar client.jar`