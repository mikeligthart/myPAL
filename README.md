# myPAL
The application is build in the PlayFramework (https://www.playframework.com/).

To run the application you'll need to do the following (based on this installation guide: https://www.playframework.com/documentation/2.4.x/Installing):
    - Have the latest stable Java 8 JDK
    - Check that java and javac are properly installed (on Windows):
        + run 'java -version' and 'javac -version' in your terminal
        + If it does not recognize one or both it will not work. You can try in this order:
            > restarting your terminal
            > restarting your computer
            > https://www.java.com/nl/download/help/path.xml and then restart your computer
    - In the myPAL folder a tool called Activator has been supplied. Add this to your PATH as well (see Play installation guide for more details).
    - Navigate in your terminal to the myPAL folder 'cd path/to/myPAL/'
    - Start the Activator: 'activator ui'. If this does not work you can try the following:
        + Navigate in a file explorer to the myPAL folder
        + (In Windows) Double click 'activator.bat'
    - Activator is now opened as a webpage in your default browser (I tested it mostly in the latest version of Firefox, so I recommend using that as well).
    - Go to the build page. Here you can see the progress of the compiling software. The first time usually takes a while.
    - When the code is compiled go to the run page and hit 'run' to start the application.
    - When ready hit the url-button: 'http://localhost:9000' to open the application. The actual MyPAL webapp is now displayed in a different tab.
    - When running it the first time (or in dev mode every time) you'll need to spark the database by clicking 'Apply this script now' (all the hard stuff is being done for you).
    - If all went according to plan you now get the login screen and you can log in with:
        + username: admin
        + password: PAL_admin
